/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.features.statusDetails.model

import androidx.compose.runtime.Stable
import com.rainy.mastodroid.core.domain.model.status.statusThread.ReplyType
import com.rainy.mastodroid.ui.elements.statusListItem.model.StatusListItemModel

@Stable
data class StatusThreadElement(
    val status: StatusListItemModel,
    val reply: ReplyType = ReplyType.NONE,
    val repliedTo: ReplyType = ReplyType.NONE
)
