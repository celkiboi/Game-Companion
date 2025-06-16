package hr.ferit.tomislavcelic.gamecompanion.ui.screens.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun MainScreen(viewModel: EventViewModel = viewModel()) {
    val events by viewModel.events

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn {
            items(events) { event ->
                Text(
                    text = "Game: ${event.game} - ${event.title} (Expires: ${event.expires})",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
