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

package com.squaredem.composecalendar.utils

import java.time.DayOfWeek
import kotlin.math.abs

fun DayOfWeek.isFirstDayOfWeek(firstWeekday: DayOfWeek): Boolean = this == firstWeekday

fun DayOfWeek.isLastDayOfWeek(firstWeekday: DayOfWeek): Boolean = firstWeekday.plus(6) == this

/**
 * Get the DayOfWeek values starting on DayOfWeek.
 *
 * @param dayOfWeek starting day of week.
 * @return list of days of the week starting on [dayOfWeek].
 */
fun daysOfWeekFromDay(dayOfWeek: DayOfWeek): List<DayOfWeek> {
    return (dayOfWeek.value until dayOfWeek.value + DayOfWeek.values().size).map {
        DayOfWeek.of(((it - 1) % DayOfWeek.values().size) + 1)
    }
}

/**
 * Calculate the distance from firstWeekday to lastWeekday in days.
 */
fun calculateWeekdayDistance(firstWeekday: DayOfWeek, secondWeekday: DayOfWeek): Int {
    val weekdays = DayOfWeek.values()
    val firstIndex = weekdays.indexOf(firstWeekday)
    val secondIndex = weekdays.indexOf(secondWeekday)
    return abs(firstIndex - secondIndex)
}
