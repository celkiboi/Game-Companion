package hr.ferit.tomislavcelic.gamecompanion.ui.screens.home

import androidx.lifecycle.ViewModel
import hr.ferit.tomislavcelic.gamecompanion.data.model.GameEvent
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import hr.ferit.tomislavcelic.gamecompanion.data.repository.AuthRepository
import hr.ferit.tomislavcelic.gamecompanion.data.repository.EventRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class EventViewModel(
    private val authRepo:  AuthRepository  = AuthRepository(),
    private val eventRepo: EventRepository = EventRepository()
) : ViewModel() {

    private val _events = MutableStateFlow<List<GameEvent>>(emptyList())
    val events: StateFlow<List<GameEvent>> = _events

    init {
        authRepo.currentUser?.uid?.let { uid ->
            viewModelScope.launch {
                eventRepo.observeUserEvents(uid)
                    .collect { list -> _events.value = list }
            }
        }
    }

    fun refresh() {
        authRepo.currentUser?.uid?.let { uid ->
            viewModelScope.launch {
                _events.value = eventRepo.fetchEvents(uid)
            }
        }
    }
}

