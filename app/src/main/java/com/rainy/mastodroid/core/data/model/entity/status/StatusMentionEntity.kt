/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.core.data.model.entity.status

import com.rainy.mastodroid.core.domain.model.status.StatusMention
import kotlinx.serialization.Serializable

@Serializable
data class StatusMentionEntity(
    val id: String,
    val username: String,
    val url: String,
    val accountUri: String
)

fun StatusMention.toStatusMentionEntity(): StatusMentionEntity {
    return StatusMentionEntity(
        id = id,
        username = username,
        url = url,
        accountUri = accountUri
    )
}