package com.rainy.mastodroid.features.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.rainy.mastodroid.core.navigation.NavRoute
import com.rainy.mastodroid.features.login.ui.LoginScreen
import org.koin.androidx.compose.koinViewModel

object LoginRoute : NavRoute<LoginViewModel> {

    override val route: String
        get() = "login"

    @Composable
    override fun viewModel(): LoginViewModel = koinViewModel()

    @Composable
    override fun Content(viewModel: LoginViewModel) {
        val loginViewState by viewModel.loginStateFlow.collectAsState()
        LoginScreen(loginViewState, viewModel::onInstanceHostSubmit)
    }

}
