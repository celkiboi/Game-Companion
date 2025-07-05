package hr.ferit.tomislavcelic.gamecompanion.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

@RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
fun Context.showNotification(id: Int, build: NotificationCompat.Builder.() -> Unit) {
    val chanId = "events"
    NotificationChannel(
        chanId, "Events", NotificationManager.IMPORTANCE_DEFAULT
    ).also {
        getSystemService(NotificationManager::class.java).createNotificationChannel(it)
    }
    val notif = NotificationCompat.Builder(this, chanId).apply(build).build()
    NotificationManagerCompat.from(this).notify(id, notif)
}

fun Context.postNotificationSafely(id: Int, block: NotificationCompat.Builder.() -> Unit): Boolean {
    val needsPerm = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    val hasPerm = !needsPerm || ContextCompat.checkSelfPermission(
        this, Manifest.permission.POST_NOTIFICATIONS
    ) == PackageManager.PERMISSION_GRANTED

    if (!hasPerm)
        return false

    showNotification(id, block)
    return true
}
