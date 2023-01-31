package com.squaredem.composecalendardemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.squaredem.composecalendardemo.ui.theme.ComposeCalendarDemoTheme
import java.time.LocalDate
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.squaredem.composecalendar.ComposeRangeCalendar
import com.squaredem.composecalendar.RangeDatePicker
import com.squaredem.composecalendar.composable.DateRangeSelection
import com.squaredem.composecalendar.model.CalendarDefaults
import com.squaredem.composecalendar.model.DayOption
import com.squaredem.composecalendar.model.ExtraButtonHelperType
import com.squaredem.composecalendar.model.WeekDaysMode
import java.time.DayOfWeek

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeCalendarDemoTheme {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    MainActivityContent()
                }
            }
        }
    }
}

@Composable
private fun MainActivityContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        var calendarMode: CalendarMode by rememberSaveable { mutableStateOf(CalendarMode.Hidden) }
        var selectedDateText by rememberSaveable { mutableStateOf<String?>(null) }
        var selectedDate by rememberSaveable { mutableStateOf<LocalDate?>(null) }
        var selectedRange by remember { mutableStateOf<DateRangeSelection?>(null) }

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            selectedDateText?.let {
                Text(text = it)
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Button(onClick = { calendarMode = CalendarMode.Popup }) {
                    Text("Show dialog")
                }

                Button(onClick = { calendarMode = CalendarMode.InPlace }) {
                    Text("Show in-place")
                }
            }

            AnimatedVisibility(
                visible = calendarMode == CalendarMode.InPlace,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    RangeDatePicker(
                        mode = com.squaredem.composecalendar.composable.CalendarMode.Range(
                            minDate = LocalDate.now().minusMonths(2),
                            maxDate = LocalDate.MAX,
                            selection = selectedRange,
                        ),
                        onChanged = {
                            selectedRange = it.selection
                            selectedDateText = generateSelectionText(it.selection)
                        },
                        contentConfig = CalendarDefaults.defaultContentConfig(
                            showSelectedDateTitle = false,
                            weekdaysMode = WeekDaysMode.DoubleLetter,
                            extraButtonHelper = ExtraButtonHelperType.Today,
                            calendarDayOption = {
                                // Example of a filter for days.
                                when (it.dayOfWeek) {
                                    in listOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY) -> {
                                        DayOption.Disabled(true)
                                    }

                                    else -> DayOption.Default
                                }
                            }
                        ),
                        calendarColors = CalendarDefaults.defaultColors(
                            monthChevron = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    
                    Button(onClick = { calendarMode = CalendarMode.Hidden }) {
                        Text(text = "Dismiss")
                    }
                }
            }
        }

        if (calendarMode == CalendarMode.Popup) {
            // Range popup.
            ComposeRangeCalendar(
                initialSelection = selectedRange,
                minDate = LocalDate.of(2019, 1, 1),
                maxDate = LocalDate.now(),
                onDone = {
                    selectedRange = it
                    selectedDateText = generateSelectionText(it)
                    calendarMode = CalendarMode.Hidden
                },
                onDismiss = { calendarMode = CalendarMode.Hidden }
            )

            // Single date popup.
            /*
            ComposeCalendar(
                startDate = selectedDate ?: LocalDate.now(),
                minDate = LocalDate.of(2019, 1, 1),
                maxDate = LocalDate.now(),
                onDone = {
                    selectedDate = it
                    selectedDateText = it.toString()
                    calendarMode = CalendarMode.Hidden
                },
                onDismiss = { calendarMode = CalendarMode.Hidden }
            )
            */
        }
    }
}

private fun generateSelectionText(selection: DateRangeSelection?): String? = when {
    selection == null -> {
        null
    }

    selection.endDate == null -> {
        "From: ${selection.startDate}"
    }

    else -> {
        "From: ${selection.startDate} to ${selection.endDate}"
    }
}

@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    ComposeCalendarDemoTheme {
        MainActivityContent()
    }
}

enum class CalendarMode {
    Hidden,
    Popup,
    InPlace,
}
