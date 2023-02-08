package com.rainy.mastodroid.core.data.model.response.user

import com.rainy.mastodroid.core.data.model.response.StatusVisibility
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AccountSourceResponse(
    @SerialName("fields")
    val fields: List<AccountFieldResponse>?,
    @SerialName("follow_requests_count")
    val followRequestsCount: Int?,
    @SerialName("language")
    val language: String?,
    @SerialName("note")
    val note: String?,
    @SerialName("privacy")
    val privacy: StatusVisibility?,
    @SerialName("sensitive")
    val sensitive: Boolean?
)
