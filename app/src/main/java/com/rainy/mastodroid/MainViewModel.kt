package com.rainy.mastodroid

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rainy.mastodroid.core.domain.interactor.AuthInteractor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val authInteractor: AuthInteractor
) : ViewModel() {

    private val isLoggedInMutableFlow = MutableStateFlow<Boolean?>(null)
    val isLoggedInFlow = isLoggedInMutableFlow.asStateFlow()

    init {
        isLoggedIn()
    }

    private fun isLoggedIn() {
        viewModelScope.launch {
            isLoggedInMutableFlow.value = authInteractor.isLoggedIn()
        }
    }
}
