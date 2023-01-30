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

package com.squaredem.composecalendar.composable

import java.time.LocalDate

/**
 * Configuration settings for [CalendarContent].
 *
 * @param showSelectedDateTitle should show title with the current date selection.
 * @param showCurrentMonthOnly should show days for the current month only.
 * @param calendarDayOption should apply different options for specific days.
 */
data class CalendarContentConfig(
    val showSelectedDateTitle: Boolean,
    val showCurrentMonthOnly: Boolean,
    val calendarDayOption: ((LocalDate) -> DayOption)?,
)

sealed class DayOption {
    data class Disabled(val selectable: Boolean = false) : DayOption()
    object Default : DayOption()
}
