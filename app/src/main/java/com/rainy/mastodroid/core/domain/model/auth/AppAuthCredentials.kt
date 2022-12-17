package com.rainy.mastodroid.core.domain.model.auth

import com.rainy.mastodroid.core.data.model.response.AuthAppResponse

data class AppAuthCredentials(
    val clientId: String,
    val clientSecret: String
)

fun AuthAppResponse.toDomain(): AppAuthCredentials {
    return AppAuthCredentials(
        clientId = clientId,
        clientSecret = clientSecret
    )
}
