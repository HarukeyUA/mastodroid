/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.database.converters

import androidx.room.TypeConverter
import com.rainy.mastodroid.core.data.model.entity.status.MediaAttachmentEntity
import com.rainy.mastodroid.core.data.model.entity.status.StatusAccountEntity
import com.rainy.mastodroid.core.data.model.entity.status.StatusCustomEmojiEntity
import com.rainy.mastodroid.core.data.model.entity.status.StatusMentionEntity
import com.rainy.mastodroid.core.data.model.entity.status.StatusTagEntity
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DbStatusConverter: KoinComponent {

    private val json: Json by inject()

    @TypeConverter
    fun fromStatusAccountEntity(account: StatusAccountEntity): String {
        return json.encodeToString(account)
    }

    @TypeConverter
    fun toStatusAccountEntity(jsonString: String): StatusAccountEntity {
        return json.decodeFromString(jsonString)
    }

    @TypeConverter
    fun fromStatusMentionEntityList(mentions: List<StatusMentionEntity>): String {
        return json.encodeToString(mentions)
    }

    @TypeConverter
    fun toStatusMentionEntityList(jsonString: String): List<StatusMentionEntity> {
        return json.decodeFromString(jsonString)
    }

    @TypeConverter
    fun fromStatusTagEntityList(tags: List<StatusTagEntity>): String {
        return json.encodeToString(tags)
    }

    @TypeConverter
    fun toStatusTagEntityList(jsonString: String): List<StatusTagEntity> {
        return json.decodeFromString(jsonString)
    }

    @TypeConverter
    fun fromCustomEmojisList(emojis: List<StatusCustomEmojiEntity>): String {
        return json.encodeToString(emojis)
    }

    @TypeConverter
    fun toCustomEmojisList(jsonString: String): List<StatusCustomEmojiEntity> {
        return json.decodeFromString(jsonString)
    }

    @TypeConverter
    fun fromMediaAttachmentEntity(attachments: List<MediaAttachmentEntity>): String {
        return json.encodeToString(attachments)
    }

    @TypeConverter
    fun toMediaAttachmentEntity(jsonString: String): List<MediaAttachmentEntity> {
        return json.decodeFromString(jsonString)
    }


 }