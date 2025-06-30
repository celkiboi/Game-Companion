package hr.ferit.tomislavcelic.gamecompanion.core.nav

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideOut
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
import hr.ferit.tomislavcelic.gamecompanion.ui.screens.allevents.AllEventsScreen
import hr.ferit.tomislavcelic.gamecompanion.ui.screens.challenges.ChallengesScreen
import hr.ferit.tomislavcelic.gamecompanion.ui.screens.events.EventDetailScreen
import hr.ferit.tomislavcelic.gamecompanion.ui.screens.games.GameDetailScreen
import hr.ferit.tomislavcelic.gamecompanion.ui.screens.createevent.CreateEventScreen

@ExperimentalMaterial3Api
@Composable
fun GCNavHost(startOn: Boolean, nav: NavHostController) {
    NavHost(nav, startDestination = if (startOn) "home" else "login") {
        composable("login") { LoginScreen() }
        composable("games")   { GamesScreen(nav) }
        composable("home")  { HomeScreen(nav) }
        composable("challenges")  { ChallengesScreen(nav) }
        composable("allevents") { AllEventsScreen(nav = nav) }

        composable(
            route = "game/{key}/{name}",
            arguments = listOf(
                navArgument("key")  { type = NavType.StringType },
                navArgument("name") { type = NavType.StringType }
            ),
            popExitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(500))
            },
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(500))
            },
            popEnterTransition = {
                null
            }
        ) { backStack ->
            val key = backStack.arguments?.getString("key")!!
            val name = backStack.arguments?.getString("name")!!
            GameDetailScreen(nav = nav, gameKey = key, gameName = name)
        }

        composable(
            route = "event/{id}",
            arguments = listOf(
                navArgument("id") { type = NavType.StringType }
            ),
            popExitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(500))
            },
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(500))
            }
        ) { backStack ->
            val id = backStack.arguments?.getString("id")!!
            EventDetailScreen(nav = nav, eventId = id)
        }

        composable(
            route =
                "createEvent?" +
                        "gameKey={gameKey}&" +
                        "isChallenge={isChallenge}",
            arguments = listOf(
                navArgument("gameKey") { type = NavType.StringType; nullable = true },
                navArgument("isChallenge") { type = NavType.BoolType;  defaultValue = false }
            ),
            popExitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, tween(500))
            },
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, tween(500))
            }
        ) { backStack ->
            val gameKey = backStack.arguments?.getString("gameKey")
            val isChallenge = backStack.arguments?.getBoolean("isChallenge") ?: false
            CreateEventScreen(nav, gameKey, isChallenge)
        }
    }
}
