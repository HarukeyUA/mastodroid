/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.ui.elements.player

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.rainy.mastodroid.util.logi

@Composable
fun rememberExoPlayerInstance(): ExoPlayer {
    val context = LocalContext.current
    return remember {
        ExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_ALL
            volume = 0f
        }
    }
}

@Composable
fun ExoPlayerLifecycleEvents(
    exoPlayer: ExoPlayer
) {
    val lifecycle = LocalLifecycleOwner.current
    DisposableEffect(exoPlayer) {
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    logi("Start player - lifecycle")
                    exoPlayer.play()
                }

                Lifecycle.Event.ON_STOP -> {
                    logi("Stop player - lifecycle")
                    exoPlayer.pause()
                }

                else -> {}
            }
        }

        lifecycle.lifecycle.addObserver(lifecycleObserver)

        onDispose {
            logi("Release player - lifecycle")
            lifecycle.lifecycle.removeObserver(lifecycleObserver)
            exoPlayer.release()
        }
    }
}