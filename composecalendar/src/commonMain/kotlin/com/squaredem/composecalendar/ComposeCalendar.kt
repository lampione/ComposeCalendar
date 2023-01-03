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

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.squaredem.composecalendar.composable.CalendarContent
import com.squaredem.composecalendar.composable.CalendarTopBar
import com.squaredem.composecalendar.composable.TextButtons
import com.squaredem.composecalendar.utils.CalendarDialog
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun ComposeCalendar(
    title: @Composable () -> Unit,
    headlinePattern: String = "EEE, LLL d",
    startDate: LocalDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
    minDate: LocalDate = LocalDate(1900, 1, 1),
    maxDate: LocalDate = LocalDate(2100, 12, 31),
    okText: String = "OK",
    cancelText: String = "Cancel",
    onDone: (selectedDate: LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedDate by remember { mutableStateOf(startDate) }

    CalendarDialog(
        onDismissRequest = onDismiss,
        content = {
            Surface(
                modifier = Modifier.width(328.dp),
                tonalElevation = 6.dp,
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Column {
                    CalendarTopBar(
                        title = title,
                        selectedDate = selectedDate,
                        headlinePattern = headlinePattern,
                    )

                    CalendarContent(
                        selectedDate = selectedDate,
                        minDate = minDate,
                        maxDate = maxDate,
                        onSelected = {
                            selectedDate = it
                        }
                    )
                    Spacer(Modifier.height(8.dp))
                    TextButtons(
                        onDone = { onDone(selectedDate) },
                        okText = okText,
                        onCancel = onDismiss,
                        cancelText = cancelText,
                    )
                }
            }
        }
    )
}
