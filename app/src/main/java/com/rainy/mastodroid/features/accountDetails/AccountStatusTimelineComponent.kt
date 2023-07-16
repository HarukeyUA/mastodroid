/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.features.accountDetails

import androidx.paging.cachedIn
import androidx.paging.map
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.rainy.mastodroid.core.data.model.entity.status.AccountStatusTimelineType
import com.rainy.mastodroid.core.domain.interactor.StatusInteractor
import com.rainy.mastodroid.core.domain.interactor.TimelineInteractor
import com.rainy.mastodroid.core.domain.model.status.Status
import com.rainy.mastodroid.extensions.coroutineExceptionHandler
import com.rainy.mastodroid.ui.elements.statusListItem.model.toStatusListItemModel
import com.rainy.mastodroid.util.ErrorModel
import com.rainy.mastodroid.util.toErrorModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RetainedAccountStatusTimelineComponent(
    timelineType: AccountStatusTimelineType,
    accountId: String,
    timelineInteractor: TimelineInteractor,
    private val statusInteractor: StatusInteractor,
) : InstanceKeeper.Instance {
    private val coroutineScope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())

    private val exceptionHandler = coroutineExceptionHandler { throwable ->
        errorEventChannel.trySend(throwable.toErrorModel())
    }

    private val errorEventChannel = Channel<ErrorModel>(Channel.BUFFERED)
    val errorEventFlow = errorEventChannel.receiveAsFlow()

    private val expandedItems = MutableStateFlow<Set<String>>(setOf())

    val timeline = timelineInteractor.getAccountTimelinePaging(
        accountId = accountId,
        accountStatusTimelineType = timelineType
    ).map { pagingData -> pagingData.map(Status::toStatusListItemModel) }
        .flowOn(Dispatchers.Default)
        .cachedIn(coroutineScope)
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
        coroutineScope.launch(exceptionHandler) {
            statusInteractor.setFavoriteStatus(id, action)
        }
    }

    fun setReblog(id: String, action: Boolean) {
        coroutineScope.launch(exceptionHandler) {
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

    override fun onDestroy() {
        coroutineScope.cancel()
    }
}

class AccountStatusTimelineComponent(
    componentContext: ComponentContext,
    private val timelineType: AccountStatusTimelineType,
    private val accountId: String,
    private val timelineInteractor: TimelineInteractor,
    private val statusInteractor: StatusInteractor,
) : ComponentContext by componentContext {

    private val retainedAccountStatusTimelineComponent = instanceKeeper.getOrCreate {
        RetainedAccountStatusTimelineComponent(
            timelineType = timelineType,
            accountId = accountId,
            timelineInteractor = timelineInteractor,
            statusInteractor = statusInteractor
        )
    }

    val errorEvents get() = retainedAccountStatusTimelineComponent.errorEventFlow
    val timeline get() = retainedAccountStatusTimelineComponent.timeline

    fun setFavorite(id: String, action: Boolean) {
        retainedAccountStatusTimelineComponent.setFavorite(id, action)
    }

    fun setReblog(id: String, action: Boolean) {
        retainedAccountStatusTimelineComponent.setReblog(id, action)
    }

    fun expandSensitiveStatus(id: String) {
        retainedAccountStatusTimelineComponent.expandSensitiveStatus(id)
    }


}