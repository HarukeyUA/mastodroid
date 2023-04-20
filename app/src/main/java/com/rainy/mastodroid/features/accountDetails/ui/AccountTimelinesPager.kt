/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.features.accountDetails.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
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
        pageCount = 3,
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
    modifier: Modifier = Modifier
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = modifier) {
        items(statuses, key = { it.id }) { status ->
            status?.also {
                StatusListItem(
                    item = status,
                    reply = ReplyType.NONE,
                    repliedTo = ReplyType.NONE,
                    onUrlClicked = onUrlClicked,
                    onFavoriteClicked = onFavoriteClicked,
                    onReblogClicked = onReblogClicked,
                    onSensitiveExpandClicked = onSensitiveExpandClicked,
                    onClick = onClick,
                    onAccountClick = onAccountClicked
                )
            }
        }
    }
}