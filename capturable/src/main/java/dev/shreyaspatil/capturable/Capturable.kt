package dev.shreyaspatil.capturable

import android.graphics.Bitmap
import android.util.Log
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.doOnLayout
import androidx.core.view.drawToBitmap
import dev.shreyaspatil.capturable.controller.CaptureController
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * A composable with [content] which supports to capture [ImageBitmap] from a [content].
 *
 * @param controller A [CaptureController] which gives control to capture the [content].
 * @param modifier The modifier to be applied to the layout.
 * @param onCaptured The callback which gives back [ImageBitmap] after composable is captured.
 * @param content Composable content to be captured.
 *
 * Example usage:
 *
 * ```
 *  val captureController = rememberCaptureController()
 *  Capturable(
 *      controller = captureController,
 *      onCaptured = { bitmap ->
 *          // Do something with [bitmap]
 *      }
 *  ) {
 *      // Composable content
 *  }
 *
 *  Button(onClick = {
 *      // Capture content
 *      captureController.capture()
 *  }) { ... }
 * ```
 */
@Composable
fun Capturable(
    controller: CaptureController,
    modifier: Modifier = Modifier,
    onCaptured: (ImageBitmap) -> Unit,
    content: @Composable () -> Unit
) {
    AndroidView(
        factory = { ComposeView(it).applyCapturability(controller, onCaptured, content) },
        modifier = modifier
    )
}

/**
 * Sets the [content] in [ComposeView] and handles the capture of a [content].
 */
private inline fun ComposeView.applyCapturability(
    controller: CaptureController,
    noinline onCaptured: (ImageBitmap) -> Unit,
    crossinline content: @Composable () -> Unit
) = apply {
    setContent {
        content()
        LaunchedEffect(controller, onCaptured) {
            controller.captureRequests
                .mapNotNull { config -> drawToBitmapPostLaidOut(config) }
                .onEach { bitmap -> onCaptured(bitmap.asImageBitmap()) }
                .catch { Log.e("Capturable", "Failed to capture composable", it) }
                .launchIn(this)
        }
    }
}

/**
 * Waits till this [View] is laid off and then draws it to the [Bitmap] with specified [config].
 */
private suspend fun View.drawToBitmapPostLaidOut(config: Bitmap.Config): Bitmap {
    return suspendCoroutine { continuation ->
        doOnLayout { view ->
            continuation.resume(view.drawToBitmap(config))
        }
    }
}
