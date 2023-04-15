/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.features.accountDetails.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import coil.compose.AsyncImage
import com.rainy.mastodroid.R
import com.rainy.mastodroid.core.data.model.entity.status.AccountStatusTimelineType
import com.rainy.mastodroid.core.domain.model.status.statusThread.ReplyType
import com.rainy.mastodroid.extensions.ifTrue
import com.rainy.mastodroid.features.accountDetails.AccountStatusesTimelineViewModel
import com.rainy.mastodroid.features.accountDetails.model.AccountDetailsItemModel
import com.rainy.mastodroid.features.accountDetails.model.AccountRelationshipsState
import com.rainy.mastodroid.ui.elements.FlowerShape
import com.rainy.mastodroid.ui.elements.statusListItem.StatusListItem
import com.rainy.mastodroid.ui.elements.statusListItem.StatusTextContent
import com.rainy.mastodroid.ui.styledText.textInlineCustomEmojis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun AccountDetailsScreen(
    accountDetails: AccountDetailsItemModel,
    relationships: AccountRelationshipsState,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = rememberProfileCollapsingToolbarScrollBehavior()
    val pagerState = rememberPagerState()

    Box(modifier = modifier) {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                AccountDetailsTopContent(
                    accountDetails,
                    relationships,
                    scrollBehavior,
                    pagerState
                )
            }
        ) {
            CompositionLocalProvider(
                LocalOverscrollConfiguration provides null,
                content = {
                    AccountTimelinesPager(
                        pagerState = pagerState,
                        accountId = accountDetails.id,
                        onAccountClicked = {},
                        onUrlClicked = {},
                        onClick = {},
                        modifier = Modifier.padding(it)
                    )
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
private fun AccountTimelinesPager(
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

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(statuses, key = { it.id }) { status ->
                status?.also {
                    StatusListItem(
                        item = status,
                        reply = ReplyType.NONE,
                        repliedTo = ReplyType.NONE,
                        onUrlClicked = onUrlClicked,
                        onFavoriteClicked = viewModel::setFavorite,
                        onReblogClicked = viewModel::setReblog,
                        onSensitiveExpandClicked = viewModel::expandSensitiveStatus,
                        onClick = onClick,
                        onAccountClick = onAccountClicked
                    )
                }

            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AccountDetailsTopContent(
    accountDetails: AccountDetailsItemModel,
    relationships: AccountRelationshipsState,
    scrollBehavior: ProfileCollapsingToolbarScrollBehavior,
    pagerState: PagerState,
    modifier: Modifier = Modifier
) {
    ProfileCollapsingToolbar(
        banner = {
            AsyncImage(
                model = accountDetails.bannerUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = modifier
                    .height(145.dp)
            )
        },
        avatar = {
            Box(
                modifier = Modifier.size(125.dp)
            ) {
                AnimatedAvatar(accountDetails.avatarUrl, scrollBehavior)
            }

        },
        accountActions = {
            AccountActions(
                mainAction = {
                    AnimatedVisibility(
                        visible = relationships is AccountRelationshipsState.Relationships,
                        enter = fadeIn()
                    ) {
                        Button(
                            onClick = { /*TODO*/ }
                        ) {
                            Text(
                                text =
                                when {
                                    (relationships as AccountRelationshipsState.Relationships).following -> stringResource(
                                        id = R.string.following_account
                                    )

                                    relationships.requested -> stringResource(
                                        id = R.string.follow_account_requested
                                    )

                                    else -> stringResource(id = R.string.follow_account)
                                }
                            )
                        }
                    }
                },
                statusesCount = accountDetails.statusesCount,
                followingCount = accountDetails.followingCount,
                followersCount = accountDetails.followersCount,
                onStatusesClicked = {},
                onFollowingClicked = {},
                onFollowersClicked = {},
                modifier = Modifier.padding(end = 16.dp)
            )
        },
        persistentContent = {
            val coroutineScope = rememberCoroutineScope()
            ScrollableTabRow(selectedTabIndex = pagerState.currentPage) {
                Tab(selected = pagerState.currentPage == 0, onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(0)
                    }
                }, text = {
                    Text(text = stringResource(R.string.posts_account_tab))
                }
                )
                Tab(selected = pagerState.currentPage == 1, onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(1)
                    }
                }, text = {
                    Text(text = stringResource(R.string.posts_and_replies_account_tab))
                }
                )
                Tab(selected = pagerState.currentPage == 2, onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(2)
                    }
                }, text = {
                    Text(text = stringResource(R.string.media_account_tab))
                }
                )
                Tab(selected = pagerState.currentPage == 3, onClick = { /*TODO*/ }, text = {
                    Text(text = stringResource(R.string.info_account_tab))
                }
                )
            }
        },
        hideOnScrollContent = {
            AccountHeadHideableContent(accountDetails)
        },
        expandedUsername = {

            Text(
                text = accountDetails.displayName,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                inlineContent = textInlineCustomEmojis(
                    emojis = accountDetails.customEmojis,
                    size = MaterialTheme.typography.headlineSmall.fontSize
                )
            )
        },
        collapsedUsername = {
            Text(
                text = accountDetails.displayName,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                inlineContent = textInlineCustomEmojis(
                    emojis = accountDetails.customEmojis,
                    size = MaterialTheme.typography.headlineSmall.fontSize
                ),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        },
        navigationIcon = {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(Icons.Default.ArrowBack, "")
            }
        },
        topActions = {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(Icons.Default.MoreVert, "")
            }
        },
        scrollBehavior = scrollBehavior
    )
}

@Composable
private fun AnimatedAvatar(
    avatarUrl: String,
    scrollBehavior: ProfileCollapsingToolbarScrollBehavior
) {
    val infiniteTransition = rememberInfiniteTransition()
    val avatarShapeRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(easing = LinearEasing, durationMillis = 50 * 1000),
            repeatMode = RepeatMode.Restart
        ), label = ""
    )
    val avatarAnimation = remember {
        derivedStateOf { lerp(4.dp, 0.dp, scrollBehavior.state.collapsedFraction) }
    }
    AsyncImage(
        model = avatarUrl,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxSize()
            .clip(
                FlowerShape(
                    amplitudeDp = avatarAnimation.value,
                    rotationDegrees = avatarShapeRotation
                )
            )
            .ifTrue(avatarAnimation.value != 0.dp) {
                background(MaterialTheme.colorScheme.secondary)
            }
            .padding(avatarAnimation.value)
            .clip(
                FlowerShape(
                    amplitudeDp = avatarAnimation.value,
                    rotationDegrees = avatarShapeRotation
                )
            )
    )
}

@Composable
fun AccountHeadHideableContent(
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