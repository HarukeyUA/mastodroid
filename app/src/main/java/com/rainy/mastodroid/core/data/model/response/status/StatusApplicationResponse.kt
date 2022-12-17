package com.rainy.mastodroid.core.data.model.response.status


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StatusApplicationResponse(
    @SerialName("name")
    val name: String?,
    @SerialName("website") // Optional
    val website: String?
)
