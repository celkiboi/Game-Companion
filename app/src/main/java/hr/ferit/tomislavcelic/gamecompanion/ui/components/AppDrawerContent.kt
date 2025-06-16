package hr.ferit.tomislavcelic.gamecompanion.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import hr.ferit.tomislavcelic.gamecompanion.ui.screens.login.AuthViewModel
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue

@Composable
fun AppDrawerContent(
    drawerState: DrawerState,
    authVM: AuthViewModel,
    onGotoHome: () -> Unit = {},
    onGotoAllEvents: () -> Unit = {},
    onGotoGames: () -> Unit = {},
    onGotoSettings: () -> Unit = {},
    onUsername: () -> Unit = { authVM.logout() }
) {
    val scope = rememberCoroutineScope()
    val username   by authVM.displayName.collectAsState()

    fun closeAnd(action: () -> Unit) = scope.launch {
        drawerState.close(); action()
    }

    ModalDrawerSheet {
        Text(
            "Game Companion",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp)
        )

        /* nav items */
        NavigationDrawerItem(
            label = { Text("Home") },
            selected = false,
            onClick = { closeAnd(onGotoHome) }
        )
        NavigationDrawerItem(
            label = { Text("All events") },
            selected = false,
            onClick  = { closeAnd(onGotoAllEvents) }
        )
        NavigationDrawerItem(
            label = { Text("Games") },
            selected = false,
            onClick  = { closeAnd(onGotoGames) }
        )

        Spacer(Modifier.weight(1f))

        NavigationDrawerItem(
            label = { Text("Settings") },
            selected = false,
            onClick = { closeAnd(onGotoSettings) }
        )

        HorizontalDivider()
        NavigationDrawerItem(
            label = { Text("Signed in as $username") },
            selected = false,
            onClick = onUsername
        )
    }
}