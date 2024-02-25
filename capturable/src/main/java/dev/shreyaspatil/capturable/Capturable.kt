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
package dev.shreyaspatil.capturable

import android.graphics.Bitmap
import android.graphics.Picture
import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.CacheDrawModifierNode
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.draw
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DelegatingNode
import androidx.compose.ui.node.ModifierNodeElement
import dev.shreyaspatil.capturable.controller.CaptureController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ***Deprecated: Use Modifier.[capturable] for replacement.***
 *
 * A composable with [content] which supports to capture [ImageBitmap] from a [content].
 *
 * Example usage:
 *
 * ```
 *  val captureController = rememberCaptureController()
 *  Capturable(
 *      controller = captureController,
 *      onCaptured = { bitmap, error ->
 *          // Do something with [bitmap]
 *          // Handle [error] if required
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
 *
 * @param controller A [CaptureController] which gives control to capture the [content].
 * @param modifier The [Modifier] to be applied to the layout.
 * @param onCaptured The callback which gives back [ImageBitmap] after composable is captured.
 * If any error is occurred while capturing bitmap, [Throwable] is provided.
 * @param content [Composable] content to be captured.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Deprecated(
    "This Composable method has been deprecated & will be removed in the future releases. " +
        "Use Modifier `capturable()` directly.",
    level = DeprecationLevel.WARNING
)
@Composable
fun Capturable(
    controller: CaptureController,
    modifier: Modifier = Modifier,
    onCaptured: (ImageBitmap?, Throwable?) -> Unit,
    content: @Composable () -> Unit
) {
    val updatedOnCaptured by rememberUpdatedState(newValue = onCaptured)

    Column(modifier = modifier.capturable(controller)) {
        content()
    }

    LaunchedEffect(key1 = controller) {
        controller.captureRequests.collect { request ->
            try {
                val imageBitmap = request.imageBitmapDeferred.await()
                updatedOnCaptured(imageBitmap, null)
            } catch (error: Throwable) {
                updatedOnCaptured(null, error)
            }
        }
    }
}

/**
 * Adds a capture-ability on the Composable which can draw Bitmap from the Composable component.
 *
 * Example usage:
 *
 * ```
 *  val captureController = rememberCaptureController()
 *  val uiScope = rememberCoroutineScope()
 *
 *  // The content to be captured in to Bitmap
 *  Column(
 *      modifier = Modifier.capturable(captureController),
 *  ) {
 *      // Composable content
 *  }
 *
 *  Button(onClick = {
 *      // Capture content
 *      val bitmapAsync = captureController.captureAsync()
 *      try {
 *          val bitmap = bitmapAsync.await()
 *          // Do something with `bitmap`.
 *      } catch (error: Throwable) {
 *          // Error occurred, do something.
 *      }
 *  }) { ... }
 * ```
 *
 * @param controller A [CaptureController] which gives control to capture the Composable content.
 */
@ExperimentalComposeUiApi
fun Modifier.capturable(controller: CaptureController): Modifier {
    return this then CapturableModifierNodeElement(controller)
}

/**
 * Modifier implementation of Capturable
 */
private data class CapturableModifierNodeElement(
    private val controller: CaptureController
) : ModifierNodeElement<CapturableModifierNode>() {
    override fun create(): CapturableModifierNode {
        return CapturableModifierNode(controller)
    }

    override fun update(node: CapturableModifierNode) {
        node.controller = controller
    }
}

/**
 * Capturable Modifier node which delegates task to the [CacheDrawModifierNode] for drawing
 * Composable UI to the Picture and then helping it to converting picture into a Bitmap.
 */
@Suppress("unused")
private class CapturableModifierNode(
    var controller: CaptureController
) : DelegatingNode(), DelegatableNode {

    /**
     * Delegates the drawing to [CacheDrawModifierNode] in order to draw content rendered on the
     * canvas directly to the [picture].
     */
    val drawModifierNode = delegate(
        CacheDrawModifierNode {
            // Example that shows how to redirect rendering to an Android Picture and then
            // draw the picture into the original destination
            val width = this.size.width.toInt()
            val height = this.size.height.toInt()

            onDrawWithContent {
                val pictureCanvas = Canvas(picture.beginRecording(width, height))

                draw(this, this.layoutDirection, pictureCanvas, this.size) {
                    this@onDrawWithContent.drawContent()
                }
                picture.endRecording()

                drawIntoCanvas { canvas -> canvas.nativeCanvas.drawPicture(picture) }
            }
        }
    )

    override fun onAttach() {
        super.onAttach()
        coroutineScope.launch {
            controller.captureRequests.collect { request ->
                val completable = request.imageBitmapDeferred
                try {
                    val picture = getCurrentContentAsPicture()
                    val bitmap = withContext(Dispatchers.Default) {
                        picture.asBitmap(request.config)
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

    /**
     * Draws the current content into the provided [picture]
     */
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

/**
 * Creates a [Bitmap] from a [Picture] with provided [config]
 */
private fun Picture.asBitmap(config: Bitmap.Config): Bitmap {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        Bitmap.createBitmap(this@asBitmap)
    } else {
        val bitmap = Bitmap.createBitmap(
            /* width = */this@asBitmap.width,
            /* height = */this@asBitmap.height,
            /* config = */config
        )
        val canvas = android.graphics.Canvas(bitmap)
        canvas.drawColor(android.graphics.Color.WHITE)
        canvas.drawPicture(this@asBitmap)
        bitmap
    }
}