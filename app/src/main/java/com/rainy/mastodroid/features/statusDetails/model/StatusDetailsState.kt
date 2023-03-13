package com.rainy.mastodroid.features.statusDetails.model

sealed class StatusDetailsState {
    object Loading: StatusDetailsState()
    data class Ready(val statusInContext: StatusInContextItemModel): StatusDetailsState()
}
