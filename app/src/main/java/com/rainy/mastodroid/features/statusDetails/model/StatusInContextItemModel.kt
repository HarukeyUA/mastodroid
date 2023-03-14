/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.features.statusDetails.model

import androidx.compose.runtime.Stable
import com.rainy.mastodroid.ui.elements.statusListItem.model.StatusListItemModel
import com.rainy.mastodroid.util.ImmutableWrap

@Stable
data class StatusInContextItemModel(
    val ancestors: ImmutableWrap<List<StatusListItemModel>>,
    val descendants: ImmutableWrap<List<StatusThreadElement>>,
    val focusedStatus: FocusedStatusItemModel
)
