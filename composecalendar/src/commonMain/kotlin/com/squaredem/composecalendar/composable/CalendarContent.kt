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

package com.squaredem.composecalendar.composable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.squaredem.composecalendar.daterange.DateRange
import com.squaredem.composecalendar.daterange.DateRangeStep
import com.squaredem.composecalendar.utils.LogCompositions
import com.squaredem.composecalendar.utils.getFirstLetter
import com.squaredem.composecalendar.utils.withDayOfMonth
import com.squaredem.composecalendar.utils.withYear
import kotlinx.coroutines.launch
import kotlinx.datetime.*

@OptIn(ExperimentalPagerApi::class)
@Composable
internal fun CalendarContent(
    selectedDate: LocalDate,
    minDate: LocalDate,
    maxDate: LocalDate,
    onSelected: (LocalDate) -> Unit,
) {
    LogCompositions("CalendarContent")

    val dateRange = DateRange(minDate, maxDate, DateRangeStep.Month())
    val dateRangeByYear = dateRange.step(DateRangeStep.Year(1))
    val totalPageCount = dateRange.count()
    val initialPage = remember { getStartPage(selectedDate, dateRange, totalPageCount) }

    var isPickingYear by remember { mutableStateOf(false) }

    // for display only, used in CalendarMonthYearSelector
    val currentPagerDate = remember { mutableStateOf(selectedDate) }

    val pagerState = rememberPagerState(initialPage)
    val coroutineScope = rememberCoroutineScope()
    val gridState = with(dateRangeByYear.indexOfFirst { it.year == selectedDate.year }) {
        rememberLazyGridState(initialFirstVisibleItemIndex = this)
    }

    if (!LocalInspectionMode.current) {
        LaunchedEffect(pagerState) {
            snapshotFlow { pagerState.currentPage }.collect { page ->
                val currentDate = getDateFromCurrentPage(page, dateRange)
                currentDate?.let { currentPagerDate.value = it }
            }
        }
    }

    Column {
        CalendarMonthYearSelector(
            currentPagerDate.value,
            onClick = { isPickingYear = !isPickingYear },
            onNextMonth = {
                coroutineScope.launch {
                    try {
                        val newPage = pagerState.currentPage + 1
                        pagerState.animateScrollToPage(newPage)
                    } catch (e: Exception) {
                        // avoid IndexOutOfBounds and animation crashes
                    }
                }
            },
            onPreviousMonth = {
                coroutineScope.launch {
                    try {
                        val newPage = pagerState.currentPage - 1
                        pagerState.animateScrollToPage(newPage)
                    } catch (e: Exception) {
                        // avoid IndexOutOfBounds and animation crashes
                    }
                }
            },
            expanded = isPickingYear
        )

        if (!isPickingYear) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DayOfWeek.values().forEach {
                    Box(
                        modifier = Modifier.size(40.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = it.getFirstLetter(),
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }

            HorizontalPager(
                count = totalPageCount,
                state = pagerState
            ) { page ->
                val currentDate = getDateFromCurrentPage(page, dateRange)
                currentDate?.let {
                    // grid
                    CalendarGrid(
                        it.withDayOfMonth(1),
                        dateRange,
                        selectedDate,
                        onSelected,
                        true,
                    )
                }
            }

        } else {

            val currentDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
            CalendarYearGrid(
                gridState = gridState,
                dateRangeByYear = dateRangeByYear,
                selectedYear = selectedDate.year,
                currentYear = currentDate.year,
                onYearSelected = { year ->
                    coroutineScope.launch {
                        val newPage = dateRange.indexOfFirst {
                            it.year == year && it.month == selectedDate.month
                        }
                        pagerState.scrollToPage(newPage)
                    }
                    currentPagerDate.value = currentPagerDate.value.withYear(year)
                    isPickingYear = false
                }
            )

        }
    }
}

private fun getStartPage(
    startDate: LocalDate,
    dateRange: DateRange,
    pageCount: Int
): Int {
    if (startDate <= dateRange.start) {
        return 0
    }
    if (startDate >= dateRange.endInclusive) {
        return pageCount
    }
    val indexOfRange = dateRange.indexOfFirst {
        it.year == startDate.year && it.monthNumber == startDate.monthNumber
    }
    return if (indexOfRange != -1) indexOfRange else pageCount / 2
}

private fun getDateFromCurrentPage(
    currentPage: Int,
    dateRange: DateRange,
): LocalDate? {
    return try {
        dateRange.elementAt(currentPage)
    } catch (e: Throwable) {
        null
    }
}

//@Preview(showBackground = true)
//@Composable
//private fun Preview() {
//    CalendarContent(
//        startDate = LocalDate.now(),
//        minDate = LocalDate.now(),
//        maxDate = LocalDate.MAX,
//        onSelected = {},
//    )
//}
