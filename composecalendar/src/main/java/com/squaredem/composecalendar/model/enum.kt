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

/**
 * Force ranged selection mode.
 */
enum class ForcedSelectMode {
    /**
     * Force Start date change.
     *
     * The next date selection will change the start date. When the selected date is after the
     * end date, selection will be reset and the result selection will contain only start date.
     *
     */
    StartDate,

    /**
     * Force End date change.
     *
     * The next selection will change the end date. If the selected date is before the start date
     * start date will move to that date.
     */
    EndDate;
}
