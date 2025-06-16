package hr.ferit.tomislavcelic.gamecompanion.ui.screens.games

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import hr.ferit.tomislavcelic.gamecompanion.data.model.GameEvent
import hr.ferit.tomislavcelic.gamecompanion.data.repository.EventRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.collect

class GameEventsViewModel(
    private val gameKey: String,
    private val repo: EventRepository = EventRepository(),
    auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _events = MutableStateFlow<List<GameEvent>>(emptyList())
    val events: StateFlow<List<GameEvent>> = _events

    init {
        val uid = auth.currentUser?.uid

        if (uid != null) {
            viewModelScope.launch {
                repo.observeUserEvents(uid)
                    .map { list -> list.filter { it.gameKey.lowercase() == gameKey } }
                    .collect { filtered -> _events.value = filtered }
            }
        }
    }
}