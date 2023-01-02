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

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.squaredem.composecalendar.utils.LogCompositions
import com.squaredem.composecalendar.utils.monthYearFormat
import kotlinx.datetime.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CalendarMonthYearSelector(
    pagerDate: LocalDate,
    onChipClicked: () -> Unit,
    onNextMonth: () -> Unit,
    onPreviousMonth: () -> Unit,
) {
    LogCompositions("CalendarMonthYearSelector")

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilterChip(
            label = {
                Text(
                    pagerDate.monthYearFormat()
                )
            },
            selected = false,
            border = null,
            trailingIcon = {
                Icon(Icons.Default.ArrowDropDown, "ArrowDropDown")
            },
            onClick = onChipClicked,
        )
        Spacer(modifier = Modifier.weight(1F))
        IconButton(onClick = onPreviousMonth) {
            Icon(Icons.Default.ChevronLeft, "ChevronLeft")
        }
        IconButton(onClick = onNextMonth) {
            Icon(Icons.Default.ChevronRight, "ChevronRight")
        }
    }
}
