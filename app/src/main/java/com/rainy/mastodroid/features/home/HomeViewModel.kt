package com.rainy.mastodroid.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.map
import com.rainy.mastodroid.core.domain.interactor.HomeTimelineInteractor
import com.rainy.mastodroid.core.domain.model.status.Status
import com.rainy.mastodroid.core.navigation.RouteNavigator
import com.rainy.mastodroid.features.home.model.CurrentlyPlayingMedia
import com.rainy.mastodroid.features.home.model.StatusListItemModel
import com.rainy.mastodroid.features.home.model.VideoAttachmentItemModel
import com.rainy.mastodroid.features.home.model.toStatusListItemModel
import com.rainy.mastodroid.util.ErrorModel
import com.rainy.mastodroid.util.ImmutableWrap
import com.rainy.mastodroid.util.NetworkExceptionIdentifier
import com.rainy.mastodroid.util.logi
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

class HomeViewModel(
    private val homeTimelineInteractor: HomeTimelineInteractor,
    private val exceptionIdentifier: NetworkExceptionIdentifier,
    routeNavigator: RouteNavigator
) : ViewModel(), RouteNavigator by routeNavigator {

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        errorEventChannel.trySend(exceptionIdentifier.identifyException(throwable))
    }

    private val errorEventChannel = Channel<ErrorModel>()
    val errorEventFlow = errorEventChannel.receiveAsFlow()

    val currentlyPlayingItem = MutableStateFlow<CurrentlyPlayingMedia?>(null)
    private val expandedItems = MutableStateFlow<Set<String>>(setOf())

    val homeStatusesFlow = homeTimelineInteractor.getTimeLinePaging(viewModelScope)
        .map { statusPagingData ->
            statusPagingData.map(Status::toStatusListItemModel)
        }.combine(currentlyPlayingItem) { timeline, currentlyPlaying ->
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

    fun setFavorite(id: String, actionId: String, action: Boolean) {
        viewModelScope.launch(exceptionHandler) {
            homeTimelineInteractor.setFavoriteStatus(id, actionId, action)
        }
    }

    fun setReblog(id: String, actionId: String, action: Boolean) {
        viewModelScope.launch(exceptionHandler) {
            homeTimelineInteractor.setReblogStatus(id, actionId, action)
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
