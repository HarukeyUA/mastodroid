/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.features.accountDetails.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import coil.compose.AsyncImage
import com.rainy.mastodroid.R
import com.rainy.mastodroid.extensions.ifTrue
import com.rainy.mastodroid.features.accountDetails.model.AccountDetailsItemModel
import com.rainy.mastodroid.features.accountDetails.model.AccountRelationshipsState
import com.rainy.mastodroid.ui.elements.FlowerShape
import com.rainy.mastodroid.ui.styledText.textInlineCustomEmojis

@Composable
fun AccountDetailsTopBar(
    accountDetails: AccountDetailsItemModel,
    relationships: AccountRelationshipsState,
    scrollBehavior: ProfileCollapsingToolbarScrollBehavior,
    modifier: Modifier = Modifier
) {
    ProfileCollapsingToolbar(
        modifier = modifier,
        banner = {
            AsyncImage(
                model = accountDetails.bannerUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
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
        ),
        label = "AvatarAnimation"
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
            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f))
            .run {
                if (avatarAnimation.value != 0.dp) {
                    border(
                        width = avatarAnimation.value,
                        color = MaterialTheme.colorScheme.secondary,
                        shape = FlowerShape(
                            amplitudeDp = avatarAnimation.value,
                            rotationDegrees = avatarShapeRotation
                        )
                    )
                } else {
                    this
                }
            }


    )
}