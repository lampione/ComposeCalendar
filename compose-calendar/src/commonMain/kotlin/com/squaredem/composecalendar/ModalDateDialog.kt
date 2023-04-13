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

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.squaredem.composecalendar.composable.CalendarInput
import com.squaredem.composecalendar.composable.CalendarPicker
import com.squaredem.composecalendar.composable.CalendarTopBar
import com.squaredem.composecalendar.composable.TextButtons
import com.squaredem.composecalendar.utils.CalendarDialog
import com.squaredem.composecalendar.utils.formatInput
import com.squaredem.composecalendar.utils.headlineFormat
import com.squaredem.composecalendar.utils.parseInput
import kotlinx.datetime.LocalDate

@ExperimentalFoundationApi
@ExperimentalMaterial3Api
@Composable
fun ModalDateDialog(
    title: @Composable () -> Unit,
    headlineGenerator: ((LocalDate?) -> String)? = null,
    initialDate: LocalDate? = null,
    minDate: LocalDate = LocalDate(1900, 1, 1),
    maxDate: LocalDate = LocalDate(2100, 12, 31),
    okText: String = "OK",
    cancelText: String = "Cancel",
    startOnPicker: Boolean = true,
    inputFormatter: (LocalDate) -> String = ::formatInput,
    inputParser: (String) -> LocalDate? = ::parseInput,
    inputPattern: String = "mm/dd/yyyy",
    onDone: (selectedDate: LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    check(minDate <= maxDate)

    var selectedDate by remember { mutableStateOf(initialDate) }
    var isPicker by remember { mutableStateOf(startOnPicker) }

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
                        headline = headlineGenerator?.invoke(selectedDate)
                            ?: selectedDate?.headlineFormat()
                            ?: if (isPicker) "Select a date" else "Enter a date",
                        iconContent = {
                            IconButton(
                                onClick = { isPicker = !isPicker }
                            ) {
                                if (isPicker) {
                                    Icon(Icons.Default.Edit, null)
                                } else {
                                    Icon(Icons.Default.CalendarToday, null)
                                }
                            }
                        }
                    )

                    if (isPicker) {
                        CalendarPicker(
                            selectedDate = selectedDate,
                            minDate = minDate,
                            maxDate = maxDate,
                            onSelected = {
                                selectedDate = it
                            }
                        )
                    } else {
                        CalendarInput(
                            selectedDate = selectedDate,
                            onChanged = {
                                selectedDate = it
                            },
                            dateFormatter = inputFormatter,
                            dateParser = inputParser,
                            pattern = inputPattern,
                        )
                    }

                    Spacer(Modifier.height(8.dp))
                    TextButtons(
                        okEnabled = selectedDate != null,
                        onDone = { selectedDate?.let { onDone(it) } },
                        okText = okText,
                        onCancel = onDismiss,
                        cancelText = cancelText,
                    )
                }
            }
        }
    )
}
