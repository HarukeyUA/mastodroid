package com.rainy.mastodroid.core.data.model.entity.status

import com.rainy.mastodroid.core.domain.model.user.UserField
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class StatusAccountUserFieldEntity(
    val name: String,
    val value: String,
    val verifiedAt: Instant?
)

fun UserField.toStatusAccountUserFieldEntity(): StatusAccountUserFieldEntity {
    return StatusAccountUserFieldEntity(
        name = name,
        value = value,
        verifiedAt = verifiedAt
    )
}
