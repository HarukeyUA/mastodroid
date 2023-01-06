package com.rainy.mastodroid.features.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.media3.exoplayer.ExoPlayer
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import com.rainy.mastodroid.features.home.model.StatusListItemModel
import com.rainy.mastodroid.ui.styledText.annotateMastodonContent

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    statusesPagingList: LazyPagingItems<StatusListItemModel>,
    isRefreshing: Boolean,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    exoPlayer: ExoPlayer? = null,
    onRefreshInvoked: () -> Unit,
    onUrlClicked: (url: String) -> Unit
) {
    val pullRefreshState = rememberPullRefreshState(
        isRefreshing, onRefresh = onRefreshInvoked
    )

    Box(
        modifier = modifier.pullRefresh(pullRefreshState)
    ) {
        Timeline(statusesPagingList, lazyListState, exoPlayer, onUrlClicked)
        PullRefreshIndicator(
            isRefreshing,
            pullRefreshState,
            Modifier.align(Alignment.TopCenter),
            backgroundColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    }

}

@Composable
fun Timeline(
    statusesPagingList: LazyPagingItems<StatusListItemModel>,
    lazyListState: LazyListState = rememberLazyListState(),
    exoPlayer: ExoPlayer? = null,
    onUrlClicked: (url: String) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        state = lazyListState,
        modifier = Modifier.fillMaxSize()
    ) {
        items(statusesPagingList, key = { status ->
            status.id
        }) { item ->
            item?.also {
                StatusListItem(item, exoPlayer, onUrlClicked)
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

@Composable
fun StatusListItem(item: StatusListItemModel, exoPlayer: ExoPlayer? = null, onUrlClicked: (url: String) -> Unit) {
    val contentText by remember {
        derivedStateOf {
            item.content.annotateMastodonContent(item.emojis.map { it.shortcode })
        }
    }
    StatusCard(fullAccountName = item.authorDisplayName,
        accountUserName = item.authorAccountHandle,
        accountAvatarUrl = item.authorAvatarUrl,
        updatedTime = item.lastUpdate,
        isEdited = item.edited,
        content = {
            Column {
                StatusTextContent(text = contentText, item.emojis) { url ->
                    onUrlClicked(url)
                }
                StatusAttachmentsPreview(item.attachments, exoPlayer)
            }

        }

    )
}
