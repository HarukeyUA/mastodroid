package com.rainy.mastodroid.core.data.model.entity.status

import androidx.room.Embedded
import androidx.room.Relation

data class StatusInTimeline(
    @Embedded
    val timelineElementEntity: TimelineElementEntity,
    @Relation(
        parentColumn = TIMELINE_ELEMENT_STATUS_ID,
        entityColumn = STATUS_ENTITY_ORIGINAL_ID
    )
    val statusEntity: StatusEntity
)
