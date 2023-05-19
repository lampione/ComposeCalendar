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

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.squaredem.composecalendar.composable.CalendarContent
import java.text.DateFormat
import java.time.LocalDate

@Composable
fun ComposeCalendar(
    startDate: LocalDate = LocalDate.now(),
    minDate: LocalDate = LocalDate.MIN,
    maxDate: LocalDate = LocalDate.MAX,
    showSelectedDate: Boolean = true,
    selectedDateFormat: DateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT),
    onDone: (millis: LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val selectedDate = remember { mutableStateOf(LocalDate.now()) }

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
            CalendarContent(
                startDate = startDate,
                minDate = minDate,
                maxDate = maxDate,
                onDateSelected = { selectedDate.value = it },
                showSelectedDate = showSelectedDate,
                selectedDateFormat = selectedDateFormat
            )
        }
    )
}
