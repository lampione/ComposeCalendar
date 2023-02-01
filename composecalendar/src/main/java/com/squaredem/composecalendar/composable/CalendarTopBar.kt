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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.squaredem.composecalendar.model.CalendarMode
import com.squaredem.composecalendar.model.ColorScheme
import com.squaredem.composecalendar.utils.LogCompositions

@Composable
internal fun CalendarTopBar(mode: CalendarMode) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        LogCompositions("CalendarTopBar")
        val text = when (mode) {
            is CalendarMode.Range -> mode.titleFormatter(mode.selection)
            is CalendarMode.Single -> mode.titleFormatter(mode.selectedDate)
        }

        Text(
            text = text,
            style = when (mode) {
                is CalendarMode.Range -> MaterialTheme.typography.headlineMedium
                is CalendarMode.Single -> MaterialTheme.typography.headlineLarge
            },
            color = ColorScheme.headerText,
        )
    }
}
