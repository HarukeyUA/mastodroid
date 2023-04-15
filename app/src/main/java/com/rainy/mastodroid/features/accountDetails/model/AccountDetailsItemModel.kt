/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.features.accountDetails.model

import androidx.compose.runtime.Stable
import androidx.compose.ui.text.AnnotatedString
import com.rainy.mastodroid.core.domain.model.user.FeaturedTag
import com.rainy.mastodroid.ui.elements.statusListItem.model.CustomEmojiItemModel
import com.rainy.mastodroid.util.ImmutableWrap

@Stable
data class AccountDetailsItemModel(
    val id: String,
    val bannerUrl: String,
    val avatarUrl: String,
    val statusesCount: Long,
    val followingCount: Long,
    val followersCount: Long,
    val accountUri: String,
    val bio: AnnotatedString,
    val customEmojis: ImmutableWrap<List<CustomEmojiItemModel>>,
    val customEmojisCodes: ImmutableWrap<List<String>>,
    val displayName: AnnotatedString,
    val featuredTags: ImmutableWrap<List<FeaturedTag>> = ImmutableWrap(listOf())
)
