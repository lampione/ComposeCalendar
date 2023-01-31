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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.squaredem.composecalendar.model.ColorScheme
import com.squaredem.composecalendar.utils.LogCompositions
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.OffsetTime
import java.util.*

@Composable
internal fun CalendarMonthYearSelector(
    pagerDate: LocalDate,
    onChipClicked: () -> Unit,
    onNextMonth: () -> Unit,
    onPreviousMonth: () -> Unit,
    onGoToToday: () -> Unit,
    modifier: Modifier = Modifier,
    isNextMonthEnabled: Boolean = true,
    isPreviousMonthEnabled: Boolean = true,
    isMonthSelectorVisible: Boolean = true,
    isTodayButtonVisible: Boolean = true,
) {
    LogCompositions("CalendarMonthYearSelector")


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        YearPicker(
            pagerDate = pagerDate,
            onChipClicked = onChipClicked,
        )

        Spacer(modifier = Modifier.weight(1F))

        MonthChevrons(
            isVisible = isMonthSelectorVisible,
            isNextMonthEnabled = isNextMonthEnabled,
            isPreviousMonthEnabled = isPreviousMonthEnabled,
            onNextMonth = onNextMonth,
            onPreviousMonth = onPreviousMonth,
        )

        TodayButton(
            isVisible = isTodayButtonVisible,
            onGoToToday = onGoToToday,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun YearPicker(
    pagerDate: LocalDate,
    onChipClicked: () -> Unit,
) {
    val pagerMonthFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())

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
}

@Composable
private fun TodayButton(
    isVisible: Boolean,
    onGoToToday: () -> Unit,
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        TextButton(
            onClick = { onGoToToday() },
            colors = ButtonDefaults.textButtonColors(
                contentColor = ColorScheme.todayButtonText,
            )
        ) {
            Text("Today")
        }
    }
}

@Composable
private fun MonthChevrons(
    isVisible: Boolean,
    isNextMonthEnabled: Boolean = true,
    isPreviousMonthEnabled: Boolean = true,
    onNextMonth: () -> Unit,
    onPreviousMonth: () -> Unit,
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = onPreviousMonth,
                enabled = isPreviousMonthEnabled,
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = ColorScheme.monthChevron
                ),
                modifier = Modifier.padding(0.dp),
            ) {
                Icon(Icons.Default.ChevronLeft, "ChevronLeft", Modifier.size(16.dp))
            }
            IconButton(
                onClick = onNextMonth,
                enabled = isNextMonthEnabled,
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = ColorScheme.monthChevron
                )
            ) {
                Icon(Icons.Default.ChevronRight, "ChevronRight", Modifier.size(16.dp))
            }
        }
    }
}
