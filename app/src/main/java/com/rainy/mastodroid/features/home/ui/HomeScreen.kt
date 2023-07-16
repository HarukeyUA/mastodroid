/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.features.home.ui

import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListLayoutInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastFirstOrNull
import androidx.compose.ui.util.fastMap
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.rainy.mastodroid.core.domain.model.status.statusThread.ReplyType
import com.rainy.mastodroid.extensions.observeWithLifecycle
import com.rainy.mastodroid.features.home.HomeComponent
import com.rainy.mastodroid.features.home.model.CurrentlyPlayingMedia
import com.rainy.mastodroid.ui.elements.statusListItem.StatusListItem
import com.rainy.mastodroid.ui.elements.statusListItem.model.StatusListItemModel
import com.rainy.mastodroid.ui.elements.statusListItem.model.VideoAttachmentItemModel
import com.rainy.mastodroid.util.ImmutableWrap
import com.rainy.mastodroid.util.logi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlin.math.abs

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    homeComponent: HomeComponent
) {
    val statusItems = homeComponent.homeStatusesFlow.collectAsLazyPagingItems(Dispatchers.Default)
    val lazyListState = rememberLazyListState()
    val exoPlayer = rememberExoPlayerInstance()
    val currentlyPlaying by homeComponent.currentlyPlayingMedia.collectAsState()
    val context = LocalContext.current

    homeComponent.errorEventFlow.observeWithLifecycle {
        Toast.makeText(context, it.resolveText(context), Toast.LENGTH_SHORT).show()
    }

    LaunchedEffect(Unit) {
        snapshotFlow {
            determineCurrentlyPlayingItem(
                lazyListState.layoutInfo,
                statusItems.itemSnapshotList.items
            )
        }.distinctUntilChanged().collect {
            homeComponent.setFocussedVideoAttachment(it)
        }
    }

    PlayFocusedVideo(currentlyPlaying, exoPlayer)
    ExoPlayerLifecycleEvents(exoPlayer)

    val onUrlClicked = remember {
        { url: String ->
            val builder: CustomTabsIntent.Builder = CustomTabsIntent.Builder()
            val customTabsIntent: CustomTabsIntent = builder.build()
            customTabsIntent.launchUrl(context, url.toUri())
        }
    }

    val isRefreshing by remember {
        derivedStateOf {
            statusItems.loadState.refresh == LoadState.Loading
        }
    }

    val pullRefreshState = rememberPullRefreshState(
        isRefreshing, onRefresh = statusItems::refresh
    )

    HomeScreen(
        statusesPagingList = statusItems,
        pullRefreshState = pullRefreshState,
        isRefreshing = isRefreshing,
        lazyListState = lazyListState,
        exoPlayer = ImmutableWrap(exoPlayer),
        onUrlClicked = onUrlClicked,
        onFavoriteClicked = homeComponent::setFavorite,
        onReblogClicked = homeComponent::setReblog,
        onSensitiveExpandClicked = homeComponent::expandSensitiveStatus,
        onClick = homeComponent::onStatusClicked,
        onAccountClick = homeComponent::onAccountClicked,
        onAttachmentClicked = homeComponent::onAttachmentClicked
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    statusesPagingList: LazyPagingItems<StatusListItemModel>,
    pullRefreshState: PullRefreshState,
    isRefreshing: Boolean,
    onUrlClicked: (url: String) -> Unit,
    onFavoriteClicked: (id: String, action: Boolean) -> Unit,
    onReblogClicked: (id: String, action: Boolean) -> Unit,
    onSensitiveExpandClicked: (id: String) -> Unit,
    onAttachmentClicked: (statusId: String, attachmentIndex: Int) -> Unit,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    exoPlayer: ImmutableWrap<ExoPlayer>? = null,
    onClick: (String) -> Unit = {},
    onAccountClick: (String) -> Unit = {}
) {

    Box(
        modifier = modifier.pullRefresh(pullRefreshState)
    ) {
        Timeline(
            statusesPagingList = statusesPagingList,
            lazyListState = lazyListState,
            exoPlayer = exoPlayer,
            onUrlClicked = onUrlClicked,
            onFavoriteClicked = onFavoriteClicked,
            onReblogClicked = onReblogClicked,
            onSensitiveExpandClicked = onSensitiveExpandClicked,
            onClick = onClick,
            onAccountClick = onAccountClick,
            onAttachmentClicked = onAttachmentClicked
        )
        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter),
            backgroundColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    }

}

@Composable
fun Timeline(
    statusesPagingList: LazyPagingItems<StatusListItemModel>,
    onUrlClicked: (url: String) -> Unit,
    onFavoriteClicked: (id: String, action: Boolean) -> Unit,
    onReblogClicked: (id: String, action: Boolean) -> Unit,
    onSensitiveExpandClicked: (id: String) -> Unit,
    onAttachmentClicked: (statusId: String, attachmentIndex: Int) -> Unit,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    exoPlayer: ImmutableWrap<ExoPlayer>? = null,
    onClick: (String) -> Unit = {},
    onAccountClick: (String) -> Unit = {}
) {
    LazyColumn(
        state = lazyListState,
        modifier = modifier.fillMaxSize()
    ) {
        items(
            count = statusesPagingList.itemCount,
            key = statusesPagingList.itemKey { status ->
                status.id
            },
        ) { index ->
            val item = statusesPagingList[index]
            item?.also {
                val isReply = statusesPagingList.peekOrNull(index - 1)?.let {
                    it.id == item.inReplyToId
                } ?: false
                val isRepliedTo = statusesPagingList.peekOrNull(index + 1)?.let {
                    it.inReplyToId == item.id
                } ?: false
                if (!isReply && index != 0) {
                    Spacer(modifier = Modifier.size(8.dp))
                }
                StatusListItem(
                    item = item,
                    exoPlayer = exoPlayer,
                    reply = if (isReply) ReplyType.DIRECT_REPLY else ReplyType.NONE,
                    repliedTo = if (isRepliedTo) ReplyType.DIRECT_REPLY else ReplyType.NONE,
                    onUrlClicked = onUrlClicked,
                    onFavoriteClicked = onFavoriteClicked,
                    onReblogClicked = onReblogClicked,
                    onSensitiveExpandClicked = onSensitiveExpandClicked,
                    onClick = onClick,
                    onAccountClick = onAccountClick,
                    onAttachmentClicked = onAttachmentClicked
                )
            }
        }

        if (statusesPagingList.loadState.append == LoadState.Loading) {
            item {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

fun <T : Any> LazyPagingItems<T>.peekOrNull(index: Int): T? {
    return try {
        peek(index)
    } catch (oob: IndexOutOfBoundsException) {
        null
    }
}
