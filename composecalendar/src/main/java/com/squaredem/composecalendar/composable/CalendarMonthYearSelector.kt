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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.squaredem.composecalendar.model.ColorScheme
import com.squaredem.composecalendar.utils.LogCompositions
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.OffsetTime
import java.util.*

@Composable
internal fun CalendarMonthYearSelector(
    pagerDate: LocalDate,
    dateFormat: String,
    todayTitle: String,
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
            .heightIn(40.dp)
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        YearPicker(
            pagerDate = pagerDate,
            dateFormat = dateFormat,
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
            todayTitle = todayTitle,
        )
    }
}

@Composable
private fun YearPicker(
    pagerDate: LocalDate,
    dateFormat: String,
    onChipClicked: () -> Unit,
) {
    val pagerMonthFormat = SimpleDateFormat(dateFormat, Locale.getDefault())

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onChipClicked() },
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = pagerMonthFormat.format(
                    Date.from(pagerDate.atTime(OffsetTime.now()).toInstant())
                ),
                color = ColorScheme.yearPickerTitleHighlight,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
            )

            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "ArrowDropDown",
                tint = ColorScheme.yearPickerTitleHighlight
            )
        }
    }
}

@Composable
private fun TodayButton(
    todayTitle: String,
    isVisible: Boolean,
    onGoToToday: () -> Unit,
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Box(modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onGoToToday() }) {

            Text(
                text = todayTitle,
                modifier = Modifier.padding(vertical = 4.dp),
                color = ColorScheme.todayButtonText,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
            )
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
                modifier = Modifier.padding(0.dp),
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
