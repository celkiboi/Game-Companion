package hr.ferit.tomislavcelic.gamecompanion.ui.screens.games

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.ui.Alignment
import hr.ferit.tomislavcelic.gamecompanion.ui.components.EventList


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameDetailScreen(
    nav: NavHostController,
    gameKey:   String,
    gameName:  String
) {
    val vm: GameEventsViewModel = viewModel(factory = GameEventsVMFactory(gameKey))
    val events by vm.events.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(gameName) },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            val context = LocalContext.current
            val resId = remember(gameKey) {
                val id = context.resources.getIdentifier(
                    "ic_game_$gameKey", "drawable", context.packageName
                )
                if (id != 0) id
                else hr.ferit.tomislavcelic.gamecompanion.R.drawable.ic_game_default
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(resId),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    alignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                )
            }

            HorizontalDivider()
            Text(
                text = "Upcoming events:",
                style = typography.titleMedium,
                modifier = Modifier
                    .padding(start = 16.dp, top = 8.dp, bottom = 4.dp)
            )
            EventList(
                events = events,
                contentPadding = PaddingValues(0.dp),
                nav = nav
            )
        }
    }
}