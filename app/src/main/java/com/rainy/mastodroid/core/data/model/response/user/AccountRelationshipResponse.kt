/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.core.data.model.response.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AccountRelationshipResponse(
    @SerialName("blocked_by")
    val blockedBy: Boolean?,
    @SerialName("blocking")
    val blocking: Boolean?,
    @SerialName("domain_blocking")
    val domainBlocking: Boolean?,
    @SerialName("endorsed")
    val endorsed: Boolean?,
    @SerialName("followed_by")
    val followedBy: Boolean?,
    @SerialName("following")
    val following: Boolean?,
    @SerialName("id")
    val accountId: String?,
    @SerialName("muting")
    val muting: Boolean?,
    @SerialName("muting_notifications")
    val mutingNotifications: Boolean?,
    @SerialName("notifying")
    val notifying: Boolean?,
    @SerialName("requested")
    val requested: Boolean?,
    @SerialName("showing_reblogs")
    val showingReblogs: Boolean?,
    @SerialName("note")
    val note: String?
)