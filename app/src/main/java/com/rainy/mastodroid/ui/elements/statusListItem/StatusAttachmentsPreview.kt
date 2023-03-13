package com.rainy.mastodroid.ui.elements.statusListItem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
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
import com.rainy.mastodroid.extensions.ifTrue
import com.rainy.mastodroid.ui.elements.statusListItem.model.ImageAttachmentItemModel
import com.rainy.mastodroid.ui.elements.statusListItem.model.MediaAttachmentItemModel
import com.rainy.mastodroid.ui.elements.statusListItem.model.VideoAttachmentItemModel
import com.rainy.mastodroid.ui.elements.AsyncBlurImage
import com.rainy.mastodroid.ui.elements.MediaPreviewGrid
import com.rainy.mastodroid.ui.elements.VideoPlayer
import com.rainy.mastodroid.util.ImmutableWrap

private const val MAX_ATTACHMENTS_HEIGHT = 800

@Composable
fun StatusAttachmentsPreview(
    attachments: ImmutableWrap<List<MediaAttachmentItemModel>>,
    modifier: Modifier = Modifier,
    exoPlayer: ImmutableWrap<ExoPlayer>? = null
) {
    MediaPreviewGrid(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = MAX_ATTACHMENTS_HEIGHT.dp)
            .clip(MaterialTheme.shapes.large)
            .background(
                color = MaterialTheme.colorScheme.surfaceColorAtElevation(
                    LocalAbsoluteTonalElevation.current + 4.dp
                )
            )
    ) {
        attachments.content.take(4).fastForEach { mediaAttachment ->
            when (mediaAttachment) {
                is ImageAttachmentItemModel -> {
                    ImageAttachment(
                        mediaAttachment,
                        modifier = Modifier.ifTrue(attachments.content.size == 1) {
                            aspectRatio(mediaAttachment.aspect ?: 1f)
                        }
                    )
                }

                is VideoAttachmentItemModel -> {
                    VideoAttachment(
                        attachments,
                        exoPlayer,
                        mediaAttachment,
                        modifier = Modifier.ifTrue(attachments.content.size == 1) {
                            aspectRatio(mediaAttachment.previewAspect ?: 1f)
                        }
                    )
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
    attachments: ImmutableWrap<List<MediaAttachmentItemModel>>,
    exoPlayer: ImmutableWrap<ExoPlayer>?,
    mediaAttachment: VideoAttachmentItemModel,
    modifier: Modifier = Modifier
) {
    if ((attachments.content.firstOrNull() as? VideoAttachmentItemModel)?.currentlyPlaying == true &&
        exoPlayer != null
    ) {
        VideoPlayer(
            exoPlayer.content,
            modifier = modifier
        )
    } else {
        Box(modifier = modifier) {
            AsyncBlurImage(
                url = mediaAttachment.url,
                blurHash = mediaAttachment.blurHash,
                contentDescription = mediaAttachment.description,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
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

