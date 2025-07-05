package hr.ferit.tomislavcelic.gamecompanion.ui.screens.allevents

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import hr.ferit.tomislavcelic.gamecompanion.ui.components.AppDrawerContent
import hr.ferit.tomislavcelic.gamecompanion.ui.components.EventList
import hr.ferit.tomislavcelic.gamecompanion.ui.filter.FilterMenu
import hr.ferit.tomislavcelic.gamecompanion.ui.screens.login.AuthViewModel
import hr.ferit.tomislavcelic.gamecompanion.ui.sort.SortMenu
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllEventsScreen(
    nav: NavHostController,
    authVM: AuthViewModel = viewModel(),
    allViewModel: AllEventsViewModel = viewModel()
) {
    val events by allViewModel.events.collectAsState()
    val sortOpt by allViewModel.sort.collectAsState()

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var showSearch by rememberSaveable { mutableStateOf(false) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = { AppDrawerContent(drawerState, authVM, nav) }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("All events") },
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
                    actions = {
                        IconToggleButton(
                            checked = showSearch,
                            onCheckedChange = {showSearch = !showSearch}
                        ) { Icon(Icons.Default.Search, contentDescription = "Search toggle") }

                        IconButton(
                            onClick = { nav.navigate("createEvent?isChallenge=false") }
                        ) { Icon(Icons.Default.Add, contentDescription = "New") }

                        SortMenu(
                            current = sortOpt,
                            onPick = allViewModel::setSort,
                            progressEnabled = false
                        )

                        FilterMenu(
                            currentTime = allViewModel.timeFilter.collectAsState().value,
                            currentCompletion = allViewModel.completionFilter.collectAsState().value,
                            onPickTime = allViewModel::setTimeFilter,
                            onPickCompletion = null
                        )
                    }
                )
            }
        ) { padding ->
            EventList(
                events = events,
                nav = nav,
                contentPadding = padding,
                showSearch = showSearch
            )
        }
    }
}