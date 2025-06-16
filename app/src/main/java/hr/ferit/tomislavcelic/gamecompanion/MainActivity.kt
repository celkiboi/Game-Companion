package hr.ferit.tomislavcelic.gamecompanion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.material3.MaterialTheme
import com.google.firebase.FirebaseApp
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import hr.ferit.tomislavcelic.gamecompanion.ui.screens.main.EventViewModel
import hr.ferit.tomislavcelic.gamecompanion.ui.screens.main.MainScreen

class MainActivity : ComponentActivity() {

    private val eventViewModel: EventViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                MainScreen(viewModel = eventViewModel)
            }
        }
    }
}
