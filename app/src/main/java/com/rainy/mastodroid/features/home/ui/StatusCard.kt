package com.rainy.mastodroid.features.home.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@Composable
fun StatusCard(
    fullAccountName: String,
    accountUserName: String,
    accountAvatarUrl: String,
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
                    .padding(start = 4.dp)
            ) {
                Text(
                    text = fullAccountName,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = stringResource(id = R.string.username_handle, accountUserName),
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

        }

        content()
    }
}

@Composable
@ColorSchemePreview
private fun StatusCardPreview() {
    MastodroidTheme {
        StatusCard(
            fullAccountName = "Historias mori!",
            accountUserName = "Ecce, emeritis gabalium!",
            accountAvatarUrl = "",
            content = {}
        )
    }
}
