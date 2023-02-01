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

package com.squaredem.composecalendar

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.squaredem.composecalendar.composable.CalendarContent
import com.squaredem.composecalendar.model.CalendarMode
import com.squaredem.composecalendar.model.CalendarColors
import com.squaredem.composecalendar.model.CalendarContentConfig
import com.squaredem.composecalendar.model.CalendarDefaults

/**
 * Display a single date picker calendar in place.
 */
@Composable
fun SingleDatePicker(
    mode: CalendarMode.Single,
    onChanged: (CalendarMode.Single) -> Unit,
    modifier: Modifier = Modifier,
    contentConfig: CalendarContentConfig = CalendarDefaults.defaultContentConfig(),
    calendarColors: CalendarColors = CalendarDefaults.defaultColors(),
) {
    CalendarContent(
        mode = mode,
        onChanged = { onChanged(it as CalendarMode.Single) },
        modifier = modifier,
        contentConfig = contentConfig,
        calendarColors = calendarColors,
    )
}
