package com.rainy.mastodroid.core.domain.model.user

import com.rainy.mastodroid.core.data.model.response.Visibility
import com.rainy.mastodroid.core.data.model.response.user.AccountFieldResponse
import com.rainy.mastodroid.core.data.model.response.user.AccountSourceResponse

data class UserSource(
    val fields: List<UserField>,
    val followRequestsCount: Int,
    val language: String,
    val note: String,
    val privacy: Visibility,
    val sensitive: Boolean
)

fun AccountSourceResponse.toDomain(): UserSource {
    return UserSource(
        fields = fields?.map(AccountFieldResponse::toDomain) ?: listOf(),
        followRequestsCount = followRequestsCount ?: 0,
        language = language ?: "",
        note = note ?: "",
        privacy = privacy ?: Visibility.PUBLIC,
        sensitive = sensitive ?: true
    )
}
