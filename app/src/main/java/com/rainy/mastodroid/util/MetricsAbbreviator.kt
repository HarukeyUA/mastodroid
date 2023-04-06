/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.rainy.mastodroid.R
import java.text.DecimalFormat

private const val MILLION = 1_000_000
private const val THOUSAND = 1_000

@Composable
fun Int.abbreviateCount() = when {
    this >= MILLION -> {
        stringResource(
            id = R.string.count_millions,
            DecimalFormat("#.#").format(this / MILLION.toFloat())
        )
    }

    this >= THOUSAND -> {
        stringResource(
            id = R.string.count_thousands,
            DecimalFormat("#.#").format(this / THOUSAND.toFloat())
        )
    }

    else -> this.toString()
}

@Composable
fun Long.abbreviateCount() = when {
    this >= MILLION -> {
        stringResource(
            id = R.string.count_millions,
            DecimalFormat("#.#").format(this / MILLION.toFloat())
        )
    }

    this >= THOUSAND -> {
        stringResource(
            id = R.string.count_thousands,
            DecimalFormat("#.#").format(this / THOUSAND.toFloat())
        )
    }

    else -> this.toString()
}