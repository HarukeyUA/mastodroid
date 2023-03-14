/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.core.domain.model.status.statusThread

import com.rainy.mastodroid.core.domain.model.status.Status

data class StatusThreadedContext(
    val ancestors: List<Status>,
    val descendants: List<StatusNode>
)
