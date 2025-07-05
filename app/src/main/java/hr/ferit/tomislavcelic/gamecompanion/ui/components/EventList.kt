package hr.ferit.tomislavcelic.gamecompanion.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import hr.ferit.tomislavcelic.gamecompanion.ui.time.formatForUi

@Composable
fun EventList(
    events: List<GameEvent>,
    nav: NavHostController,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    showSearch: Boolean = false
) {
    var query by rememberSaveable { mutableStateOf("") }

    val visibleEvents by remember(events, query, showSearch) {
        derivedStateOf {
            if (query.isBlank() || !showSearch)
                events
            else
                events.filter {
                    it.title.contains(query, ignoreCase = true)
                }
        }
    }

    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding
    ) {
        if (showSearch) {
            item {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null)
                    },
                    placeholder = { Text("Search...") },
                    singleLine = true,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .fillMaxWidth()
                )
            }
        }

        items(
            items = visibleEvents,
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
