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
import androidx.compose.ui.unit.Dp
import java.time.DayOfWeek
import java.time.LocalDate

/**
 * Configuration settings for the calendar view.
 *
 * @param showSelectedDateTitle should show title with the current date selection.
 * @param extraButtonHelper which helper button should be shown.
 * @param calendarDayOption should apply different options for specific days.
 * @param calendarYearPickerFormat date format to show on the date picker placeholder.
 * @param currentPagerDate force scroll to [LocalDate].
 * @param hasDividers has dividers between components.
 * @param selectorBackgroundRadius shape radius for the mid range dates on first and last day of
 * each week.
 * @param maxWidth calendar max width (448dp is 64dp*7 each day max).
 */
data class CalendarContentConfig(
    val showSelectedDateTitle: Boolean,
    val extraButtonHelper: ExtraButtonHelperType?,
    val weekDaysMode: WeekDaysMode,
    val calendarDayOption: ((LocalDate) -> DayOption)?,
    val calendarYearPickerFormat: String,
    val currentPagerDate: LocalDate?,
    val hasDividers: Boolean,
    val selectorBackgroundRadius: Dp,
    val maxWidth: Dp,
    val weekStartDay: DayOfWeek,
)

sealed class DayOption {
    data class Disabled(val selectable: Boolean = false) : DayOption()
    object Default : DayOption()

    val isClickable: Boolean
        get() = when (this) {
            Default -> true
            is Disabled -> selectable
        }

    val hasDisabledStyle: Boolean
        get() = when (this) {
            Default -> false
            is Disabled -> true
        }
}

enum class WeekDaysMode {
    SingleLetter, DoubleLetter
}

enum class ExtraButtonHelperType {
    /**
     * Button to navigate to current month.
     */
    Today,

    /**
     * Navigation buttons to go to next and previous months.
     */
    MonthChevrons
}

internal val Config: CalendarContentConfig
    @Composable
    @ReadOnlyComposable
    get() = LocalCalendarConfig.current

internal val LocalCalendarConfig = staticCompositionLocalOf {
    CalendarDefaults.defaultContentConfig()
}
