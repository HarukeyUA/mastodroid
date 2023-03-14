/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.network.interceptors

import com.rainy.mastodroid.core.domain.data.local.LocalUserLocalDataSource
import com.rainy.mastodroid.core.domain.model.user.LocalUserAuthInfo
import kotlinx.coroutines.runBlocking
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.net.HttpURLConnection

class AuthenticatedInstanceInterceptor(
    private val localUserLocalDataSourceImpl: LocalUserLocalDataSource
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val userAuthData = getCurrentUserAuthData()
        return if (userAuthData == null) {
            Response.Builder()
                .code(HttpURLConnection.HTTP_UNAUTHORIZED)
                .message("Couldn't acquire current user auth data")
                .protocol(Protocol.HTTP_2)
                .body("".toResponseBody("text/plain".toMediaType()))
                .request(chain.request())
                .build()
        } else {
            val builder: Request.Builder = request.newBuilder()
            builder.url(swapHost(request.url, userAuthData.instanceHost))
            builder.addHeader("Authorization", "Bearer ${userAuthData.authToken.trim()}")
            chain.proceed(builder.build()) // TODO: Handle HTTP_UNAUTHORIZED by removing user from DB or presenting user with re-auth dialog
        }
    }

    private fun swapHost(url: HttpUrl, host: String): HttpUrl {
        return url.newBuilder().host(host).build()
    }

    private fun getCurrentUserAuthData(): LocalUserAuthInfo? {
        return runBlocking {
            localUserLocalDataSourceImpl.getUserAuthInfo()
        }
    }

}
