package com.squaredem.composecalendardemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.squaredem.composecalendar.SelectedDateView
import com.squaredem.composecalendar.model.DateRangeSelection
import com.squaredem.composecalendar.model.CalendarDefaults
import com.squaredem.composecalendar.model.CalendarMode
import com.squaredem.composecalendar.model.DayOption
import com.squaredem.composecalendar.model.DefaultTitleFormatters
import com.squaredem.composecalendar.model.ExtraButtonHelperType
import com.squaredem.composecalendar.model.ForcedSelectMode
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
        var calendarMode: CalendarDisplayMode by rememberSaveable {
            mutableStateOf(CalendarDisplayMode.Hidden)
        }
        var selectedDateText by rememberSaveable { mutableStateOf<String?>(null) }
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
                Button(onClick = { calendarMode = CalendarDisplayMode.Popup }) {
                    Text("Show dialog")
                }

                Button(onClick = { calendarMode = CalendarDisplayMode.InPlace }) {
                    Text("Show in-place")
                }
            }

            var mode by remember {
                mutableStateOf(
                    CalendarMode.Range(
                        minDate = LocalDate.now().minusMonths(2),
                        maxDate = LocalDate.MAX,
                        selection = null,
                    )
                )
            }
            var calendarConfig by remember {
                mutableStateOf(
                    CalendarDefaults.defaultContentConfig(
                        showSelectedDateTitle = false,
                        calendarYearPickerFormat = "MMM YY",
                        weekdaysMode = WeekDaysMode.DoubleLetter,
                        extraButtonHelper = ExtraButtonHelperType.Today,
                        hasDividers = true,
                        calendarDayOption = {
                            // Example of a filter for days.
                            when (it.dayOfWeek) {
                                in listOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY) -> {
                                    DayOption.Disabled(true)
                                }

                                else -> DayOption.Default
                            }
                        }
                    )
                )
            }
            AnimatedVisibility(
                visible = calendarMode == CalendarDisplayMode.InPlace,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        SelectedDateView(
                            value = mode.selection?.startDate,
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    if (mode.selectionMode == ForcedSelectMode.EndDate) {
                                        mode = mode.copy(
                                            selectionMode = ForcedSelectMode.StartDate,
                                        )
                                    } else {
                                        calendarConfig = calendarConfig.copy(
                                            currentPagerDate = mode.selection?.startDate,
                                        )
                                    }
                                },
                            highlighted = mode.selectionMode == ForcedSelectMode.StartDate,
                            label = "Start date",
                        )

                        val endDate = mode.selection?.endDate ?: mode.selection?.startDate
                        SelectedDateView(
                            value = endDate,
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    if (mode.selection != null && mode.selectionMode == ForcedSelectMode.StartDate) {
                                        mode = mode.copy(
                                            selectionMode = ForcedSelectMode.EndDate,
                                        )
                                    } else {
                                        calendarConfig = calendarConfig.copy(
                                            currentPagerDate = endDate,
                                        )
                                    }
                                },
                            highlighted = mode.selectionMode == ForcedSelectMode.EndDate,
                            label = "End date",
                        )
                    }

                    RangeDatePicker(
                        mode = mode,
                        onChanged = {
                            mode = it
                        },
                        contentConfig = calendarConfig,
                        calendarColors = CalendarDefaults.defaultColors(
                            monthChevron = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Button(onClick = { calendarMode = CalendarDisplayMode.Hidden }) {
                        Text(text = "Dismiss")
                    }
                }
            }
        }

        if (calendarMode == CalendarDisplayMode.Popup) {
            // Range popup.
            ComposeRangeCalendar(
                initialSelection = selectedRange,
                minDate = LocalDate.of(2019, 1, 1),
                maxDate = LocalDate.now(),
                onDone = {
                    selectedRange = it
                    selectedDateText = generateSelectionText(it)
                    calendarMode = CalendarDisplayMode.Hidden
                },
                onDismiss = { calendarMode = CalendarDisplayMode.Hidden },
                titleFormatter = DefaultTitleFormatters.dateRange(
                    dateFormat = "dd MMM yyyy",
                    dateFormatWithoutYear = "dd MMM",
                    emptyTitle = "Selected range",
                    dateJoiner = " -> ",
                )
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
                    calendarMode = CalendarDisplayMode.Hidden
                },
                onDismiss = { calendarMode = CalendarDisplayMode.Hidden }
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

enum class CalendarDisplayMode {
    Hidden,
    Popup,
    InPlace,
}
