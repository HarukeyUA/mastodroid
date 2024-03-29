/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.ui.elements.player

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView

@SuppressLint("UnsafeOptInUsageError")
@Composable
fun VideoPlayer(
    exoPlayer: ExoPlayer?,
    modifier: Modifier = Modifier,
    scaleType: Int = AspectRatioFrameLayout.RESIZE_MODE_ZOOM,
) {
    AndroidView(
        factory = { context ->
            PlayerView(context).apply {
                player = exoPlayer
                setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS)
                resizeMode = scaleType
                useController = false
            }
        },
        onReset = {
            it.player = exoPlayer
        },
        modifier = modifier
    )
}
