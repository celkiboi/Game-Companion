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
    private val auth: FirebaseAuth    = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _games = MutableStateFlow<List<Game>>(hardcodedGames)
    val games: StateFlow<List<Game>> = _games

    init {
        val uid = auth.currentUser?.uid

        viewModelScope.launch {
            if (uid != null) {
                repo.observeUserGames(uid)
                    .map { remote ->
                        (hardcodedGames + remote)
                            .distinctBy { it.key }
                            .sortedBy { it.name.lowercase() }
                    }
                    .collect { merged -> _games.value = merged }
            }
            else {
                _games.value = hardcodedGames
            }
        }
    }

    companion object {
        val hardcodedGames = listOf(
            Game("Apex Legends", "apex"),
            Game("Counter-Strike 2", "cs"),
            Game("Destiny 2", "destiny"),
            Game("Fortnite", "fortnite"),
            Game("Grand Theft Auto V", "gtav"),
            Game("League of Legends", "lol"),
            Game("Overwatch 2", "overwatch"),
            Game("PUBG: Battlegrounds", "pubg"),
            Game("Rainbow Six Siege", "r6s"),
            Game("Valorant", "valorant"),
            Game("Warframe", "warframe"),
            Game("War Thunder", "warthunder"),
            Game("Call of Duty: Warzone II", "warzone"),
            Game("World of Warcraft", "wow")
        )

        val gameNameMap = GamesViewModel.hardcodedGames.associate { it.key to it.name }
    }
}