package dev.shreyaspatil.capturable

import androidx.compose.ui.draw.CacheDrawModifierNode
import androidx.compose.ui.graphics.asComposeCanvas
import androidx.compose.ui.graphics.asComposeImageBitmap
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
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorInfo
import org.jetbrains.skia.ColorSpace
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.ImageInfo
import org.jetbrains.skia.Picture
import org.jetbrains.skia.PictureRecorder

@Suppress("unused")
actual class CapturableModifierNode actual constructor(controller: CaptureController) : DelegatingNode(), DelegatableNode {

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
                        picture.asBitmap(request.config)
                    }

                    completable.complete(bitmap.asComposeImageBitmap())
                } catch (error: Throwable) {
                    completable.completeExceptionally(error)
                }
            }
    }

    private suspend fun getCurrentContentAsPicture(): Picture {
        return drawCanvasIntoPicture()
    }

    private suspend fun drawCanvasIntoPicture(): Picture {
        val pictureDrawn = CompletableDeferred<Unit>()
        val pictureRecorder = PictureRecorder()
        var picture = Picture.makePlaceholder(org.jetbrains.skia.Rect(0f, 0f, 0f, 0f))

        val delegatedNode = delegate(
            CacheDrawModifierNode {
                val width = this.size.width
                val height = this.size.height

                onDrawWithContent {
                    val pictureCanvas = pictureRecorder.beginRecording(org.jetbrains.skia.Rect.makeWH(width, height))

                    draw(this, this.layoutDirection, pictureCanvas.asComposeCanvas(), this.size) {
                        this@onDrawWithContent.drawContent()
                    }

                    picture = pictureRecorder.finishRecordingAsPicture()

                    drawIntoCanvas { canvas ->
                        (canvas.nativeCanvas as org.jetbrains.skia.Canvas).drawPicture(picture)
                        // Notify that picture is drawn
                        pictureDrawn.complete(Unit)
                    }
                }
            }
        )
        pictureDrawn.await()

        undelegate(delegatedNode)

        return picture;
    }
}


private fun Picture.asBitmap(config: ColorType = ColorType.ARGB_4444): Bitmap {
    val bitmap = Bitmap()
    val ci = ColorInfo(config, ColorAlphaType.OPAQUE, ColorSpace.sRGB)
    val width = this@asBitmap.cullRect.width
    val height = this@asBitmap.cullRect.height
    bitmap.setImageInfo(ImageInfo(ci, width.toInt(), height.toInt()))
    bitmap.allocN32Pixels(width.toInt(), height.toInt())
    val canvas = org.jetbrains.skia.Canvas(bitmap)
    canvas.drawPicture(this)
    bitmap.setImmutable()
    return bitmap
}
