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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
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
import com.squaredem.composecalendar.model.CalendarColors
import com.squaredem.composecalendar.model.CalendarContentConfig
import com.squaredem.composecalendar.model.CalendarDefaults
import com.squaredem.composecalendar.model.CalendarMode
import com.squaredem.composecalendar.model.ColorScheme
import com.squaredem.composecalendar.model.Config
import com.squaredem.composecalendar.model.ExtraButtonHelperType
import com.squaredem.composecalendar.model.LocalCalendarColorScheme
import com.squaredem.composecalendar.model.LocalCalendarConfig
import com.squaredem.composecalendar.utils.LogCompositions
import com.squaredem.composecalendar.utils.assertValidPageOrNull
import com.squaredem.composecalendar.utils.closestValidRange
import com.squaredem.composecalendar.utils.customLog
import com.squaredem.composecalendar.utils.daysOfWeekFromDay
import com.squaredem.composecalendar.utils.getText
import com.squaredem.composecalendar.utils.logDebugWarning
import com.squaredem.composecalendar.utils.nextPage
import com.squaredem.composecalendar.utils.previousPage
import java.time.LocalDate
import java.util.*
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class, ExperimentalAnimationApi::class)
@Composable
internal fun CalendarContent(
    mode: CalendarMode,
    onChanged: (CalendarMode) -> Unit,
    modifier: Modifier = Modifier,
    contentConfig: CalendarContentConfig = CalendarDefaults.defaultContentConfig(),
    calendarColors: CalendarColors = CalendarDefaults.defaultColors(),
) {
    LogCompositions("CalendarContent")
    CompositionLocalProvider(
        LocalCalendarColorScheme provides calendarColors,
        LocalCalendarConfig provides contentConfig,
    ) {
        val dateRange by remember(mode) {
            derivedStateOf { getDateRange(mode.minDate, mode.maxDate) }
        }
        val dateRangeByYear = dateRange.step(DateRangeStep.Year(1))
        val totalPageCount = dateRange.count()
        val initialPage = getStartPage(mode.startDate, dateRange, totalPageCount)
        var isPickingYear by remember { mutableStateOf(false) }

        // for display only, used in CalendarMonthYearSelector
        var currentPagerDate by remember { mutableStateOf(LocalDate.now()) }
        val selectedYear by remember(mode) {
            derivedStateOf { currentPagerDate.year }
        }
        val pagerState = rememberPagerState(initialPage ?: 0)
        val coroutineScope = rememberCoroutineScope()

        val gridState = with(dateRangeByYear.indexOfFirst { it.year == selectedYear }) {
            rememberLazyGridState(initialFirstVisibleItemIndex = coerceAtLeast(0))
        }

        val setCurrentMode: (CalendarMode) -> Unit = {
            onChanged(it)
        }

        val isTodayAvailable by remember {
            derivedStateOf {
                val today = LocalDate.now()
                today.isAfter(dateRange.start) && today.isBefore(dateRange.endInclusive)
            }
        }

        if (!LocalInspectionMode.current) {
            LaunchedEffect(pagerState) {
                snapshotFlow { pagerState.currentPage }.collect { page ->
                    val currentDate = getDateFromCurrentPage(page, dateRange)
                    currentPagerDate = currentDate
                }
            }
        }

        val jumpToPageDate = Config.currentPagerDate
        LaunchedEffect(jumpToPageDate) {
            jumpToPageDate?.let {
                    val range = dateRange
                    range.indexOfFirst {
                        it.withDayOfMonth(1) == jumpToPageDate.withDayOfMonth(1)
                    }.assertValidPageOrNull(pagerState)?.let {
                        pagerState.scrollToPage(it)
                    }
            }
        }

        Column(
            modifier = Modifier
                .wrapContentHeight()
                .widthIn(max = contentConfig.maxWidth)
                .then(modifier),
        ) {
            if (contentConfig.showSelectedDateTitle) {
                CalendarTopBar(mode)
            }
            val scope = rememberCoroutineScope()
            CalendarMonthYearSelector(
                pagerDate = currentPagerDate,
                dateFormat = contentConfig.calendarYearPickerFormat,
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
                                "Trying to get page and failed ${pagerState.currentPage}.",
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
                onGoToToday = {
                    scope.launch {
                        val range = dateRange
                        range.indexOfFirst {
                            it.withDayOfMonth(1) == LocalDate.now().withDayOfMonth(1)
                        }.assertValidPageOrNull(pagerState)?.let {
                            pagerState.scrollToPage(it)
                        }
                    }
                },
                isNextMonthEnabled = pagerState.nextPage() != null,
                isPreviousMonthEnabled = pagerState.previousPage() != null,
                isMonthSelectorVisible = !isPickingYear &&
                    contentConfig.extraButtonHelper == ExtraButtonHelperType.MonthChevrons,
                isTodayButtonVisible = !isPickingYear && isTodayAvailable &&
                    contentConfig.extraButtonHelper == ExtraButtonHelperType.Today,
                todayTitle = contentConfig.todayTitle,
            )

            var minHeight by remember { mutableStateOf(375.dp) }
            val density = LocalDensity.current
            AnimatedContent(
                targetState = isPickingYear,
            ) { isYearPicker ->
                when (isYearPicker) {
                    true -> {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            if (Config.hasDividers) {
                                Divider(
                                    modifier = Modifier.fillMaxWidth(),
                                    thickness = 1.dp,
                                    color = ColorScheme.dividerColor,
                                )

                                Spacer(modifier = Modifier.size(8.dp))
                            }

                            CalendarYearGrid(
                                gridState = gridState,
                                dateRangeByYear = dateRangeByYear,
                                selectedYear = currentPagerDate.year,
                                currentYear = LocalDate.now().year,
                                onYearSelected = { year ->
                                    val currentMonth = getDateFromCurrentPage(
                                        currentPage = pagerState.currentPage,
                                        dateRange = dateRange,
                                    )?.month ?: mode.startDate.month
                                    coroutineScope.launch {
                                        dateRange
                                            .indexOfFirst {
                                                it.year == year && it.month == currentMonth
                                            }
                                            .assertValidPageOrNull(pagerState)
                                            .closestValidRange(
                                                date = LocalDate.of(year, currentMonth, 1),
                                                maxDate = mode.maxDate,
                                                minDate = mode.minDate,
                                                maxIndex = dateRange.count() - 1,
                                            )
                                            ?.let {
                                                currentPagerDate = dateRange.toList()[it]
                                                pagerState.scrollToPage(it)
                                            }
                                    }
                                    isPickingYear = false
                                },
                                modifier = Modifier.height(minHeight)
                            )

                            if (Config.hasDividers) {
                                Spacer(modifier = Modifier.size(8.dp))

                                Divider(
                                    modifier = Modifier.fillMaxWidth(),
                                    thickness = 1.dp,
                                    color = ColorScheme.dividerColor,
                                )
                            }
                        }
                    }
                    false -> {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            if (Config.hasDividers) {
                                Divider(
                                    modifier = Modifier.fillMaxWidth(),
                                    thickness = 1.dp,
                                    color = ColorScheme.dividerColor,
                                )

                                Spacer(modifier = Modifier.size(12.dp))
                            }
                            Column(
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier
                                    .padding(vertical = 8.dp)
                                    // Give it max height so it does not crash apps with scrolling.
                                    .heightIn(max = Config.maxWidth * 2)
                                    .onGloballyPositioned {
                                        minHeight = with(density) { it.size.height.toDp() }
                                    },
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    daysOfWeekFromDay(Config.weekStartDay).forEach {
                                        Text(
                                            modifier = Modifier.weight(1f),
                                            text = contentConfig.weekDaysMode.getText(it),
                                            textAlign = TextAlign.Center,
                                            fontWeight = FontWeight.SemiBold,
                                            color = ColorScheme.dayOfWeek,
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
                                            mode = mode,
                                            onSelected = setCurrentMode,
                                            showCurrentMonthOnly = false,
                                            calendarDayOption = contentConfig.calendarDayOption,
                                        )
                                    }
                                }
                            }
                            if (Config.hasDividers) {
                                Divider(
                                    modifier = Modifier.fillMaxWidth(),
                                    thickness = 1.dp,
                                    color = ColorScheme.dividerColor,
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
