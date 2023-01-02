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
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.util.*

actual fun LocalDate.defaultFormat(): String {
    val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
    return formatter.format(toJavaLocalDate())
}

actual fun LocalDate.monthYearFormat(): String {
    val pagerMonthFormat = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())
    return pagerMonthFormat.format(toJavaLocalDate())
}

actual fun DayOfWeek.getDisplayName(): String {
    return getDisplayName(TextStyle.NARROW, Locale.getDefault())
}