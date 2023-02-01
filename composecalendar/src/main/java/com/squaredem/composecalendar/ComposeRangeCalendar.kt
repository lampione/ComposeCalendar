/*
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

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.squaredem.composecalendar.model.CalendarMode
import com.squaredem.composecalendar.model.DateRangeSelection
import com.squaredem.composecalendar.model.CalendarColors
import com.squaredem.composecalendar.model.CalendarContentConfig
import com.squaredem.composecalendar.model.CalendarDefaults
import com.squaredem.composecalendar.model.DefaultTitleFormatters
import java.time.LocalDate

/**
 * Display an alert dialog to pick a date range.
 */
@Composable
fun ComposeRangeCalendar(
    minDate: LocalDate = LocalDate.MIN,
    maxDate: LocalDate = LocalDate.MAX,
    onDone: (selection: DateRangeSelection) -> Unit,
    onDismiss: () -> Unit,
    initialSelection: DateRangeSelection? = null,
    contentConfig: CalendarContentConfig = CalendarDefaults.defaultContentConfig(),
    calendarColors: CalendarColors = CalendarDefaults.defaultColors(),
    titleFormatter: (DateRangeSelection?) -> String = DefaultTitleFormatters.dateRange(),
) {
    var selectedDate by remember { mutableStateOf(initialSelection) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    selectedDate?.let(onDone)
                },
                enabled = selectedDate != null,
            ) {
                Text(stringResource(id = android.R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = android.R.string.cancel))
            }
        },
        text = {
            RangeDatePicker(
                mode = CalendarMode.Range(
                    selection = selectedDate,
                    minDate = minDate,
                    maxDate = maxDate,
                    titleFormatter = titleFormatter,
                ),
                onChanged = {
                    selectedDate = it.selection
                },
                contentConfig = contentConfig,
                calendarColors = calendarColors,
            )
        }
    )
}
