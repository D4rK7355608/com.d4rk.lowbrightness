package com.d4rk.lowbrightness.app.brightness.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.d4rk.lowbrightness.R
import com.d4rk.lowbrightness.app.brightness.domain.services.SchedulerService
import com.d4rk.lowbrightness.app.brightness.domain.ext.fragmentActivity
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.coroutines.delay
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

@Composable
fun ScheduleCard() {
    val context = LocalContext.current

    var enabled by remember { mutableStateOf(SchedulerService.isEnabled(context)) }
    var startHour by remember { mutableIntStateOf(SchedulerService.getCalendarForStart(context).get(Calendar.HOUR_OF_DAY)) }
    var startMinute by remember { mutableIntStateOf(SchedulerService.getCalendarForStart(context).get(Calendar.MINUTE)) }
    var endHour by remember { mutableIntStateOf(SchedulerService.getCalendarForEnd(context).get(Calendar.HOUR_OF_DAY)) }
    var endMinute by remember { mutableIntStateOf(SchedulerService.getCalendarForEnd(context).get(Calendar.MINUTE)) }
    var remaining by remember { mutableStateOf("") }

    LaunchedEffect(enabled, startHour, startMinute, endHour, endMinute) {
        while (enabled) {
            val now = Calendar.getInstance()
            val start = SchedulerService.getCalendarForStart(context)
            val end = SchedulerService.getCalendarForEnd(context)
            remaining = if (now.timeInMillis > start.timeInMillis && now.timeInMillis < end.timeInMillis) {
                val diff = end.timeInMillis - now.timeInMillis
                val formatted = String.format(Locale.getDefault(), "%tT", diff - TimeZone.getDefault().rawOffset)
                context.getString(R.string.time_remaining_to_lighten_label) + ": " + formatted
            } else {
                val diff = if (now.timeInMillis < start.timeInMillis) {
                    start.timeInMillis - now.timeInMillis
                } else {
                    start.add(Calendar.DATE, 1)
                    start.timeInMillis - now.timeInMillis
                }
                val formatted = String.format(Locale.getDefault(), "%tT", diff - TimeZone.getDefault().rawOffset)
                context.getString(R.string.time_remaining_to_darken_label) + ": " + formatted
            }
            delay(1000)
        }
    }

    Card(
        shape = MaterialTheme.shapes.extraLarge,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .animateContentSize()
    ) {
        Column {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.schedule),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(id = R.string.summary_scheduler),
                    style = MaterialTheme.typography.bodyMedium
                )
                Button(
                    onClick = {
                        if (enabled) {
                            SchedulerService.disable(context)
                        } else {
                            SchedulerService.enable(context)
                        }
                        enabled = !enabled
                    },
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = if (enabled) stringResource(id = R.string.disable_scheduler)
                        else stringResource(id = R.string.enable_scheduler)
                    )
                }
                if (enabled) {
                    Text(
                        modifier = Modifier.padding(top = 10.dp),
                        text = stringResource(id = R.string.enabled_only_during_this_interval),
                        textAlign = TextAlign.Center
                    )
                    Row(modifier = Modifier.fillMaxWidth().padding(top = 10.dp)) {
                        val activity = context.fragmentActivity as FragmentActivity?
                        Button(
                            onClick = {
                                val dlg = TimePickerDialog.newInstance({ _, h, m, _ ->
                                    startHour = h
                                    startMinute = m
                                    SchedulerService.setFrom(context, h, m)
                                    SchedulerService.evaluateSchedule(context)
                                }, startHour, startMinute, true)
                                activity?.let { dlg.show(it.supportFragmentManager, "from") }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(String.format(Locale.getDefault(), "%02d:%02d", startHour, startMinute))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val dlg = TimePickerDialog.newInstance({ _, h, m, _ ->
                                    endHour = h
                                    endMinute = m
                                    SchedulerService.setTo(context, h, m)
                                    SchedulerService.evaluateSchedule(context)
                                }, endHour, endMinute, true)
                                activity?.let { dlg.show(it.supportFragmentManager, "to") }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(String.format(Locale.getDefault(), "%02d:%02d", endHour, endMinute))
                        }
                    }
                    if (remaining.isNotEmpty()) {
                        Text(
                            text = remaining,
                            modifier = Modifier.padding(top = 10.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
