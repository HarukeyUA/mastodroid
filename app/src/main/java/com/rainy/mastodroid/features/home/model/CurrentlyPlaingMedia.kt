package com.rainy.mastodroid.features.home.model

import androidx.compose.runtime.Stable

@Stable
data class CurrentlyPlayingMedia(
    val statusId: String,
    val mediaId: String,
    val url: String
)
