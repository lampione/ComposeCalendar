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

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.squaredem.composecalendar.model.ColorScheme
import com.squaredem.composecalendar.model.DateWrapper
import com.squaredem.composecalendar.model.DayOption
import com.squaredem.composecalendar.model.HighlightedType
import com.squaredem.composecalendar.utils.LogCompositions
import java.time.LocalDate

@Composable
internal fun CalendarDay(
    date: DateWrapper,
    dayOption: DayOption,
    onSelected: (LocalDate) -> Unit,
) {
    LogCompositions("CalendarDay")

    var currentModifier = Modifier.aspectRatio(1F)

    if (!date.isCurrentMonth && date.showCurrentMonthOnly) {
        Box(modifier = currentModifier)
        return
    }

    if (date.isInDateRange && dayOption.isClickable) {
        currentModifier = currentModifier.clickable {
            onSelected(date.localDate)
        }
    }

    val textColor = when {
        !date.isInDateRange -> {
            ColorScheme.defaultText.copy(alpha = 0.38F)
        }

        date.isSelectedDay -> {
            ColorScheme.selectedDayText
        }

        date.isCurrentDay -> {
            ColorScheme.currentDayHighlight
        }

        !date.isCurrentMonth -> {
            ColorScheme.defaultText.copy(alpha = 0.6F)
        }

        else -> ColorScheme.defaultText
    }.let {
        // Disabled style always overrides other styles.
        if (dayOption.hasDisabledStyle) {
            it.copy(alpha = 0.6F)
        } else {
            it
        }
    }

    val text = "${date.localDate.dayOfMonth}"

    Box(
        modifier = currentModifier,
        contentAlignment = Alignment.Center
    ) {
        when (date.highlightedType) {
            HighlightedType.Start -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .fillMaxHeight()
                            .background(ColorScheme.inRangeDayBackground)
                    )
                }
            }

            HighlightedType.End -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .fillMaxHeight()
                            .background(ColorScheme.inRangeDayBackground)
                    )
                }
            }

            HighlightedType.Full -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(ColorScheme.inRangeDayBackground),
                )
            }

            null -> Unit
        }

        if (date.isCurrentDay) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(2.dp)
                    .border(
                        width = 1.dp,
                        color = ColorScheme.currentDayHighlight,
                        shape = CircleShape
                    ),
            )
        }

        if (date.isSelectedDay) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = ColorScheme.selectedDayBackground,
                        shape = CircleShape
                    ),
            )
        }

        Text(
            text = text,
            color = textColor,
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
        )
    }
}
