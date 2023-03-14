/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.features.home.model

import androidx.compose.runtime.Stable

@Stable
data class CurrentlyPlayingMedia(
    val statusId: String,
    val mediaId: String,
    val url: String
)
