package hr.ferit.tomislavcelic.gamecompanion.ui.time

import com.google.firebase.Timestamp
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val eventFormatter by lazy {
    DateTimeFormatter.ofPattern("d MMM yyyy  •  HH:mm", Locale.getDefault())
}

fun Timestamp?.formatForUi(): String =
    this?.toDate()
        ?.toInstant()
        ?.atZone(ZoneId.systemDefault())
        ?.format(eventFormatter)
        ?: "unknown"