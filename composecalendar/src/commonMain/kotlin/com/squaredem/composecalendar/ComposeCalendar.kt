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

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.squaredem.composecalendar.composable.CalendarContent
import com.squaredem.composecalendar.utils.AlertDialog
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun ComposeCalendar(
    startDate: LocalDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
    minDate: LocalDate = LocalDate(1900, 1, 1),
    maxDate: LocalDate = LocalDate(2100, 12, 31),
    okText: String = "OK",
    cancelText: String = "Cancel",
    onDone: (millis: LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val selectedDate = remember { mutableStateOf(startDate) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDone(selectedDate.value)
            }) {
                Text(okText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(cancelText)
            }
        },
        text = {
            CalendarContent(
                startDate = startDate,
                minDate = minDate,
                maxDate = maxDate,
                onSelected = {
                    selectedDate.value = it
                }
            )
        }
    )
}
