package hr.ferit.tomislavcelic.gamecompanion.ui.screens.createevent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import hr.ferit.tomislavcelic.gamecompanion.data.model.Game
import hr.ferit.tomislavcelic.gamecompanion.data.model.GameEvent
import hr.ferit.tomislavcelic.gamecompanion.data.repository.AuthRepository
import hr.ferit.tomislavcelic.gamecompanion.data.repository.EventRepository
import hr.ferit.tomislavcelic.gamecompanion.data.repository.GamesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CreateEventViewModel(
    private val preselectKey: String?,
    val isChallenge: Boolean,
    authRepo: AuthRepository = AuthRepository(),
    private val eventRepo: EventRepository = EventRepository(),
    private val gamesRepo: GamesRepository = GamesRepository()
) : ViewModel() {

    val allGames: StateFlow<List<Game>> =
        gamesRepo.observeAllGames(authRepo.currentUser?.uid)
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val title = MutableStateFlow("")
    val selectedKey = MutableStateFlow(preselectKey ?: "")
    val starts = MutableStateFlow<Timestamp?>(null)
    val expires = MutableStateFlow<Timestamp?>(null)
    val additionalInfo = MutableStateFlow("")

    val goal = MutableStateFlow(0)
    val challengeInfo   = MutableStateFlow("")

    private val uid = authRepo.currentUser?.uid

    val saveEnabled = combine(
        title, selectedKey, expires, starts, goal
    ) { t, k, exp, sta, g ->
        t.isNotBlank() && k.isNotBlank() && exp != null &&
                (sta == null || sta < exp) &&
                (!isChallenge || g > 0)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun save(onDone: (String) -> Unit, onError: (Throwable) -> Unit) {
        val uid = uid ?: return
        viewModelScope.launch {
            try {
                val event = GameEvent(
                    title = title.value.trim(),
                    gameKey = selectedKey.value,
                    additionalInfo = additionalInfo.value,
                    starts = starts.value,
                    expires = expires.value,
                    isChallenge = isChallenge,
                    currentProgress = 0,
                    challengeGoal = goal.value,
                    challengeInfo = challengeInfo.value
                )
                val id = eventRepo.addEvent(uid, event)
                onDone(id)
            } catch (e: Throwable) { onError(e) }
        }
    }
}