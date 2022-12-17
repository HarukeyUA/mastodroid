package com.rainy.mastodroid.core.domain.interactor

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.rainy.mastodroid.core.domain.data.remote.TimelineRemoteDataSource
import com.rainy.mastodroid.core.domain.model.status.Status

// TODO: Migrate to db paging mediator
class HomeTimelinePagingSource(
    private val timelineRemoteDataSource: TimelineRemoteDataSource
) : PagingSource<String, Status>() {
    override val jumpingSupported: Boolean
        get() = super.jumpingSupported

    override fun getRefreshKey(state: PagingState<String, Status>): String? {
        return null
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, Status> {
        return try {
            val statuses = timelineRemoteDataSource.getHomeStatuses(
                olderThanId = params.key,
                limit = params.loadSize
            )
            LoadResult.Page(
                statuses,
                nextKey = statuses.last().id,
                prevKey = null
            )
        } catch (e: Throwable) {
            LoadResult.Error(
                e
            )
        }
    }


}
