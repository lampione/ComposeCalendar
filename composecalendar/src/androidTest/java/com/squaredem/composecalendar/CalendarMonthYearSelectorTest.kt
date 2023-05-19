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

package com.squaredem.composecalendar

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.squaredem.composecalendar.composable.CalendarMonthYearSelector
import org.junit.Rule
import org.junit.Test
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Month
import java.util.Locale

class CalendarMonthYearSelectorTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun onTap_MonthYearChip_callLambda() {
        var isChipClicked = false

        val pagerDate = LocalDate.of(2023, Month.FEBRUARY, 1)
        val format = SimpleDateFormat("MMMM yyyy", Locale.ENGLISH)

        val onChipClicked = { isChipClicked = true }

        composeRule.setContent {
            CalendarMonthYearSelector(
                pagerDate = pagerDate,
                onChipClicked = onChipClicked,
                onNextMonth = {},
                onPreviousMonth = {},
                pagerMonthFormat = format
            )
        }

        composeRule.onNodeWithText("february 2023", ignoreCase = true).performClick()

        assert(isChipClicked)
    }

    @Test
    fun onTap_previousMonthChevron_callLambda() {
        var previousMonthTapped = false

        val onPreviousMonth = { previousMonthTapped = true }

        composeRule.setContent {
            CalendarMonthYearSelector(
                pagerDate = LocalDate.now(),
                onChipClicked = {},
                onNextMonth = {},
                onPreviousMonth = onPreviousMonth
            )
        }

        composeRule.onNodeWithContentDescription("ChevronLeft").performClick()

        assert(previousMonthTapped)
    }

    @Test
    fun onTap_nextMonthChevron_callLambda() {
        var nextMonthTapped = false

        val onNextMonth = { nextMonthTapped = true }

        composeRule.setContent {
            CalendarMonthYearSelector(
                pagerDate = LocalDate.now(),
                onChipClicked = {},
                onNextMonth = onNextMonth,
                onPreviousMonth = {}
            )
        }

        composeRule.onNodeWithContentDescription("ChevronRight").performClick()

        assert(nextMonthTapped)
    }

}
