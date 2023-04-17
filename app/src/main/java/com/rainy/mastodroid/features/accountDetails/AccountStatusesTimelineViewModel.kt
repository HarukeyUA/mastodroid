/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.features.accountDetails

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import com.rainy.mastodroid.core.base.BaseViewModel
import com.rainy.mastodroid.core.data.model.entity.status.AccountStatusTimelineType
import com.rainy.mastodroid.core.domain.interactor.TimelineInteractor
import com.rainy.mastodroid.core.domain.interactor.StatusInteractor
import com.rainy.mastodroid.core.domain.model.status.Status
import com.rainy.mastodroid.ui.elements.statusListItem.model.toStatusListItemModel
import com.rainy.mastodroid.util.ErrorModel
import com.rainy.mastodroid.util.NetworkExceptionIdentifier
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AccountStatusesTimelineViewModel(
    private val timelineInteractor: TimelineInteractor,
    private val statusInteractor: StatusInteractor,
    private val exceptionIdentifier: NetworkExceptionIdentifier,
    private val timelineType: AccountStatusTimelineType,
    private val accountId: String
) : BaseViewModel() {

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        errorEventChannel.trySend(exceptionIdentifier.identifyException(throwable))
    }

    private val errorEventChannel = Channel<ErrorModel>(Channel.BUFFERED)
    val errorEventFlow = errorEventChannel.receiveAsFlow()

    private val expandedItems = MutableStateFlow<Set<String>>(setOf())

    val timeline = timelineInteractor.getAccountTimelinePaging(
        accountId = accountId,
        accountStatusTimelineType = timelineType
    ).map { pagingData -> pagingData.map(Status::toStatusListItemModel) }
        .flowOn(Dispatchers.Default)
        .cachedIn(viewModelScope)
        .combine(expandedItems) { timeline, expandedItems ->
            timeline.map { status ->
                if (expandedItems.contains(status.id)) {
                    status.copy(isSensitiveExpanded = true)
                } else {
                    status
                }
            }
        }
        .flowOn(Dispatchers.Default)

    fun setFavorite(id: String, action: Boolean) {
        viewModelScope.launch(exceptionHandler) {
            statusInteractor.setFavoriteStatus(id, action)
        }
    }

    fun setReblog(id: String, action: Boolean) {
        viewModelScope.launch(exceptionHandler) {
            statusInteractor.setReblogStatus(id, action)
        }
    }

    fun expandSensitiveStatus(id: String) {
        expandedItems.update {
            it.toMutableSet().apply {
                add(id)
            }
        }
    }
}