package com.rainy.mastodroid.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.rainy.mastodroid.core.data.model.entity.LocalUserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalUserDao {

    @Insert
    suspend fun insertUser(user: LocalUserEntity)

    @Delete
    suspend fun deleteUser(user: LocalUserEntity)

    @Query("SELECT * FROM LocalUserEntity LIMIT 1")
    suspend fun getUser(): LocalUserEntity?

    @Query("SELECT * FROM LocalUserEntity LIMIT 1")
    fun getUserFlow(): Flow<LocalUserEntity?>
}
