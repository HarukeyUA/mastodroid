/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.features.accountDetails.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.arkivanov.decompose.router.pages.ChildPages
import com.arkivanov.decompose.value.Value
import com.rainy.mastodroid.core.domain.model.status.statusThread.ReplyType
import com.rainy.mastodroid.features.accountDetails.AccountStatusTimelineComponent
import com.rainy.mastodroid.ui.elements.Pages
import com.rainy.mastodroid.ui.elements.statusListItem.StatusListItem
import com.rainy.mastodroid.ui.elements.statusListItem.model.StatusListItemModel
import kotlinx.coroutines.Dispatchers

@ExperimentalDecomposeApi
@Composable
@OptIn(ExperimentalFoundationApi::class)
fun AccountTimelinesPager(
    pagerState: PagerState,
    accountId: String,
    onAccountClicked: (String) -> Unit,
    onUrlClicked: (String) -> Unit,
    onClick: (String) -> Unit,
    onPageSelected: (index: Int) -> Unit,
    pages: () -> Value<ChildPages<*, AccountStatusTimelineComponent>>,
    modifier: Modifier = Modifier
) {
    val pagesState by pages().subscribeAsState()
    Pages(pages = pagesState, onPageSelected = onPageSelected, pagerState = pagerState, modifier = modifier) { index, page ->
        val statuses = page.timeline.collectAsLazyPagingItems(Dispatchers.Default)
        val isRefresh =
            remember { derivedStateOf { statuses.loadState.refresh == LoadState.Loading } }

        AccountTimeline(
            statuses = statuses,
            onUrlClicked = onUrlClicked,
            onClick = onClick,
            onAccountClicked = onAccountClicked,
            onFavoriteClicked = page::setFavorite,
            onReblogClicked = page::setReblog,
            onSensitiveExpandClicked = page::expandSensitiveStatus,
            isRefresh = isRefresh.value,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun AccountTimeline(
    statuses: LazyPagingItems<StatusListItemModel>,
    onUrlClicked: (String) -> Unit,
    onClick: (String) -> Unit,
    onAccountClicked: (String) -> Unit,
    onFavoriteClicked: (id: String, action: Boolean) -> Unit,
    onReblogClicked: (id: String, action: Boolean) -> Unit,
    onSensitiveExpandClicked: (id: String) -> Unit,
    isRefresh: Boolean,
    modifier: Modifier = Modifier
) {
    Box {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 8.dp),
            modifier = modifier
        ) {
            items(
                count = statuses.itemCount,
                key = statuses.itemKey(key = { it.id })
            ) { index ->
                val item = statuses[index]
                item?.also {
                    StatusListItem(
                        item = item,
                        reply = ReplyType.NONE,
                        repliedTo = ReplyType.NONE,
                        onUrlClicked = onUrlClicked,
                        onFavoriteClicked = onFavoriteClicked,
                        onReblogClicked = onReblogClicked,
                        onSensitiveExpandClicked = onSensitiveExpandClicked,
                        onClick = onClick,
                        onAccountClick = onAccountClicked,
                        onAttachmentClicked = { s: String, i: Int -> },
                        exoPlayer = null
                    )
                }
            }
        }
        AnimatedVisibility(
            visible = isRefresh,
            modifier = Modifier.fillMaxWidth(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
    }
}