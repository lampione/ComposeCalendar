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
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.util.*

actual fun LocalDate.headlineFormat(): String {
    // TODO: make this work for non-US locales
    //   https://stackoverflow.com/questions/74995307
    val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
    return formatter.format(toJavaLocalDate())
}

actual fun LocalDate.monthYearFormat(): String {
    val pagerMonthFormat = DateTimeFormatter.ofPattern("MMMM yyyy")
    return pagerMonthFormat.format(toJavaLocalDate())
}

actual fun DayOfWeek.getFirstLetter(): String {
    return getDisplayName(TextStyle.NARROW, Locale.getDefault())
}

actual fun LocalDate.inputFormat(): String {
    val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
    return formatter.format(toJavaLocalDate())
}

actual fun parseInput(input: String): LocalDate? {
    val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
    return try {
        java.time.LocalDate.parse(input, formatter).toKotlinLocalDate()
    } catch (tr: Throwable) {
        null
    }
}