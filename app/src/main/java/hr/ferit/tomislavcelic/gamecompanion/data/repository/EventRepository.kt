package hr.ferit.tomislavcelic.gamecompanion.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import hr.ferit.tomislavcelic.gamecompanion.data.model.GameEvent
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class EventRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    fun userEvents(uid: String) =
        db.collection("users").document(uid).collection("events")

    suspend fun fetchEvents(uid: String): List<GameEvent> =
        userEvents(uid).get().await().mapNotNull { it.toObject(GameEvent::class.java) }

    fun observeUserEvents(uid: String): Flow<List<GameEvent>> = callbackFlow {
        val reg: ListenerRegistration = userEvents(uid)
            .addSnapshotListener { snap, err ->
                if (err != null) {
                    close(err)
                    return@addSnapshotListener
                }
                val list = snap?.documents
                    ?.mapNotNull { it.toObject(GameEvent::class.java) }
                    ?: emptyList()
                trySend(list)
            }
        awaitClose { reg.remove() }
    }
}
