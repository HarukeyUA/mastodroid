/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.network.interceptors

import com.rainy.mastodroid.network.MastodonPublicApi.Companion.HOST_HEADER
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.net.HttpURLConnection

class InstanceHostInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val instanceHost = request.headers[HOST_HEADER]

        return if (instanceHost.isNullOrEmpty()) {
            Response.Builder()
                .code(HttpURLConnection.HTTP_BAD_REQUEST)
                .message("Instance host is required")
                .protocol(Protocol.HTTP_2)
                .body("".toResponseBody("text/plain".toMediaType()))
                .request(chain.request())
                .build()
        } else {
            val builder: Request.Builder = request.newBuilder()
            builder.url(swapHost(request.url, instanceHost))
            chain.proceed(builder.build())
        }
    }

    private fun swapHost(url: HttpUrl, host: String): HttpUrl {
        return url.newBuilder().host(host).build()
    }

}