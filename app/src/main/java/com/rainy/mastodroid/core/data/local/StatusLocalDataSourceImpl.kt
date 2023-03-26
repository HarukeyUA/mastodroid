/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.core.data.local

import androidx.paging.PagingSource
import com.rainy.mastodroid.Database
import com.rainy.mastodroid.core.domain.model.status.ContextStatusType
import com.rainy.mastodroid.core.data.model.entity.status.MediaAttachmentEntity
import com.rainy.mastodroid.core.data.model.entity.status.StatusAccountUserFieldEntity
import com.rainy.mastodroid.core.data.model.entity.status.StatusCustomEmojiEntity
import com.rainy.mastodroid.core.data.model.entity.status.StatusMentionEntity
import com.rainy.mastodroid.core.data.model.entity.status.StatusTagEntity
import com.rainy.mastodroid.core.data.model.entity.status.toGifvAttachmentEntity
import com.rainy.mastodroid.core.data.model.entity.status.toImageAttachmentEntity
import com.rainy.mastodroid.core.data.model.entity.status.toStatusApplicationEntity
import com.rainy.mastodroid.core.data.model.entity.status.toStatusCustomEmojiEntity
import com.rainy.mastodroid.core.data.model.entity.status.toStatusMentionEntity
import com.rainy.mastodroid.core.data.model.entity.status.toStatusTagEntity
import com.rainy.mastodroid.core.data.model.entity.status.toUrlPreviewCardEntity
import com.rainy.mastodroid.core.data.model.entity.status.toVideoAttachmentEntity
import com.rainy.mastodroid.core.domain.data.remote.StatusLocalDataSource
import com.rainy.mastodroid.core.domain.model.mediaAttachment.GifvAttachment
import com.rainy.mastodroid.core.domain.model.mediaAttachment.ImageAttachment
import com.rainy.mastodroid.core.domain.model.mediaAttachment.VideoAttachment
import com.rainy.mastodroid.core.domain.model.mediaAttachment.toDomain
import com.rainy.mastodroid.core.domain.model.status.Status
import com.rainy.mastodroid.core.domain.model.status.StatusContext
import com.rainy.mastodroid.core.domain.model.status.StatusMention
import com.rainy.mastodroid.core.domain.model.status.StatusTag
import com.rainy.mastodroid.core.domain.model.status.toDomain
import com.rainy.mastodroid.core.domain.model.user.CustomEmoji
import com.rainy.mastodroid.core.domain.model.user.Account
import com.rainy.mastodroid.core.domain.model.user.toDomain
import com.rainy.mastodroid.database.MastodroidDatabase
import com.rainy.mastodroid.database.OffsetQueryPagingSource
import com.rainy.mastodroid.database.extensions.upsertAccount
import com.rainy.mastodroid.database.toInt
import com.rainy.mastodroidDb.StatusContextEntity
import com.rainy.mastodroidDb.StatusWithAccount
import com.rainy.mastodroidDb.TimelineWithOffset
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class StatusLocalDataSourceImpl(
    private val db: MastodroidDatabase
) : StatusLocalDataSource {

    override fun getHomeTimelinePagingSource(): PagingSource<Int, Status> {
        db.timelineElementQueries.countTimeline()
        return OffsetQueryPagingSource(
            countQuery = db.timelineElementQueries.countTimeline().toInt(),
            db = db,
            queryProvider = { limit, offset ->
                db.timelineElementQueries.timelineWithOffset(limit.toLong(), offset.toLong())
            },
            mapper = { timelineElement: TimelineWithOffset ->
                timelineElementToStatus(timelineElement)
            })
    }

    override suspend fun replaceTimelineStatuses(statuses: List<Status>) {
        db.await(true) {
            timelineElementQueries.removeAllTimelineElements()
            statuses.forEach {
                upsertStatusWithAccount(it)
                insertTimelineElement(it)
            }
        }
    }

    private fun Database.upsertStatusWithAccount(status: Status) {
        status.reblogAuthorAccount?.also { reblogAccount ->
            upsertAccount(reblogAccount)
        }
        upsertAccount(status.account)
        upsertStatus(status)
    }

    override suspend fun insertTimelineStatuses(statuses: List<Status>) {
        db.await(true) {
            statuses.forEach {
                upsertStatusWithAccount(it)
                insertTimelineElement(it)
            }
        }
    }

    override suspend fun getStatusById(id: String): Status? {
        return db.awaitAsOneOrNull { statusQueries.statusWithAccountById(id) }
            ?.let { statusWithAccountToStatus(it) }
    }

    override fun getStatusFlowById(id: String): Flow<Status?> {
        return db.asFlowOfOneOrNull { statusQueries.statusWithAccountById(id) }
            .map { it?.let { statusWithAccount -> statusWithAccountToStatus(statusWithAccount) } }
    }

    override suspend fun getLastTimeLineElementId(): String? {
        val lastElement = db.awaitAsOneOrNull {
            timelineElementQueries.lastTimelineElement()
        }
        return lastElement?.reblogId ?: lastElement?.statusId
    }

    override suspend fun setFavourite(id: String) {
        db.await { statusQueries.setFavorite(id) }
    }

    override suspend fun unFavourite(id: String) {
        db.await { statusQueries.unFavorite(id) }
    }

    override suspend fun setRebloged(id: String) {
        db.await { statusQueries.setReblog(id) }
    }

    override suspend fun unReblog(id: String) {
        db.await { statusQueries.unReblog(id) }
    }

    override suspend fun insertStatusContext(statusInContext: StatusContext, forStatusId: String) {
        val ancestors = statusInContext.ancestors
        val descendants = statusInContext.descendants
        db.await(true) {
            ancestors.forEach {
                upsertStatusWithAccount(it)
            }
            descendants.forEach {
                upsertStatusWithAccount(it)
            }
            statusContextQueries.deleteAllForStatus(forStatusId)
            ancestors.forEachIndexed { index, status ->
                statusContextQueries.insertOrReplace(
                    StatusContextEntity(
                        statusId = status.id,
                        contextForStatusId = forStatusId,
                        contextStatusType = ContextStatusType.ANCESTOR,
                        orderIndex = index.toLong()
                    )
                )
            }

            descendants.forEachIndexed { index, status ->
                statusContextQueries.insertOrReplace(
                    StatusContextEntity(
                        statusId = status.id,
                        contextForStatusId = forStatusId,
                        contextStatusType = ContextStatusType.DESCENDANT,
                        orderIndex = index.toLong()
                    )
                )
            }

        }
    }

    override suspend fun insertStatus(status: Status) {
        db.await(true) {
            upsertStatusWithAccount(status)
        }
    }

    override fun getContextFlowForStatus(statusId: String): Flow<StatusContext> {
        val ancestors = db.asFlowOfList {
            statusContextQueries.contextForStatusId(
                statusId = statusId,
                ContextStatusType.ANCESTOR
            )
        }
        val descendants = db.asFlowOfList {
            statusContextQueries.contextForStatusId(
                statusId = statusId,
                ContextStatusType.DESCENDANT
            )
        }

        return combine(ancestors, descendants) { ancestorsList, descendantsList ->
            StatusContext(
                ancestors = ancestorsList.map {
                    statusWithAccountToStatus(it)
                },
                descendants = descendantsList.map { statusWithAccountToStatus(it) }
            )
        }
    }

    private fun statusWithAccountToStatus(statusWithAccount: StatusWithAccount) =
        with(statusWithAccount) {
            Status(
                id = id,
                reblogId = null,
                reblogAuthorAccount = null,
                uri = uri,
                createdAt = createdAt,
                account = Account(
                    accountUri = accountUri,
                    avatarUrl = accountAvatarUrl,
                    avatarStaticUrl = accountAvatarStatisUrl,
                    bot = accountBot,
                    createdAt = accountCreatedAt,
                    displayName = accountDisplayName,
                    customEmojis = accountCustomEmojis.map(StatusCustomEmojiEntity::toDomain),
                    fields = accountFields.map(StatusAccountUserFieldEntity::toDomain),
                    followersCount = accountFollowersCount,
                    followingCount = accountFollowingCount,
                    headerUrl = accountHeaderUrl,
                    headerStaticUrl = accountHeaderStaticUrl,
                    id = accountId,
                    locked = accountLocked,
                    note = accountNote,
                    source = null,
                    statusesCount = accountStatusesCount,
                    url = accountUrl,
                    username = accountUsername,
                    group = accountGroupActor,
                    discoverable = accountDiscoverable,
                    suspended = accountSuspended,
                    limited = accountLimited
                ),
                content = content,
                visibility = visibility,
                sensitive = sensitive,
                spoilerText = spoilerText,
                application = application?.toDomain(),
                mentions = mentions.map(StatusMentionEntity::toDomain),
                tags = tags.map(StatusTagEntity::toDomain),
                customEmojis = customEmojis.map(StatusCustomEmojiEntity::toDomain),
                reblogsCount = reblogsCount.toInt(),
                favouritesCount = favouritesCount.toInt(),
                repliesCount = repliesCount.toInt(),
                url = url,
                inReplyToId = inReplyToId,
                inReplyToAccountId = inReplyToAccountId,
                urlPreviewCard = urlPreviewCard?.toDomain(),
                language = language,
                text = text,
                editedAt = editedAt,
                favourited = favourited,
                reblogged = reblogged,
                muted = muted,
                bookmarked = bookmarked,
                pinned = pinned,
                mediaAttachments = mediaAttachments.map {
                    when (it) {
                        is MediaAttachmentEntity.GifvAttachmentEntity -> it.toDomain()
                        is MediaAttachmentEntity.ImageAttachmentEntity -> it.toDomain()
                        is MediaAttachmentEntity.VideoAttachmentEntity -> it.toDomain()
                    }
                }

            )
        }

    private fun timelineElementToStatus(timelineElement: TimelineWithOffset) =
        with(timelineElement) {
            Status(
                id = id,
                reblogId = reblogId,
                reblogAuthorAccount = extractReblogAccount(timelineElement),
                uri = uri,
                createdAt = createdAt,
                account = extractAccount(timelineElement),
                content = content,
                visibility = visibility,
                sensitive = sensitive,
                spoilerText = spoilerText,
                application = application?.toDomain(),
                mentions = mentions.map(StatusMentionEntity::toDomain),
                tags = tags.map(StatusTagEntity::toDomain),
                customEmojis = customEmojis.map(StatusCustomEmojiEntity::toDomain),
                reblogsCount = reblogsCount.toInt(),
                favouritesCount = favouritesCount.toInt(),
                repliesCount = repliesCount.toInt(),
                url = url,
                inReplyToId = inReplyToId,
                inReplyToAccountId = inReplyToAccountId,
                urlPreviewCard = urlPreviewCard?.toDomain(),
                language = language,
                text = text,
                editedAt = editedAt,
                favourited = favourited,
                reblogged = reblogged,
                muted = muted,
                bookmarked = bookmarked,
                pinned = pinned,
                mediaAttachments = mediaAttachments.map {
                    when (it) {
                        is MediaAttachmentEntity.GifvAttachmentEntity -> it.toDomain()
                        is MediaAttachmentEntity.ImageAttachmentEntity -> it.toDomain()
                        is MediaAttachmentEntity.VideoAttachmentEntity -> it.toDomain()
                    }
                },
            )
        }

    private fun extractAccount(timelineElement: TimelineWithOffset) =
        with(timelineElement) {
            Account(
                accountUri = accountUri,
                avatarUrl = accountAvatarUrl,
                avatarStaticUrl = accountAvatarStatisUrl,
                bot = accountBot,
                createdAt = accountCreatedAt,
                displayName = accountDisplayName,
                customEmojis = accountCustomEmojis.map(StatusCustomEmojiEntity::toDomain),
                fields = accountFields.map(StatusAccountUserFieldEntity::toDomain),
                followersCount = accountFollowersCount,
                followingCount = accountFollowingCount,
                headerUrl = accountHeaderUrl,
                headerStaticUrl = accountHeaderStaticUrl,
                id = accountId,
                locked = accountLocked,
                note = accountNote,
                source = null,
                statusesCount = accountStatusesCount,
                url = accountUrl,
                username = accountUsername,
                group = accountGroupActor,
                discoverable = accountDiscoverable,
                suspended = accountSuspended,
                limited = accountLimited
            )
        }

    private fun extractReblogAccount(timelineElement: TimelineWithOffset) =
        with(timelineElement) {
            if (reblogAccountId != null) {
                Account(
                    accountUri = reblogAccountUri ?: "",
                    avatarUrl = reblogAccountAvatarUrl ?: "",
                    avatarStaticUrl = reblogAccountAvatarStatisUrl ?: "",
                    bot = reblogAccountBot ?: false,
                    createdAt = reblogAccountCreatedAt,
                    displayName = reblogAccountDisplayName ?: "",
                    customEmojis = reblogAccountCustomEmojis?.map(
                        StatusCustomEmojiEntity::toDomain
                    )
                        ?: listOf(),
                    fields = reblogAccountFields?.map(
                        StatusAccountUserFieldEntity::toDomain
                    )
                        ?: listOf(),
                    followersCount = reblogAccountFollowersCount ?: 0,
                    followingCount = reblogAccountFollowingCount ?: 0,
                    headerUrl = reblogAccountHeaderUrl ?: "",
                    headerStaticUrl = reblogAccountHeaderStaticUrl ?: "",
                    id = reblogAccountId,
                    locked = reblogAccountLocked ?: false,
                    note = reblogAccountNote ?: "",
                    source = null,
                    statusesCount = reblogAccountStatusesCount ?: 0,
                    url = reblogAccountUrl ?: "",
                    username = reblogAccountUsername ?: "",
                    group = reblogAccountGroupActor ?: false,
                    discoverable = reblogAccountDiscoverable ?: false,
                    suspended = reblogAccountSuspended ?: false,
                    limited = reblogAccountLimited ?: false
                )
            } else {
                null
            }
        }

    private fun Database.insertTimelineElement(status: Status) {
        timelineElementQueries.insertOrReplace(
            statusId = status.id,
            reblogId = status.reblogId,
            reblogAuthorId = status.reblogAuthorAccount?.id
        )
    }

    private fun Database.upsertStatus(status: Status) {
        statusQueries.upsertStatus(
            uri = status.uri,
            createdAt = status.createdAt,
            content = status.content,
            visibility = status.visibility,
            sensitive = status.sensitive,
            spoilerText = status.spoilerText,
            reblogsCount = status.reblogsCount.toLong(),
            url = status.url,
            inReplyToId = status.inReplyToId,
            inReplyToAccountId = status.inReplyToAccountId,
            language = status.language,
            favouritesCount = status.favouritesCount.toLong(),
            repliesCount = status.repliesCount.toLong(),
            text = status.text,
            editedAt = status.editedAt,
            favourited = status.favourited,
            reblogged = status.reblogged,
            muted = status.muted,
            bookmarked = status.bookmarked,
            pinned = status.pinned,
            application = status.application?.toStatusApplicationEntity(),
            mentions = status.mentions.map(StatusMention::toStatusMentionEntity),
            tags = status.tags.map(StatusTag::toStatusTagEntity),
            customEmojis = status.customEmojis.map(CustomEmoji::toStatusCustomEmojiEntity),
            urlPreviewCard = status.urlPreviewCard?.toUrlPreviewCardEntity(),
            mediaAttachments = status.mediaAttachments.map {
                when (it) {
                    is GifvAttachment -> it.toGifvAttachmentEntity()
                    is ImageAttachment -> it.toImageAttachmentEntity()
                    is VideoAttachment -> it.toVideoAttachmentEntity()
                }
            },
            accountId = status.account.id,
            id = status.id
        )
    }
}