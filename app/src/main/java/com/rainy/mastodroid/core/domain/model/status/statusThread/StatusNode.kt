/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.core.domain.model.status.statusThread

import com.rainy.mastodroid.core.domain.model.status.Status

data class StatusNode(
    var parent: StatusNode? = null,
    val children: MutableList<StatusNode> = mutableListOf(),
    val content: Status
)
