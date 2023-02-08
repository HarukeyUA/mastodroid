package com.rainy.mastodroid.core.domain.interactor

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.rainy.mastodroid.core.data.model.entity.status.StatusEntity
import com.rainy.mastodroid.core.domain.data.remote.TimelineLocalDataSource
import com.rainy.mastodroid.core.domain.data.remote.TimelineRemoteDataSource
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class HomeTimelinePagingSource(
    private val timelineRemoteDataSource: TimelineRemoteDataSource,
    private val timelineLocalDataSource: TimelineLocalDataSource
) : RemoteMediator<Int, StatusEntity>() {
    /*override val jumpingSupported: Boolean
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
                nextKey = statuses.last().originalId,
                prevKey = null
            )
        } catch (e: Throwable) {
            LoadResult.Error(
                e
            )
        }
    }*/
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, StatusEntity>
    ): MediatorResult {
        try {
            val loadKey = when (loadType) {
                LoadType.REFRESH -> null
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> state.lastItemOrNull()?.originalId
                    ?: return MediatorResult.Success(endOfPaginationReached = true)
            }

            val statuses = timelineRemoteDataSource.getHomeStatuses(
                olderThanId = loadKey,
                limit = state.config.pageSize
            )

            if (loadType == LoadType.REFRESH) {
                timelineLocalDataSource.replaceStatuses(statuses)
            } else {
                timelineLocalDataSource.insertStatuses(statuses)
            }

            return MediatorResult.Success(endOfPaginationReached = statuses.isEmpty())
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        } catch (e: HttpException) {
            return MediatorResult.Error(e)
        }
    }


}
