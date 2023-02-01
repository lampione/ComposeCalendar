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

import java.time.LocalDate
import java.time.temporal.ChronoUnit

sealed class CalendarMode {
    abstract val minDate: LocalDate
    abstract val maxDate: LocalDate

    val startDate: LocalDate
        get() = when (this) {
            is Range -> selection?.startDate ?: LocalDate.now()
            is Single -> selectedDate ?: LocalDate.now()
        }

    fun onSelectedDay(localDate: LocalDate): CalendarMode {
        return when (this) {
            is Range -> {
                onDayClicked(localDate)
            }

            is Single -> {
                copy(
                    selectedDate = localDate,
                )
            }
        }
    }

    data class Single(
        override val minDate: LocalDate,
        override val maxDate: LocalDate,
        val selectedDate: LocalDate? = null,
        val titleFormatter: (LocalDate?) -> String = DefaultTitleFormatters.singleDate(),
    ) : CalendarMode()

    data class Range(
        override val minDate: LocalDate,
        override val maxDate: LocalDate,
        val selection: DateRangeSelection? = null,
        val titleFormatter: (DateRangeSelection?) -> String = DefaultTitleFormatters.dateRange(),
    ) : CalendarMode()
}

internal fun CalendarMode.Range.onDayClicked(day: LocalDate): CalendarMode.Range = when {
    selection == null -> {
        copy(selection = DateRangeSelection(day))
    }

    day == selection.startDate && selection.endDate == null -> {
        copy(
            selection = null
        )
    }

    day.isBefore(selection.startDate) && selection.endDate == null -> {
        copy(
            selection = selection.copy(
                startDate = day,
                endDate = selection.startDate
            ),
        )
    }

    day.isBefore(selection.startDate) -> {
        copy(
            selection = selection.copy(startDate = day),
        )
    }

    selection.endDate == null || day.isAfter(selection.endDate) -> {
        copy(
            selection = selection.copy(endDate = day),
        )
    }

    selection.startDate == day -> {
        copy(
            selection = DateRangeSelection(
                startDate = selection.endDate
            )
        )
    }

    selection.endDate == day -> {
        copy(
            selection = DateRangeSelection(
                startDate = selection.startDate
            )
        )
    }

    day.isAfter(selection.startDate) && day.isBefore(selection.endDate) -> {
        val daysFromStart = ChronoUnit.DAYS.between(selection.startDate, day)
        val daysToEnd = ChronoUnit.DAYS.between(day, selection.endDate)

        if (daysFromStart < daysToEnd) {
            copy(
                selection = selection.copy(startDate = day),
            )
        } else {
            copy(
                selection = selection.copy(endDate = day),
            )
        }
    }

    else -> this
}

internal fun CalendarMode.highlightedTypeForDay(day: LocalDate): HighlightedType? = when (this) {
    is CalendarMode.Range -> {
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
    is CalendarMode.Range -> day == selection?.startDate || day == selection?.endDate
    is CalendarMode.Single -> selectedDate == day
}

data class DateRangeSelection(
    val startDate: LocalDate,
    val endDate: LocalDate? = null,
)
