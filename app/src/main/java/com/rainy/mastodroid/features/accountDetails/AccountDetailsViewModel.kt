/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.features.accountDetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.rainy.mastodroid.core.base.BaseViewModel
import com.rainy.mastodroid.core.domain.interactor.AccountInteractor
import com.rainy.mastodroid.core.domain.model.user.CustomEmoji
import com.rainy.mastodroid.core.domain.model.user.FeaturedTag
import com.rainy.mastodroid.core.navigation.RouteNavigator
import com.rainy.mastodroid.core.navigation.getOrThrow
import com.rainy.mastodroid.features.accountDetails.model.AccountDetailsItemModel
import com.rainy.mastodroid.features.accountDetails.model.AccountRelationshipsState
import com.rainy.mastodroid.ui.elements.statusListItem.model.toItemModel
import com.rainy.mastodroid.ui.styledText.annotateMastodonContent
import com.rainy.mastodroid.ui.styledText.annotateMastodonEmojis
import com.rainy.mastodroid.util.ErrorModel
import com.rainy.mastodroid.util.ImmutableWrap
import com.rainy.mastodroid.util.NetworkExceptionIdentifier
import com.rainy.mastodroid.util.onErrorValue
import com.rainy.mastodroid.util.wrapResult
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AccountDetailsViewModel(
    private val accountInteractor: AccountInteractor,
    savedStateHandle: SavedStateHandle,
    private val exceptionIdentifier: NetworkExceptionIdentifier,
    routeNavigator: RouteNavigator
) : BaseViewModel(),
    RouteNavigator by routeNavigator {

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        handleException(throwable)
    }

    private val errorEventChannel = Channel<ErrorModel>(Channel.BUFFERED)
    val errorEventFlow = errorEventChannel.receiveAsFlow()

    private val accountId = savedStateHandle.getOrThrow<String>(AccountDetailsRoute.ACCOUNT_ID_ARG)
    private val accountIdFlow: Flow<String> = savedStateHandle.getStateFlow<String?>(
        AccountDetailsRoute.ACCOUNT_ID_ARG,
        null
    ).mapNotNull { it }

    private val _accountRelationships =
        MutableStateFlow<AccountRelationshipsState>(AccountRelationshipsState.Loading)
    val accountRelationships = _accountRelationships.asStateFlow()

    private val accountFeaturedTags = MutableStateFlow<List<FeaturedTag>>(listOf())

    val accountDetails = accountIdFlow.flatMapLatest {
        accountInteractor.getAccountFlow(it)
    }.map { account ->
        account?.let {
            val emojiShortCodes = it.customEmojis.map { it.shortcode }
            AccountDetailsItemModel(
                bannerUrl = it.headerUrl,
                avatarUrl = it.avatarUrl,
                statusesCount = it.statusesCount,
                followingCount = it.followingCount,
                followersCount = it.followersCount,
                accountUri = it.accountUri,
                bio = it.note.annotateMastodonContent(emojiShortCodes),
                customEmojis = ImmutableWrap(content = it.customEmojis.map(CustomEmoji::toItemModel)),
                customEmojisCodes = ImmutableWrap(content = emojiShortCodes),
                displayName = it.displayName.annotateMastodonEmojis(emojiShortCodes)

            )
        }
    }.combine(accountFeaturedTags) { account, tags ->
        account?.copy(featuredTags = ImmutableWrap(tags))
    }
        .flowOn(Dispatchers.Default)
        .stateIn(null)

    init {
        fetchAccountDetails()
    }

    fun fetchAccountDetails() {
        viewModelScope.launch(exceptionHandler) {
            loadingTask {
                val accountDeffered = async { accountInteractor.fetchAccount(accountId) }
                val relationshipsDeffered =
                    async { loadRelationships() }
                val featuredTagsDeffered = async { loadFeaturedTags() }

                relationshipsDeffered.await()
                featuredTagsDeffered.await()
                accountDeffered.await()
            }
        }
    }

    private suspend fun loadRelationships() {
        wrapResult {
            accountInteractor.getRelationshipsWithAccount(accountId).also {
                _accountRelationships.value = AccountRelationshipsState.Relationships(
                    blockedBy = it.blockedBy,
                    followedBy = it.followedBy,
                    following = it.following,
                    requested = it.requested
                )
            }
        }.onErrorValue { throwable ->
            _accountRelationships.update {
                if (it is AccountRelationshipsState.Relationships) {
                    it
                } else {
                    AccountRelationshipsState.Error
                }
            }
            handleException(throwable)
        }
    }

    private suspend fun loadFeaturedTags() {
        wrapResult {
            accountInteractor.getFeaturedTags(accountId).also {
                accountFeaturedTags.value = it
            }
        }.onErrorValue {
            handleException(it)
        }
    }

    private fun handleException(throwable: Throwable) {
        errorEventChannel.trySend(exceptionIdentifier.identifyException(throwable))
    }
}