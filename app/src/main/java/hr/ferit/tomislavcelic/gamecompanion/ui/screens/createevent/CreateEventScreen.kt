package hr.ferit.tomislavcelic.gamecompanion.ui.screens.createevent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import hr.ferit.tomislavcelic.gamecompanion.ui.datetime.DateTimeField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import hr.ferit.tomislavcelic.gamecompanion.data.repository.GamesRepository.Companion.nameFromKey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventScreen(
    nav: NavHostController,
    presetGameKey: String?,
    isChallenge:  Boolean
) {
    val viewModel: CreateEventViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                CreateEventViewModel(presetGameKey, isChallenge) as T
        }
    )

    val allGames by viewModel.allGames.collectAsState()
    val saveOK by viewModel.saveEnabled.collectAsState()
    val challenge by viewModel.isChallenge.collectAsState()

    Scaffold(
        topBar = {
            val title = if (challenge) "Create challenge" else "Create event"
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close"
                        )
                    }
                },
                actions = {
                    TextButton(
                        enabled = saveOK,
                        onClick = {
                            viewModel.save(
                                onDone = { nav.popBackStack() },
                                onError = { /* TODO: snackbar */ }
                            )
                        }
                    ) { Text("Save") }
                }
            )
        }
    ) { padding ->

        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            OutlinedTextField(
                value = viewModel.title.collectAsState().value,
                onValueChange = { viewModel.title.value = it },
                label = { Text("Title") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            GamePicker(
                allGames = allGames,
                selectedKey = viewModel.selectedKey.collectAsState().value,
                onPick = { viewModel.selectedKey.value = it }
            )

            DateTimeField(
                label = "Starts*",
                timestamp = viewModel.starts.collectAsState().value,
                onPick = { ts -> viewModel.starts.value = ts }
            )
            DateTimeField(
                label = "Expires*",
                timestamp = viewModel.expires.collectAsState().value,
                onPick = { ts -> viewModel.expires.value = ts }
            )

            OutlinedTextField(
                value = viewModel.additionalInfo.collectAsState().value,
                onValueChange = { viewModel.additionalInfo.value = it },
                label = { Text("Notes (optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            if (challenge) {
                OutlinedTextField(
                    value = viewModel.goal.collectAsState().value.toString(),
                    onValueChange = { viewModel.goal.value = it.toIntOrNull() ?: 0 },
                    label = { Text("Goal (e.g. 10 wins)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = viewModel.challengeInfo.collectAsState().value,
                    onValueChange = { viewModel.challengeInfo.value = it },
                    label = { Text("Challenge notes") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }

            val modeSwitchButtonText = if (!challenge) "Add a challenge instead" else "Add an event instead"
            TextButton(onClick = viewModel::toggleChallenge)
            { Text(modeSwitchButtonText) }

            Text(
                "* Expires is required.  Starts may be blank (starts immediately).",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}