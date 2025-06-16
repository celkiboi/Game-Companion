package hr.ferit.tomislavcelic.gamecompanion.ui.screens.games

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class GameEventsVMFactory(private val key: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        GameEventsViewModel(key) as T
}
