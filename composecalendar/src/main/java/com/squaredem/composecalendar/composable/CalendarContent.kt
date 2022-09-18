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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.squaredem.composecalendar.daterange.DateRange
import com.squaredem.composecalendar.daterange.DateRangeStep
import com.squaredem.composecalendar.daterange.rangeTo
import com.squaredem.composecalendar.utils.LogCompositions
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*
import kotlin.math.abs
import kotlin.math.absoluteValue
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

    val dateRange = getDateRange(minDate, maxDate)
    val dateRangeByYear = dateRange.step(DateRangeStep.Year(1))
    val totalPageCount = dateRange.count()
    val initialPage = getStartPage(startDate, dateRange, totalPageCount)

    val isPickingYear = remember { mutableStateOf(false) }
    val currentPagerDate = remember { mutableStateOf(startDate.withDayOfMonth(1)) }
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
            snapshotFlow { pagerState.targetPage }.collect { page ->
                val pageDiff = page.minus(initialPage).absoluteValue.toLong()

                val date = if (page > initialPage) {
                    startDate.plusMonths(pageDiff)
                } else if (page < initialPage) {
                    startDate.minusMonths(pageDiff)
                } else {
                    startDate
                }

                currentPagerDate.value = date
            }
        }
    }

    Column(
        modifier = Modifier.wrapContentHeight(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CalendarTopBar(selectedDate.value)

        CalendarMonthYearSelector(
            coroutineScope,
            pagerState,
            currentPagerDate.value
        ) {
            isPickingYear.value = !isPickingYear.value
        }

        if (!isPickingYear.value) {

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                DayOfWeek.values().forEach {
                    Text(
                        modifier = Modifier
                            .weight(1f),
                        text = it.getDisplayName(
                            TextStyle.NARROW,
                            Locale.getDefault()
                        ),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            HorizontalPager(
                count = totalPageCount,
                state = pagerState
            ) { page ->
                val pageDiff = page.minus(initialPage).absoluteValue.toLong()

                val date = if (page > initialPage) {
                    startDate.plusMonths(pageDiff)
                } else if (page < initialPage) {
                    startDate.minusMonths(pageDiff)
                } else {
                    startDate
                }

                // grid
                CalendarGrid(
                    date.withDayOfMonth(1),
                    dateRange,
                    selectedDate.value,
                    setSelectedDate,
                    true
                )
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
    val indexOfRange = dateRange.indexOf(startDate.withDayOfMonth(1))
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

@Preview(showBackground = true)
@Composable
private fun Preview() {
    CalendarContent(
        startDate = LocalDate.now(),
        minDate = LocalDate.now(),
        maxDate = LocalDate.MAX,
        onSelected = {},
    )
}
