package hr.ferit.tomislavcelic.gamecompanion.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import hr.ferit.tomislavcelic.gamecompanion.data.model.GameEvent
import kotlinx.coroutines.tasks.await

class EventRepository {
    private val db = FirebaseFirestore.getInstance()

    fun userEvents(uid: String) = db.collection("users")
        .document(uid).collection("events")
}

suspend fun EventRepository.fetchEvents(uid: String): List<GameEvent> =
    userEvents(uid).get().await().map { it.toObject(GameEvent::class.java) }
