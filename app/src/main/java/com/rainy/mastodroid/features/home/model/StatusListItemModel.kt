package com.rainy.mastodroid.features.home.model

import androidx.compose.runtime.Immutable
import com.rainy.mastodroid.core.domain.model.status.Status

@Immutable
data class StatusListItemModel(
    val id: String,
    val authorDisplayName: String,
    val authorAccountHandle: String,
    val authorAvatarUrl: String,
    val content: String
)

fun Status.toStatusListItemModel(): StatusListItemModel {
    return StatusListItemModel(
        id = id,
        authorDisplayName = account.displayName,
        authorAccountHandle = account.accountUri,
        authorAvatarUrl = account.avatarUrl,
        content = content
    )
}
