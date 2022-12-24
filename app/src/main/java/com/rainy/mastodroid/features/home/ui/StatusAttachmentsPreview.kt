package com.rainy.mastodroid.features.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalAbsoluteTonalElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import com.rainy.mastodroid.extensions.ifNotNull
import com.rainy.mastodroid.features.home.model.ImageAttachmentItemModel
import com.rainy.mastodroid.features.home.model.MediaAttachmentItemModel
import com.rainy.mastodroid.ui.elements.AsyncBlurImage
import com.rainy.mastodroid.ui.elements.MediaPreviewGrid

@Composable
fun StatusAttachmentsPreview(attachments: List<MediaAttachmentItemModel>) {
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
                    AsyncBlurImage(
                        url = mediaAttachment.url,
                        blurHash = mediaAttachment.blurHash,
                        contentDescription = mediaAttachment.description,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.ifNotNull(mediaAttachment.aspect) {
                            if (attachments.size == 1) {
                                aspectRatio(it)
                            } else {
                                Modifier
                            }
                        }
                    )
                }

                else -> {}
            }

        }

    }
}
