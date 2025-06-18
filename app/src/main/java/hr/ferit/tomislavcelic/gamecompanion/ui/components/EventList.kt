package hr.ferit.tomislavcelic.gamecompanion.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import hr.ferit.tomislavcelic.gamecompanion.data.model.GameEvent
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.navigation.NavHostController
import hr.ferit.tomislavcelic.gamecompanion.ui.time.formatForUi

@Composable
fun EventList(
    events: List<GameEvent>,
    nav: NavHostController,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues()
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding
    ) {
        items(
            items = events,
            key   = { it.id }
        ) { e ->
            ListItem(
                headlineContent = { Text(e.title) },

                supportingContent = {
                    Column {
                        if (e.hasStarted) {
                            Text("Expires ${e.expires.formatForUi()}")
                            Countdown(e.expires)
                        } else {
                            Text("Starts  ${e.starts .formatForUi()}")
                            Countdown(e.starts)
                        }
                    }
                },

                trailingContent = {
                    Icon(
                        imageVector =
                            if (e.isChallenge)
                                Icons.Filled.EmojiEvents
                            else
                                Icons.Filled.CalendarMonth,
                        contentDescription = null
                    )
                },

                modifier = Modifier.clickable {
                    if (e.id.isNotBlank()) nav.navigate("event/${e.id}")
                }
            )
            HorizontalDivider()
        }
    }
}
