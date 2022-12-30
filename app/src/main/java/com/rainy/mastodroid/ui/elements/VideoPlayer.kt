package com.rainy.mastodroid.ui.elements

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

@SuppressLint("UnsafeOptInUsageError")
@Composable
fun VideoPlayer(
    exoPlayer: ExoPlayer?,
    modifier: Modifier = Modifier,
) {
    AndroidView(
        factory = { context ->
            PlayerView(context).apply {
                player = exoPlayer
                setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS)
                useController = false
            }
        },
        modifier = modifier
    )
}