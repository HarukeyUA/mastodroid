/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.features.accountDetails

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.rainy.mastodroid.core.navigation.NavRoute
import com.rainy.mastodroid.extensions.observeWithLifecycle
import com.rainy.mastodroid.features.accountDetails.ui.AccountDetailsScreen
import org.koin.androidx.compose.koinViewModel

object AccountDetailsRoute : NavRoute<AccountDetailsViewModel> {
    const val ACCOUNT_ID_ARG = "account_id"

    override val route: String = "accountDetails/{${ACCOUNT_ID_ARG}}/"

    fun getRoute(accountId: String): String {
        return route.replace("{${ACCOUNT_ID_ARG}}", accountId)
    }

    override fun getArguments(): List<NamedNavArgument> {
        return listOf(
            navArgument(ACCOUNT_ID_ARG) { type = NavType.StringType }
        )
    }

    @Composable
    override fun viewModel(): AccountDetailsViewModel = koinViewModel()

    @Composable
    override fun Content(viewModel: AccountDetailsViewModel) {
        val accountDetails by viewModel.accountDetails.collectAsStateWithLifecycle()
        val relationships by viewModel.accountRelationships.collectAsStateWithLifecycle()
        val loading by viewModel.loadingState.collectAsStateWithLifecycle()
        val context = LocalContext.current

        viewModel.errorEventFlow.observeWithLifecycle {
            Toast.makeText(context, it.resolveText(context), Toast.LENGTH_SHORT).show()
        }

        AccountDetailsScreen(accountDetails, relationships, loading)
    }

}

