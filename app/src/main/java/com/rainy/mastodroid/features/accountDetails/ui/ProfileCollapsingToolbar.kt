/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.features.accountDetails.ui

import androidx.annotation.FloatRange
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastFirstOrNull
import androidx.compose.ui.util.lerp
import com.rainy.mastodroid.ui.theme.MastodroidTheme
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToInt

private val collapsedToolbarHeight = 64.dp
private val collapsedAvatarSize = 48.dp
private val startPadding = 16.dp
private val itemBottomPadding = 8.dp
private val topBarItemPadding = 4.dp

private const val EXPANDED_TITLE_ID = "expandedTitle"
private const val COLLAPSED_TITLE_ID = "collapsedTitle"
private const val NAVIGATION_ICON_ID = "navigationIcon"
private const val ACTIONS_ID = "actions"
private const val PERSISTENT_CONTENT_ID = "persistentContent"
private const val BANNER_ID = "banner"
private const val AVATAR_ID = "avatar"
private const val ACCOUNT_ACTIONS_ID = "accountActions"
private const val HIDE_ON_SCROLL_CONTENT_ID = "hideOnScrollContent"

@Composable
fun ProfileCollapsingToolbar(
    banner: @Composable () -> Unit,
    avatar: @Composable () -> Unit,
    accountActions: @Composable () -> Unit,
    persistentContent: @Composable () -> Unit,
    hideOnScrollContent: @Composable () -> Unit,
    expandedUsername: @Composable () -> Unit,
    collapsedUsername: @Composable () -> Unit,
    scrollBehavior: ProfileCollapsingToolbarScrollBehavior,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable (() -> Unit)? = {
        IconButton(onClick = { }) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = null
            )
        }

    },
    topActions: @Composable (RowScope.() -> Unit)? = null,
) {
    val collapsedFraction = scrollBehavior.state.collapsedFraction
    val isCollapsed = collapsedFraction == 1f
    val appBarDragModifier = modifier.draggable(
        orientation = Orientation.Vertical,
        state = rememberDraggableState { delta ->
            scrollBehavior.state.heightOffset = scrollBehavior.state.heightOffset + delta
        },
        onDragStopped = { velocity ->
            flingToolbar(
                scrollBehavior.state,
                velocity,
                scrollBehavior.flingAnimationSpec
            )
        }
    )
    Surface(
        modifier = appBarDragModifier
    ) {
        Layout(
            content = {
                if (navigationIcon != null) {
                    Box(
                        modifier = Modifier
                            .layoutId(NAVIGATION_ICON_ID)
                            .wrapContentHeight()
                    ) {
                        navigationIcon()
                    }
                }
                if (topActions != null) {
                    Row(
                        modifier = Modifier
                            .layoutId(ACTIONS_ID)
                            .wrapContentHeight()
                    ) {
                        topActions()
                    }
                }
                Box(
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .layoutId(BANNER_ID)
                        .height(IntrinsicSize.Min)
                ) {
                    banner()
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        MaterialTheme.colorScheme.background.copy(alpha = 0.4f)
                                    ),
                                    startY = with(LocalDensity.current) { collapsedToolbarHeight.toPx() },
                                    endY = 0f
                                )
                            )
                    )
                }
                Box(
                    modifier = Modifier.layoutId(AVATAR_ID)
                ) {
                    avatar()
                }
                Box(modifier = Modifier.layoutId(ACCOUNT_ACTIONS_ID)) {
                    accountActions()
                }
                Box(
                    modifier = Modifier.layoutId(EXPANDED_TITLE_ID)
                ) {
                    expandedUsername()
                }
                Box(
                    modifier = Modifier.layoutId(COLLAPSED_TITLE_ID)
                ) {
                    collapsedUsername()
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .layoutId(HIDE_ON_SCROLL_CONTENT_ID)
                ) {
                    hideOnScrollContent()
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .layoutId(PERSISTENT_CONTENT_ID)
                ) {
                    persistentContent()
                }
            }
        ) { measurables, constraints ->
            val collapsedToolbarHeightPx = collapsedToolbarHeight.roundToPx()
            val collapsedAvatarSizePx = collapsedAvatarSize.roundToPx()
            val startPaddingPx = startPadding.roundToPx()
            val innerItemsPaddingPx = itemBottomPadding.roundToPx()
            val topItemsPaddingPx = topBarItemPadding.roundToPx()

            val navigationIconPlaceable =
                measurables.fastFirstOrNull { it.layoutId == NAVIGATION_ICON_ID }
                    ?.measure(
                        constraints.copy(
                            maxHeight = collapsedToolbarHeightPx,
                            minHeight = collapsedToolbarHeightPx
                        )
                    )

            val topActionsPlaceable = measurables.fastFirstOrNull { it.layoutId == ACTIONS_ID }
                ?.measure(
                    constraints.copy(
                        maxHeight = collapsedToolbarHeightPx,
                        minHeight = collapsedToolbarHeightPx
                    )
                )

            val bannerPlaceable = measurables.fastFirstOrNull { it.layoutId == BANNER_ID }
                ?.measure(constraints) ?: throw IllegalStateException("Banner measurable not found")

            val accountActionsPlaceable =
                measurables.fastFirstOrNull { it.layoutId == ACCOUNT_ACTIONS_ID }
                    ?.measure(constraints.copy(maxWidth = constraints.maxWidth - startPaddingPx))
                    ?: throw IllegalStateException("Account actions measurable not found")

            val avatarPlaceable = measurables.fastFirstOrNull { it.layoutId == AVATAR_ID }
                ?.measure(
                    constraints.copy(
                        maxWidth = constraints.maxWidth - (startPaddingPx + innerItemsPaddingPx + accountActionsPlaceable.width),
                        maxHeight = constraints.maxWidth - (startPaddingPx + innerItemsPaddingPx + accountActionsPlaceable.width)
                    )
                ) ?: throw IllegalStateException("Avatar measurable not found")


            val usernamePlaceable = measurables.fastFirstOrNull { it.layoutId == EXPANDED_TITLE_ID }
                ?.measure(constraints.copy(maxWidth = constraints.maxWidth - (startPaddingPx * 2)))
                ?: throw IllegalStateException("Expanded username measurable not found")

            val topItemPaddingsNumber = listOfNotNull(
                navigationIconPlaceable,
                topActionsPlaceable,
                avatarPlaceable
            ).size
            val collapsedUserNamePlaceable =
                measurables.fastFirstOrNull { it.layoutId == COLLAPSED_TITLE_ID }
                    ?.measure(
                        constraints.copy(
                            maxWidth = constraints.maxWidth - ((navigationIconPlaceable?.width
                                ?: 0) + collapsedAvatarSizePx + (topActionsPlaceable?.width
                                ?: 0) + (topItemsPaddingPx * topItemPaddingsNumber)),
                            maxHeight = collapsedToolbarHeightPx
                        )
                    ) ?: throw IllegalStateException("Collapsed username measurable not found")

            val hideOnScrollContentPlaceable =
                measurables.fastFirstOrNull { it.layoutId == HIDE_ON_SCROLL_CONTENT_ID }
                    ?.measure(constraints)
                    ?: throw IllegalStateException("Hide on scroll content measurable not found")

            val persistentContentPlaceable =
                measurables.fastFirstOrNull { it.layoutId == PERSISTENT_CONTENT_ID }
                    ?.measure(constraints)
                    ?: throw IllegalStateException("Persistent content measurable not found")

            val navigationIconX = 0
            val navigationIconY = 0

            val topActionsX = constraints.maxWidth - (topActionsPlaceable?.width ?: 0)
            val topActionsY = 0

            val bannerX = 0
            val bannerY = 0

            val avatarX = startPaddingPx
            val avatarY =
                (bannerPlaceable.height - (0.7 * avatarPlaceable.height)).roundToInt()

            val collapsedAvatarX =
                navigationIconX + (navigationIconPlaceable?.width?.plus(topItemsPaddingPx) ?: 0)
            val collapsedAvatarY = (collapsedToolbarHeightPx - collapsedAvatarSizePx) / 2

            val accountActionsX = constraints.maxWidth - accountActionsPlaceable.width
            val accountActionsY =
                abs((avatarY + avatarPlaceable.height) - accountActionsPlaceable.height)

            val usernameX = startPaddingPx
            val usernameY = avatarY + avatarPlaceable.height + innerItemsPaddingPx

            val usernameCollapsedX =
                collapsedAvatarX + collapsedAvatarSizePx + topItemsPaddingPx
            val usernameCollapsedY =
                (collapsedToolbarHeightPx - collapsedUserNamePlaceable.height) / 2


            val hideOnScrollContentX = 0
            val hideOnScrollContentY = (usernameY + usernamePlaceable.height)

            val persistentContentX = 0
            val persistentContentY = hideOnScrollContentY + hideOnScrollContentPlaceable.height

            val persistentContentCollapsedY = collapsedToolbarHeightPx

            val transformPersistentContentY =
                lerp(persistentContentY, persistentContentCollapsedY, collapsedFraction)

            val totalHeight = persistentContentY + persistentContentPlaceable.height

            val userNameTransformX = quadBezier(
                usernameX,
                (avatarX + avatarPlaceable.width),
                usernameCollapsedX,
                collapsedFraction
            )
            val userNameTransformY =
                quadBezier(usernameY, avatarY, usernameCollapsedY, collapsedFraction)

            val collapsedHeight =
                collapsedToolbarHeightPx + persistentContentPlaceable.height

            val transformHeight = lerp(totalHeight, collapsedHeight, collapsedFraction)

            val scrollingHeightOffsetLimit = -(totalHeight - collapsedHeight).toFloat()
            if (scrollBehavior.state.heightOffsetLimit != scrollingHeightOffsetLimit) {
                scrollBehavior.state.heightOffsetLimit = scrollingHeightOffsetLimit
            }

            layout(constraints.maxWidth, transformHeight) {

                if (!isCollapsed) {
                    bannerPlaceable.placeRelativeWithLayer(bannerX, bannerY) {
                        alpha = 1 - collapsedFraction
                    }
                }

                navigationIconPlaceable?.placeRelative(navigationIconX, navigationIconY)

                topActionsPlaceable?.placeRelative(topActionsX, topActionsY)

                avatarPlaceable.placeRelativeWithLayer(
                    lerp(avatarX, collapsedAvatarX, collapsedFraction),
                    lerp(avatarY, collapsedAvatarY, collapsedFraction)
                ) {
                    scaleX = lerp(
                        1f,
                        collapsedAvatarSize.toPx() / avatarPlaceable.width,
                        collapsedFraction
                    )
                    scaleY = lerp(
                        1f,
                        collapsedAvatarSize.toPx() / avatarPlaceable.height,
                        collapsedFraction
                    )
                    transformOrigin = TransformOrigin(0f, 0f)
                }

                if (!isCollapsed) {
                    accountActionsPlaceable.placeRelativeWithLayer(
                        accountActionsX,
                        accountActionsY
                    ) {
                        alpha = 1 - collapsedFraction
                    }
                }

                if (!isCollapsed) {
                    usernamePlaceable.placeRelativeWithLayer(
                        userNameTransformX,
                        userNameTransformY
                    ) {
                        alpha = 1 - collapsedFraction
                    }
                }

                if (collapsedFraction > 0.1f) {
                    collapsedUserNamePlaceable.placeRelativeWithLayer(
                        userNameTransformX,
                        userNameTransformY
                    ) {
                        alpha = collapsedFraction
                    }
                }

                if (!isCollapsed) {
                    hideOnScrollContentPlaceable.placeRelativeWithLayer(
                        x = hideOnScrollContentX,
                        y = hideOnScrollContentY
                    ) {
                        alpha = 1 - collapsedFraction
                    }
                }

                persistentContentPlaceable.placeRelative(
                    persistentContentX,
                    transformPersistentContentY
                )
            }
        }
    }

}

private fun quadBezier(
    p1: Int,
    p2: Int,
    p3: Int,
    @FloatRange(0.0, 1.0, true, true) fraction: Float
): Int {
    return (p2 + (1 - fraction).pow(2) * (p1 - p2) + fraction.pow(2) * (p3 - p2)).roundToInt()
}

@Composable
@Preview
private fun CustomToolbarPreview() {
    MastodroidTheme {
        Surface {
            val scrollingBehavior = rememberProfileCollapsingToolbarScrollBehavior()
            ProfileCollapsingToolbar(
                banner = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(Color.Cyan)
                    )
                },
                avatar = {
                    Box(
                        modifier = Modifier
                            .size(125.dp)
                            .background(Color.Red)
                    )
                },
                accountActions = {
                    Box(
                        modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .background(Color.Green.copy(alpha = 0.5f))
                    )

                },
                persistentContent = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(24.dp)
                            .background(Color.Magenta)
                    )
                },
                hideOnScrollContent = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .background(Color.Yellow)
                    )
                },
                expandedUsername = { Text("Test", style = MaterialTheme.typography.headlineSmall) },
                collapsedUsername = {
                    Text(
                        "Test, but collapsed",
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                scrollBehavior = scrollingBehavior
            )
        }
    }
}
