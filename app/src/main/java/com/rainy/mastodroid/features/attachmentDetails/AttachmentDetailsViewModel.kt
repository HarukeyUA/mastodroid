/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.features.attachmentDetails

import androidx.lifecycle.SavedStateHandle
import com.rainy.mastodroid.core.base.BaseViewModel
import com.rainy.mastodroid.core.domain.interactor.StatusInteractor
import com.rainy.mastodroid.core.domain.model.mediaAttachment.GifvAttachment
import com.rainy.mastodroid.core.domain.model.mediaAttachment.ImageAttachment
import com.rainy.mastodroid.core.domain.model.mediaAttachment.VideoAttachment
import com.rainy.mastodroid.core.navigation.RouteNavigator
import com.rainy.mastodroid.features.attachmentDetails.AttachmentDetailsRoute.ATTACHMENT_INDEX_ARG
import com.rainy.mastodroid.features.attachmentDetails.AttachmentDetailsRoute.STATUS_ID_ARG
import com.rainy.mastodroid.ui.elements.statusListItem.model.toItemModel
import com.rainy.mastodroid.util.ImmutableWrap
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

class AttachmentDetailsViewModel(
    private val statusInteractor: StatusInteractor,
    savedStateHandle: SavedStateHandle,
    routeNavigator: RouteNavigator
) : BaseViewModel(), RouteNavigator by routeNavigator {

    private val statusIdFlow = savedStateHandle.getStateFlow(STATUS_ID_ARG, "")
    private val initialPageFlow = savedStateHandle.getStateFlow(ATTACHMENT_INDEX_ARG, 0)
    private val statusAttachmentFlow = statusIdFlow.flatMapLatest {
        statusInteractor.getStatusFlow(it)
    }.map {
        ImmutableWrap(it?.mediaAttachments?.map { attachment ->
            when (attachment) {
                is GifvAttachment -> attachment.toItemModel()
                is ImageAttachment -> attachment.toItemModel()
                is VideoAttachment -> attachment.toItemModel()
            }
        } ?: listOf())
    }

    val attachmentsState = combine(
        initialPageFlow,
        statusAttachmentFlow,
        ::AttachmentDetailsState
    ).stateIn(
        AttachmentDetailsState(
            initialPage = 0,
            attachments = ImmutableWrap(content = listOf())
        )
    )
}