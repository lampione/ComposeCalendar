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

import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import java.time.LocalDate

@OptIn(ExperimentalPagerApi::class)
/**
 * Return pager next page index or null if there is no next page available.
 */
internal fun PagerState.nextPage(): Int? = (currentPage + 1).assertValidPageOrNull(this)

@OptIn(ExperimentalPagerApi::class)
/**
 * Return pager previous page index or null if there is no previous page.
 */
internal fun PagerState.previousPage(): Int? = (currentPage - 1).assertValidPageOrNull(this)

@OptIn(ExperimentalPagerApi::class)
/**
 * Assert if the value is a valid page index for pager. When pager is null asserts if page is
 * bigger than 0.
 */
internal fun Int.assertValidPageOrNull(pagerState: PagerState? = null): Int? =
    takeIf { currentPage ->
        val hasValidPage = pagerState?.let { currentPage < it.pageCount } ?: true
        hasValidPage && currentPage >= 0
    }

/**
 * Compute closes valid index range for dates. When the date value is lower than the lower bound
 * value it returns 0, when is bigger it returns the maximum index value.
 */
internal fun Int?.closestValidRange(
    date: LocalDate,
    maxDate: LocalDate,
    minDate: LocalDate,
    maxIndex: Int,
): Int? = if (date < minDate) {
    0
} else if (date > maxDate) {
    maxIndex
} else {
    this
}
