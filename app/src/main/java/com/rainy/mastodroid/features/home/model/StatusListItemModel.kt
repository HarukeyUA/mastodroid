package com.rainy.mastodroid.features.home.model

import androidx.compose.runtime.Immutable
import com.rainy.mastodroid.core.domain.model.status.Status
import kotlinx.datetime.Instant

@Immutable
data class StatusListItemModel(
    val id: String,
    val authorDisplayName: String,
    val authorAccountHandle: String,
    val authorAvatarUrl: String,
    val content: String,
    val lastUpdate: Instant?,
    val edited: Boolean
)

fun Status.toStatusListItemModel(): StatusListItemModel {
    return StatusListItemModel(
        id = id,
        authorDisplayName = account.displayName,
        authorAccountHandle = account.accountUri,
        authorAvatarUrl = account.avatarUrl,
        content = content,
        lastUpdate = editedAt ?: createdAt,
        edited = editedAt != null
    )
}
