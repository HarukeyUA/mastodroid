package com.rainy.mastodroid.core.data.model.response.user

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AccountFieldResponse(
    @SerialName("name")
    val name: String?,
    @SerialName("value")
    val value: String?,
    @SerialName("verified_at")
    val verifiedAt: Instant?
)
