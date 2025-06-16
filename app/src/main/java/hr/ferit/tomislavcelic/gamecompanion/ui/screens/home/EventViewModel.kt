package hr.ferit.tomislavcelic.gamecompanion.ui.screens.home

import androidx.lifecycle.ViewModel
import hr.ferit.tomislavcelic.gamecompanion.data.model.GameEvent
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import hr.ferit.tomislavcelic.gamecompanion.data.repository.AuthRepository
import hr.ferit.tomislavcelic.gamecompanion.data.repository.EventRepository
import hr.ferit.tomislavcelic.gamecompanion.data.repository.fetchEvents
import kotlinx.coroutines.launch


class EventViewModel(
    private val authRepo: AuthRepository = AuthRepository(),
    private val eventRepo: EventRepository = EventRepository()
) : ViewModel() {

    private val _events = mutableStateOf<List<GameEvent>>(emptyList())
    val events: State<List<GameEvent>> = _events

    init { refresh() }

    fun refresh() {
        authRepo.currentUser?.uid?.let { uid ->
            viewModelScope.launch {
                _events.value = eventRepo.fetchEvents(uid)
            }
        }
    }
}
