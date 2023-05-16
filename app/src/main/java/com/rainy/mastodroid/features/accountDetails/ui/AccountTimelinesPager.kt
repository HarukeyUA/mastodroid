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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.rainy.mastodroid.core.data.model.entity.status.AccountStatusTimelineType
import com.rainy.mastodroid.core.domain.model.status.statusThread.ReplyType
import com.rainy.mastodroid.features.accountDetails.AccountStatusesTimelineViewModel
import com.rainy.mastodroid.ui.elements.statusListItem.StatusListItem
import com.rainy.mastodroid.ui.elements.statusListItem.model.StatusListItemModel
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun AccountTimelinesPager(
    pagerState: PagerState,
    accountId: String,
    onAccountClicked: (String) -> Unit,
    onUrlClicked: (String) -> Unit,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    HorizontalPager(
        state = pagerState,
        modifier = modifier
            .fillMaxSize(),
        key = { it }
    ) { page ->
        val viewModel: AccountStatusesTimelineViewModel = when (page) {
            0 -> koinViewModel(parameters = {
                parametersOf(
                    accountId,
                    AccountStatusTimelineType.POSTS
                )
            }, key = page.toString())

            1 -> koinViewModel(parameters = {
                parametersOf(
                    accountId,
                    AccountStatusTimelineType.POSTS_REPLIES
                )
            }, key = page.toString())

            2 -> koinViewModel(parameters = {
                parametersOf(
                    accountId,
                    AccountStatusTimelineType.MEDIA
                )
            }, key = page.toString())

            else -> throw IllegalStateException("Max page limit reached")
        }
        val statuses = viewModel.timeline.collectAsLazyPagingItems(Dispatchers.Default)

        AccountTimeline(
            statuses = statuses,
            onUrlClicked = onUrlClicked,
            onClick = onClick,
            onAccountClicked = onAccountClicked,
            onFavoriteClicked = viewModel::setFavorite,
            onReblogClicked = viewModel::setReblog,
            onSensitiveExpandClicked = viewModel::expandSensitiveStatus,
            isRefresh = statuses.loadState.refresh == LoadState.Loading,
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