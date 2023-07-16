/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.features.accountDetails

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.pages.ChildPages
import com.arkivanov.decompose.router.pages.Pages
import com.arkivanov.decompose.router.pages.PagesNavigation
import com.arkivanov.decompose.router.pages.childPages
import com.arkivanov.decompose.router.pages.select
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.rainy.mastodroid.core.data.model.entity.status.AccountStatusTimelineType
import com.rainy.mastodroid.core.domain.interactor.AccountInteractor
import com.rainy.mastodroid.core.domain.interactor.StatusInteractor
import com.rainy.mastodroid.core.domain.interactor.TimelineInteractor
import com.rainy.mastodroid.core.domain.model.user.CustomEmoji
import com.rainy.mastodroid.core.domain.model.user.FeaturedTag
import com.rainy.mastodroid.extensions.coroutineExceptionHandler
import com.rainy.mastodroid.extensions.launchLoading
import com.rainy.mastodroid.features.accountDetails.model.AccountDetailsItemModel
import com.rainy.mastodroid.features.accountDetails.model.AccountRelationshipsState
import com.rainy.mastodroid.ui.elements.statusListItem.model.toItemModel
import com.rainy.mastodroid.ui.styledText.annotateMastodonContent
import com.rainy.mastodroid.ui.styledText.annotateMastodonEmojis
import com.rainy.mastodroid.util.ErrorModel
import com.rainy.mastodroid.util.ImmutableWrap
import com.rainy.mastodroid.util.onErrorValue
import com.rainy.mastodroid.util.toErrorModel
import com.rainy.mastodroid.util.wrapResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class RetainedAccountDetailsComponent(
    private val accountInteractor: AccountInteractor,
    private val accountId: String
) : InstanceKeeper.Instance {

    private val coroutineScope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())

    private val exceptionHandler = coroutineExceptionHandler { throwable ->
        handleException(throwable)
    }

    private val errorEventChannel = Channel<ErrorModel>(Channel.BUFFERED)
    val errorEventFlow = errorEventChannel.receiveAsFlow()

    private val _accountRelationships =
        MutableStateFlow<AccountRelationshipsState>(AccountRelationshipsState.Loading)
    val accountRelationships = _accountRelationships.asStateFlow()

    private val accountFeaturedTags = MutableStateFlow<List<FeaturedTag>>(listOf())

    val accountDetails = accountInteractor.getAccountFlow(accountId)
        .map { account ->
            account?.let {
                val emojiShortCodes = it.customEmojis.map { it.shortcode }
                AccountDetailsItemModel(
                    id = it.id,
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
        .stateIn(
            coroutineScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )

    private val _loadingState = MutableStateFlow(false)
    val loadingState = _loadingState.asStateFlow()

    init {
        fetchAccountDetails()
    }

    fun fetchAccountDetails() {
        coroutineScope.launchLoading(_loadingState, exceptionHandler) {
            val accountDeffered = async { accountInteractor.fetchAccount(accountId) }
            val relationshipsDeffered =
                async { loadRelationships() }
            val featuredTagsDeffered = async { loadFeaturedTags() }

            relationshipsDeffered.await()
            featuredTagsDeffered.await()
            accountDeffered.await()
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
        errorEventChannel.trySend(throwable.toErrorModel())
    }


    override fun onDestroy() {
        coroutineScope.cancel()
    }

}

class AccountDetailsComponent(
    componentContext: ComponentContext,
    private val accountInteractor: AccountInteractor,
    private val timelineInteractor: TimelineInteractor,
    private val statusInteractor: StatusInteractor,
    private val accountId: String
) : ComponentContext by componentContext {

    private val navigation = PagesNavigation<AccountTimelineConfig>()

    val childPages: Value<ChildPages<*, AccountStatusTimelineComponent>> =
        childPages(
            source = navigation,
            initialPages = {
                Pages(
                    items = listOf(
                        AccountTimelineConfig(
                            accountId,
                            AccountStatusTimelineType.POSTS
                        ),
                        AccountTimelineConfig(
                            accountId,
                            AccountStatusTimelineType.POSTS_REPLIES
                        ),
                        AccountTimelineConfig(
                            accountId,
                            AccountStatusTimelineType.MEDIA
                        )
                    ),
                    selectedIndex = 0
                )
            },

            ) { configuration: AccountTimelineConfig, componentContext: ComponentContext ->
            AccountStatusTimelineComponent(
                componentContext,
                configuration.timelineType,
                configuration.accountId,
                timelineInteractor,
                statusInteractor
            )
        }

    private val retainedAccountComponent = instanceKeeper.getOrCreate {
        RetainedAccountDetailsComponent(
            accountInteractor = accountInteractor, accountId = accountId
        )
    }

    val errorEventFlow get() = retainedAccountComponent.errorEventFlow
    val loadingState get() = retainedAccountComponent.loadingState
    val accountRelationships get() = retainedAccountComponent.accountRelationships
    val accountDetails get() = retainedAccountComponent.accountDetails

    fun fetchAccountDetails() {
        retainedAccountComponent.fetchAccountDetails()
    }

    fun onPageSelected(pageIndex: Int) {
        navigation.select(pageIndex)
    }

    @Parcelize
    data class AccountTimelineConfig(
        val accountId: String,
        val timelineType: AccountStatusTimelineType
    ) : Parcelable
}
