/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.ui.elements.statusListItem

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.rainy.mastodroid.R
import com.rainy.mastodroid.ui.theme.MastodroidTheme
import com.rainy.mastodroid.util.ColorSchemePreviews

@Composable
fun SpoilerStatusContent(
    text: String,
    isExpanded: Boolean,
    onExpandClicked: () -> Unit,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    Crossfade(
        targetState = isExpanded,
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
    ) { expanded ->
        if (expanded) {
            content()
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.large)
                    .clickable(onClick = onExpandClicked)
            ) {
                Text(
                    text = text.ifEmpty { stringResource(R.string.sensitive_content_warning) },
                    style = MaterialTheme.typography.headlineSmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = stringResource(R.string.tap_to_reveal),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }

    }
}

@ColorSchemePreviews
@Composable
private fun SpoilerStatusContentPreview() {
    MastodroidTheme {
        ElevatedCard {
            SpoilerStatusContent(
                text = "Text spoiler warning",
                isExpanded = false,
                onExpandClicked = {},
                content = {}
            )
        }
    }
}