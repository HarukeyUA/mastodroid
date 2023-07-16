/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.features.accountDetails.ui

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastFirstOrNull
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.arkivanov.decompose.router.pages.ChildPages
import com.arkivanov.decompose.value.Value
import com.rainy.mastodroid.R
import com.rainy.mastodroid.extensions.observeWithLifecycle
import com.rainy.mastodroid.features.accountDetails.AccountDetailsComponent
import com.rainy.mastodroid.features.accountDetails.AccountStatusTimelineComponent
import com.rainy.mastodroid.features.accountDetails.model.AccountDetailsItemModel
import com.rainy.mastodroid.features.accountDetails.model.AccountRelationshipsState
import com.rainy.mastodroid.ui.elements.statusListItem.StatusTextContent
import kotlinx.coroutines.launch

private const val ACCOUNT_TOP_BAR_ID = "top_bar"
private const val ACCOUNT_CONTENT_ID = "account_content"

@Composable
fun AccountDetailsScreen(
    component: AccountDetailsComponent
) {
    val accountDetails by component.accountDetails.collectAsStateWithLifecycle()
    val relationships by component.accountRelationships.collectAsStateWithLifecycle()
    val loading by component.loadingState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    component.errorEventFlow.observeWithLifecycle {
        Toast.makeText(context, it.resolveText(context), Toast.LENGTH_SHORT).show()
    }

    AccountDetailsScreen(accountDetails, relationships, loading, pages =
    remember {
        {
            component.childPages
        }
    },
        onPageSelected = component::onPageSelected
    )
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun AccountDetailsScreen(
    accountDetails: AccountDetailsItemModel?,
    relationships: AccountRelationshipsState,
    isLoading: Boolean,
    pages: () -> Value<ChildPages<*, AccountStatusTimelineComponent>>,
    onPageSelected: (index: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val topBarScrollBehavior = rememberProfileCollapsingToolbarScrollBehavior()
    val pagesState = pages().subscribeAsState()
    val accountTimelinePagerState = rememberPagerState(pageCount = { pagesState.value.items.size })
    val accountDetailsScrollState = rememberScrollState()

    Column {
        if (accountDetails != null) {
            CompositionLocalProvider(
                LocalOverscrollConfiguration provides null,
                content = {
                    Layout(
                        content = {
                            AccountDetailsTopBar(
                                accountDetails,
                                relationships,
                                topBarScrollBehavior,
                                modifier = Modifier.layoutId(ACCOUNT_TOP_BAR_ID)
                            )
                            AccountDetailsScreenContent(
                                accountDetailsScrollState,
                                accountDetails,
                                accountTimelinePagerState,
                                pages = pages,
                                onPageSelected = onPageSelected,
                                modifier = Modifier
                                    .layoutId(ACCOUNT_CONTENT_ID)
                                    .padding(top = 4.dp)
                            )
                        },
                        modifier = modifier.nestedScroll(topBarScrollBehavior.nestedScrollConnection)
                    ) { measurables, constraints ->
                        val topBar =
                            measurables.fastFirstOrNull { it.layoutId == ACCOUNT_TOP_BAR_ID }
                                ?.measure(constraints)
                                ?: throw IllegalStateException()
                        val scrollableContent =
                            measurables.fastFirstOrNull { it.layoutId == ACCOUNT_CONTENT_ID }
                                ?.measure(
                                    constraints.copy(
                                        minHeight = constraints.maxHeight - collapsedToolbarHeight.roundToPx(),
                                        maxHeight = constraints.maxHeight - collapsedToolbarHeight.roundToPx()
                                    )
                                ) ?: throw IllegalStateException()

                        layout(height = constraints.maxHeight, width = constraints.maxWidth) {
                            topBar.place(0, 0)
                            scrollableContent.place(0, topBar.height)
                        }

                    }
                }
            )
        }
        AnimatedVisibility(visible = isLoading, enter = fadeIn(), exit = fadeOut()) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun AccountDetailsScreenContent(
    scrollState: ScrollState,
    accountDetails: AccountDetailsItemModel,
    pagerState: PagerState,
    pages: () -> Value<ChildPages<*, AccountStatusTimelineComponent>>,
    onPageSelected: (index: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier) {
        val screenHeight = this.maxHeight
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)

        ) {
            AccountDetailsHeadHideableContent(accountDetails, modifier = Modifier)
            val coroutineScope = rememberCoroutineScope()
            Column(modifier = Modifier.height(screenHeight)) {
                ScrollableTabRow(
                    selectedTabIndex = pagerState.currentPage,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Tab(
                        selected = pagerState.currentPage == 0,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(0)
                            }
                        },
                        text = {
                            Text(text = stringResource(R.string.posts_account_tab))
                        }
                    )
                    Tab(
                        selected = pagerState.currentPage == 1,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(1)
                            }
                        },
                        text = {
                            Text(text = stringResource(R.string.posts_and_replies_account_tab))
                        }
                    )
                    Tab(
                        selected = pagerState.currentPage == 2,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(2)
                            }
                        },
                        text = {
                            Text(text = stringResource(R.string.media_account_tab))
                        }
                    )
                    Tab(
                        selected = pagerState.currentPage == 3,
                        onClick = { /*TODO*/ },
                        text = {
                            Text(text = stringResource(R.string.info_account_tab))
                        }
                    )
                }
                AccountTimelinesPager(
                    pagerState = pagerState,
                    accountId = accountDetails.id,
                    onAccountClicked = {},
                    onUrlClicked = {},
                    onClick = {},
                    pages = pages,
                    onPageSelected = onPageSelected,
                    modifier = Modifier
                        .nestedScroll(remember {
                            object : NestedScrollConnection {
                                override fun onPreScroll(
                                    available: Offset,
                                    source: NestedScrollSource
                                ): Offset {
                                    return if (available.y > 0) Offset.Zero else Offset(
                                        x = 0f,
                                        y = -scrollState.dispatchRawDelta(-available.y)
                                    )
                                }
                            }
                        })
                        .fillMaxHeight()
                )
            }
        }
    }
}

@Composable
fun AccountDetailsHeadHideableContent(
    accountDetails: AccountDetailsItemModel,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.animateContentSize()) {
        Text(
            text = stringResource(
                id = R.string.username_handle,
                accountDetails.accountUri
            ),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        StatusTextContent(
            text = accountDetails.bio,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 4.dp
            ),
            customEmoji = accountDetails.customEmojis,
        )
        if (accountDetails.featuredTags.content.isNotEmpty()) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                accountDetails.featuredTags.content.forEach {
                    SuggestionChip(
                        onClick = { /*TODO*/ },
                        label = {
                            Text(
                                text = stringResource(
                                    id = R.string.hashtag,
                                    it.name
                                )
                            )
                        },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    )
                }
            }
        }
    }
}