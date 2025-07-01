package hr.ferit.tomislavcelic.gamecompanion.ui.screens.createeditevent

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventScreen(
    nav: NavHostController,
    presetGameKey: String?,
    isChallenge:  Boolean
) {
    val viewModel: CreateEditEventViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                CreateEditEventViewModel(null, presetGameKey, isChallenge) as T
        }
    )

    CreateOrEditEventScaffold(
        nav,
        null,
        presetGameKey,
        isChallenge
    )
}