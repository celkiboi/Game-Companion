package hr.ferit.tomislavcelic.gamecompanion.ui.screens.main

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import hr.ferit.tomislavcelic.gamecompanion.model.GameEvent
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf


class EventViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _events = mutableStateOf<List<GameEvent>>(emptyList())
    val events: State<List<GameEvent>> = _events

    init {
        db.collection("events")
            .get()
            .addOnSuccessListener { result ->
                _events.value = result.mapNotNull { it.toObject(GameEvent::class.java) }
            }
    }
}
