/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.core.domain.model.user

import com.rainy.mastodroid.core.data.model.response.user.AccountRelationshipResponse

data class AccountRelationships(
    val blockedBy: Boolean,
    val blocking: Boolean,
    val domainBlocking: Boolean,
    val endorsed: Boolean,
    val followedBy: Boolean,
    val following: Boolean,
    val accountId: String,
    val muting: Boolean,
    val mutingNotifications: Boolean,
    val notifying: Boolean,
    val requested: Boolean,
    val showingReblogs: Boolean,
    val note: String
)

fun AccountRelationshipResponse.toDomain(): AccountRelationships {
    return AccountRelationships(
        blockedBy = blockedBy ?: false,
        blocking = blocking ?: false,
        domainBlocking = domainBlocking ?: false,
        endorsed = endorsed ?: false,
        followedBy = followedBy ?: false,
        following = following ?: false,
        accountId = accountId ?: "",
        muting = muting ?: false,
        mutingNotifications = mutingNotifications ?: false,
        notifying = notifying ?: false,
        requested = requested ?: false,
        showingReblogs = showingReblogs ?: false,
        note = note ?: ""
    )
}
