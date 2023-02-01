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

import com.squaredem.composecalendar.utils.formatWithFormatter
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Locale

/**
 * Date formatter builders for popup dialogs, [singleDate] and [dateRange] options available.
 */
object DefaultTitleFormatters {
    /**
     * @param dateFormat format used on the title.
     * @param emptyTitle title used when no date is selected.
     */
    fun singleDate(
        dateFormat: String = "MMM DD yyyy",
        emptyTitle: String = "Selected date",
    ): (LocalDate?) -> String = { date ->
        date?.let {
            val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
            it.formatWithFormatter(formatter)
        } ?: emptyTitle
    }

    /**
     * @param dateFormat format used when a single date is shown or when the years from start to
     *  end dates are different.
     * @param dateFormatWithoutYear format used when the start and end dates have the same year.
     * @param emptyTitle title used when no date is selected.
     * @param dateJoiner string used to join the start and end dates.
     */
    fun dateRange(
        dateFormat: String = "MMM dd yyyy",
        dateFormatWithoutYear: String = "MMM dd",
        emptyTitle: String = "Selected date",
        dateJoiner: String = " - ",
    ): (DateRangeSelection?) -> String = { range ->
        if (range == null) {
            emptyTitle
        } else {
            val (start, end) = range
            val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
            when {
                end != null -> {
                    buildString {
                        if (start.year == end.year) {
                            val simple =
                                SimpleDateFormat(dateFormatWithoutYear, Locale.getDefault())
                            append(start.formatWithFormatter(simple))
                        } else {
                            append(start.formatWithFormatter(formatter))
                        }
                        append(dateJoiner)
                        append(end.formatWithFormatter(formatter))
                    }
                }

                else -> {
                    start.formatWithFormatter(formatter)
                }
            }
        }
    }
}
