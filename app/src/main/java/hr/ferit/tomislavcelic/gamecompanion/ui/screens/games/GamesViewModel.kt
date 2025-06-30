package hr.ferit.tomislavcelic.gamecompanion.ui.screens.games

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import hr.ferit.tomislavcelic.gamecompanion.data.model.Game
import hr.ferit.tomislavcelic.gamecompanion.data.repository.GamesRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GamesViewModel(
    private val repo: GamesRepository = GamesRepository(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    val games: StateFlow<List<Game>> =
        repo.observeAllGames(auth.currentUser?.uid)
            .stateIn(
                viewModelScope,
                SharingStarted.Eagerly,
                emptyList()
            )
}