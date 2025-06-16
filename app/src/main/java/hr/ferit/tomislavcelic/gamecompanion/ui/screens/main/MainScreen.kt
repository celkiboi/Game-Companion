package hr.ferit.tomislavcelic.gamecompanion.ui.screens.main

import androidx.compose.runtime.Composable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun MainScreen(viewModel: EventViewModel = viewModel()) {
    val events by viewModel.events

    LazyColumn {
        items(events) { event ->
            Text("Game: ${event.game} - ${event.title} (Expires: ${event.expires})")
        }
    }
}
