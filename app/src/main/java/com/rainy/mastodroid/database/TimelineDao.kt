package com.rainy.mastodroid.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.rainy.mastodroid.core.data.model.entity.status.StatusEntity

@Dao
interface TimelineDao {

    @Upsert
    suspend fun insertAll(statuses: List<StatusEntity>)

    @Query("DELETE FROM StatusEntity")
    suspend fun removeAll()

    @Query("SELECT * FROM StatusEntity ORDER BY LENGTH(originalId) DESC, originalId DESC")
    fun getTimelinePaging(): PagingSource<Int, StatusEntity>

    @Update
    suspend fun updateStatus(statusEntity: StatusEntity)

    @Query("SELECT * FROM StatusEntity WHERE originalId = :id")
    suspend fun getTimelineStatusById(id: String): StatusEntity?

    @Transaction
    suspend fun replaceStatuses(statuses: List<StatusEntity>) {
        removeAll()
        insertAll(statuses)
    }
}