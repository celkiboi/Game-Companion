package hr.ferit.tomislavcelic.gamecompanion.ui.screens.createeditevent

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

class CreateEditEventViewModel(
    private val original: GameEvent?,
    private val preselectKey: String?,
    private val initialIsChallenge: Boolean,
    authRepo: AuthRepository = AuthRepository(),
    private val eventRepo: EventRepository = EventRepository(),
    private val gamesRepo: GamesRepository = GamesRepository()
) : ViewModel() {

    val allGames: StateFlow<List<Game>> =
        gamesRepo.observeAllGames(authRepo.currentUser?.uid)
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val title = MutableStateFlow(original?.title ?: "")
    val selectedKey = MutableStateFlow(original?.gameKey ?: preselectKey ?: "")
    val starts = MutableStateFlow(original?.starts )
    val expires = MutableStateFlow(original?.expires )
    val additionalInfo = MutableStateFlow(original?.additionalInfo ?: "")

    val goal = MutableStateFlow(original?.challengeGoal ?: 0)
    val progress = MutableStateFlow(original?.currentProgress ?: 0)
    val challengeInfo = MutableStateFlow(original?.challengeInfo ?: "")

    private val _isChallenge = MutableStateFlow(initialIsChallenge || (original?.isChallenge ?: false))
    val isChallenge = _isChallenge.asStateFlow()

    private val uid = authRepo.currentUser?.uid

    private val isEdit = original != null

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
                    id = original?.id ?: "",
                    title = title.value.trim(),
                    gameKey = selectedKey.value,
                    additionalInfo = additionalInfo.value,
                    starts = starts.value,
                    expires = expires.value,
                    isChallenge = isChallenge.value,
                    currentProgress = progress.value,
                    challengeGoal = goal.value,
                    challengeInfo = challengeInfo.value
                )
                if (isEdit)
                    eventRepo.updateEvent(uid, event)
                else
                    eventRepo.addEvent(uid, event)

                onDone(event.id)
            } catch (e: Throwable) { onError(e) }
        }
    }

    fun applyOcrResult(text: String) {
        val textLines = text.split('\n')
        val textLinesCount = textLines.count()

        if (textLinesCount >= 1) {
            title.value = textLines[0]
        }

        val challengeRegex = Regex("""(?i)(?:challenge|goal|progress|score)?[^\dO]*(\d+|O)\s*/\s*(\d+)""")
        if (textLinesCount >= 2) {
            additionalInfo.value = ""
            textLines.drop(1).forEach { line ->
                val regexResult = challengeRegex.find(line)
                if (regexResult != null) {
                    _isChallenge.value = true
                    goal.value  = regexResult.value
                        .split("/")
                        .getOrNull(1)
                        ?.trim()
                        ?.toIntOrNull()
                        ?: 0
                }
                else
                    additionalInfo.value += "${line}\n"
            }
        }
    }
}