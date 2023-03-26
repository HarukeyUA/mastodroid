/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.di

import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.rainy.mastodroid.Database
import com.rainy.mastodroid.database.MastodroidDatabase
import com.rainy.mastodroid.database.getAccountFieldsAdapter
import com.rainy.mastodroid.database.getCustomEmojisAdapter
import com.rainy.mastodroid.database.getStatusApplicationAdapter
import com.rainy.mastodroid.database.getStatusMediaAttachmentAdapter
import com.rainy.mastodroid.database.getStatusMentionsAdapter
import com.rainy.mastodroid.database.getStatusTagsAdapter
import com.rainy.mastodroid.database.getStatusUrlPreviewCardAdapter
import com.rainy.mastodroid.database.instantAdapter
import com.rainy.mastodroidDb.AccountEntity
import com.rainy.mastodroidDb.StatusContextEntity
import com.rainy.mastodroidDb.StatusEntity
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val dbModule = module {
    single {
        AndroidSqliteDriver(Database.Schema, androidApplication(), "mastodroid.db",
            factory = FrameworkSQLiteOpenHelperFactory(),
            callback = object : AndroidSqliteDriver.Callback(Database.Schema) {
                override fun onOpen(db: SupportSQLiteDatabase) {
                    super.onOpen(db)
                    setPragma(db, "foreign_keys = ON")
                    setPragma(db, "journal_mode = WAL")
                    setPragma(db, "synchronous = NORMAL")
                }

                private fun setPragma(db: SupportSQLiteDatabase, pragma: String) {
                    val cursor = db.query("PRAGMA $pragma")
                    cursor.moveToFirst()
                    cursor.close()
                }
            }
        )
    }

    single {
        val json = get<Json>()
        Database(
            get<AndroidSqliteDriver>(),
            accountEntityAdapter = AccountEntity.Adapter(
                createdAtAdapter = instantAdapter,
                customEmojisAdapter = getCustomEmojisAdapter(json),
                fieldsAdapter = getAccountFieldsAdapter(json)
            ),
            statusEntityAdapter = StatusEntity.Adapter(
                createdAtAdapter = instantAdapter,
                visibilityAdapter = EnumColumnAdapter(),
                editedAtAdapter = instantAdapter,
                applicationAdapter = getStatusApplicationAdapter(json),
                mentionsAdapter = getStatusMentionsAdapter(json),
                tagsAdapter = getStatusTagsAdapter(json),
                customEmojisAdapter = getCustomEmojisAdapter(json),
                urlPreviewCardAdapter = getStatusUrlPreviewCardAdapter(json),
                mediaAttachmentsAdapter = getStatusMediaAttachmentAdapter(json)
            ),
            statusContextEntityAdapter = StatusContextEntity.Adapter(
                contextStatusTypeAdapter = EnumColumnAdapter()
            )
        )
    }

    single {
        MastodroidDatabase(
            db = get<Database>(),
            driver = get<AndroidSqliteDriver>()
        )
    }
}
