package hr.ferit.tomislavcelic.gamecompanion.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.graphics.Color
import com.google.firebase.Timestamp
import kotlinx.coroutines.delay
import androidx.compose.material3.Text
import java.util.concurrent.TimeUnit


@Composable
fun Countdown(expires: Timestamp?) {
    if (expires == null)
        return

    val targetMillis = expires.toDate().time

    val remaining by produceState(
        initialValue = targetMillis - System.currentTimeMillis(),
        key1 = targetMillis
    ) {
        while (true) {
            value = targetMillis - System.currentTimeMillis()
            delay(60_000)
        }
    }

    if (remaining <= 0) {
        Text("Expired", color = Color.Red)
    } else {
        Text("Time left: ${remaining.toReadable()}")
    }
}

private fun Long.toReadable(): String {
    var secs = TimeUnit.MILLISECONDS.toSeconds(this)
    val days  = secs / 86_400; secs %= 86_400
    val hrs   = secs / 3_600;  secs %= 3_600
    val mins  = secs / 60;     secs %= 60
    val sb = StringBuilder()
    if (days  > 0) sb.append("${days}d ")
    if (hrs   > 0 || days > 0) sb.append("${hrs}h ")
    sb.append(String.format("%02dm", mins))
    return sb.toString()
}