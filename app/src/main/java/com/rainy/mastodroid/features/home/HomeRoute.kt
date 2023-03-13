package com.rainy.mastodroid.features.home

import android.widget.Toast
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
import androidx.compose.ui.util.fastFirstOrNull
import androidx.compose.ui.util.fastMap
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.rainy.mastodroid.core.navigation.NavRoute
import com.rainy.mastodroid.extensions.observeWithLifecycle
import com.rainy.mastodroid.features.home.model.CurrentlyPlayingMedia
import com.rainy.mastodroid.ui.elements.statusListItem.model.StatusListItemModel
import com.rainy.mastodroid.ui.elements.statusListItem.model.VideoAttachmentItemModel
import com.rainy.mastodroid.features.home.ui.HomeScreen
import com.rainy.mastodroid.util.ImmutableWrap
import com.rainy.mastodroid.util.logi
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
        val context = LocalContext.current

        viewModel.errorEventFlow.observeWithLifecycle {
            Toast.makeText(context, it.resolveText(context), Toast.LENGTH_SHORT).show()
        }

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
        ExoPlayerLifecycleEvents(exoPlayer)


        HomeScreen(
            statusesPagingList = statusItems,
            isRefreshing = statusItems.loadState.refresh == LoadState.Loading,
            lazyListState = lazyListState,
            exoPlayer = ImmutableWrap(exoPlayer),
            onRefreshInvoked = {
                statusItems.refresh()
            },
            onUrlClicked = { url ->
                val builder: CustomTabsIntent.Builder = CustomTabsIntent.Builder()
                val customTabsIntent: CustomTabsIntent = builder.build()
                customTabsIntent.launchUrl(context, url.toUri())
            },
            onFavoriteClicked = viewModel::setFavorite,
            onReblogClicked = viewModel::setReblog,
            onSensitiveExpandClicked = viewModel::expandSensitiveStatus,
            onClick = viewModel::onStatusClicked
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
                    logi("Stop player - timeline")
                    stop()
                }
            }
        }
    }

    @Composable
    private fun ExoPlayerLifecycleEvents(
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

    private fun determineCurrentlyPlayingItem(
        layoutInfo: LazyListLayoutInfo,
        items: List<StatusListItemModel>
    ): StatusListItemModel? {
        if (items.isEmpty())
            return null

        val visibleItems =
            layoutInfo.visibleItemsInfo.mapNotNull { info -> items.fastFirstOrNull { it.id == info.key } }
        val itemsWithVideo =
            visibleItems.filter { it.attachments.content.size == 1 && it.attachments.content.firstOrNull() is VideoAttachmentItemModel }
        return when (itemsWithVideo.size) {
            0 -> null
            1 -> itemsWithVideo.first()
            else -> {
                val midPoint = (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2
                val itemsFromCenter =
                    layoutInfo.visibleItemsInfo.sortedBy { abs((it.offset + it.size / 2) - midPoint) }
                itemsFromCenter.fastMap { info -> items.fastFirstOrNull { it.id == info.key } }
                    .filterNotNull()
                    .fastFirstOrNull { it.isSubjectForAutoPlay }
            }
        }
    }
}
