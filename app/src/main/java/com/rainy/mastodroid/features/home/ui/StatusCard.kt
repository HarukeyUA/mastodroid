package com.rainy.mastodroid.features.home.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.rainy.mastodroid.R
import com.rainy.mastodroid.ui.theme.MastodroidTheme
import com.rainy.mastodroid.util.ColorSchemePreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.util.concurrent.TimeUnit

const val YEAR_IN_DAYS = 365

@Composable
fun StatusCard(
    fullAccountName: String,
    accountUserName: String,
    accountAvatarUrl: String,
    updatedTime: Instant?,
    isEdited: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {},
) {
    ElevatedCard(shape = RectangleShape, modifier = modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp)) {
            AsyncImage(
                model = accountAvatarUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(48.dp)
                    .clip(MaterialTheme.shapes.large)
            )
            Column(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .weight(1f)
                    .padding(start = 4.dp)
            ) {
                Row {
                    Text(
                        text = fullAccountName,
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .alignByBaseline()
                    )
                    updatedTime?.also {
                        Text(
                            "\u2022",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier
                                .padding(start = 2.dp)
                                .alignByBaseline()
                        )
                        StatusCardTimeCounter(
                            it,
                            modifier = Modifier
                                .padding(start = 2.dp)
                                .alignByBaseline()
                        )
                    }
                    if (isEdited) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = stringResource(id = R.string.edited),
                            modifier = Modifier
                                .size(14.dp)
                                .padding(start = 4.dp)
                                .align(Alignment.CenterVertically)
                        )
                    }

                }

                Text(
                    text = stringResource(id = R.string.username_handle, accountUserName),
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.Outlined.MoreVert,
                    contentDescription = null,
                    tint = LocalContentColor.current.copy(alpha = 0.5f)
                )
            }

        }

        content()
    }
}

@Composable
fun StatusCardTimeCounter(lastUpdated: Instant, modifier: Modifier = Modifier) {
    var timeSinceUpdate by remember {
        mutableStateOf(lastUpdated.minus(Clock.System.now()).absoluteValue)
    }
    LaunchedEffect(lastUpdated) {
        while (isActive) {
            timeSinceUpdate = lastUpdated.minus(Clock.System.now()).absoluteValue
            delay(
                if (timeSinceUpdate.inWholeMinutes > 0) {
                    TimeUnit.MINUTES.toMillis(1)
                } else {
                    1000L
                }
            )
        }
    }
    Text(
        text = when {
            timeSinceUpdate.inWholeDays > 0L -> {
                if (timeSinceUpdate.inWholeDays >= YEAR_IN_DAYS) {
                    stringResource(
                        id = R.string.years,
                        timeSinceUpdate.inWholeDays.div(YEAR_IN_DAYS)
                    )
                } else {
                    stringResource(id = R.string.days, timeSinceUpdate.inWholeDays)
                }
            }

            timeSinceUpdate.inWholeHours > 0L -> {
                stringResource(id = R.string.hours, timeSinceUpdate.inWholeHours)
            }

            timeSinceUpdate.inWholeMinutes > 0L -> {
                stringResource(id = R.string.minutes, timeSinceUpdate.inWholeMinutes)
            }

            else -> stringResource(id = R.string.seconds, timeSinceUpdate.inWholeSeconds)
        },
        style = MaterialTheme.typography.labelSmall,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
    )

}

@Composable
@ColorSchemePreview
private fun StatusCardPreview() {
    MastodroidTheme {
        StatusCard(
            fullAccountName = "Historias mori!",
            accountUserName = "Ecce, emeritis gabalium!",
            accountAvatarUrl = "",
            updatedTime = Instant.parse("2021-12-17T23:11:43.130Z"),
            isEdited = true,
            content = {}
        )
    }
}
