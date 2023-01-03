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

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.squaredem.composecalendar.utils.LogCompositions
import com.squaredem.composecalendar.utils.monthYearFormat
import kotlinx.datetime.LocalDate

@Composable
internal fun CalendarMonthYearSelector(
    pagerDate: LocalDate,
    onClick: () -> Unit,
    onNextMonth: () -> Unit,
    onPreviousMonth: () -> Unit,
    expanded: Boolean,
) {
    LogCompositions("CalendarMonthYearSelector")

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp).height(40.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // This should be DropdownMenuItem when it is implemented all on targets
        // See https://github.com/JetBrains/compose-jb/issues/2037
        TextButton(
            onClick = onClick
        ) {
            Row(
                Modifier.height(24.dp)
            ) {
                Text(
                    text = pagerDate.monthYearFormat(),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.width(8.dp))
                RotatableIcon(
                    imageVector = Icons.Default.ArrowDropDown,
                    isRotated = expanded,
                    contentDescription = if (expanded) "Hide menu" else "Show menu",
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
        Spacer(modifier = Modifier.weight(1F))
        IconButton(onClick = onPreviousMonth) {
            Icon(Icons.Default.ChevronLeft, "ChevronLeft", Modifier.size(24.dp))
        }
        IconButton(onClick = onNextMonth) {
            Icon(Icons.Default.ChevronRight, "ChevronRight", Modifier.size(24.dp))
        }
    }
}

@Composable
internal fun RotatableIcon(
    imageVector: ImageVector,
    isRotated: Boolean,
    contentDescription: String?,
    tint: Color,
    modifier: Modifier = Modifier,
) {
    val transitionState = remember { MutableTransitionState(isRotated) }
    transitionState.targetState = isRotated
    val transition = updateTransition(transitionState, label = "transition")
    val degrees by transition.animateFloat(
        transitionSpec = { tween() },
        label = "rotationDegreeTransition"
    ) {
        if (it) -180f else 0f
    }
    Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = modifier.rotate(degrees),
        tint = tint,
    )
}