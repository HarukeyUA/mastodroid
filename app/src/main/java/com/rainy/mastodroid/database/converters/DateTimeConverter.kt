package com.rainy.mastodroid.database.converters

import androidx.room.TypeConverter
import kotlinx.datetime.Instant

class DbDateTimeConverter {
    @TypeConverter
    fun fromInstant(instant: Instant): Long {
        return instant.toEpochMilliseconds()
    }

    @TypeConverter
    fun toInstant(epochMilliseconds: Long): Instant {
        return Instant.fromEpochMilliseconds(epochMilliseconds)
    }
}
