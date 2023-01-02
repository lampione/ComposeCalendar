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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.squaredem.composecalendar.daterange.DateRange
import com.squaredem.composecalendar.daterange.DateRangeStep
import com.squaredem.composecalendar.daterange.rangeTo
import com.squaredem.composecalendar.utils.LogCompositions
import com.squaredem.composecalendar.utils.getDisplayName
import com.squaredem.composecalendar.utils.withDayOfMonth
import com.squaredem.composecalendar.utils.withYear
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor

@OptIn(ExperimentalPagerApi::class)
@Composable
internal fun CalendarContent(
    startDate: LocalDate,
    minDate: LocalDate,
    maxDate: LocalDate,
    onSelected: (LocalDate) -> Unit,
) {
    LogCompositions("CalendarContent")

    val dateRange = DateRange(minDate, maxDate, DateRangeStep.Month())
    val dateRangeByYear = dateRange.step(DateRangeStep.Year(1))
    val totalPageCount = dateRange.count()
    val initialPage = getStartPage(startDate, dateRange, totalPageCount)

    val isPickingYear = remember { mutableStateOf(false) }

    // for display only, used in CalendarMonthYearSelector
    val currentPagerDate = remember { mutableStateOf(startDate) }

    val selectedDate = remember { mutableStateOf(startDate) }

    val pagerState = rememberPagerState(initialPage)
    val coroutineScope = rememberCoroutineScope()
    val gridState = with(dateRangeByYear.indexOfFirst { it.year == selectedDate.value.year }) {
        rememberLazyGridState(initialFirstVisibleItemIndex = this)
    }

    val setSelectedDate: (LocalDate) -> Unit = {
        onSelected(it)
        selectedDate.value = it
    }

    if (!LocalInspectionMode.current) {
        LaunchedEffect(pagerState) {
            snapshotFlow { pagerState.currentPage }.collect { page ->
                val currentDate = getDateFromCurrentPage(page, dateRange)
                currentDate?.let { currentPagerDate.value = it }
            }
        }
    }

    Column(
        modifier = Modifier.wrapContentHeight(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CalendarTopBar(selectedDate.value)

        CalendarMonthYearSelector(
            currentPagerDate.value,
            onChipClicked = { isPickingYear.value = !isPickingYear.value },
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
            }
        )

        if (!isPickingYear.value) {

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                DayOfWeek.values().forEach {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = it.getDisplayName(),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold
                    )
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
                        selectedDate.value,
                        setSelectedDate,
                        true
                    )
                }
            }

        } else {

            CalendarYearGrid(
                gridState = gridState,
                dateRangeByYear = dateRangeByYear,
                selectedYear = selectedDate.value.year,
                currentYear = startDate.year,
                onYearSelected = { year ->
                    coroutineScope.launch {
                        val newPage = dateRange.indexOfFirst {
                            it.year == year && it.month == selectedDate.value.month
                        }
                        pagerState.scrollToPage(newPage)
                    }
                    currentPagerDate.value = currentPagerDate.value.withYear(year)
                    isPickingYear.value = false
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