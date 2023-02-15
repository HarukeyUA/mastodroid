package com.rainy.mastodroid.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rainy.mastodroid.core.data.model.entity.LocalUserEntity
import com.rainy.mastodroid.core.data.model.entity.status.StatusEntity
import com.rainy.mastodroid.core.data.model.entity.status.TimelineElementEntity
import com.rainy.mastodroid.database.converters.DbDateTimeConverter
import com.rainy.mastodroid.database.converters.DbStatusConverter

@Database(
    entities = [LocalUserEntity::class, StatusEntity::class, TimelineElementEntity::class],
    exportSchema = false,
    version = 1
)
@TypeConverters(DbDateTimeConverter::class, DbStatusConverter::class)
abstract class MainDatabase : RoomDatabase() {
    abstract fun getUserDao(): LocalUserDao
    abstract fun getTimelineDao(): TimelineDao
}
