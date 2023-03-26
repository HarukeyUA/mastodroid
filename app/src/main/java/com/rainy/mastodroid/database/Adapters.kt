/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.database

import app.cash.sqldelight.ColumnAdapter
import com.rainy.mastodroid.core.data.model.entity.status.MediaAttachmentEntity
import com.rainy.mastodroid.core.data.model.entity.status.StatusAccountUserFieldEntity
import com.rainy.mastodroid.core.data.model.entity.status.StatusApplicationEntity
import com.rainy.mastodroid.core.data.model.entity.status.StatusCustomEmojiEntity
import com.rainy.mastodroid.core.data.model.entity.status.StatusMentionEntity
import com.rainy.mastodroid.core.data.model.entity.status.StatusTagEntity
import com.rainy.mastodroid.core.data.model.entity.status.UrlPreviewCardEntity
import kotlinx.datetime.Instant
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val instantAdapter = object : ColumnAdapter<Instant, String> {
    override fun decode(databaseValue: String): Instant {
        return Instant.parse(databaseValue)
    }

    override fun encode(value: Instant): String {
        return value.toString()
    }

}

fun getCustomEmojisAdapter(json: Json): ColumnAdapter<List<StatusCustomEmojiEntity>, String> {
    return object : ColumnAdapter<List<StatusCustomEmojiEntity>, String> {
        override fun decode(databaseValue: String): List<StatusCustomEmojiEntity> {
            return json.decodeFromString(databaseValue)
        }

        override fun encode(value: List<StatusCustomEmojiEntity>): String {
            return json.encodeToString(value)
        }
    }
}

fun getAccountFieldsAdapter(json: Json): ColumnAdapter<List<StatusAccountUserFieldEntity>, String> {
    return object : ColumnAdapter<List<StatusAccountUserFieldEntity>, String> {
        override fun decode(databaseValue: String): List<StatusAccountUserFieldEntity> {
            return json.decodeFromString(databaseValue)
        }

        override fun encode(value: List<StatusAccountUserFieldEntity>): String {
            return json.encodeToString(value)
        }

    }
}

fun getStatusApplicationAdapter(json: Json): ColumnAdapter<StatusApplicationEntity, String> {
    return object : ColumnAdapter<StatusApplicationEntity, String> {
        override fun decode(databaseValue: String): StatusApplicationEntity {
            return json.decodeFromString(databaseValue)
        }

        override fun encode(value: StatusApplicationEntity): String {
            return json.encodeToString(value)
        }
    }
}

fun getStatusMentionsAdapter(json: Json): ColumnAdapter<List<StatusMentionEntity>, String> {
    return object : ColumnAdapter<List<StatusMentionEntity>, String> {
        override fun decode(databaseValue: String): List<StatusMentionEntity> {
            return json.decodeFromString(databaseValue)
        }

        override fun encode(value: List<StatusMentionEntity>): String {
            return json.encodeToString(value)
        }
    }
}

fun getStatusTagsAdapter(json: Json): ColumnAdapter<List<StatusTagEntity>, String> {
    return object : ColumnAdapter<List<StatusTagEntity>, String> {
        override fun decode(databaseValue: String): List<StatusTagEntity> {
            return json.decodeFromString(databaseValue)
        }

        override fun encode(value: List<StatusTagEntity>): String {
            return json.encodeToString(value)
        }
    }
}

fun getStatusUrlPreviewCardAdapter(json: Json): ColumnAdapter<UrlPreviewCardEntity, String> {
    return object : ColumnAdapter<UrlPreviewCardEntity, String> {
        override fun decode(databaseValue: String): UrlPreviewCardEntity {
            return json.decodeFromString(databaseValue)
        }

        override fun encode(value: UrlPreviewCardEntity): String {
            return json.encodeToString(value)
        }

    }
}

fun getStatusMediaAttachmentAdapter(json: Json): ColumnAdapter<List<MediaAttachmentEntity>, String> {
    return object : ColumnAdapter<List<MediaAttachmentEntity>, String> {
        override fun decode(databaseValue: String): List<MediaAttachmentEntity> {
            return json.decodeFromString(databaseValue)
        }

        override fun encode(value: List<MediaAttachmentEntity>): String {
            return json.encodeToString(value)
        }

    }
}
