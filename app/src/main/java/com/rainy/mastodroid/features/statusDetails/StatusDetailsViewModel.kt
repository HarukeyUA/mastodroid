/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.features.statusDetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.rainy.mastodroid.core.base.BaseViewModel
import com.rainy.mastodroid.core.domain.interactor.StatusInteractor
import com.rainy.mastodroid.core.domain.model.status.Status
import com.rainy.mastodroid.core.domain.model.status.statusThread.ReplyType
import com.rainy.mastodroid.core.domain.model.status.statusThread.StatusNode
import com.rainy.mastodroid.core.navigation.RouteNavigator
import com.rainy.mastodroid.core.navigation.getOrThrow
import com.rainy.mastodroid.features.statusDetails.model.StatusDetailsState
import com.rainy.mastodroid.features.statusDetails.model.StatusInContextItemModel
import com.rainy.mastodroid.features.statusDetails.model.StatusThreadElement
import com.rainy.mastodroid.features.statusDetails.model.toFocusedStatusItemModel
import com.rainy.mastodroid.ui.elements.statusListItem.model.toStatusListItemModel
import com.rainy.mastodroid.util.ErrorModel
import com.rainy.mastodroid.util.ImmutableWrap
import com.rainy.mastodroid.util.NetworkExceptionIdentifier
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StatusDetailsViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val statusInteractor: StatusInteractor,
    private val exceptionIdentifier: NetworkExceptionIdentifier,
    routeNavigator: RouteNavigator
) : BaseViewModel(), RouteNavigator by routeNavigator {

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        errorEventChannel.trySend(exceptionIdentifier.identifyException(throwable))
    }

    private val errorEventChannel = Channel<ErrorModel>()
    val errorEventFlow = errorEventChannel.receiveAsFlow()

    private val statusId: String by lazy { savedStateHandle.getOrThrow(StatusDetailsRoute.STATUS_ID_ARG) }
    private val statusIdFlow = savedStateHandle.getStateFlow(StatusDetailsRoute.STATUS_ID_ARG, "")
    private val statusFlow = statusIdFlow.flatMapLatest {
        statusInteractor.getStatusFlow(it)
    }
    private val sensitiveStatusExpanded = MutableStateFlow(setOf<String>())
    val statusContextFlow =
        statusIdFlow.flatMapLatest {
            if (it.isNotEmpty()) {
                statusInteractor.getStatusContextFlow(it)
            } else {
                flowOf()
            }
        }.combine(statusFlow) { statusContext, status ->
            if (status != null) {
                StatusDetailsState.Ready(
                    StatusInContextItemModel(
                        ancestors = ImmutableWrap(statusContext.ancestors.map(Status::toStatusListItemModel)),
                        descendants = ImmutableWrap(flattenReplyForest(statusContext.descendants)),
                        focusedStatus = status.toFocusedStatusItemModel()
                    )
                )
            } else {
                StatusDetailsState.Loading
            }
        }.combine(sensitiveStatusExpanded) { statusContext, expandedStatuses ->
            if (statusContext is StatusDetailsState.Ready) {
                StatusDetailsState.Ready(
                    statusContext.statusInContext.copy(
                        ancestors = ImmutableWrap(
                            setSensitiveAncestorsExpandedState(statusContext, expandedStatuses)
                        ),
                        descendants = ImmutableWrap(
                            setSensitiveDescendantsExpandedState(statusContext, expandedStatuses)
                        )
                    )
                )
            } else {
                statusContext
            }
        }.flowOn(Dispatchers.Default)
            .catch {
                errorEventChannel.trySend(exceptionIdentifier.identifyException(it))
            }
            .stateIn(StatusDetailsState.Loading)

    private fun setSensitiveAncestorsExpandedState(
        statusContext: StatusDetailsState.Ready,
        expandedStatuses: Set<String>
    ) = statusContext.statusInContext.ancestors.content.map {
        if (expandedStatuses.contains(it.id)) {
            it.copy(isSensitiveExpanded = true)
        } else {
            it
        }
    }

    private fun setSensitiveDescendantsExpandedState(
        statusContext: StatusDetailsState.Ready,
        expandedStatuses: Set<String>
    ) = statusContext.statusInContext.descendants.content.map { threadElement ->
        if (expandedStatuses.contains(threadElement.status.id)) {
            threadElement.copy(
                status = threadElement.status.copy(isSensitiveExpanded = true)
            )
        } else {
            threadElement
        }
    }

    init {
        loadStatus()
    }

    fun loadStatus() {
        viewModelScope.launch(exceptionHandler) {
            loadingTask {
                val statusDetailsDeffered =
                    async { statusInteractor.fetchStatusDetails(statusId) }
                val statusContextDeffered =
                    async { statusInteractor.getContextTreesForStatus(statusId) }
                statusContextDeffered.await()
                statusDetailsDeffered.await()
            }
        }
    }

    private fun flattenReplyForest(
        statusNode: List<StatusNode>,
        isTopLevel: Boolean = true,
        isLastBranch: Boolean = false
    ): List<StatusThreadElement> {
        return statusNode.flatMapIndexed { index: Int, childStatusNode: StatusNode ->
            val isChildInLastBranch = isLastBranch && statusNode.size - 1 == index
            buildList {
                add(
                    StatusThreadElement(
                        childStatusNode.content.toStatusListItemModel(),
                        reply = when {
                            !isTopLevel && index > 0 -> ReplyType.INDIRECT_REPLY
                            !isTopLevel -> ReplyType.DIRECT_REPLY
                            else -> ReplyType.NONE
                        },
                        repliedTo = when {
                            childStatusNode.children.isEmpty() &&
                                    !isChildInLastBranch &&
                                    !isTopLevel -> ReplyType.INDIRECT_REPLY

                            childStatusNode.children.isNotEmpty() -> ReplyType.DIRECT_REPLY
                            else -> ReplyType.NONE
                        }
                    )
                )
                if (childStatusNode.children.isNotEmpty()) {
                    addAll(
                        flattenReplyForest(
                            childStatusNode.children,
                            false,
                            if (isTopLevel) true else isChildInLastBranch
                        )
                    )
                }
            }
        }
    }

    fun onFavoriteClicked(id: String, action: Boolean) {
        viewModelScope.launch(exceptionHandler) {
            statusInteractor.setFavoriteStatus(id, action)
        }
    }

    fun onReblogClicked(id: String, action: Boolean) {
        viewModelScope.launch(exceptionHandler) {
            statusInteractor.setReblogStatus(id, action)
        }
    }

    fun onSensitiveExpandClicked(id: String) {
        sensitiveStatusExpanded.update {
            it.plus(id)
        }
    }

    fun onStatusClicked(id: String) {
        performNavigation {
            navigate(StatusDetailsRoute.getRoute(id))
        }
    }
}