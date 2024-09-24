package com.shreyaspatil.capturable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.shreyaspatil.capturable.capturable
import dev.shreyaspatil.capturable.controller.CaptureController
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import kotlin.math.ceil
import kotlinx.coroutines.CompletableDeferred

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun TestContent(
    captureController : CaptureController,
) {
    Box(
        Modifier
            .size(contentWidth, contentHeight)
            .capturable(captureController)
    ) {
        Text("Hello! Inside Capturable")
    }
}

internal val contentHeight = 100.dp
internal val contentWidth = 200.dp

/**
 * Converts float value to the integer value by rounding up to ceiling.
 */
fun Float.roundToInt(): Int {
    return ceil(this).toInt()
}