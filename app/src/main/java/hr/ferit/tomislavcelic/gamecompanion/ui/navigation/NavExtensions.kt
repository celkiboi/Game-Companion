package hr.ferit.tomislavcelic.gamecompanion.ui.navigation

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

fun NavHostController.navigateTopLevel(route: String) {
    navigate(route) {
        launchSingleTop = true

        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }

        restoreState = true
    }
}