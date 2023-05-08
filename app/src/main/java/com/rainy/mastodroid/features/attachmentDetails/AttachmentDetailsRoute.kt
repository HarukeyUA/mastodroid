/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.features.attachmentDetails

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FIT
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.mxalbert.zoomable.OverZoomConfig
import com.mxalbert.zoomable.Zoomable
import com.mxalbert.zoomable.rememberZoomableState
import com.rainy.mastodroid.core.navigation.NavRoute
import com.rainy.mastodroid.ui.elements.player.ExoPlayerLifecycleEvents
import com.rainy.mastodroid.ui.elements.player.VideoPlayer
import com.rainy.mastodroid.ui.elements.player.rememberExoPlayerInstance
import com.rainy.mastodroid.ui.elements.statusListItem.model.ImageAttachmentItemModel
import com.rainy.mastodroid.ui.elements.statusListItem.model.VideoAttachmentItemModel
import com.rainy.mastodroid.util.BlurHashDecoder
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

object AttachmentDetailsRoute : NavRoute<AttachmentDetailsViewModel> {
    const val STATUS_ID_ARG = "status_id"
    const val ATTACHMENT_INDEX_ARG = "attachment_index"

    override val route: String = "attachmentDetails/{${STATUS_ID_ARG}}/{${ATTACHMENT_INDEX_ARG}}"

    fun getRoute(statusId: String, attachmentIndexArg: Int = 0): String {
        return route
            .replace("{${STATUS_ID_ARG}}", statusId)
            .replace("{${ATTACHMENT_INDEX_ARG}}", attachmentIndexArg.toString())
    }

    override fun getArguments(): List<NamedNavArgument> {
        return listOf(
            navArgument(STATUS_ID_ARG) { type = NavType.StringType },
            navArgument(ATTACHMENT_INDEX_ARG) { type = NavType.IntType }
        )
    }

    @Composable
    override fun viewModel(): AttachmentDetailsViewModel = koinViewModel()

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun Content(viewModel: AttachmentDetailsViewModel) {
        val attachmentsState by viewModel.attachmentsState.collectAsStateWithLifecycle()

        if (attachmentsState.attachments.content.isNotEmpty()) {
            val pagerState = rememberPagerState(initialPage = attachmentsState.initialPage)
            val exoPlayer = rememberExoPlayerInstance()
            ExoPlayerLifecycleEvents(exoPlayer)

            LaunchedEffect(attachmentsState) {
                snapshotFlow {
                    pagerState.settledPage
                }.collectLatest {
                    val videoUrl =
                        (attachmentsState.attachments.content.getOrNull(it) as? VideoAttachmentItemModel)?.url
                    exoPlayer.apply {
                        if (videoUrl != null) {
                            val mediaItem = MediaItem.fromUri(videoUrl)
                            setMediaItem(mediaItem)
                            prepare()
                            playWhenReady = true
                        } else {
                            stop()
                        }
                    }
                }
            }

            AttachmentsPager(attachmentsState, pagerState, exoPlayer)
        }
    }

    @Composable
    @OptIn(ExperimentalFoundationApi::class)
    private fun AttachmentsPager(
        attachmentsWrap: AttachmentDetailsState,
        pagerState: PagerState,
        exoPlayer: ExoPlayer
    ) {
        HorizontalPager(
            pageCount = attachmentsWrap.attachments.content.size,
            state = pagerState,
            key = {
                when (val attachment = attachmentsWrap.attachments.content[it]) {
                    is ImageAttachmentItemModel -> attachment.id
                    is VideoAttachmentItemModel -> attachment.id
                }
            },
            modifier = Modifier.fillMaxSize(),
        ) {

            when (val attachment = attachmentsWrap.attachments.content[it]) {
                is ImageAttachmentItemModel -> ImageAttachmentPage(
                    url = attachment.url,
                    blurHash = attachment.blurHash,
                    aspectRatio = attachment.aspect ?: 1f
                )

                is VideoAttachmentItemModel -> VideoAttachmentPage(
                    isPlaying = pagerState.settledPage == it,
                    exoPlayer = exoPlayer,
                    attachment
                )
            }
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    @Composable
    private fun VideoAttachmentPage(
        isPlaying: Boolean,
        exoPlayer: ExoPlayer,
        attachment: VideoAttachmentItemModel
    ) {
        if (isPlaying) {
            VideoPlayer(
                exoPlayer,
                modifier = Modifier.run {
                    if (attachment.previewAspect != null) {
                        aspectRatio(attachment.previewAspect)
                    } else {
                        fillMaxSize()
                    }
                },
                scaleType = RESIZE_MODE_FIT
            )
        } else {
            ImageAttachmentPage(
                url = attachment.url,
                blurHash = attachment.blurHash,
                aspectRatio = attachment.previewAspect ?: 1f
            )
        }
    }

    @Composable
    private fun ImageAttachmentPage(
        url: String,
        blurHash: String,
        aspectRatio: Float,
        modifier: Modifier = Modifier
    ) {
        val zoomState = rememberZoomableState(
            minScale = 0.8f,
            maxScale = 8f,
            overZoomConfig = OverZoomConfig(1f, 7f)
        )
        Zoomable(
            state = zoomState,
            enabled = true,
            modifier = modifier.graphicsLayer {
                clip = true

            },
        ) {

            val resources = LocalContext.current.resources
            val blurPlaceholder = remember(blurHash) {
                BlurHashDecoder.decode(
                    blurHash,
                    32,
                    32
                )?.toDrawable(resources)
            }

            val painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(
                        url
                    )
                    .placeholder(blurPlaceholder)
                    .size(Size.ORIGINAL)
                    .build()
            )
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier
                    .aspectRatio(aspectRatio)
                    .fillMaxSize()
            )
        }
    }

}