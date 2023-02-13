/*
 * Copyright 2022 Matteo Miceli
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.squaredem.composecalendar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.squaredem.composecalendar.model.CalendarMode
import com.squaredem.composecalendar.model.CalendarColors
import com.squaredem.composecalendar.model.CalendarContentConfig
import com.squaredem.composecalendar.model.CalendarDefaults
import com.squaredem.composecalendar.model.DefaultTitleFormatters
import java.time.LocalDate

/**
 * Display an alert dialog to pick a single date.
 */
@Composable
fun ComposeCalendar(
    startDate: LocalDate = LocalDate.now(),
    minDate: LocalDate = LocalDate.MIN,
    maxDate: LocalDate = LocalDate.MAX,
    onDone: (millis: LocalDate) -> Unit,
    onDismiss: () -> Unit,
    contentConfig: CalendarContentConfig = CalendarDefaults.defaultContentConfig(),
    calendarColors: CalendarColors = CalendarDefaults.defaultColors(),
    titleFormatter: (LocalDate?) -> String = DefaultTitleFormatters.singleDate(),
) {
    val selectedDate = remember { mutableStateOf(startDate) }
    var mode by remember {
        mutableStateOf(
            CalendarMode.Single(
                selectedDate = startDate,
                minDate = minDate,
                maxDate = maxDate,
                titleFormatter = titleFormatter,
            )
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDone(selectedDate.value)
            }) {
                Text(stringResource(id = android.R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = android.R.string.cancel))
            }
        },
        text = {
            SingleDatePicker(
                mode = mode,
                onChanged = {
                    mode = it
                    selectedDate.value = it.selectedDate ?: it.startDate
                },
                contentConfig = contentConfig,
                calendarColors = calendarColors,
            )
        }
    )
}
