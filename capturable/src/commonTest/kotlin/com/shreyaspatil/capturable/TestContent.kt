package com.shreyaspatil.capturable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import dev.shreyaspatil.capturable.capturable
import dev.shreyaspatil.capturable.controller.CaptureController
import kotlin.math.ceil

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun TestContent(
    captureController : CaptureController,
    contentHeight : Dp,
    contentWidth : Dp
) {
    Box(
        Modifier
            .size(contentWidth, contentHeight)
            .capturable(captureController)
    ) {
        Text("Hello! Inside Capturable")
    }
}

/**
 * Converts float value to the integer value by rounding up to ceiling.
 */
fun Float.roundToInt(): Int {
    return ceil(this).toInt()
}