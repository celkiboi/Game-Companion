package hr.ferit.tomislavcelic.gamecompanion.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp

data class GameEvent(
    @get:Exclude
    var id: String = "",
    val title: String = "",
    val gameKey: String = "",
    val additionalInfo: String = "",

    @ServerTimestamp
    val starts: Timestamp? = null,
    @ServerTimestamp
    val expires:  Timestamp? = null,

    @get:PropertyName("isChallenge")
    @set:PropertyName("isChallenge")
    var isChallenge: Boolean = false,
    var currentProgress: Int = 0,
    var challengeGoal: Int = 0,
    val challengeInfo: String = ""
) {
    val solved: Boolean
        get() = isChallenge && currentProgress >= challengeGoal

    val hasStarted: Boolean
        get() = starts == null ||
                starts.toDate().time <= System.currentTimeMillis()
}
