package hr.ferit.tomislavcelic.gamecompanion.notifications

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.compose.material3.ExperimentalMaterial3Api
import com.google.firebase.firestore.FirebaseFirestore
import hr.ferit.tomislavcelic.gamecompanion.R
import hr.ferit.tomislavcelic.gamecompanion.data.model.GameEvent
import java.util.Date
import androidx.core.net.toUri
import androidx.navigation.NavDeepLinkBuilder
import hr.ferit.tomislavcelic.gamecompanion.MainActivity
import java.time.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class NotificationReceiver : BroadcastReceiver() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onReceive(context: Context, intent: Intent) {

        val eventId = intent.getStringExtra("eventId") ?: return
        val fireAt = intent.getLongExtra("fireAt", 0L)
        val shift = intent.getLongExtra("shift", 0L).milliseconds
        val duration = shift.absoluteValue
        val isStart  = shift <= Duration.ZERO

        FirebaseFirestore.getInstance()
            .collection("users").document(intent.getStringExtra("uid")!!)
            .collection("events").document(eventId)
            .get()
            .addOnSuccessListener { snap ->
                val ev = snap.toObject(GameEvent::class.java) ?: return@addOnSuccessListener

                var title = if (ev.isChallenge) "Challenge " else "Event "
                title += "alert"
                var body = if (isStart) "${ev.title} starts" else "${ev.title} ends"
                body += if (duration.inWholeDays.toInt() == 1) " in 1 day"
                        else if (duration.inWholeHours.toInt() == 1) " in 1 hour"
                        else if (duration.inWholeMinutes.toInt() > 0) " in ${duration.inWholeMinutes.toInt()} minutes"
                        else " now"


                val clickUri = "gc://event/$eventId".toUri()
                val clickIntent = Intent(context, MainActivity::class.java).apply {
                    action = Intent.ACTION_VIEW
                    data = clickUri
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_SINGLE_TOP
                }

                val clickPending = PendingIntent.getActivity(
                    context,
                    ev.id.hashCode(),
                    clickIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val posted = context.postNotificationSafely(eventId.hashCode()) {
                    setContentTitle(title)
                    setContentText(body)
                    setSmallIcon(R.drawable.ic_game_default)
                    setAutoCancel(true)
                    setContentIntent(clickPending)
                }
            }
    }
}
