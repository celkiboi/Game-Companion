package hr.ferit.tomislavcelic.gamecompanion.ui.screens.createeditevent

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import hr.ferit.tomislavcelic.gamecompanion.data.model.GameEvent
import hr.ferit.tomislavcelic.gamecompanion.data.repository.EventRepository

@Composable
fun EditEventScreen(
    nav: NavHostController,
    eventId: String
) {
    val repo = remember { EventRepository() }
    val auth = FirebaseAuth.getInstance()
    val evt  by produceState<GameEvent?>(initialValue = null) {
        repo.userEvents(auth.currentUser!!.uid)
            .document(eventId)
            .get()
            .addOnSuccessListener {
                value = it.toObject(GameEvent::class.java)?.apply { id = it.id }
            }
    }

    if (evt == null) {
        Box(
            Modifier.fillMaxSize(),
            Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    CreateOrEditEventScaffold(
        nav = nav,
        originalEvent = evt,
        presetGameKey = evt?.gameKey,
        isChallenge = evt?.isChallenge ?: false
    )
}