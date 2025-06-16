package hr.ferit.tomislavcelic.gamecompanion

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.google.firebase.FirebaseApp
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import hr.ferit.tomislavcelic.gamecompanion.core.nav.GCNavHost
import hr.ferit.tomislavcelic.gamecompanion.ui.screens.login.AuthViewModel
import hr.ferit.tomislavcelic.gamecompanion.ui.theme.GameCompanionTheme
import androidx.compose.runtime.getValue

@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        setContent {
            GameCompanionTheme {
                val authVM: AuthViewModel = viewModel()
                val nav     = rememberNavController()
                val user by authVM.user.collectAsState()

                // whenever auth state changes, move to right screen
                LaunchedEffect(user) {
                    nav.navigate(if (user == null) "login" else "home") {
                        popUpTo(nav.graph.startDestinationId) { inclusive = true }
                    }
                }

                GCNavHost(startOn = user != null, nav = nav)
            }
        }
    }
}

