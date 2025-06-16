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
import androidx.navigation.NavHostController
import hr.ferit.tomislavcelic.gamecompanion.ui.navigation.navigateTopLevel

@Composable
fun AppDrawerContent(
    drawerState: DrawerState,
    authVM: AuthViewModel,
    nav: NavHostController,
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
            selected = nav.currentDestination?.route == "home",
            onClick = { closeAnd { nav.navigateTopLevel("home") } }
        )
        NavigationDrawerItem(
            label = { Text("All events") },
            selected = nav.currentDestination?.route == "allevents",
            onClick  = { closeAnd { } }
        )
        NavigationDrawerItem(
            label = { Text("Games") },
            selected = nav.currentDestination?.route == "games",
            onClick  = { closeAnd { nav.navigateTopLevel("games") } }
        )

        Spacer(Modifier.weight(1f))

        NavigationDrawerItem(
            label = { Text("Settings") },
            selected = nav.currentDestination?.route == "settings",
            onClick = { closeAnd { } }
        )

        HorizontalDivider()
        NavigationDrawerItem(
            label = { Text("Signed in as $username") },
            selected = false,
            onClick = onUsername
        )
    }
}