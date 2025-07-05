package hr.ferit.tomislavcelic.gamecompanion

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth

@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {

    private lateinit var nav: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            nav = rememberNavController()
            GameCompanionTheme {
                val authVM: AuthViewModel = viewModel()
                val nav = nav
                val user by authVM.user.collectAsState()

                LaunchedEffect(user) {
                    val dest = nav.currentDestination?.route ?: return@LaunchedEffect
                    if (dest == "login") {
                        nav.navigate(if (user == null) "login" else "home") {
                            popUpTo(nav.graph.startDestinationId) { inclusive = true }
                        }
                    }
                }

                GCNavHost(startOn = user != null, nav = nav)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        Log.d("DeepLink",
            "newIntent: ${intent.action}  uri=${intent.data}  extras=${intent.extras}")
        if (::nav.isInitialized) {
            nav.handleDeepLink(intent)
        }
    }
}

