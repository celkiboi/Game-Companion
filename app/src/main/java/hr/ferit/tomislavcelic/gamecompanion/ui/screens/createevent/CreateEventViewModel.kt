package hr.ferit.tomislavcelic.gamecompanion.ui.screens.createevent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import hr.ferit.tomislavcelic.gamecompanion.data.model.Game
import hr.ferit.tomislavcelic.gamecompanion.data.model.GameEvent
import hr.ferit.tomislavcelic.gamecompanion.data.repository.AuthRepository
import hr.ferit.tomislavcelic.gamecompanion.data.repository.EventRepository
import hr.ferit.tomislavcelic.gamecompanion.data.repository.GamesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CreateEventViewModel(
    private val preselectKey: String?,
    private val initialIsChallenge: Boolean,
    authRepo: AuthRepository = AuthRepository(),
    private val eventRepo: EventRepository = EventRepository(),
    private val gamesRepo: GamesRepository = GamesRepository()
) : ViewModel() {

    private val _isChallenge = MutableStateFlow(initialIsChallenge)
    val isChallenge = _isChallenge.asStateFlow()

    val allGames: StateFlow<List<Game>> =
        gamesRepo.observeAllGames(authRepo.currentUser?.uid)
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val title = MutableStateFlow("")
    val selectedKey = MutableStateFlow(preselectKey ?: "")
    val starts = MutableStateFlow<Timestamp?>(null)
    val expires = MutableStateFlow<Timestamp?>(null)
    val additionalInfo = MutableStateFlow("")

    val goal = MutableStateFlow(0)
    val challengeInfo = MutableStateFlow("")

    private val uid = authRepo.currentUser?.uid

    init {
        viewModelScope.launch {
            allGames
                .filter { it.isNotEmpty() }
                .first()
                .let { games ->
                    if (selectedKey.value.isBlank()) {
                        selectedKey.value = games.first().key
                    }
                }
        }
    }

    private val baseEnabled: Flow<Boolean> = combine(
        title,
        selectedKey,
        expires,
        starts
    ) { title, key, expire, start ->
        title.isNotBlank() &&
                key.isNotBlank() &&
                expire != null &&
                (start == null || start < expire)
    }

    val saveEnabled: StateFlow<Boolean> = combine(baseEnabled, goal, isChallenge) { base, goal, challenge ->
        if (challenge) base && (goal > 0)
        else base
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = false
    )

    fun toggleChallenge() {
        _isChallenge.value = !_isChallenge.value
    }

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
                    isChallenge = isChallenge.value,
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