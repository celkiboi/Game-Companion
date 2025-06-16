package hr.ferit.tomislavcelic.gamecompanion.ui.screens.home

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import hr.ferit.tomislavcelic.gamecompanion.ui.screens.login.AuthViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import hr.ferit.tomislavcelic.gamecompanion.ui.components.AppDrawerContent
import kotlinx.coroutines.launch

@ExperimentalMaterial3Api
@Composable
fun HomeScreen(
    nav: NavHostController,
    eventVM: EventViewModel = viewModel(),
    authVM:  AuthViewModel  = viewModel()
) {
    val events by eventVM.events
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawerContent(drawerState, authVM, nav)
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Home") },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    if (drawerState.isOpen) drawerState.close()
                                    else drawerState.open()
                                }
                            }
                        ) { Icon(Icons.Default.Menu, contentDescription = "Menu") }
                    },
                    actions = { }
                )
            }
        ) { padding ->
            LazyColumn(contentPadding = padding) {
                items(events) { e ->
                    ListItem(
                        headlineContent    = { Text(e.title) },
                        supportingContent  = { Text("expires ${e.expires}") }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

