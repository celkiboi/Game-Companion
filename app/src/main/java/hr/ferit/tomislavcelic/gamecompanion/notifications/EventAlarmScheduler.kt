package hr.ferit.tomislavcelic.gamecompanion.notifications

import android.app.ActivityOptions
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import hr.ferit.tomislavcelic.gamecompanion.data.model.GameEvent
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.app.PendingIntent
import android.os.Build
import kotlin.time.Duration
import android.provider.Settings
import androidx.core.content.ContextCompat.startActivity

private val MARKERS = listOf(
    (-24).hours,
    (-1).hours,
    Duration.ZERO,
    5.minutes,
    1.hours,
    24.hours
)

class EventAlarmScheduler(
    private val context: Context
) {

    private val alarmMgr = context.getSystemService(AlarmManager::class.java)

    fun start(uid: String, flow: Flow<List<GameEvent>>) {
        CoroutineScope(Dispatchers.Default).launch {
            flow.collect { events ->
                rescheduleAll(uid, events)
            }
        }
    }

    private fun rescheduleAll(uid: String, events: List<GameEvent>) {
        cancelAll(uid)

        val now = System.currentTimeMillis()

        events.forEach { ev ->
            val start = ev.starts?.toDate()?.time ?: return@forEach
            val end = ev.expires?.toDate()?.time ?: return@forEach

            MARKERS.forEach { delta ->
                val shift = delta.inWholeMilliseconds

                val at = if (delta <= Duration.ZERO) {
                    start + shift
                } else {
                    end - shift
                }

                if (at > now) {
                    scheduleOne(uid, ev.id, at, shift)
                }
            }
        }
    }

    private fun scheduleOne(uid: String, eventId: String, triggerAt: Long, shift: Long) {
        val requestCode = (eventId.hashCode() xor triggerAt.hashCode()) and 0x7FFFFFFF
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("uid", uid)
            putExtra("eventId", eventId)
            putExtra("fireAt", triggerAt)
            putExtra("shift", shift)
        }
        val pi = PendingIntent.getBroadcast(
            context, requestCode, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmMgr.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP, triggerAt, pi
        )
    }

    private fun tryScheduleExact(
        alarmMgr: AlarmManager,
        type: Int,
        triggerAt: Long,
        pi: PendingIntent
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmMgr.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                val options = ActivityOptions.makeBasic().toBundle()
                startActivity(context, intent, options)
                return
            }
        }

        try {
            alarmMgr.setExactAndAllowWhileIdle(type, triggerAt, pi)
        } catch (se: SecurityException) {
            // TODO: toast
        }
    }

    fun cancelAll(uid: String) {
        val dummy = Intent(context, NotificationReceiver::class.java)
        val pi = PendingIntent.getBroadcast(
            context, 0, dummy,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pi != null) {
            pi.cancel()
            alarmMgr.cancel(pi)
        }
    }
}