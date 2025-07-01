package hr.ferit.tomislavcelic.gamecompanion.ui.screens.events

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.room.Delete
import hr.ferit.tomislavcelic.gamecompanion.ui.components.Countdown
import hr.ferit.tomislavcelic.gamecompanion.ui.time.formatForUi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    nav: NavHostController,
    eventId: String
) {
    val vm: EventDetailViewModel = viewModel(factory = EventDetailVMFactory(eventId))
    val event by vm.event.collectAsState()
    val progress by vm.progress.collectAsState()

    var askDelete by remember { mutableStateOf(false) }
    var showProgressDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val title = if (event?.isChallenge == true) "Challenge" else "Event"
                    Text(title)
                },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                actions = {
                    IconButton(
                        onClick = { askDelete = true },
                        enabled = event != null
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            )
        }
    ) { padding ->

        if (event == null) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
            return@Scaffold
        }

        val e = event!!

        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            val ctx  = LocalContext.current
            val icon = remember(e.gameKey) {
                val id = ctx.resources.getIdentifier(
                    "ic_game_${e.gameKey}", "drawable", ctx.packageName
                )
                if (id != 0) id
                else hr.ferit.tomislavcelic.gamecompanion.R.drawable.ic_game_default
            }

            Box(
                Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(icon),
                    contentScale = ContentScale.Fit,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                )
            }

            HorizontalDivider()

            ListItem(
                headlineContent = { Text(e.title, style = MaterialTheme.typography.titleLarge) },
                supportingContent = {
                    Column {
                        Text("Expires ${e.expires.formatForUi()}")
                        Countdown(e.expires)
                    }
                }
            )

            if (e.additionalInfo.isNotBlank()) {
                ListItem(
                    headlineContent = { Text("Notes") },
                    supportingContent = { Text(e.additionalInfo) }
                )
            }

            if (e.isChallenge) {
                HorizontalDivider()
                Text(
                    text = "Challenge",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 4.dp)
                )

                if (e.challengeInfo.isNotBlank()) {
                    ListItem(
                        headlineContent = { Text("Challenge notes") },
                        supportingContent = { Text(e.challengeInfo) }
                    )
                }

                val progress = e.currentProgress.coerceAtMost(e.challengeGoal)
                val fraction = if (e.challengeGoal == 0) 0f
                else progress / e.challengeGoal.toFloat()

                ListItem(
                    headlineContent = {
                        Text(
                            if (e.solved)
                                "Completed!"
                            else
                                "$progress / ${e.challengeGoal}"
                        )
                    },
                    supportingContent = {
                        LinearProgressIndicator(
                            progress = { fraction },
                        )
                    },
                    trailingContent = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (e.solved) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.padding(end = 4.dp)
                                )
                            }
                            IconButton(onClick = { showProgressDialog = true }) {
                                Icon(Icons.Default.Edit, contentDescription = "Update progress")
                            }
                        }
                    }
                )
            }
        }

        if (askDelete) {
            AlertDialog(
                onDismissRequest = { askDelete = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            vm.delete(
                                onDone  = { nav.popBackStack() },
                                onError = { /* TODO: Snackbar */ }
                            )
                            askDelete = false
                        }
                    ) { Text("Delete") }
                },
                dismissButton = {
                    TextButton(onClick = { askDelete = false }) { Text("Cancel") }
                },
                title = { Text("Delete event?") },
                text = { Text("This action cannot be undone.") }
            )
        }

        if (showProgressDialog) {
            var newValueText by remember(progress) {
                mutableStateOf(progress?.toString() ?: "")
            }

            AlertDialog(
                onDismissRequest = { showProgressDialog = false },
                confirmButton = {
                    TextButton(
                        enabled = newValueText.toIntOrNull() != null,
                        onClick = {
                            newValueText.toIntOrNull()?.let { vm.setProgress(it) }
                            showProgressDialog = false
                        }
                    ) { Text("Save") }
                },
                dismissButton = {
                    TextButton(onClick = { showProgressDialog = false }) {
                        Text("Cancel")
                    }
                },
                title = { Text("Set progress") },
                text = {
                    OutlinedTextField(
                        value = newValueText,
                        onValueChange = { newValueText = it },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        label = { Text("Current progress") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            )
        }
    }
}

