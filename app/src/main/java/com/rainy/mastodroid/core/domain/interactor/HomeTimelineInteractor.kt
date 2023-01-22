package com.rainy.mastodroid.core.domain.interactor

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.rainy.mastodroid.core.domain.data.remote.StatusRemoteDataSource
import com.rainy.mastodroid.core.domain.data.remote.TimelineRemoteDataSource
import com.rainy.mastodroid.core.domain.model.status.Status
import com.rainy.mastodroid.util.dispatchOrThrow
import com.rainy.mastodroid.util.onErrorValue
import com.rainy.mastodroid.util.wrapResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update

interface HomeTimelineInteractor {
    suspend fun setFavoriteStatus(id: String, action: Boolean)
    fun getTimeLinePaging(scope: CoroutineScope): Flow<PagingData<Status>>
    suspend fun setReblogStatus(id: String, action: Boolean)
}

class HomeTimelineInteractorImpl(
    private val timelineRemoteDataSource: TimelineRemoteDataSource,
    private val statusRemoteDataSource: StatusRemoteDataSource
) : HomeTimelineInteractor {

    private val localFavoriteStatuses = MutableStateFlow(mapOf<String, Boolean>())
    private val localReblogedStatuses = MutableStateFlow(mapOf<String, Boolean>())

    private val rawPagingSource = Pager(
        PagingConfig(
            pageSize = HOME_TIMELINE_LOAD_SIZE,
            initialLoadSize = HOME_TIMELINE_LOAD_SIZE,
            enablePlaceholders = false
        )
    ) {
        HomeTimelinePagingSource(timelineRemoteDataSource).apply {
            registerInvalidatedCallback {
                localFavoriteStatuses.value = mapOf()
                localReblogedStatuses.value = mapOf()
            }
        }
    }.flow

    override fun getTimeLinePaging(scope: CoroutineScope) = combine(
        rawPagingSource.cachedIn(scope),
        localFavoriteStatuses,
        localReblogedStatuses
    ) { timeline, localFavorited, localRebloged ->
        timeline.map { status ->
            val localReblogToggle = localRebloged[status.actionId]
            val localFavoriteAction = localFavorited[status.actionId]
            if (localReblogToggle != null || localFavoriteAction != null) {
                status.copy(
                    reblogged = localReblogToggle ?: status.reblogged,
                    favourited = localFavoriteAction ?: status.favourited,
                    favouritesCount = calculateLocalStatCount(
                        localFavoriteAction,
                        status.favouritesCount
                    ),
                    reblogsCount = calculateLocalStatCount(localReblogToggle, status.reblogsCount),
                )
            } else {
                status
            }
        }
    }

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

    override suspend fun setFavoriteStatus(id: String, action: Boolean) {
        localFavoriteStatuses.update {
            buildMap {
                putAll(it)
                if (it[id] == !action) {
                    remove(id)
                } else {
                    put(id, action)
                }
            }
        }
        wrapResult {
            if (action) {
                statusRemoteDataSource.favoriteStatus(id)
            } else {
                statusRemoteDataSource.unfavoriteStatus(id)
            }
            return@wrapResult
        }.onErrorValue {
            localFavoriteStatuses.update {
                buildMap {
                    putAll(it)
                    remove(id)
                }
            }
        }.dispatchOrThrow()
    }

    override suspend fun setReblogStatus(id: String, action: Boolean) {
        localReblogedStatuses.update {
            buildMap {
                putAll(it)
                if (it[id] == !action) {
                    remove(id)
                } else {
                    put(id, action)
                }
            }
        }
        wrapResult {
            if (action) {
                statusRemoteDataSource.reblogStatus(id)
            } else {
                statusRemoteDataSource.unreblogStatus(id)
            }
            return@wrapResult
        }.onErrorValue {
            localReblogedStatuses.update {
                buildMap {
                    putAll(it)
                    remove(id)
                }
            }
        }.dispatchOrThrow()
    }

    companion object {
        private const val HOME_TIMELINE_LOAD_SIZE = 20
    }
}
