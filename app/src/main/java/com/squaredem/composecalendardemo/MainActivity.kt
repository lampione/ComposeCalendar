package com.squaredem.composecalendardemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.squaredem.composecalendar.ComposeCalendar
import com.squaredem.composecalendardemo.ui.theme.ComposeCalendarDemoTheme
import kotlinx.datetime.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeCalendarDemoTheme {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    MainActivityContent()
                }
            }
        }
    }
}

@Composable
private fun MainActivityContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        val showDialog = rememberSaveable { mutableStateOf(false) }
        val selectedDateMillis = rememberSaveable { mutableStateOf<LocalDate?>(null) }

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            selectedDateMillis.value?.let {
                Text(text = it.toString())
            }

            Button(onClick = { showDialog.value = true }) {
                Text("Show dialog")
            }
        }

        if (showDialog.value) {
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
            ComposeCalendar(
                startDate = today,
                minDate = today,
                maxDate = today.plus(100, DateTimeUnit.YEAR),
                onDone = { it: LocalDate ->
                    selectedDateMillis.value = it
                    showDialog.value = false
                },
                onDismiss = { showDialog.value = false }
            )
        }

    }
}

@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    ComposeCalendarDemoTheme {
        MainActivityContent()
    }
}