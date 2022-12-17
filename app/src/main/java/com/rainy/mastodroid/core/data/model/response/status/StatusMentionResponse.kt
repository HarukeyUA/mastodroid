package com.rainy.mastodroid.core.data.model.response.status

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StatusMentionResponse(
    @SerialName("id")
    val id: String?,
    @SerialName("username")
    val username: String?,
    @SerialName("url")
    val url: String?,
    @SerialName("acct")
    val accountUri: String?
)
