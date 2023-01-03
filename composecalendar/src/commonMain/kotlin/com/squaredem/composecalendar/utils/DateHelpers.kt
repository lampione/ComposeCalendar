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

package com.squaredem.composecalendar.utils

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

expect fun LocalDate.headlineFormat(pattern: String): String

expect fun LocalDate.monthYearFormat(): String

expect fun DayOfWeek.getFirstLetter(): String

fun LocalDate.withDayOfMonth(day: Int): LocalDate {
    return LocalDate(year = year, monthNumber = monthNumber, dayOfMonth = day)
}

fun LocalDate.withYear(year: Int): LocalDate {
    return LocalDate(year = year, monthNumber = monthNumber, dayOfMonth = dayOfMonth)
}