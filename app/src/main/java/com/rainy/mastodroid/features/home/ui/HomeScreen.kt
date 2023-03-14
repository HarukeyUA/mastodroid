/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.features.home.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.media3.exoplayer.ExoPlayer
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.rainy.mastodroid.core.domain.model.status.statusThread.ReplyType
import com.rainy.mastodroid.ui.elements.statusListItem.StatusListItem
import com.rainy.mastodroid.ui.elements.statusListItem.model.StatusListItemModel
import com.rainy.mastodroid.util.ImmutableWrap

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    statusesPagingList: LazyPagingItems<StatusListItemModel>,
    isRefreshing: Boolean,
    onRefreshInvoked: () -> Unit,
    onUrlClicked: (url: String) -> Unit,
    onFavoriteClicked: (id: String, actionId: String, action: Boolean) -> Unit,
    onReblogClicked: (id: String, actionId: String, action: Boolean) -> Unit,
    onSensitiveExpandClicked: (id: String) -> Unit,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    exoPlayer: ImmutableWrap<ExoPlayer>? = null,
    onClick: (String) -> Unit = {},
) {
    val pullRefreshState = rememberPullRefreshState(
        isRefreshing, onRefresh = onRefreshInvoked
    )

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
            onClick = onClick
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
    onFavoriteClicked: (id: String, actionId: String, action: Boolean) -> Unit,
    onReblogClicked: (id: String, actionId: String, action: Boolean) -> Unit,
    onSensitiveExpandClicked: (id: String) -> Unit,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    exoPlayer: ImmutableWrap<ExoPlayer>? = null,
    onClick: (String) -> Unit = {},
) {
    LazyColumn(
        state = lazyListState,
        modifier = modifier.fillMaxSize()
    ) {
        itemsIndexed(statusesPagingList, key = { _, status ->
            status.id
        }) { index, item ->
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
                    onClick = onClick
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
