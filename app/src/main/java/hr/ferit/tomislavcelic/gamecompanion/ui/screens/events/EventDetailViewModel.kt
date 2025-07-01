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

    private val uid = auth.currentUser?.uid

    private val _progress = MutableStateFlow<Int?>(null)
    val  progress  : StateFlow<Int?> = _progress

    init {
        auth.currentUser?.uid?.let { uid ->
            viewModelScope.launch {
                repo.observeUserEvents(uid)
                    .map { list -> list.firstOrNull { it.id == eventId } }
                    .collect { ev ->
                        _event.value = ev
                        _progress.value = ev?.currentProgress
                    }
            }
        }
    }

    fun delete(onDone: () -> Unit, onError: (Throwable) -> Unit) {
        val u = uid ?: return
        viewModelScope.launch {
            try {
                repo.deleteEvent(u, eventId)
                onDone()
            } catch (t: Throwable) { onError(t) }
        }
    }

    fun setProgress(value: Int) {
        val u = uid ?: return
        val safe = value.coerceAtLeast(0)
        _progress.value = safe

        viewModelScope.launch {
            try   { repo.updateProgress(u, eventId, safe) }
            catch (t: Throwable) { /* TODO snackbar */ }
        }
    }
}