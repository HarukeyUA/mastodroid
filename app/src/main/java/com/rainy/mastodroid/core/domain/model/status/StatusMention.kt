package com.rainy.mastodroid.core.domain.model.status

import com.rainy.mastodroid.core.data.model.entity.status.StatusMentionEntity
import com.rainy.mastodroid.core.data.model.response.status.StatusMentionResponse

data class StatusMention(
    val id: String,
    val username: String,
    val url: String,
    val accountUri: String
)

fun StatusMentionResponse.toDomain(): StatusMention? {
    if (id.isNullOrEmpty()) {
        return null
    }

    return StatusMention(
        id = id,
        username = username ?: "",
        url = url ?: "",
        accountUri = accountUri ?: ""
    )
}

fun StatusMentionEntity.toDomain(): StatusMention {
    return StatusMention(
        id = id,
        username = username,
        url = url,
        accountUri = accountUri
    )
}
