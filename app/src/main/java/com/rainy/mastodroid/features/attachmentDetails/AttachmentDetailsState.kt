/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.features.attachmentDetails

import androidx.compose.runtime.Stable
import com.rainy.mastodroid.ui.elements.statusListItem.model.MediaAttachmentItemModel
import com.rainy.mastodroid.util.ImmutableWrap

@Stable
data class AttachmentDetailsState(
    val initialPage: Int = 0,
    val attachments: ImmutableWrap<List<MediaAttachmentItemModel>>
)
