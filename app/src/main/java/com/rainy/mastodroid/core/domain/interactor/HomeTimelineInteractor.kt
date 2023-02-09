package com.rainy.mastodroid.core.domain.interactor

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.rainy.mastodroid.core.data.model.entity.status.StatusEntity
import com.rainy.mastodroid.core.domain.data.remote.StatusRemoteDataSource
import com.rainy.mastodroid.core.domain.data.remote.TimelineLocalDataSource
import com.rainy.mastodroid.core.domain.data.remote.TimelineRemoteDataSource
import com.rainy.mastodroid.core.domain.model.status.Status
import com.rainy.mastodroid.core.domain.model.status.toDomain
import com.rainy.mastodroid.util.dispatchOrThrow
import com.rainy.mastodroid.util.onErrorValue
import com.rainy.mastodroid.util.wrapResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface HomeTimelineInteractor {
    suspend fun setFavoriteStatus(id: String, actionId: String, action: Boolean)
    fun getTimeLinePaging(scope: CoroutineScope): Flow<PagingData<Status>>
    suspend fun setReblogStatus(id: String, actionId: String, action: Boolean)
}

class HomeTimelineInteractorImpl(
    timelineRemoteDataSource: TimelineRemoteDataSource,
    private val timelineLocalDataSource: TimelineLocalDataSource,
    private val statusRemoteDataSource: StatusRemoteDataSource
) : HomeTimelineInteractor {

    @OptIn(ExperimentalPagingApi::class)
    private val rawPagingSource = Pager(
        config = PagingConfig(
            pageSize = HOME_TIMELINE_LOAD_SIZE,
            initialLoadSize = HOME_TIMELINE_LOAD_SIZE,
            enablePlaceholders = true, // Workaround for https://issuetracker.google.com/issues/235319241
        ),
        remoteMediator = HomeTimelineMediator(
            timelineRemoteDataSource,
            timelineLocalDataSource
        )
    ) {
        timelineLocalDataSource.getPagingSource()
    }.flow

    override fun getTimeLinePaging(scope: CoroutineScope) =
        rawPagingSource.map { it.map(StatusEntity::toDomain) }.cachedIn(scope)

    private fun calculateLocalStatCount(
        localFavoriteAction: Boolean?,
        stat: Int,
    ) = when (localFavoriteAction) {
        true -> {
            stat + 1
        }

        false -> {
            stat - 1
        }

        else -> {
            stat
        }
    }

    override suspend fun setFavoriteStatus(id: String, actionId: String, action: Boolean) {
        val localStatus = timelineLocalDataSource.getStatusById(id)
        localStatus?.also {
            timelineLocalDataSource.updateStatus(
                localStatus.copy(
                    favouritesCount = calculateLocalStatCount(
                        action,
                        localStatus.favouritesCount
                    ),
                    favourited = action
                )
            )
        }
        wrapResult {
            if (action) {
                statusRemoteDataSource.favoriteStatus(actionId)
            } else {
                statusRemoteDataSource.unfavoriteStatus(actionId)
            }
            return@wrapResult
        }.onErrorValue {
            localStatus?.also { timelineLocalDataSource.updateStatus(it) }
        }.dispatchOrThrow()
    }

    override suspend fun setReblogStatus(id: String, actionId: String, action: Boolean) {
        val localStatus = timelineLocalDataSource.getStatusById(id)
        localStatus?.also {
            timelineLocalDataSource.updateStatus(
                localStatus.copy(
                    reblogsCount = calculateLocalStatCount(
                        action,
                        localStatus.favouritesCount
                    ),
                    reblogged = action
                )
            )
        }
        wrapResult {
            if (action) {
                statusRemoteDataSource.reblogStatus(actionId)
            } else {
                statusRemoteDataSource.unreblogStatus(actionId)
            }
            return@wrapResult
        }.onErrorValue {
            localStatus?.also { timelineLocalDataSource.updateStatus(it) }
        }.dispatchOrThrow()
    }

    companion object {
        private const val HOME_TIMELINE_LOAD_SIZE = 40
    }
}
