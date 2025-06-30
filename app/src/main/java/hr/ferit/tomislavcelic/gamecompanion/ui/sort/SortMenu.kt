package hr.ferit.tomislavcelic.gamecompanion.ui.sort

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun SortMenu(
    current: SortOption,
    onPick: (SortOption) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    IconButton(onClick = { expanded = true }) {
        Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = "Sort")
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        SortOption.entries.forEach { opt ->
            DropdownMenuItem(
                text = { Text(opt.label) },
                onClick = {
                    onPick(opt)
                    expanded = false
                },
                leadingIcon = {
                    if (opt == current)
                        Icon(Icons.Default.Check, contentDescription = null)
                }
            )
        }
    }
}
