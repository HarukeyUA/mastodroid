package com.rainy.mastodroid.core.domain.model.status.statusThread

import com.rainy.mastodroid.core.domain.model.status.Status

data class StatusNode(
    var parent: StatusNode? = null,
    val children: MutableList<StatusNode> = mutableListOf(),
    val content: Status
)
