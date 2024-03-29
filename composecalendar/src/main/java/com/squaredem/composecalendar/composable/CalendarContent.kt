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

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Surface
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.squaredem.composecalendar.daterange.DateRange
import com.squaredem.composecalendar.daterange.DateRangeStep
import com.squaredem.composecalendar.daterange.rangeTo
import com.squaredem.composecalendar.utils.LogCompositions
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun CalendarContent(
    startDate: LocalDate,
    minDate: LocalDate,
    maxDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    showSelectedDate: Boolean,
    selectedDateFormat: DateFormat
) {
    LogCompositions("CalendarContent")

    val dateRange = getDateRange(minDate, maxDate)
    val dateRangeByYear = dateRange.step(DateRangeStep.Year(1))
    val totalPageCount = dateRange.count()
    val initialPage = getStartPage(startDate, dateRange, totalPageCount)

    val isPickingYear = remember { mutableStateOf(false) }

    // for display only, used in CalendarMonthYearSelector
    val currentPagerDate = remember { mutableStateOf(startDate.withDayOfMonth(1)) }

    val selectedDate = remember { mutableStateOf(startDate) }

    val pagerState = rememberPagerState(initialPage)
    val coroutineScope = rememberCoroutineScope()
    val gridState = with(dateRangeByYear.indexOfFirst { it.year == selectedDate.value.year }) {
        rememberLazyGridState(initialFirstVisibleItemIndex = this)
    }

    val setSelectedDate: (LocalDate) -> Unit = {
        onDateSelected(it)
        selectedDate.value = it
    }

    if (!LocalInspectionMode.current) {
        LaunchedEffect(pagerState) {
            snapshotFlow { pagerState.currentPage }.collect { page ->
                val currentDate = getDateFromCurrentPage(page, dateRange)
                currentPagerDate.value = currentDate
            }
        }
    }

    Column(
        modifier = Modifier.wrapContentHeight(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (showSelectedDate) {
            CalendarTopBar(
                selectedDate = selectedDate.value,
                dateFormat = selectedDateFormat
            )
        }

        CalendarMonthYearSelector(
            pagerDate = currentPagerDate.value,
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

        Box {

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    DayOfWeek.values().forEach {
                        Text(
                            modifier = Modifier.weight(1f),
                            text = it.getDisplayName(TextStyle.NARROW, Locale.getDefault()),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                HorizontalPager(
                    pageCount = totalPageCount,
                    state = pagerState
                ) { page ->
                    val currentDate = getDateFromCurrentPage(page, dateRange)
                    currentDate?.let {
                        // grid
                        CalendarGrid(
                            it.withDayOfMonth(1),
                            dateRange,
                            selectedDate.value,
                            setSelectedDate
                        )
                    }
                }

            }

            if (isPickingYear.value) {
                Surface {
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
        it.year == startDate.year && it.monthValue == startDate.monthValue
    }
    return if (indexOfRange != -1) indexOfRange else pageCount / 2
}

private fun getDateRange(min: LocalDate, max: LocalDate): DateRange {
    val lowerBound = with(min) {
        val year = with(LocalDate.now().minusYears(100).year) {
            100.0 * (floor(abs(this / 100.0)))
        }
        coerceAtLeast(
            LocalDate.now().withYear(year.toInt()).withDayOfYear(1)
        )
    }
    val upperBound = with(max) {
        val year = with(LocalDate.now().year) {
            100.0 * (ceil(abs(this / 100.0)))
        }
        coerceAtMost(LocalDate.now().withYear(year.toInt())).apply {
            withDayOfYear(this.lengthOfYear())
        }
    }
    return lowerBound.rangeTo(upperBound) step DateRangeStep.Month()
}

private fun getDateFromCurrentPage(
    currentPage: Int,
    dateRange: DateRange,
): LocalDate? {
    return try {
        dateRange.elementAt(currentPage)
    } catch (e: Exception) {
        null
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    CalendarContent(
        startDate = LocalDate.now(),
        minDate = LocalDate.now(),
        maxDate = LocalDate.MAX,
        onDateSelected = {},
        showSelectedDate = true,
        selectedDateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT)
    )
}
