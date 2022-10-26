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

import java.time.LocalDate

internal class DateIterator(
    startDate: LocalDate,
    private val endDateInclusive: LocalDate,
    private val step: DateRangeStep
) : Iterator<LocalDate> {

    private var currentDate = startDate

    override fun hasNext(): Boolean = when (step) {
        is DateRangeStep.Day -> currentDate <= endDateInclusive
        is DateRangeStep.Month -> currentDate.atFirstDay() <= endDateInclusive.atFirstDay()
        is DateRangeStep.Year -> currentDate.year <= endDateInclusive.year
    }

    override fun next(): LocalDate {
        val next = currentDate
        currentDate = getNextStep()
        return next
    }

    private fun getNextStep(): LocalDate {
        return when (step) {
            is DateRangeStep.Day -> currentDate.plusDays(step.value.toLong())
            is DateRangeStep.Month -> currentDate.plusMonths(step.value.toLong())
            is DateRangeStep.Year -> currentDate.plusYears(step.value.toLong())
        }
    }
}

internal class DateRange(
    override val start: LocalDate,
    override val endInclusive: LocalDate,
    private val step: DateRangeStep = DateRangeStep.Day()
) : Iterable<LocalDate>, ClosedRange<LocalDate> {

    override fun iterator(): Iterator<LocalDate> = DateIterator(start, endInclusive, step)

    infix fun step(step: DateRangeStep) = DateRange(start, endInclusive, step)
}

internal sealed class DateRangeStep(val value: Int) {
    class Day(value: Int = 1) : DateRangeStep(value)
    class Month(value: Int = 1) : DateRangeStep(value)
    class Year(value: Int = 1) : DateRangeStep(value)
}

internal operator fun LocalDate.rangeTo(other: LocalDate) = DateRange(this, other)

internal fun LocalDate.atFirstDay() = LocalDate.of(year, month, 1)
