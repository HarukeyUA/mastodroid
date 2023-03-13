package com.rainy.mastodroid.features.statusDetails.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastMap
import com.rainy.mastodroid.core.domain.model.status.statusThread.ReplyType
import com.rainy.mastodroid.features.statusDetails.model.StatusDetailsState
import com.rainy.mastodroid.ui.elements.statusListItem.SpoilerStatusContent
import com.rainy.mastodroid.ui.elements.statusListItem.StatusContent
import com.rainy.mastodroid.ui.elements.statusListItem.StatusListItem
import com.rainy.mastodroid.ui.styledText.annotateMastodonContent

@Composable
@OptIn(ExperimentalMaterialApi::class)
fun StatusDetailsScreen(
    pullRefreshState: PullRefreshState,
    statusDetailsState: StatusDetailsState,
    onFavoriteClicked: (id: String, actionId: String, action: Boolean) -> Unit,
    onReblogClicked: (id: String, actionId: String, action: Boolean) -> Unit,
    onSensitiveExpandClicked: (id: String) -> Unit,
    onUrlClicked: (url: String) -> Unit,
    onStatusClicked: (id: String) -> Unit,
    loadingState: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        when (statusDetailsState) {
            StatusDetailsState.Loading -> {}
            is StatusDetailsState.Ready -> {
                val lazyColumnState =
                    rememberLazyListState(initialFirstVisibleItemIndex = statusDetailsState.statusInContext.ancestors.content.size)
                StatusDetailThread(
                    statusDetailsState = statusDetailsState,
                    lazyColumnState = lazyColumnState,
                    onFavoriteClicked = onFavoriteClicked,
                    onReblogClicked = onReblogClicked,
                    onSensitiveExpandClicked = onSensitiveExpandClicked,
                    onUrlClicked = onUrlClicked,
                    onStatusClicked = onStatusClicked
                )
            }
        }
        PullRefreshIndicator(
            refreshing = loadingState,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter),
            backgroundColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
fun StatusDetailThread(
    statusDetailsState: StatusDetailsState.Ready,
    lazyColumnState: LazyListState,
    onFavoriteClicked: (id: String, actionId: String, action: Boolean) -> Unit,
    onReblogClicked: (id: String, actionId: String, action: Boolean) -> Unit,
    onSensitiveExpandClicked: (id: String) -> Unit,
    onUrlClicked: (url: String) -> Unit,
    onStatusClicked: (id: String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(state = lazyColumnState, modifier = modifier) {
        itemsIndexed(
            items = statusDetailsState.statusInContext.ancestors.content,
            key = { _, status -> status.id }
        ) { index, status ->
            StatusListItem(
                item = status,
                reply = if (index == 0) ReplyType.NONE else ReplyType.DIRECT_REPLY,
                repliedTo = ReplyType.DIRECT_REPLY,
                onUrlClicked = onUrlClicked,
                onFavoriteClicked = onFavoriteClicked,
                onReblogClicked = onReblogClicked,
                onSensitiveExpandClicked = onSensitiveExpandClicked,
                onClick = onStatusClicked
            )
        }
        item(key = statusDetailsState.statusInContext.focusedStatus.id) {
            Spacer(
                modifier = Modifier
                    .height(8.dp)
                    .fillMaxWidth()
            )
            FocusedStatusListItem(statusDetailsState, onSensitiveExpandClicked, onUrlClicked)
            Spacer(
                modifier = Modifier
                    .height(8.dp)
                    .fillMaxWidth()
            )
        }
        items(
            items = statusDetailsState.statusInContext.descendants.content,
            key = { it.status.id }
        ) { statusThreadElement ->
            if (statusThreadElement.reply == ReplyType.NONE) {
                Spacer(
                    modifier = Modifier
                        .height(4.dp)
                        .fillMaxWidth()
                )
            }

            StatusListItem(
                item = statusThreadElement.status,
                reply = statusThreadElement.reply,
                repliedTo = statusThreadElement.repliedTo,
                onUrlClicked = onUrlClicked,
                onFavoriteClicked = onFavoriteClicked,
                onReblogClicked = onReblogClicked,
                onSensitiveExpandClicked = onSensitiveExpandClicked,
                onClick = onStatusClicked
            )

            if (statusThreadElement.repliedTo == ReplyType.NONE) {
                Spacer(
                    modifier = Modifier
                        .height(4.dp)
                        .fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun FocusedStatusListItem(
    statusDetailsState: StatusDetailsState.Ready,
    onSensitiveExpandClicked: (id: String) -> Unit,
    onUrlClicked: (url: String) -> Unit
) {
    val focusedStatus = statusDetailsState.statusInContext.focusedStatus
    val view = LocalView.current
    val contentText by remember {
        derivedStateOf {
            if (view.isInEditMode) {
                AnnotatedString(focusedStatus.content)
            } else {
                focusedStatus.content.annotateMastodonContent(focusedStatus.emojis.content.fastMap { it.shortcode })
            }
        }
    }
    FocusedStatus(
        accountAvatarUrl = focusedStatus.authorAvatarUrl,
        fullAccountName = focusedStatus.authorDisplayName,
        accountUserName = focusedStatus.authorAccountHandle,
        usernameEmojis = focusedStatus.authorDisplayNameEmojis,
        favorites = focusedStatus.favorites,
        isFavorite = focusedStatus.isFavorite,
        reblogs = focusedStatus.reblogs,
        isRebloged = focusedStatus.isRebloged,
        replies = focusedStatus.replies,
        statusAppText = focusedStatus.applicationName,
        createdAt = focusedStatus.createdAt,
        editedAt = focusedStatus.updatedAt
    ) {
        if (focusedStatus.isSensitive) {
            SpoilerStatusContent(
                text = focusedStatus.spoilerText,
                isExpanded = true,
                onExpandClicked = { onSensitiveExpandClicked(focusedStatus.id) },
                content = {
                    StatusContent(
                        contentText = contentText,
                        customEmojis = focusedStatus.emojis,
                        attachments = focusedStatus.attachments,
                        exoPlayer = null,
                        onTextClicked = {},
                        onUrlClicked = onUrlClicked
                    )
                }
            )
        } else {
            StatusContent(
                contentText = contentText,
                customEmojis = focusedStatus.emojis,
                attachments = focusedStatus.attachments,
                exoPlayer = null,
                onTextClicked = {},
                onUrlClicked = onUrlClicked
            )
        }
    }
}