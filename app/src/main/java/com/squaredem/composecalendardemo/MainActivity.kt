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
import com.squaredem.composecalendar.ComposeCalendar
import com.squaredem.composecalendar.composable.CalendarContent
import com.squaredem.composecalendardemo.ui.theme.ComposeCalendarDemoTheme
import java.time.LocalDate
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.squaredem.composecalendar.composable.CalendarDefaults

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
        val selectedDate = rememberSaveable { mutableStateOf<LocalDate?>(null) }

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            selectedDate.value?.let {
                Text(text = it.toString())
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
                    CalendarContent(
                        mode = com.squaredem.composecalendar.composable.CalendarMode.Single(
                            selectedDate = selectedDate.value ?: LocalDate.now(),
                            minDate = LocalDate.now().minusMonths(2),
                            maxDate = LocalDate.MAX,
                        ),
                        onChanged = {
                            when (it) {
                                is com.squaredem.composecalendar.composable.CalendarMode.Multi -> TODO()
                                is com.squaredem.composecalendar.composable.CalendarMode.Single -> {
                                    selectedDate.value = it.selectedDate
                                    calendarMode = CalendarMode.Hidden
                                }
                            }
                        },
                        contentConfig = CalendarDefaults.defaultContentConfig(
                            showSelectedDateTitle = false,
                        ),
                        calendarColors = CalendarDefaults.defaultColors(
                            monthChevronColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    
                    Button(onClick = { calendarMode = CalendarMode.Hidden }) {
                        Text(text = "Dismiss")
                    }
                }
            }
        }

        if (calendarMode == CalendarMode.Popup) {
            ComposeCalendar(
                startDate = selectedDate.value ?: LocalDate.now(),
                minDate = LocalDate.of(2019, 1, 1),
                maxDate = LocalDate.now(),
                onDone = {
                    selectedDate.value = it
                    calendarMode = CalendarMode.Hidden
                },
                onDismiss = { calendarMode = CalendarMode.Hidden }
            )
        }
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
