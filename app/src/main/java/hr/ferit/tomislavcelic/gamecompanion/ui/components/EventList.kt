package hr.ferit.tomislavcelic.gamecompanion.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import hr.ferit.tomislavcelic.gamecompanion.data.model.GameEvent
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import hr.ferit.tomislavcelic.gamecompanion.ui.time.formatForUi


@Composable
fun EventList(
    events: List<GameEvent>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues()
) {
    LazyColumn(
        modifier        = modifier,
        contentPadding  = contentPadding
    ) {
        items(events, key = { it.gameKey }) { e ->
            ListItem(
                headlineContent   = { Text(e.title) },
                supportingContent = { Text("Expires ${e.expires?.formatForUi()}") }
            )
            HorizontalDivider()
        }
    }
}