package hr.ferit.tomislavcelic.gamecompanion.ui.screens.createeditevent

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import hr.ferit.tomislavcelic.gamecompanion.data.model.Game
import hr.ferit.tomislavcelic.gamecompanion.data.repository.GamesRepository.Companion.nameFromKey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamePicker(
    allGames: List<Game>,
    selectedKey: String,
    onPick: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = nameFromKey(selectedKey, allGames),
            onValueChange = {},
            label = { Text("Game") },
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            allGames.forEach { g ->
                DropdownMenuItem(
                    text = { Text(g.name) },
                    onClick = {
                        onPick(g.key)
                        expanded = false
                    }
                )
            }
        }
    }
}