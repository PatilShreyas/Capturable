/*
* MIT License
*
* Copyright (c) 2022 Shreyas Patil
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*
*/
package dev.shreyaspatil.capturableExample

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import capturable.composeapp.generated.resources.Res
import capturable.composeapp.generated.resources.ic_baseline_check_circle_24
import capturable.composeapp.generated.resources.ic_baseline_qr_code_24
import dev.shreyaspatil.capturable.capturable
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import dev.shreyaspatil.capturableExample.ui.theme.CapturableExampleTheme
import dev.shreyaspatil.capturableExample.ui.theme.LightGray
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun App() {
    CapturableExampleTheme {
        TicketScreen()
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TicketScreen() {
    val captureController = rememberCaptureController()
    val uiScope = rememberCoroutineScope()

    // This will hold captured bitmap
    // So that we can demo it
    var ticketBitmap: ImageBitmap? by remember { mutableStateOf(null) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .safeDrawingPadding()
            .padding(24.dp)
    ) {
        // The content to be captured ⬇️
        Ticket(modifier = Modifier.capturable(captureController))

        Spacer(modifier = Modifier.size(32.dp))

        // Captures ticket bitmap on click
        Button(
            onClick = {
                uiScope.launch {
                    ticketBitmap = captureController.captureAsync().await()
                }
            }
        ) {
            Text("Preview Ticket Image")
        }

        // When Ticket's Bitmap image is captured, show preview in dialog
        ticketBitmap?.let { bitmap ->
            Dialog(onDismissRequest = { }) {
                Column(
                    modifier = Modifier
                        .background(LightGray)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Preview of Ticket image \uD83D\uDC47")
                    Spacer(Modifier.size(16.dp))
                    Image(
                        bitmap = bitmap,
                        contentDescription = "Preview of ticket"
                    )
                    Spacer(Modifier.size(4.dp))
                    Button(onClick = { ticketBitmap = null }) {
                        Text("Close Preview")
                    }
                }
            }
        }

        LaunchedEffect(Unit) {
            // delay 2 second reason of ios target
            delay(2000L)
            ticketBitmap = captureController.captureAsync().await()
        }
    }
}

@Composable
fun Ticket(modifier: Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BookingConfirmedContent()
            MovieTitle()
            Spacer(modifier = Modifier.size(32.dp))
            BookingDetail()
            Spacer(modifier = Modifier.size(32.dp))
            BookingQRCode()
            HorizontalDivider(Modifier.padding(vertical = 8.dp))
            Timer()
        }
    }
}

@Composable
private fun Timer() {
    var time by remember { mutableStateOf("") }
    Text(time)

    LaunchedEffect(Unit) {
        repeat(1000) {
            delay(1000)
            time = "Seconds: $it"
        }
    }
}

@Composable
fun BookingConfirmedContent() {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "Booking Confirmed",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.weight(0.8f)
        )
        Image(
            painter = painterResource(resource = Res.drawable.ic_baseline_check_circle_24),
            contentDescription = "Successfully booked",
            modifier = Modifier
                .weight(0.2f)
                .size(128.dp),
            colorFilter = ColorFilter.tint(Color(0xff05CB4E))
            )
    }
}

@Composable
fun MovieTitle() {
    Text(
        "Jetpack Compose - The Movie",
        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
    )
}

@Composable
fun BookingDetail() {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Column {
            Text("Sat, 1 Jan", style = MaterialTheme.typography.titleMedium)
            Text("12:45 PM", style = MaterialTheme.typography.titleMedium)
        }

        Column {
            Text("SCREEN", style = MaterialTheme.typography.titleMedium)
            Text("JET 01", style = MaterialTheme.typography.titleMedium)
        }

        Column {
            Text("SEATS", style = MaterialTheme.typography.titleMedium)
            Text("J1, J2, J3", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
fun BookingQRCode() {
    Text(
        "----- SCAN QR CODE AT CINEMA -----",
        style = MaterialTheme.typography.titleSmall
    )
    Icon(
        painter = painterResource(resource = Res.drawable.ic_baseline_qr_code_24),
        contentDescription = "Success",
        modifier = Modifier.size(128.dp)
    )

    Text("Booking ID: JETPACK0000012345", style = MaterialTheme.typography.titleSmall)
}

//Common Preview support only Fleet..
@Preview
@Composable
fun DefaultPreview() {
    CapturableExampleTheme {
        TicketScreen()
    }
}