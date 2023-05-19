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
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.squaredem.composecalendar.composable.CalendarDay
import com.squaredem.composecalendar.model.DateWrapper

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Rule
import java.time.LocalDate
import java.time.Month

@RunWith(AndroidJUnit4::class)
class CalendarDayTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun onTap_CalendarDay_whenCurrentMonthAndDateInRange() {
        var selectedDate: LocalDate? = null
        val targetDate = LocalDate.of(2023, Month.FEBRUARY, 1)

        val date = DateWrapper(
            localDate = targetDate,
            isSelectedDay = false,
            isCurrentDay = false,
            isCurrentMonth = true,
            isInDateRange = true
        )

        val onSelected: (LocalDate) -> Unit = {
            selectedDate = it
        }

        composeRule.setContent {
            CalendarDay(
                date = date,
                onSelected = onSelected
            )
        }

        composeRule.onNodeWithText("1").performClick()

        assertNotNull(selectedDate)
        assertEquals(2023, selectedDate!!.year)
        assertEquals(Month.FEBRUARY, selectedDate!!.month)
        assertEquals(1, selectedDate!!.dayOfMonth)
    }

    @Test
    fun onTap_CalendarDay_whenNotCurrentMonthAndNotDateInRange() {
        var selectedDate: LocalDate? = null
        val targetDate = LocalDate.of(2023, Month.FEBRUARY, 1)

        val date = DateWrapper(
            localDate = targetDate,
            isSelectedDay = false,
            isCurrentDay = false,
            isCurrentMonth = false,
            isInDateRange = false
        )

        val onSelected: (LocalDate) -> Unit = {
            selectedDate = it
        }

        composeRule.setContent {
            CalendarDay(
                date = date,
                onSelected = onSelected
            )
        }

        composeRule.onNodeWithText("1").performClick()

        assertNull(selectedDate)
    }

}