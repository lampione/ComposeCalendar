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

package com.squaredem.composecalendar.daterange

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

internal class DateIterator(
    startDate: LocalDate,
    private val endDateInclusive: LocalDate,
    private val step: DateRangeStep
) : Iterator<LocalDate> {

    private var currentDate = startDate

    override fun hasNext(): Boolean = currentDate <= endDateInclusive

    override fun next(): LocalDate {
        val next = currentDate
        currentDate = getNextStep()
        return next
    }

    private fun getNextStep(): LocalDate {
        return currentDate.plus(1, step.toDateTimeUnit())
    }
}

internal class DateRange(
    override val start: LocalDate,
    override val endInclusive: LocalDate,
    private val step: DateRangeStep = DateRangeStep.DAY
) : Iterable<LocalDate>, ClosedRange<LocalDate> {

    override fun iterator(): Iterator<LocalDate> = DateIterator(start, endInclusive, step)

}

internal enum class DateRangeStep {
    DAY,
    MONTH,
    YEAR,
}

internal fun DateRangeStep.toDateTimeUnit(): DateTimeUnit.DateBased {
    return when (this) {
        DateRangeStep.DAY -> DateTimeUnit.DAY
        DateRangeStep.MONTH -> DateTimeUnit.MONTH
        DateRangeStep.YEAR -> DateTimeUnit.YEAR
    }
}

internal fun LocalDate.rangeTo(other: LocalDate, step: DateRangeStep = DateRangeStep.DAY) = DateRange(this, other, step)
