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

package com.squaredem.composecalendar.model

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object SelectedDateViewDefaults {
    fun defaultConfig(
        dateFormat: String = "EEE, MMM dd",
    ) = SelectedDateViewConfig(
        dateFormat = dateFormat,
    )

    @Composable
    fun defaultColors(
        highlightColor: Color = MaterialTheme.colorScheme.primary,
        neutralColor: Color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.15f),
        iconTint: Color = MaterialTheme.colorScheme.onBackground,
        errorColor: Color = MaterialTheme.colorScheme.error.copy(alpha = 0.70f),
    ) = SelectedDateViewColors(
        highlightColor = highlightColor,
        neutralColor = neutralColor,
        iconTint = iconTint,
        errorColor = errorColor,
    )

    @Composable
    fun defaultTextStyles(
        value: TextStyle = TextStyle(
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Medium,
        ),
        label: TextStyle = TextStyle(
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            fontSize = 12.sp,
            lineHeight = 14.sp,
            fontWeight = FontWeight.Normal,
        ),
        hint: TextStyle = TextStyle(
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            fontSize = 16.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Medium,
        ),
    ) = SelectedDateViewTextStyles(
        value = value,
        label = label,
        hint = hint,
    )
}

data class SelectedDateViewConfig(
    val dateFormat: String,
)

data class SelectedDateViewColors(
    val highlightColor: Color,
    val neutralColor: Color,
    val iconTint: Color,
    val errorColor: Color,
)

data class SelectedDateViewTextStyles(
    val label: TextStyle,
    val hint: TextStyle,
    val value: TextStyle,
)
