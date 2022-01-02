package dev.shreyaspatil.capturable.controller

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Controller for capturing [Composable] content.
 * @see dev.shreyaspatil.capturable.Capturable for implementation details.
 */
class CaptureController internal constructor() {

    /**
     * Medium for providing capture requests
     */
    private val _captureRequests = MutableSharedFlow<Bitmap.Config>(extraBufferCapacity = 1)
    internal val captureRequests = _captureRequests.asSharedFlow()

    /**
     * Creates and send a Bitmap capture request with specified [config].
     *
     * Make sure to call this method as a part of callback function and not as a part of the
     * [Composable] function itself.
     *
     * @param config Bitmap config of the desired bitmap. Defaults to [Bitmap.Config.ARGB_8888]
     */
    fun capture(config: Bitmap.Config = Bitmap.Config.ARGB_8888) {
        _captureRequests.tryEmit(config)
    }
}

/**
 * Creates [CaptureController] and remembers it.
 */
@Composable
fun rememberCaptureController(): CaptureController {
    return remember { CaptureController() }
}