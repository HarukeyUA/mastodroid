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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.receiveAsFlow
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
        }.flowOn(Dispatchers.Default)
            .catch {
                errorEventChannel.trySend(exceptionIdentifier.identifyException(it))
            }
            .stateIn(StatusDetailsState.Loading)

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

    }

    fun onStatusClicked(id: String) {
        performNavigation {
            navigate(StatusDetailsRoute.getRoute(id))
        }
    }
}