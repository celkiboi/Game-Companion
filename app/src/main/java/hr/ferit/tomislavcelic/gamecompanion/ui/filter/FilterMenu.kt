package hr.ferit.tomislavcelic.gamecompanion.ui.filter

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.reflect.KFunction1

@Composable
fun FilterMenu(
    currentTime: TimeFilter,
    currentCompletion: CompletionFilter,
    onPickTime: (TimeFilter) -> Unit,
    onPickCompletion: ((CompletionFilter) -> Unit)?
) {
    var expanded by remember { mutableStateOf(false) }

    IconButton(onClick = { expanded = true }) {
        Icon(Icons.Default.FilterList, contentDescription = "Filter")
    }

    DropdownMenu(expanded, onDismissRequest = { expanded = false }) {
        if (onPickCompletion != null) {
            Text(
                "By time", style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }

        TimeFilter.entries.forEach { opt ->
            DropdownMenuItem(
                text = { Text(opt.label) },
                onClick = {
                    onPickTime(opt)
                    expanded = false
                },
                leadingIcon = {
                    if (opt == currentTime)
                        Icon(Icons.Default.Check, null)
                }
            )
        }

        HorizontalDivider()

        if (onPickCompletion != null) {
            Text("By completion", style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))

            CompletionFilter.entries.forEach { opt ->
                DropdownMenuItem(
                    text = { Text(opt.label) },
                    onClick = {
                        onPickCompletion(opt)
                        expanded = false
                    },
                    leadingIcon = {
                        if (opt == currentCompletion)
                            Icon(Icons.Default.Check, null)
                    }
                )
            }
        }
    }
}