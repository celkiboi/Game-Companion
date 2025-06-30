package hr.ferit.tomislavcelic.gamecompanion.data.repository

import hr.ferit.tomislavcelic.gamecompanion.data.model.Game

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import hr.ferit.tomislavcelic.gamecompanion.ui.screens.games.GamesViewModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class GamesRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    fun observeAllGames(uid: String?): Flow<List<Game>> =
        if (uid == null) {
            flowOf(hardcodedGames)
        } else {
            observeUserGames(uid).map { remote ->
                mergeAndSort(remote)
            }
        }

    private fun observeUserGames(uid: String): Flow<List<Game>> = callbackFlow {
        val reg: ListenerRegistration = db.collection("users")
            .document(uid)
            .collection("games")
            .addSnapshotListener { snap, err ->
                if (err != null) {
                    close(err); return@addSnapshotListener
                }
                val list = snap?.documents
                    ?.mapNotNull { it.toObject(Game::class.java) }
                    ?: emptyList()
                trySend(list)
            }
        awaitClose { reg.remove() }
    }

    private fun mergeAndSort(remote: List<Game>) =
        (hardcodedGames + remote)
            .distinctBy { it.key }
            .sortedBy { it.name.lowercase() }

    companion object {
        val hardcodedGames = listOf(
            Game("Apex Legends", "apex"),
            Game("Counter-Strike 2", "cs"),
            Game("Destiny 2", "destiny"),
            Game("Fortnite", "fortnite"),
            Game("Grand Theft Auto V", "gtav"),
            Game("League of Legends", "lol"),
            Game("Overwatch 2", "overwatch"),
            Game("PUBG: Battlegrounds", "pubg"),
            Game("Rainbow Six Siege", "r6s"),
            Game("Valorant", "valorant"),
            Game("Warframe", "warframe"),
            Game("War Thunder", "warthunder"),
            Game("Call of Duty: Warzone II", "warzone"),
            Game("World of Warcraft", "wow")
        )

        private val hardcodedGamesMap: Map<String, String> =
            hardcodedGames.associate { it.key to it.name }

        fun nameFromKey(key: String, games: List<Game> = emptyList()): String =
            games.firstOrNull { it.key == key }?.name
                ?: key.uppercase()
    }
}
