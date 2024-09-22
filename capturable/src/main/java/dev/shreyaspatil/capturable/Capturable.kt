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

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.CacheDrawModifierNode
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DelegatingNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import dev.shreyaspatil.capturable.controller.CaptureController
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch


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
        node.updateController(controller)
    }
}

/**
 * Capturable Modifier node which delegates task to the [CacheDrawModifierNode] for drawing in
 * runtime when content capture is requested
 * [CacheDrawModifierNode] is used for drawing Composable UI from Canvas to the Picture and then
 * this node converts picture into a Bitmap.
 *
 * @param controller A [CaptureController] which gives control to capture the Composable content.
 */
@Suppress("unused")
private class CapturableModifierNode(
    controller: CaptureController
) : DelegatingNode(), DelegatableNode {

    /**
     * State to hold the current [CaptureController] instance.
     * This can be updated via [updateController] method.
     */
    private val currentController = MutableStateFlow(controller)

    override fun onAttach() {
        super.onAttach()
        coroutineScope.launch {
            observeCaptureRequestsAndServe()
        }
    }

    /**
     * Sets new [CaptureController]
     */
    fun updateController(newController: CaptureController) {
        currentController.value = newController
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun observeCaptureRequestsAndServe() {
        currentController
            .flatMapLatest { it.captureRequests }
            .collect { request ->
                val completable = request.imageBitmapDeferred
                try {
                    val bitmap = getCurrentContentAsPicture(request.graphicsLayer)
                    completable.complete(bitmap)
                } catch (error: Throwable) {
                    completable.completeExceptionally(error)
                }
            }
    }

    /**
     * Draws the current content into the provided [graphicsLayer] and returns [ImageBitmap] out
     * of it.
     */
    private suspend fun getCurrentContentAsPicture(graphicsLayer: GraphicsLayer): ImageBitmap {
        // CompletableDeferred to wait until picture is drawn from the Canvas content
        val drawNodeAttached = CompletableDeferred<Unit>()

        // Delegate the task to draw the content into the picture
        val delegatedNode = delegate(
            DrawWithContentModifier {
                // call record to capture the content in the graphics layer
                graphicsLayer.record {
                    // draw the contents of the composable into the graphics layer
                    this@DrawWithContentModifier.drawContent()
                }
                // draw the graphics layer on the visible canvas
                drawLayer(graphicsLayer)
                drawNodeAttached.complete(Unit)
            }
        )
        // Wait until picture is drawn and wait for animation frame too
        drawNodeAttached.await()
        awaitFrame()

        return graphicsLayer.toImageBitmap().also {
            // As task is accomplished, remove the delegation of node to prevent draw operations on UI
            // updates or recompositions.
            undelegate(delegatedNode)
        }
    }
}

/**
 * A node which will be used to draw the Composable content of a node which is currently attached to.
 *
 * See https://developer.android.com/develop/ui/compose/graphics/draw/modifiers#composable-to-bitmap
 */
private class DrawWithContentModifier(
    var onDraw: ContentDrawScope.() -> Unit
) : Modifier.Node(), DrawModifierNode {

    override fun ContentDrawScope.draw() {
        onDraw()
    }
}