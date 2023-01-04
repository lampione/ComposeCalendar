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

package com.squaredem.composecalendar.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate

@Composable
fun CalendarInput(
    selectedDate: LocalDate?,
    onChanged: (LocalDate) -> Unit,
    dateFormatter: (LocalDate) -> String,
    dateParser: (String) -> LocalDate?,
    pattern: String,
) {
    var text by remember { mutableStateOf(selectedDate?.let { dateFormatter(it) } ?: "") }
    Column(
        Modifier.padding(start = 24.dp, end = 24.dp, top = 10.dp, bottom = 16.dp)
    ) {
        var hasError by remember { mutableStateOf(false) }
        OutlinedTextField(
            value = text,
            onValueChange = { value ->
                text = value
                val result = dateParser(value)
                if (result != null) {
                    hasError = false
                    onChanged(result)
                } else {
                    hasError = true
                }
            },
            label = { Text("Date") },
            placeholder = { Text(pattern) },
            isError = hasError,
        )
    }

}