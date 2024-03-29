/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.core.domain.model.status

import com.rainy.mastodroid.core.data.model.response.CustomEmojiResponse
import com.rainy.mastodroid.core.data.model.response.StatusVisibility
import com.rainy.mastodroid.core.data.model.response.mediaAttachment.AudioAttachmentResponse
import com.rainy.mastodroid.core.data.model.response.mediaAttachment.GifvAttachmentResponse
import com.rainy.mastodroid.core.data.model.response.mediaAttachment.ImageAttachmentResponse
import com.rainy.mastodroid.core.data.model.response.mediaAttachment.UnknownAttachmentResponse
import com.rainy.mastodroid.core.data.model.response.mediaAttachment.VideoAttachmentResponse
import com.rainy.mastodroid.core.data.model.response.status.StatusMentionResponse
import com.rainy.mastodroid.core.data.model.response.status.StatusResponse
import com.rainy.mastodroid.core.data.model.response.status.StatusTagResponse
import com.rainy.mastodroid.core.domain.model.mediaAttachment.MediaAttachment
import com.rainy.mastodroid.core.domain.model.mediaAttachment.toDomain
import com.rainy.mastodroid.core.domain.model.user.Account
import com.rainy.mastodroid.core.domain.model.user.CustomEmoji
import com.rainy.mastodroid.core.domain.model.user.toDomain
import com.rainy.mastodroid.util.loge
import kotlinx.datetime.Instant

data class Status(
    val id: String,
    val reblogId: String?,
    val reblogAuthorAccount: Account?,
    val uri: String,
    val createdAt: Instant?,
    val account: Account,
    val content: String,
    val visibility: StatusVisibility,
    val sensitive: Boolean,
    val spoilerText: String,
    val application: StatusApplication?,
    val mentions: List<StatusMention>,
    val tags: List<StatusTag>,
    val customEmojis: List<CustomEmoji>,
    val reblogsCount: Int,
    val favouritesCount: Int,
    val repliesCount: Int,
    val url: String?,
    val inReplyToId: String?,
    val inReplyToAccountId: String?,
    val urlPreviewCard: UrlPreviewCard?,
    val language: String?,
    val text: String?,
    val editedAt: Instant?,
    val favourited: Boolean,
    val reblogged: Boolean,
    val muted: Boolean,
    val bookmarked: Boolean,
    val pinned: Boolean,
    val mediaAttachments: List<MediaAttachment>
) {
    val timelineId get() = reblogId ?: id
}

fun StatusResponse.toDomain(): Status? {
    with(reblog ?: this) {
        if (id.isNullOrEmpty() || account == null || this@toDomain.id == null) {
            loge("Illegal timeline status response data: id: $id account: $account")
            return null
        }

        return Status(
            id = this@toDomain.reblog?.id ?: this@toDomain.id,
            reblogId = if (this@toDomain.reblog == null) null else this@toDomain.id,
            reblogAuthorAccount = if (this@toDomain.reblog != null) this@toDomain.account?.toDomain() else null,
            uri = uri ?: "",
            createdAt = createdAt,
            account = account.toDomain(),
            content = content ?: "",
            visibility = visibility ?: StatusVisibility.PUBLIC,
            sensitive = sensitive ?: false,
            spoilerText = spoilerText ?: "",
            application = application?.toDomain(),
            mentions = mentions?.mapNotNull(StatusMentionResponse::toDomain) ?: listOf(),
            tags = tags?.map(StatusTagResponse::toDomain) ?: listOf(),
            customEmojis = emojis?.mapNotNull(CustomEmojiResponse::toDomain) ?: listOf(),
            reblogsCount = reblogsCount ?: 0,
            favouritesCount = favouritesCount ?: 0,
            repliesCount = repliesCount ?: 0,
            url = url,
            inReplyToId = inReplyToId,
            inReplyToAccountId = inReplyToAccountId,
            urlPreviewCard = previewCard?.toDomain(),
            language = language,
            text = text,
            editedAt = editedAt,
            favourited = favourited ?: false,
            reblogged = reblogged ?: false,
            muted = muted ?: false,
            bookmarked = bookmarked ?: false,
            pinned = pinned ?: false,
            mediaAttachments = mediaAttachments?.mapNotNull {
                when (it) {
                    is GifvAttachmentResponse -> it.toDomain()
                    is ImageAttachmentResponse -> it.toDomain()
                    UnknownAttachmentResponse -> null
                    is VideoAttachmentResponse -> it.toDomain()
                    is AudioAttachmentResponse -> null
                }
            } ?: listOf()
        )
    }
}
