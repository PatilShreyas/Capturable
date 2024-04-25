package dev.shreyaspatil.capturable

import android.graphics.Bitmap
import android.graphics.Picture
import android.os.Build
import androidx.compose.ui.draw.CacheDrawModifierNode
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.draw
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DelegatingNode
import dev.shreyaspatil.capturable.controller.CaptureController
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Suppress("unused")
actual class CapturableModifierNode actual constructor(controller: CaptureController) :
    DelegatingNode(), DelegatableNode {

    private val currentController = MutableStateFlow(controller)

    override fun onAttach() {
        super.onAttach()
        coroutineScope.launch {
            observeCaptureRequestsAndServe()
        }
    }

    actual fun updateController(controller: CaptureController) {
        currentController.value = controller
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun observeCaptureRequestsAndServe() {
        currentController
            .flatMapLatest { it.captureRequests }
            .collect { request ->
                val completable = request.imageBitmapDeferred
                try {
                    val picture = getCurrentContentAsPicture()
                    val bitmap = withContext(Dispatchers.Default) {
                        picture.asBitmap(Bitmap.Config.ARGB_8888)//  convert request.config to BitmapConfig and use
                    }
                    completable.complete(bitmap.asImageBitmap())
                } catch (error: Throwable) {
                    completable.completeExceptionally(error)
                }
            }
    }

    private suspend fun getCurrentContentAsPicture(): Picture {
        return Picture().apply { drawCanvasIntoPicture(this) }
    }

    private suspend fun drawCanvasIntoPicture(picture: Picture) {
        // CompletableDeferred to wait until picture is drawn from the Canvas content
        val pictureDrawn = CompletableDeferred<Unit>()

        // Delegate the task to draw the content into the picture
        val delegatedNode = delegate(
            CacheDrawModifierNode {
                val width = this.size.width.toInt()
                val height = this.size.height.toInt()

                onDrawWithContent {
                    val pictureCanvas = Canvas(picture.beginRecording(width, height))

                    draw(this, this.layoutDirection, pictureCanvas, this.size) {
                        this@onDrawWithContent.drawContent()
                    }
                    picture.endRecording()

                    drawIntoCanvas { canvas ->
                        canvas.nativeCanvas.drawPicture(picture)

                        // Notify that picture is drawn
                        pictureDrawn.complete(Unit)
                    }
                }
            }
        )
        // Wait until picture is drawn
        pictureDrawn.await()

        // As task is accomplished, remove the delegation of node to prevent draw operations on UI
        // updates or recompositions.
        undelegate(delegatedNode)
    }
}

private fun Picture.asBitmap(config: Bitmap.Config): Bitmap {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        Bitmap.createBitmap(this@asBitmap)
    } else {
        val bitmap = Bitmap.createBitmap(
            /* width = */
            this@asBitmap.width,
            /* height = */
            this@asBitmap.height,
            /* config = */
            config
        )
        val canvas = android.graphics.Canvas(bitmap)
        canvas.drawColor(android.graphics.Color.WHITE)
        canvas.drawPicture(this@asBitmap)
        bitmap
    }
}