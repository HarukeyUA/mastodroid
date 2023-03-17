/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.core.data.model.entity

import androidx.room.Embedded
import androidx.room.Relation
import com.rainy.mastodroid.core.data.model.entity.status.STATUS_ENTITY_ORIGINAL_ID
import com.rainy.mastodroid.core.data.model.entity.status.StatusEntity

data class StatusInTimeline(
    @Embedded
    val timelineElementEntity: TimelineElementEntity,
    @Relation(
        parentColumn = TIMELINE_ELEMENT_STATUS_ID,
        entityColumn = STATUS_ENTITY_ORIGINAL_ID
    )
    val statusEntity: StatusEntity
)
