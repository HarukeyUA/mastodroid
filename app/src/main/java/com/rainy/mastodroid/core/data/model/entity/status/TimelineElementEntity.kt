/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.core.data.model.entity.status

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

const val TIMELINE_ELEMENT_STATUS_ID = "statusId"

@Entity(
    indices = [Index(TIMELINE_ELEMENT_STATUS_ID, unique = true)]
)
data class TimelineElementEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = TIMELINE_ELEMENT_STATUS_ID)
    val statusId: String,
)
