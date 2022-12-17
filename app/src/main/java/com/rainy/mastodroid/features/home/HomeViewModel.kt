package com.rainy.mastodroid.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import com.rainy.mastodroid.core.domain.interactor.HomeTimelineInteractor
import com.rainy.mastodroid.core.domain.model.status.Status
import com.rainy.mastodroid.core.navigation.RouteNavigator
import com.rainy.mastodroid.features.home.model.toStatusListItemModel
import kotlinx.coroutines.flow.map

class HomeViewModel(
    private val homeTimelineInteractor: HomeTimelineInteractor,
    routeNavigator: RouteNavigator
) : ViewModel(), RouteNavigator by routeNavigator {

    val homeStatusesFlow = homeTimelineInteractor.timeLinePagingFlow
        .map { statusPagingData ->
            statusPagingData.map(Status::toStatusListItemModel)
        }
        .cachedIn(viewModelScope)

}
