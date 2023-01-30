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

import com.squaredem.composecalendar.model.HighlightedType
import java.time.LocalDate

sealed class CalendarMode {
    val startDate: LocalDate
        get() = when (this) {
            is Multi -> selection?.startDate ?: LocalDate.now()
            is Single -> selectedDate ?: LocalDate.now()
        }
    abstract val minDate: LocalDate
    abstract val maxDate: LocalDate

    data class Single(
        override val minDate: LocalDate,
        override val maxDate: LocalDate,
        val selectedDate: LocalDate? = null,
    ) : CalendarMode()

    data class Multi(
        override val minDate: LocalDate,
        override val maxDate: LocalDate,
        val selection: DateRangeSelection? = null,
    ) : CalendarMode()

    fun onSelectedDay(localDate: LocalDate): CalendarMode {
        return when (this) {
            is Multi -> this.copy()
            is Single -> this.copy(
                selectedDate = localDate
            )
        }
    }
}

internal fun CalendarMode.highlightedTypeForDay(day: LocalDate): HighlightedType? = when (this) {
    is CalendarMode.Multi -> {
        when {
            selection == null -> null
            selection.endDate == null -> null
            day == selection.startDate -> HighlightedType.End
            day == selection.endDate -> HighlightedType.Start
            day > selection.startDate && day < selection.endDate -> HighlightedType.Full
            else -> null
        }
    }

    is CalendarMode.Single -> null
}

internal fun CalendarMode.hasSelectionIndicator(day: LocalDate): Boolean = when (this) {
    is CalendarMode.Multi -> day == selection?.startDate || day == selection?.endDate
    is CalendarMode.Single -> selectedDate == day
}

data class DateRangeSelection(
    val startDate: LocalDate,
    val endDate: LocalDate? = null,
)