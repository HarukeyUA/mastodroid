package com.rainy.mastodroid.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import com.rainy.mastodroid.core.domain.interactor.HomeTimelineInteractor
import com.rainy.mastodroid.core.domain.model.status.Status
import com.rainy.mastodroid.core.navigation.RouteNavigator
import com.rainy.mastodroid.features.home.model.CurrentlyPlayingMedia
import com.rainy.mastodroid.features.home.model.StatusListItemModel
import com.rainy.mastodroid.features.home.model.VideoAttachmentItemModel
import com.rainy.mastodroid.features.home.model.toStatusListItemModel
import com.rainy.mastodroid.util.logi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class HomeViewModel(
    private val homeTimelineInteractor: HomeTimelineInteractor,
    routeNavigator: RouteNavigator
) : ViewModel(), RouteNavigator by routeNavigator {

    val currentlyPlayingItem = MutableStateFlow<CurrentlyPlayingMedia?>(null)

    val homeStatusesFlow = homeTimelineInteractor.timeLinePagingFlow
        .map { statusPagingData ->
            statusPagingData.map(Status::toStatusListItemModel)
        }
        .cachedIn(viewModelScope)
        .combine(currentlyPlayingItem) { timeline, currentlyPlaying ->
            timeline.map { status ->
                if (status.id == currentlyPlaying?.statusId) {
                    status.copy(
                        attachments = status.attachments.map { attachment ->
                            if (attachment is VideoAttachmentItemModel && attachment.id == currentlyPlaying.mediaId) {
                                attachment.copy(currentlyPlaying = true)
                            } else {
                                attachment
                            }
                        }
                    )
                } else {
                    status
                }
            }
        }

    fun setFocussedVideoAttachment(status: StatusListItemModel?) {
        logi("Focused timeline item: ${status?.id}")
        if (status != null && status.attachments.size == 1) {
            currentlyPlayingItem.value = status.attachments.firstOrNull()?.let { media ->
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

}
