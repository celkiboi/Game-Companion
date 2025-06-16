package hr.ferit.tomislavcelic.gamecompanion.data.repository

import hr.ferit.tomislavcelic.gamecompanion.data.model.Game

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class GamesRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    fun observeUserGames(uid: String): Flow<List<Game>> = callbackFlow {
        val reg: ListenerRegistration = db.collection("users")
            .document(uid)
            .collection("games")
            .addSnapshotListener { snap, err ->
                if (err != null) {
                    close(err)
                    return@addSnapshotListener
                }
                val list = snap?.documents?.mapNotNull { it.toObject(Game::class.java) }
                    ?: emptyList()
                trySend(list)
            }
        awaitClose { reg.remove() }
    }
}
