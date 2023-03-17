/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.core.data.model.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.rainy.mastodroid.core.data.model.entity.status.StatusAccountEntity

const val TIMELINE_STATUS_ID = "timelineStatusId"
const val TIMELINE_ELEMENT_STATUS_ID = "statusId"

@Entity(
    indices = [Index(TIMELINE_STATUS_ID)]
)
data class TimelineElementEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = TIMELINE_ELEMENT_STATUS_ID)
    val statusId: String,
    @ColumnInfo(name = TIMELINE_STATUS_ID)
    val timelineStatusId: String,
    @Embedded
    val reblogInfo: TimelineElementReblogInfoEntity? = null
) {
    data class TimelineElementReblogInfoEntity(
        val reblogAuthor: StatusAccountEntity,
        val reblogId: String
    )
}
