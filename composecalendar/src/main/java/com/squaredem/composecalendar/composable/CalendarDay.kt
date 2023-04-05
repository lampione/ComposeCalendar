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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.squaredem.composecalendar.model.ColorScheme
import com.squaredem.composecalendar.model.Config
import com.squaredem.composecalendar.model.DateWrapper
import com.squaredem.composecalendar.model.DayOption
import com.squaredem.composecalendar.model.HighlightedType
import com.squaredem.composecalendar.utils.LogCompositions
import com.squaredem.composecalendar.utils.isFirstDayOfWeek
import com.squaredem.composecalendar.utils.isLastDayOfWeek
import java.time.LocalDate

@Composable
internal fun CalendarDay(
    date: DateWrapper,
    dayOption: DayOption,
    onSelected: (LocalDate) -> Unit,
) {
    LogCompositions("CalendarDay")

    val currentModifier = Modifier.aspectRatio(1F)
    if (!date.isCurrentMonth && date.showCurrentMonthOnly) {
        Box(modifier = currentModifier)
        return
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
        val isFirstDayOfWeek = date.localDate.dayOfWeek.isFirstDayOfWeek(Config.weekStartDay)
        val isLastDayOfWeek = date.localDate.dayOfWeek.isLastDayOfWeek(Config.weekStartDay)
        when (date.highlightedType) {
            HighlightedType.Start -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    val shape = if (isFirstDayOfWeek) {
                        RoundedCornerShape(
                            topStart = Config.selectorBackgroundRadius,
                            bottomStart = Config.selectorBackgroundRadius,
                        )
                    } else {
                        RoundedCornerShape(0.dp)
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .fillMaxHeight()
                            .background(
                                shape = shape,
                                color = ColorScheme.inRangeDayBackground
                            )
                    )
                }
            }

            HighlightedType.End -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    val shape = if (isLastDayOfWeek) {
                        RoundedCornerShape(
                            topEnd = Config.selectorBackgroundRadius,
                            bottomEnd = Config.selectorBackgroundRadius,
                        )
                    } else {
                        RoundedCornerShape(0.dp)
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .fillMaxHeight()
                            .background(
                                shape = shape,
                                color = ColorScheme.inRangeDayBackground,
                            )
                    )
                }
            }

            HighlightedType.Full -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .let {
                            when {
                                isFirstDayOfWeek -> {
                                    it.background(
                                        color = ColorScheme.inRangeDayBackground,
                                        shape = RoundedCornerShape(
                                            topStart = Config.selectorBackgroundRadius,
                                            bottomStart = Config.selectorBackgroundRadius,
                                        )
                                    )
                                }

                                isLastDayOfWeek -> {
                                    it.background(
                                        color = ColorScheme.inRangeDayBackground,
                                        shape = RoundedCornerShape(
                                            topEnd = Config.selectorBackgroundRadius,
                                            bottomEnd = Config.selectorBackgroundRadius,
                                        )
                                    )
                                }

                                else -> {
                                    it.background(ColorScheme.inRangeDayBackground)
                                }
                            }
                        },
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

        if (date.isInDateRange && dayOption.isClickable) {
            Box(modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .clickable {
                    onSelected(date.localDate)
                }
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
