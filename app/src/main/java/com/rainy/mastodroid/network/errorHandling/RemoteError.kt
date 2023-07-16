/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.network.errorHandling

import androidx.annotation.StringRes
import java.io.IOException

class RemoteHttpException(
    override val cause: Throwable?,
    val code: Int?,
    val remoteMessage: String?,
    val remoteDescription: String?,
    @StringRes val userMessage: Int
): IOException(cause)

class RemoteIOException(
    cause: Throwable?,
    @StringRes val userMessage: Int
): IOException(cause)