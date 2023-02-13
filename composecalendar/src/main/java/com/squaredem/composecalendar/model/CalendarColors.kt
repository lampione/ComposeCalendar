/*
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

package com.squaredem.composecalendar.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * @param monthChevron next and previous month chevrons color.
 * @param defaultText default day text color.
 * @param disabledText disabled days text color.
 * @param selectedDayBackground background color of the selected day, or start and end days.
 * @param selectedDayText text color of the selected day, or start and end days.
 * @param currentDayHighlight color of the highlight circle and text for the current day.
 * @param inRangeDayBackground background color for days inside the selected Range.
 * @param inRangeDayText text color for days inside the selected Range.
 * @param todayButtonText text color of the go to today button.
 * @param yearPickerTitleHighlight text and chevron color of the current year/month.
 * @param yearPickerText text color of the not selected years on year picker.
 * @param yearPickerSelectedText selected year text color on the year picker.
 * @param yearPickerSelectedBackground selected year background color on the year picker.
 * @param yearPickerCurrentYearHighlight color of the highlight circle and text for the current year.
 * @param dayOfWeek text color of the week days text.
 * @param headerText text color of the current selected date title.
 * @param dividerColor color for the calendar dividers.
 */
data class CalendarColors(
    val monthChevron: Color,
    val defaultText: Color,
    val disabledText: Color,
    val selectedDayBackground: Color,
    val selectedDayText: Color,
    val currentDayHighlight: Color,
    val inRangeDayBackground: Color,
    val inRangeDayText: Color,
    val todayButtonText: Color,
    val yearPickerTitleHighlight: Color,
    val yearPickerText: Color,
    val yearPickerSelectedText: Color,
    val yearPickerSelectedBackground: Color,
    val yearPickerCurrentYearHighlight: Color,
    val dayOfWeek: Color,
    val headerText: Color,
    val dividerColor: Color,
)

internal val ColorScheme: CalendarColors
    @Composable
    @ReadOnlyComposable
    get() = LocalCalendarColorScheme.current

internal val LocalCalendarColorScheme = staticCompositionLocalOf { initialCalendarColors() }

internal fun initialCalendarColors(): CalendarColors = CalendarColors(
    monthChevron = Color.Black,
    defaultText = Color.Black,
    disabledText = Color.Gray,
    selectedDayBackground = Color.Blue,
    selectedDayText = Color.White,
    currentDayHighlight = Color.Blue,
    inRangeDayBackground = Color.Blue.copy(alpha = 0.5f),
    inRangeDayText = Color.Black,
    todayButtonText = Color.Blue,
    yearPickerTitleHighlight = Color.Black,
    yearPickerText = Color.Blue,
    yearPickerSelectedText = Color.White,
    yearPickerSelectedBackground = Color.Blue,
    yearPickerCurrentYearHighlight = Color.Blue,
    dayOfWeek = Color.Black,
    headerText = Color.Black,
    dividerColor = Color.Black,
)
