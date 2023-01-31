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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.squaredem.composecalendar.model.ColorScheme
import com.squaredem.composecalendar.utils.LogCompositions
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.OffsetTime
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CalendarMonthYearSelector(
    pagerDate: LocalDate,
    onChipClicked: () -> Unit,
    onNextMonth: () -> Unit,
    onPreviousMonth: () -> Unit,
    modifier: Modifier = Modifier,
    isNextMonthEnabled: Boolean = true,
    isPreviousMonthEnabled: Boolean = true,
    isMonthSelectorVisible: Boolean = true,
) {
    LogCompositions("CalendarMonthYearSelector")

    val pagerMonthFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilterChip(
            label = {
                Text(
                    text = pagerMonthFormat.format(
                        Date.from(pagerDate.atTime(OffsetTime.now()).toInstant())
                    ),
                    color = ColorScheme.yearPickerTitleHighlight,
                )
            },
            selected = false,
            border = null,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "ArrowDropDown",
                    tint = ColorScheme.yearPickerTitleHighlight
                )
            },
            onClick = onChipClicked,
        )
        Spacer(modifier = Modifier.weight(1F))
        AnimatedVisibility(
            visible = isMonthSelectorVisible,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Row {
                IconButton(
                    onClick = onPreviousMonth,
                    enabled = isPreviousMonthEnabled,
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = ColorScheme.monthChevron
                    )
                ) {
                    Icon(Icons.Default.ChevronLeft, "ChevronLeft")
                }
                IconButton(
                    onClick = onNextMonth,
                    enabled = isNextMonthEnabled,
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = ColorScheme.monthChevron
                    )
                ) {
                    Icon(Icons.Default.ChevronRight, "ChevronRight")
                }
            }
        }
    }
}
