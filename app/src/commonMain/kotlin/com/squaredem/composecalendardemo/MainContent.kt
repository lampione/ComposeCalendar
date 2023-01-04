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

package com.squaredem.composecalendardemo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.squaredem.composecalendar.ComposeCalendar
import kotlinx.datetime.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        val showDialog = rememberSaveable { mutableStateOf(false) }
        val selectedDate = rememberSaveable { mutableStateOf<LocalDate?>(null) }

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            selectedDate.value?.let {
                Text(text = it.toString())
            }

            Button(onClick = { showDialog.value = true }) {
                Text("Show dialog")
            }
        }

        if (showDialog.value) {
            ComposeCalendar(
                title = { Text("Select a date") },
                initialDate = selectedDate.value,
                onDone = { it: LocalDate ->
                    selectedDate.value = it
                    showDialog.value = false
                },
                onDismiss = { showDialog.value = false }
            )
        }

    }
}