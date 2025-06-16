package hr.ferit.tomislavcelic.gamecompanion.core.nav

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import hr.ferit.tomislavcelic.gamecompanion.ui.screens.home.HomeScreen
import hr.ferit.tomislavcelic.gamecompanion.ui.screens.login.LoginScreen


@ExperimentalMaterial3Api
@Composable
fun GCNavHost(startOn: Boolean, nav: NavHostController) {
    NavHost(nav, startDestination = if (startOn) "home" else "login") {
        composable("login") { LoginScreen() }
        composable("home")  { HomeScreen() }
    }
}
