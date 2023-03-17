/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.core.data.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

const val STATUS_CONTEXT_ENTITY_STATUS_ID = "statusId"
const val STATUS_CONTEXT_ENTITY_FOR_STATUS_ID = "contextForStatusId"

@Entity(primaryKeys = [STATUS_CONTEXT_ENTITY_STATUS_ID, STATUS_CONTEXT_ENTITY_FOR_STATUS_ID])
data class StatusContextEntity(
    @ColumnInfo(name = STATUS_CONTEXT_ENTITY_STATUS_ID)
    val statusId: String,
    @ColumnInfo(name = STATUS_CONTEXT_ENTITY_FOR_STATUS_ID)
    val contextForStatusId: String,
    val contextStatusType: ContextStatusType,
    val index: Int
) {
    enum class ContextStatusType { ANCESTOR, DESCENDANT }
}
