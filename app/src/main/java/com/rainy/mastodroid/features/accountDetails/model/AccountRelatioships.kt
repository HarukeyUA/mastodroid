/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.features.accountDetails.model

sealed class AccountRelationshipsState {
    object Loading: AccountRelationshipsState()
    class Relationships(
        val blockedBy: Boolean,
        val followedBy: Boolean,
        val following: Boolean,
        val requested: Boolean
    ): AccountRelationshipsState()

    object Error: AccountRelationshipsState()
}
