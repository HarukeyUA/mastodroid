/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.features.statusDetails.model

sealed class StatusDetailsState {
    object Loading: StatusDetailsState()
    data class Ready(val statusInContext: StatusInContextItemModel): StatusDetailsState()
}
