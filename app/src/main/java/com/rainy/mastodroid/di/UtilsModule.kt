/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.di

import com.rainy.mastodroid.core.data.model.response.mediaAttachment.MediaAttachmentResponse
import com.rainy.mastodroid.core.data.model.response.mediaAttachment.UnknownAttachmentResponse
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.koin.dsl.module

@OptIn(ExperimentalSerializationApi::class)
val utilsModule = module {
    single {
        Json {
            coerceInputValues = true
            ignoreUnknownKeys = true
            explicitNulls = false
            serializersModule = SerializersModule {
                polymorphic(MediaAttachmentResponse::class) {
                    defaultDeserializer { UnknownAttachmentResponse.serializer() }
                }
            }
        }
    }
}
