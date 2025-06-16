package hr.ferit.tomislavcelic.gamecompanion

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.google.firebase.FirebaseApp
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import hr.ferit.tomislavcelic.gamecompanion.ui.screens.main.EventViewModel
import hr.ferit.tomislavcelic.gamecompanion.ui.screens.main.MainScreen
import hr.ferit.tomislavcelic.gamecompanion.ui.theme.GameCompanionTheme

class MainActivity : ComponentActivity() {

    private val eventViewModel: EventViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            GameCompanionTheme {
                MainScreen(viewModel = eventViewModel)
            }
        }
    }
}
