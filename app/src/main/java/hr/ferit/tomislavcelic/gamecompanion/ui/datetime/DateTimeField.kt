package hr.ferit.tomislavcelic.gamecompanion.ui.datetime

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.wear.compose.material.ContentAlpha
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.compose.material3.OutlinedTextFieldDefaults


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimeField(
    label: String,
    timestamp: Timestamp?,
    onPick: (Timestamp) -> Unit,
    modifier: Modifier = Modifier,
    fmt: SimpleDateFormat = remember {
        SimpleDateFormat("dd MMM yyyy  HH:mm", Locale.getDefault())
    }
) {
    var showDate by remember { mutableStateOf(false) }
    var showTime by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<Long?>(null) }

    val display = timestamp?.toDate()?.let(fmt::format) ?: ""

    Box(
        modifier
            .fillMaxWidth()
            .clickable { showDate = true }
    ) {
        OutlinedTextField(
            value = display,
            onValueChange = {},
            label = { Text(label) },
            enabled = false,
            readOnly = true,
            singleLine = true,
            trailingIcon = { Icon(Icons.Default.CalendarMonth, null) },
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = LocalContentColor.current,
                disabledBorderColor = LocalContentColor.current.copy(alpha = ContentAlpha.disabled),
                disabledLabelColor = LocalContentColor.current.copy(alpha = ContentAlpha.medium),
                disabledTrailingIconColor = LocalContentColor.current
            ),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDate = true }
        )
    }

    if (showDate) {
        val dateState = rememberDatePickerState(
            initialSelectedDateMillis = timestamp?.toDate()?.time
        )

        DatePickerDialog(
            onDismissRequest = { showDate = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val millis = dateState.selectedDateMillis
                        if (millis != null) {
                            selectedDate = millis
                            showDate = false
                            showTime = true
                        } else showDate = false
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDate = false }) { Text("Cancel") }
            }
        ) { DatePicker(state = dateState) }
    }

    if (showTime && selectedDate != null) {
        val cal = remember { Calendar.getInstance() }
        cal.timeInMillis = selectedDate!!

        val timeState = rememberTimePickerState(
            initialHour = cal[Calendar.HOUR_OF_DAY],
            initialMinute = cal[Calendar.MINUTE],
            is24Hour = true
        )

        AlertDialog(
            onDismissRequest = { showTime = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        cal.set(Calendar.HOUR_OF_DAY, timeState.hour)
                        cal.set(Calendar.MINUTE, timeState.minute)
                        cal.set(Calendar.SECOND, 0)
                        cal.set(Calendar.MILLISECOND, 0)

                        onPick(Timestamp(cal.time))
                        showTime = false
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showTime = false }) { Text("Cancel") }
            },
            title = { Text("Pick time") },
            text  = { TimePicker(state = timeState) }
        )
    }
}