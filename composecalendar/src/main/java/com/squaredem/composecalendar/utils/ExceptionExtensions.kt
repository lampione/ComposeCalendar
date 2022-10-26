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

package com.squaredem.composecalendar.utils

import android.util.Log
import com.squaredem.composecalendar.BuildConfig
import kotlinx.coroutines.CancellationException

fun Throwable.isCancelledCoroutine() = this is CancellationException

fun Throwable.customLog(message: String, verbose: Boolean = false) {
    if (!isCancelledCoroutine() && BuildConfig.DEBUG) {
        printStackTrace()
    }
    if (verbose || !isCancelledCoroutine()) {
        logDebugWarning(message)
    }
}

internal fun logDebugWarning(message: String) {
    if (BuildConfig.DEBUG) {
        Log.w(SDK_TAG, message)
    }
}
