package hr.ferit.tomislavcelic.gamecompanion.ui.screens.games

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import hr.ferit.tomislavcelic.gamecompanion.data.model.Game
import hr.ferit.tomislavcelic.gamecompanion.ui.components.AppDrawerContent
import hr.ferit.tomislavcelic.gamecompanion.ui.screens.login.AuthViewModel
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamesScreen(
    nav: NavHostController,
    authVM: AuthViewModel = viewModel(),
    gamesVM: GamesViewModel = viewModel()
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val games by gamesVM.games.collectAsState()

    ModalNavigationDrawer(
        drawerState   = drawerState,
        drawerContent = { AppDrawerContent(drawerState, authVM, nav) }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Games") },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    if (drawerState.isOpen) drawerState.close()
                                    else drawerState.open()
                                }
                            }
                        ) { Icon(Icons.Default.Menu, contentDescription = "Menu") }
                    }
                )
            }
        ) { padding ->
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = padding,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                items(items = games) { g ->
                    GameTile(game = g, nav)
                }
            }
        }
    }
}