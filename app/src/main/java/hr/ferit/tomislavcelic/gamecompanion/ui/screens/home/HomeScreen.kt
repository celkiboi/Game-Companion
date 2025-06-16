package hr.ferit.tomislavcelic.gamecompanion.ui.screens.home

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import hr.ferit.tomislavcelic.gamecompanion.ui.screens.login.AuthViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@ExperimentalMaterial3Api
@Composable
fun HomeScreen(
    eventVM: EventViewModel = viewModel(),
    authVM: AuthViewModel = viewModel()
) {
    val events by eventVM.events
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Events") },
                actions = { IconButton(onClick = authVM::logout) { Icon(Icons.Default.ExitToApp, null) } }
            )
        }
    ) { padding ->
        LazyColumn(contentPadding = padding) {
            items(events) { e ->
                ListItem(
                    headlineContent = { Text(e.title) },
                    supportingContent = { Text("expires ${e.expires}") }
                )
                HorizontalDivider()
            }
        }
    }
}
