package com.rainy.mastodroid.core.domain.interactor

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.rainy.mastodroid.core.domain.data.remote.TimelineRemoteDataSource
import com.rainy.mastodroid.core.domain.model.status.Status
import kotlinx.coroutines.flow.Flow

interface HomeTimelineInteractor {
    val timeLinePagingFlow: Flow<PagingData<Status>>
}

class HomeTimelineInteractorImpl(private val timelineRemoteDataSource: TimelineRemoteDataSource) :
    HomeTimelineInteractor {

    override val timeLinePagingFlow = Pager(
        PagingConfig(
            pageSize = HOME_TIMELINE_LOAD_SIZE,
            initialLoadSize = HOME_TIMELINE_LOAD_SIZE,
            enablePlaceholders = false
        )
    ) {
        HomeTimelinePagingSource(timelineRemoteDataSource)
    }.flow

    companion object {
        private const val HOME_TIMELINE_LOAD_SIZE = 20
    }
}
