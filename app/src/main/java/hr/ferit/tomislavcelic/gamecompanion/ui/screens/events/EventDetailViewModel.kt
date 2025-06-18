package hr.ferit.tomislavcelic.gamecompanion.ui.screens.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import hr.ferit.tomislavcelic.gamecompanion.data.model.GameEvent
import hr.ferit.tomislavcelic.gamecompanion.data.repository.EventRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class EventDetailViewModel(
    private val eventId: String,
    private val repo: EventRepository = EventRepository(),
    auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _event = MutableStateFlow<GameEvent?>(null)
    val event: StateFlow<GameEvent?> = _event

    init {
        auth.currentUser?.uid?.let { uid ->
            viewModelScope.launch {
                repo.observeUserEvents(uid)
                    .map { list -> list.firstOrNull { it.id == eventId } }
                    .collect { single -> _event.value = single }
            }
        }
    }
}