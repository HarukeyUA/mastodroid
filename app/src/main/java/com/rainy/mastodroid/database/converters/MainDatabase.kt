package com.rainy.mastodroid.database.converters

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rainy.mastodroid.core.data.model.entity.LocalUserEntity

@Database(
    entities = [LocalUserEntity::class],
    exportSchema = false,
    version = 1
)
@TypeConverters(DbDateTimeConverter::class)
abstract class MainDatabase : RoomDatabase() {
    abstract fun getUserDao(): LocalUserDao
}
