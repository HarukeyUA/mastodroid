package com.rainy.mastodroid.features.home

import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.lazy.LazyListLayoutInfo
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.rainy.mastodroid.core.navigation.NavRoute
import com.rainy.mastodroid.features.home.model.CurrentlyPlayingMedia
import com.rainy.mastodroid.features.home.model.StatusListItemModel
import com.rainy.mastodroid.features.home.model.VideoAttachmentItemModel
import com.rainy.mastodroid.features.home.ui.HomeScreen
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.androidx.compose.koinViewModel
import kotlin.math.abs

object HomeRoute : NavRoute<HomeViewModel> {

    override val route: String = "home"

    @Composable
    override fun viewModel(): HomeViewModel = koinViewModel()

    @Composable
    override fun Content(viewModel: HomeViewModel) {
        val statusItems = viewModel.homeStatusesFlow.collectAsLazyPagingItems()
        val lazyListState = rememberLazyListState()
        val exoPlayer = rememberExoPlayerInstance()
        val currentlyPlaying by viewModel.currentlyPlayingItem.collectAsState()

        LaunchedEffect(Unit) {
            snapshotFlow {
                determineCurrentlyPlayingItem(
                    lazyListState.layoutInfo,
                    statusItems.itemSnapshotList.items
                )
            }.distinctUntilChanged().collect {
                viewModel.setFocussedVideoAttachment(it)
            }
        }

        PlayFocusedVideo(currentlyPlaying, exoPlayer)
        ExoPlayerLifecycleEvents(exoPlayer, currentlyPlaying)

        val context = LocalContext.current
        HomeScreen(
            statusesPagingList = statusItems,
            isRefreshing = statusItems.loadState.refresh == LoadState.Loading,
            lazyListState = lazyListState,
            exoPlayer = exoPlayer,
            onRefreshInvoked = {
                statusItems.refresh()
            },
            onUrlClicked = { url ->
                val builder: CustomTabsIntent.Builder = CustomTabsIntent.Builder()
                val customTabsIntent: CustomTabsIntent = builder.build()
                customTabsIntent.launchUrl(context, url.toUri())
            }
        )
    }

    @Composable
    private fun rememberExoPlayerInstance(): ExoPlayer {
        val context = LocalContext.current
        return remember {
            ExoPlayer.Builder(context).build().apply {
                repeatMode = Player.REPEAT_MODE_ALL
                volume = 0f
            }
        }
    }

    @Composable
    private fun PlayFocusedVideo(
        currentlyPlaying: CurrentlyPlayingMedia?,
        exoPlayer: ExoPlayer
    ) {
        LaunchedEffect(currentlyPlaying) {
            exoPlayer.apply {
                if (currentlyPlaying != null) {
                    val mediaItem = MediaItem.fromUri(currentlyPlaying.url)
                    setMediaItem(mediaItem)
                    prepare()
                    playWhenReady = true
                } else {
                    stop()
                }
            }
        }
    }

    @Composable
    private fun ExoPlayerLifecycleEvents(
        exoPlayer: ExoPlayer,
        currentlyPlaying: CurrentlyPlayingMedia?
    ) {
        val lifecycle = LocalLifecycleOwner.current
        DisposableEffect(exoPlayer) {
            val lifecycleObserver = LifecycleEventObserver { _, event ->
                if (currentlyPlaying == null) return@LifecycleEventObserver
                when (event) {
                    Lifecycle.Event.ON_START -> exoPlayer.play()
                    Lifecycle.Event.ON_STOP -> exoPlayer.pause()
                    else -> {}
                }
            }

            lifecycle.lifecycle.addObserver(lifecycleObserver)

            onDispose {
                lifecycle.lifecycle.removeObserver(lifecycleObserver)
                exoPlayer.release()
            }
        }
    }

    private fun determineCurrentlyPlayingItem(
        layoutInfo: LazyListLayoutInfo,
        items: List<StatusListItemModel>
    ): StatusListItemModel? {
        val visibleItems = layoutInfo.visibleItemsInfo.map { items[it.index] }
        val itemsWithVideo =
            visibleItems.filter { it.attachments.size == 1 && it.attachments.firstOrNull() is VideoAttachmentItemModel }
        return when (itemsWithVideo.size) {
            0 -> null
            1 -> itemsWithVideo.first()
            else -> {
                val midPoint = (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2
                val itemsFromCenter =
                    layoutInfo.visibleItemsInfo.sortedBy { abs((it.offset + it.size / 2) - midPoint) }
                itemsFromCenter.map { items[it.index] }
                    .firstOrNull { it.attachments.size == 1 && it.attachments.firstOrNull() is VideoAttachmentItemModel }
            }
        }
    }
}
