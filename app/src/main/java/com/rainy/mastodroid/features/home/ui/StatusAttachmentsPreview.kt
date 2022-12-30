package com.rainy.mastodroid.features.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalAbsoluteTonalElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.media3.exoplayer.ExoPlayer
import com.rainy.mastodroid.R
import com.rainy.mastodroid.extensions.ifNotNull
import com.rainy.mastodroid.features.home.model.ImageAttachmentItemModel
import com.rainy.mastodroid.features.home.model.MediaAttachmentItemModel
import com.rainy.mastodroid.features.home.model.VideoAttachmentItemModel
import com.rainy.mastodroid.ui.elements.AsyncBlurImage
import com.rainy.mastodroid.ui.elements.MediaPreviewGrid
import com.rainy.mastodroid.ui.elements.VideoPlayer

@Composable
fun StatusAttachmentsPreview(
    attachments: List<MediaAttachmentItemModel>,
    exoPlayer: ExoPlayer? = null
) {
    MediaPreviewGrid(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .padding(bottom = 8.dp)
            .clip(MaterialTheme.shapes.large)
            .background(
                color = MaterialTheme.colorScheme.surfaceColorAtElevation(
                    LocalAbsoluteTonalElevation.current + 4.dp
                )
            )
    ) {
        attachments.take(4).fastForEach { mediaAttachment ->
            when (mediaAttachment) {
                is ImageAttachmentItemModel -> {
                    ImageAttachment(
                        mediaAttachment,
                        modifier = Modifier.ifNotNull(mediaAttachment.aspect) {
                            if (attachments.size == 1) {
                                aspectRatio(it)
                            } else {
                                Modifier
                            }
                        })
                }

                is VideoAttachmentItemModel -> {
                    VideoAttachment(
                        attachments,
                        exoPlayer,
                        mediaAttachment,
                        modifier = Modifier.ifNotNull(mediaAttachment.previewAspect) {
                            if (attachments.size == 1) {
                                aspectRatio(it)
                            } else {
                                Modifier
                            }
                        })
                }

                else -> {}
            }

        }

    }
}

@Composable
private fun ImageAttachment(
    mediaAttachment: ImageAttachmentItemModel,
    modifier: Modifier = Modifier
) {
    AsyncBlurImage(
        url = mediaAttachment.url,
        blurHash = mediaAttachment.blurHash,
        contentDescription = mediaAttachment.description,
        contentScale = ContentScale.Crop,
        modifier = modifier
    )
}

@Composable
fun VideoAttachment(
    attachments: List<MediaAttachmentItemModel>,
    exoPlayer: ExoPlayer?,
    mediaAttachment: VideoAttachmentItemModel,
    modifier: Modifier = Modifier
) {
    if ((attachments.firstOrNull() as? VideoAttachmentItemModel)?.currentlyPlaying == true && exoPlayer != null) {
        VideoPlayer(
            exoPlayer,
            modifier = modifier
        )
    } else {
        Box(modifier = modifier) {
            AsyncBlurImage(
                url = mediaAttachment.url,
                blurHash = mediaAttachment.blurHash,
                contentDescription = mediaAttachment.description,
                contentScale = ContentScale.Crop,
                modifier = modifier.fillMaxSize()
            )
            Icon(
                painter = painterResource(id = R.drawable.play_circle),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.Center)
            )
        }

    }
}

