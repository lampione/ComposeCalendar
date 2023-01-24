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

// FIXME: all of the JS conversions are broken. fix them
actual fun LocalDate.headlineFormat(): String {
    // TODO: make this work for non-US locales
    //   https://stackoverflow.com/questions/74995307
    return "${dayOfWeek.name.slice(0..2)}, ${month.name.slice(0..2)} $dayOfMonth"
}

actual fun LocalDate.monthYearFormat(): String {
    return "${month.name} $year"
}

actual fun DayOfWeek.getFirstLetter(): String {
    return name.slice(0..0)
}

actual fun formatInput(date: LocalDate): String {
    return "${date.month.ordinal}/${date.dayOfMonth}/${date.year}"
}

actual fun parseInput(input: String): LocalDate? {
    return LocalDate.parse(input)
}
