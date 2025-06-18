package hr.ferit.tomislavcelic.gamecompanion.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class GameEvent(
    val title: String = "",
    val gameKey: String = "",
    @ServerTimestamp
    val expires:  Timestamp?  = null
)
