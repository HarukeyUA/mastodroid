package com.rainy.mastodroid.core.data.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    @SerialName("error")
    val message: String?,
    @SerialName("error_description")
    val messageDescription: String?
)
