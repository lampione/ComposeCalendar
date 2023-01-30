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

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
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
import com.squaredem.composecalendar.utils.assertValidPageOrNull
import com.squaredem.composecalendar.utils.closestValidRange
import com.squaredem.composecalendar.utils.customLog
import com.squaredem.composecalendar.utils.logDebugWarning
import com.squaredem.composecalendar.utils.nextPage
import com.squaredem.composecalendar.utils.previousPage
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class, ExperimentalAnimationApi::class)
@Composable
fun CalendarContent(
    mode: CalendarMode,
    onChanged: (CalendarMode) -> Unit,
    modifier: Modifier = Modifier,
    contentConfig: CalendarContentConfig = CalendarDefaults.defaultContentConfig(),
    calendarColors: CalendarColors = CalendarDefaults.defaultColors(),
) {
    LogCompositions("CalendarContent")

    var currentMode by remember { mutableStateOf(mode) }
    val dateRange by remember(currentMode) {
        derivedStateOf { getDateRange(mode.minDate, mode.maxDate) }
    }
    val dateRangeByYear = dateRange.step(DateRangeStep.Year(1))
    val totalPageCount = dateRange.count()
    val initialPage = getStartPage(mode.startDate, dateRange, totalPageCount)
    var isPickingYear by remember { mutableStateOf(false) }

    // for display only, used in CalendarMonthYearSelector
    val currentPagerDate =
        remember(currentMode) { mutableStateOf(mode.startDate.withDayOfMonth(1)) }
    val currentYear = remember(mode) { mutableStateOf(currentPagerDate.value.year) }
    val pagerState = rememberPagerState(initialPage ?: 0)
    val coroutineScope = rememberCoroutineScope()

    val selectedYear = when (val yearMode = currentMode) {
        is CalendarMode.Multi -> yearMode.selection?.startDate ?: yearMode.startDate
        is CalendarMode.Single -> yearMode.selectedDate ?: yearMode.startDate
    }.year
    val gridState = with(dateRangeByYear.indexOfFirst { it.year == selectedYear }) {
        rememberLazyGridState(initialFirstVisibleItemIndex = coerceAtLeast(0))
    }

    val setCurrentMode: (CalendarMode) -> Unit = {
        onChanged(it)
        currentMode = it
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
        modifier = Modifier
            .wrapContentHeight()
            .then(modifier),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (contentConfig.showSelectedDateTitle) {
            CalendarTopBar(currentMode)
        }

        CalendarMonthYearSelector(
            pagerDate = currentPagerDate.value,
            onChipClicked = { isPickingYear = !isPickingYear },
            onNextMonth = {
                coroutineScope.launch {
                    try {
                        pagerState.nextPage()?.let { newPage ->
                            pagerState.animateScrollToPage(newPage)
                        } ?: logDebugWarning(
                            "Trying to get next page and failed ${pagerState.currentPage}.",
                        )
                    } catch (e: Throwable) {
                        e.customLog(
                            """
                            Trying to animate to invalid NewPage: [${pagerState.nextPage()}] of
                            [$pagerState]
                        """.trimIndent()
                        )
                    }
                }
            },
            onPreviousMonth = {
                coroutineScope.launch {
                    try {
                        pagerState.previousPage()?.let { newPage ->
                            pagerState.animateScrollToPage(newPage)
                        } ?: logDebugWarning(
                            "Trying to get precious page and failed ${pagerState.currentPage}.",
                        )
                    } catch (e: Throwable) {
                        e.customLog(
                            """
                            Trying to animate to invalid NewPage: [${pagerState.nextPage()}] of
                            [$pagerState]
                        """.trimIndent()
                        )
                    }
                }
            },
            isNextMonthEnabled = pagerState.nextPage() != null,
            isPreviousMonthEnabled = pagerState.previousPage() != null,
            isMonthSelectorVisible = !isPickingYear,
            calendarColors = calendarColors,
        )

        val minHeight = 375.dp
        AnimatedContent(
            targetState = isPickingYear,
        ) { isYearPicker ->
            when (isYearPicker) {
                true -> {
                    CalendarYearGrid(
                        gridState = gridState,
                        dateRangeByYear = dateRangeByYear,
                        selectedYear = currentYear.value,
                        currentYear = currentMode.startDate.year,
                        onYearSelected = { year ->
                            val currentMonth = getDateFromCurrentPage(
                                currentPage = pagerState.currentPage,
                                dateRange = dateRange,
                            )?.month ?: currentMode.startDate.month
                            currentYear.value = year
                            coroutineScope.launch {
                                dateRange
                                    .indexOfFirst { it.year == year && it.month == currentMonth }
                                    .assertValidPageOrNull(pagerState)
                                    .closestValidRange(
                                        date = LocalDate.of(year, currentMonth, 1),
                                        maxDate = currentMode.maxDate,
                                        minDate = currentMode.minDate,
                                        maxIndex = dateRange.count() - 1,
                                    )
                                    ?.let { pagerState.scrollToPage(it) }
                            }
                            currentPagerDate.value = currentPagerDate.value.withYear(year)
                            isPickingYear = false
                        },
                        modifier = Modifier.height(minHeight)
                    )
                }
                false -> {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.height(minHeight),
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            DayOfWeek.values().forEach {
                                Text(
                                    modifier = Modifier.weight(1f),
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
                            state = pagerState,
                        ) { page ->
                            val currentDate = getDateFromCurrentPage(page, dateRange)
                            currentDate?.let {
                                // grid
                                CalendarGrid(
                                    pagerDate = it.withDayOfMonth(1),
                                    dateRange = dateRange,
                                    mode = currentMode,
                                    onSelected = setCurrentMode,
                                    showCurrentMonthOnly = false,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
private fun getStartPage(
    startDate: LocalDate,
    dateRange: DateRange,
    pageCount: Int
): Int? {
    if (startDate <= dateRange.start) {
        return 0
    }
    if (startDate >= dateRange.endInclusive) {
        return pageCount - 1
    }
    val indexOfRange = dateRange.indexOfFirst {
        it.year == startDate.year && it.monthValue == startDate.monthValue
    }
    return (if (indexOfRange != -1) indexOfRange else pageCount / 2)
        .assertValidPageOrNull()
        .closestValidRange(
            date = startDate,
            minDate = dateRange.first(),
            maxDate = dateRange.last(),
            maxIndex = dateRange.count() - 1,
        )
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
    } catch (e: Throwable) {
        null
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    CalendarContent(
        mode = CalendarMode.Single(
            selectedDate = LocalDate.now(),
            minDate = LocalDate.now(),
            maxDate = LocalDate.MAX,
        ),
        onChanged = {},
    )
}
