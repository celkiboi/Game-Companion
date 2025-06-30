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
        val reg = userEvents(uid).addSnapshotListener { snap, err ->
            if (err != null) {
                close(err); return@addSnapshotListener
            }
            val list = snap?.documents?.mapNotNull { doc ->
                doc.toObject(GameEvent::class.java)?.apply {
                    id = doc.id
                }
            } ?: emptyList()
            trySend(list)
        }
        awaitClose { reg.remove() }
    }

    fun observeUserChallenges(uid: String): Flow<List<GameEvent>> = callbackFlow {
        val reg = userEvents(uid)
            .whereEqualTo("isChallenge", true)
            .addSnapshotListener { snap, err ->
                if (err != null) {
                    close(err); return@addSnapshotListener
                }
                val list = snap?.documents?.mapNotNull { doc ->
                    doc.toObject(GameEvent::class.java)?.apply { id = doc.id }
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { reg.remove() }
    }
}
