package com.rainy.mastodroid.features.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.paging.compose.items
import com.rainy.mastodroid.features.home.model.StatusListItemModel
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
            onSensitiveExpandClicked = onSensitiveExpandClicked
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
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        state = lazyListState,
        modifier = modifier.fillMaxSize()
    ) {
        items(statusesPagingList, key = { status ->
            status.id
        }) { item ->
            item?.also {
                StatusListItem(
                    item = item,
                    exoPlayer = exoPlayer,
                    onUrlClicked = onUrlClicked,
                    onFavoriteClicked = onFavoriteClicked,
                    onReblogClicked = onReblogClicked,
                    onSensitiveExpandClicked = onSensitiveExpandClicked
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
