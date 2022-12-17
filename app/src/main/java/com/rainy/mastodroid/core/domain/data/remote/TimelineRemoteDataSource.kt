package com.rainy.mastodroid.core.domain.data.remote

import androidx.annotation.IntRange
import com.rainy.mastodroid.core.domain.model.status.Status

interface TimelineRemoteDataSource {
    suspend fun getHomeStatuses(
        olderThanId: String? = null,
        newerThanId: String? = null,
        @IntRange(1, 40) limit: Int = 20
    ): List<Status>
}
