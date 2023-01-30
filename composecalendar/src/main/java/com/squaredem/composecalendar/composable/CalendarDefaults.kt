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

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import java.time.LocalDate

object CalendarDefaults {
    fun defaultContentConfig(
        showSelectedDateTitle: Boolean = true,
        showCurrentMonthOnly: Boolean = false,
        calendarDayOption: ((LocalDate) -> DayOption)? = null,
    ) = CalendarContentConfig(
        showSelectedDateTitle = showSelectedDateTitle,
        showCurrentMonthOnly = showCurrentMonthOnly,
        calendarDayOption = calendarDayOption,
    )

    @Composable
    fun defaultColors(
        monthChevronColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    ) = CalendarColors(
        monthChevronColor = monthChevronColor,
    )
}
