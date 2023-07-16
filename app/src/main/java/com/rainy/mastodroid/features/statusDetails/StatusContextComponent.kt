/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.features.statusDetails

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.rainy.mastodroid.core.domain.interactor.StatusInteractor
import com.rainy.mastodroid.core.domain.model.status.Status
import com.rainy.mastodroid.core.domain.model.status.statusThread.ReplyType
import com.rainy.mastodroid.core.domain.model.status.statusThread.StatusNode
import com.rainy.mastodroid.extensions.coroutineExceptionHandler
import com.rainy.mastodroid.extensions.launchLoading
import com.rainy.mastodroid.features.statusDetails.model.StatusDetailsState
import com.rainy.mastodroid.features.statusDetails.model.StatusInContextItemModel
import com.rainy.mastodroid.features.statusDetails.model.StatusThreadElement
import com.rainy.mastodroid.features.statusDetails.model.toFocusedStatusItemModel
import com.rainy.mastodroid.ui.elements.statusListItem.model.toStatusListItemModel
import com.rainy.mastodroid.util.ErrorModel
import com.rainy.mastodroid.util.ImmutableWrap
import com.rainy.mastodroid.util.toErrorModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RetainedStatusContextComponent(
    private val statusInteractor: StatusInteractor,
    private val statusId: String
) : InstanceKeeper.Instance {
    private val coroutineScope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())

    private val exceptionHandler = coroutineExceptionHandler { throwable ->
        errorEventChannel.trySend(throwable.toErrorModel())
    }

    private val errorEventChannel = Channel<ErrorModel>()
    val errorEventFlow = errorEventChannel.receiveAsFlow()

    private val _loadingState = MutableStateFlow(false)
    val loadingState = _loadingState.asStateFlow()

    private val statusFlow = statusInteractor.getStatusFlow(statusId)

    private val sensitiveStatusExpanded = MutableStateFlow(setOf<String>())
    val statusContextFlow = statusInteractor.getStatusContextFlow(statusId)
        .combine(statusFlow) { statusContext, status ->
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
            errorEventChannel.trySend(it.toErrorModel())
        }
        .stateIn(
            coroutineScope,
            SharingStarted.WhileSubscribed(5000),
            StatusDetailsState.Loading
        )

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
        coroutineScope.launchLoading(_loadingState, exceptionHandler) {
            val statusDetailsDeffered =
                async { statusInteractor.fetchStatusDetails(statusId) }
            val statusContextDeffered =
                async { statusInteractor.getContextTreesForStatus(statusId) }
            statusContextDeffered.await()
            statusDetailsDeffered.await()
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
        coroutineScope.launch(exceptionHandler) {
            statusInteractor.setFavoriteStatus(id, action)
        }
    }

    fun onReblogClicked(id: String, action: Boolean) {
        coroutineScope.launch(exceptionHandler) {
            statusInteractor.setReblogStatus(id, action)
        }
    }

    fun onSensitiveExpandClicked(id: String) {
        sensitiveStatusExpanded.update {
            it.plus(id)
        }
    }

    override fun onDestroy() {
        coroutineScope.cancel()
    }
}

class StatusContextComponent(
    componentContext: ComponentContext,
    private val statusInteractor: StatusInteractor,
    private val statusId: String,
    private val navigateToStatusContext: (id: String) -> Unit,
    private val navigateToAccount: (id: String) -> Unit,
    private val navigateToStatusAttachments: (statusId: String, attachmentIndex: Int) -> Unit
) : ComponentContext by componentContext {

    private val retainedStatusContextComponent = instanceKeeper.getOrCreate {
        RetainedStatusContextComponent(
            statusInteractor = statusInteractor, statusId = statusId
        )
    }

    val errorEvents get() = retainedStatusContextComponent.errorEventFlow
    val statusContextState get() = retainedStatusContextComponent.statusContextFlow
    val loadingState get() = retainedStatusContextComponent.loadingState

    fun loadStatus() {
        retainedStatusContextComponent.loadStatus()
    }

    fun onFavoriteClicked(id: String, action: Boolean) {
        retainedStatusContextComponent.onFavoriteClicked(id, action)
    }

    fun onReblogClicked(id: String, action: Boolean) {
        retainedStatusContextComponent.onReblogClicked(id, action)
    }

    fun onSensitiveExpandClicked(id: String) {
        retainedStatusContextComponent.onSensitiveExpandClicked(id)
    }

    fun onStatusClicked(id: String) {
        navigateToStatusContext(id)
    }

    fun onAccountClicked(id: String) {
        navigateToAccount(id)
    }

    fun onAttachmentClicked(statusId: String, attachmentIndex: Int) {
        navigateToStatusAttachments(statusId, attachmentIndex)
    }

}