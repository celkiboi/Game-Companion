package hr.ferit.tomislavcelic.gamecompanion.core.nav

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import hr.ferit.tomislavcelic.gamecompanion.ui.screens.games.GamesScreen
import hr.ferit.tomislavcelic.gamecompanion.ui.screens.home.HomeScreen
import hr.ferit.tomislavcelic.gamecompanion.ui.screens.login.LoginScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument
import hr.ferit.tomislavcelic.gamecompanion.ui.screens.events.EventDetailScreen
import hr.ferit.tomislavcelic.gamecompanion.ui.screens.games.GameDetailScreen


@ExperimentalMaterial3Api
@Composable
fun GCNavHost(startOn: Boolean, nav: NavHostController) {
    NavHost(nav, startDestination = if (startOn) "home" else "login") {
        composable("login") { LoginScreen() }
        composable("games")   { GamesScreen(nav) }
        composable("home")  { HomeScreen(nav) }

        composable(
            route = "game/{key}/{name}",
            arguments = listOf(
                navArgument("key")  { type = NavType.StringType },
                navArgument("name") { type = NavType.StringType }
            )
        ) { backStack ->
            val key = backStack.arguments?.getString("key")!!
            val name = backStack.arguments?.getString("name")!!
            GameDetailScreen(nav = nav, gameKey = key, gameName = name)
        }

        composable(
            route = "event/{id}",
            arguments = listOf(
                navArgument("id") { type = NavType.StringType }
            )
        ) { backStack ->
            val id = backStack.arguments?.getString("id")!!
            EventDetailScreen(nav = nav, eventId = id)
        }
    }
}
