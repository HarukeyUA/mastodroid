/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.features.home

import androidx.paging.cachedIn
import androidx.paging.map
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.rainy.mastodroid.core.domain.interactor.StatusInteractor
import com.rainy.mastodroid.core.domain.interactor.TimelineInteractor
import com.rainy.mastodroid.core.domain.model.status.Status
import com.rainy.mastodroid.extensions.coroutineExceptionHandler
import com.rainy.mastodroid.features.home.model.CurrentlyPlayingMedia
import com.rainy.mastodroid.ui.elements.statusListItem.model.StatusListItemModel
import com.rainy.mastodroid.ui.elements.statusListItem.model.VideoAttachmentItemModel
import com.rainy.mastodroid.ui.elements.statusListItem.model.toStatusListItemModel
import com.rainy.mastodroid.util.ErrorModel
import com.rainy.mastodroid.util.ImmutableWrap
import com.rainy.mastodroid.util.logi
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

class RetainedHomeComponent(
    private val timelineInteractor: TimelineInteractor,
    private val statusInteractor: StatusInteractor
) : InstanceKeeper.Instance {
    private val coroutineScope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())

    private val exceptionHandler = coroutineExceptionHandler { throwable ->
        errorEventChannel.trySend(throwable.toErrorModel())
    }

    private val errorEventChannel = Channel<ErrorModel>()
    val errorEventFlow = errorEventChannel.receiveAsFlow()

    val currentlyPlayingItem = MutableStateFlow<CurrentlyPlayingMedia?>(null)
    private val expandedItems = MutableStateFlow<Set<String>>(setOf())

    val homeStatusesFlow = timelineInteractor.homeTimelinePaging
        .map { statusPagingData ->
            statusPagingData.map(Status::toStatusListItemModel)
        }
        .flowOn(Dispatchers.Default)
        .cachedIn(coroutineScope)
        .combine(currentlyPlayingItem) { timeline, currentlyPlaying ->
            timeline.map { status ->
                if (status.id == currentlyPlaying?.statusId) {
                    setCurrentlyPlayingAttachment(status, currentlyPlaying.mediaId)
                } else {
                    status
                }
            }
        }.combine(expandedItems) { timeline, expandedItems ->
            timeline.map { status ->
                if (expandedItems.contains(status.id)) {
                    status.copy(isSensitiveExpanded = true)
                } else {
                    status
                }
            }
        }.flowOn(Dispatchers.Default)

    private fun setCurrentlyPlayingAttachment(
        status: StatusListItemModel,
        attachmentId: String,
    ) = status.copy(
        attachments = ImmutableWrap(status.attachments.content.map { attachment ->
            if (attachment is VideoAttachmentItemModel && attachment.id == attachmentId) {
                attachment.copy(currentlyPlaying = true)
            } else {
                attachment
            }
        }
        )
    )

    fun setFocussedVideoAttachment(status: StatusListItemModel?) {
        logi("Focused timeline item: ${status?.id}")
        if (status != null && status.attachments.content.size == 1) {
            currentlyPlayingItem.value = status.attachments.content.firstOrNull()?.let { media ->
                if (media is VideoAttachmentItemModel) {
                    logi("Playing: ${media.url}")
                    CurrentlyPlayingMedia(status.id, media.id, media.url)
                } else {
                    logi("Stopping playing")
                    null
                }
            }
        } else {
            logi("Stopping playing")
            currentlyPlayingItem.value = null
        }
    }

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

class HomeComponent(
    componentContext: ComponentContext,
    private val timelineInteractor: TimelineInteractor,
    private val statusInteractor: StatusInteractor,
    private val navigateToStatusContext: (id: String) -> Unit,
    private val navigateToAccount: (id: String) -> Unit,
    private val navigateToStatusAttachments: (statusId: String, attachmentIndex: Int) -> Unit
) : ComponentContext by componentContext {

    private val retainedHomeComponent = instanceKeeper.getOrCreate {
        RetainedHomeComponent(
            timelineInteractor,
            statusInteractor
        )
    }

    val homeStatusesFlow get() = retainedHomeComponent.homeStatusesFlow
    val errorEventFlow get() = retainedHomeComponent.errorEventFlow
    val currentlyPlayingMedia get() = retainedHomeComponent.currentlyPlayingItem

    fun setFocussedVideoAttachment(status: StatusListItemModel?) {
        retainedHomeComponent.setFocussedVideoAttachment(status)
    }

    fun setFavorite(id: String, action: Boolean) {
        retainedHomeComponent.setFavorite(id, action)
    }

    fun setReblog(id: String, action: Boolean) {
        retainedHomeComponent.setReblog(id, action)
    }
    fun expandSensitiveStatus(id: String) {
        retainedHomeComponent.expandSensitiveStatus(id)
    }

    fun onStatusClicked(id: String) {
        navigateToStatusContext(id)
    }

    fun onAccountClicked(id: String) {
        navigateToAccount(id)
    }

    fun onAttachmentClicked(statusId: String, index: Int) {
        navigateToStatusAttachments(statusId, index)
    }
}