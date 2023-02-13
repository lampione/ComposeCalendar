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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EditCalendar
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.squaredem.composecalendar.model.SelectedDateViewColors
import com.squaredem.composecalendar.model.SelectedDateViewConfig
import com.squaredem.composecalendar.model.SelectedDateViewDefaults
import com.squaredem.composecalendar.model.SelectedDateViewTextStyles
import com.squaredem.composecalendar.utils.formatWithFormatter
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Locale

@Composable
fun SelectedDateView(
    value: LocalDate?,
    modifier: Modifier = Modifier,
    highlighted: Boolean = false,
    label: String? = null,
    hint: String? = null,
    config: SelectedDateViewConfig = SelectedDateViewDefaults.defaultConfig(),
    colors: SelectedDateViewColors = SelectedDateViewDefaults.defaultColors(),
    textStyles: SelectedDateViewTextStyles = SelectedDateViewDefaults.defaultTextStyles(),
    leadingIcon: @Composable () -> Unit = {
        Icon(Icons.Outlined.EditCalendar, contentDescription = "day", tint = colors.iconTint)
    }
) {
    val labelText = when {
        value != null && label != null -> label
        else -> ""
    }

    val hintText = when {
        value == null && hint != null -> hint
        value == null && label != null -> label
        else -> ""
    }

    val dateText = when {
        value != null -> value.formatWithFormatter(
            formatter = SimpleDateFormat(config.dateFormat, Locale.getDefault())
        )

        else -> ""
    }

    Column(modifier = modifier) {
        Row(modifier = Modifier.heightIn(16.dp)) {
            AnimatedVisibility(
                visible = labelText.isNotBlank(),
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Text(
                    text = labelText,
                    style = textStyles.label,
                )
            }
        }

        Row(
            modifier = Modifier
                .heightIn(40.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            leadingIcon()
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart,
            ) {
                androidx.compose.animation.AnimatedVisibility(
                    visible = dateText.isNotBlank(),
                    modifier = Modifier.heightIn(16.dp),
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    Text(
                        text = dateText,
                        style = textStyles.value,
                    )
                }

                androidx.compose.animation.AnimatedVisibility(
                    visible = hintText.isNotBlank(),
                    modifier = Modifier.heightIn(16.dp),
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    Text(
                        text = hintText,
                        style = textStyles.hint,
                    )
                }
            }
        }

        Box(modifier = Modifier.fillMaxWidth()) {
            androidx.compose.animation.AnimatedVisibility(
                visible = highlighted,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Divider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 2.dp,
                    color = colors.highlightColor,
                )
            }

            androidx.compose.animation.AnimatedVisibility(
                visible = !highlighted,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Divider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 1.dp,
                    color = colors.neutralColor,
                )
            }
        }
    }
}
