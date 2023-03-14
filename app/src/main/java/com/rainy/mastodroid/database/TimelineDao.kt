/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.rainy.mastodroid.core.data.model.entity.status.StatusEntity
import com.rainy.mastodroid.core.data.model.entity.status.StatusInTimeline
import com.rainy.mastodroid.core.data.model.entity.status.TimelineElementEntity

@Dao
interface TimelineDao {

    @Upsert
    suspend fun insertAllStatuses(statuses: List<StatusEntity>)

    @Upsert
    suspend fun insertAllElements(elements: List<TimelineElementEntity>)

    @Query("DELETE FROM StatusEntity")
    suspend fun removeAllStatuses()

    @Query("DELETE FROM TimelineElementEntity")
    suspend fun removeAllElements()

    @Transaction
    @Query("SELECT * FROM TimelineElementEntity ORDER BY id ASC")
    fun getTimelinePaging(): PagingSource<Int, StatusInTimeline>

    @Update
    suspend fun updateStatus(statusEntity: StatusEntity)

    @Query("SELECT * FROM StatusEntity WHERE originalId = :id")
    suspend fun getTimelineStatusById(id: String): StatusEntity?

    @Query("SELECT * FROM TimelineElementEntity ORDER BY LENGTH(timelineStatusId) ASC, timelineStatusId ASC LIMIT 1")
    suspend fun getLastTimelineElement(): TimelineElementEntity?

    @Query("UPDATE StatusEntity SET favourited = 1, favouritesCount = favouritesCount + 1 WHERE originalId = :originalId")
    suspend fun setFavourite(originalId: String)

    @Query("UPDATE StatusEntity SET favourited = 0, favouritesCount = favouritesCount - 1 WHERE originalId = :originalId")
    suspend fun unFavourite(originalId: String)

    @Query("UPDATE StatusEntity SET reblogged = 1, reblogsCount = reblogsCount + 1 WHERE originalId = :originalId")
    suspend fun setRebloged(originalId: String)

    @Query("UPDATE StatusEntity SET reblogged = 0, reblogsCount = reblogsCount - 1 WHERE originalId = :originalId")
    suspend fun unReblog(originalId: String)

    @Transaction
    suspend fun replaceStatuses(statuses: List<StatusEntity>, timelineEntities: List<TimelineElementEntity>) {
        removeAllElements()
        insertAllElements(timelineEntities)
        insertAllStatuses(statuses)
    }
}