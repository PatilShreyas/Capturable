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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import dev.shreyaspatil.capturable.controller.CaptureController
import java.math.RoundingMode
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class CapturableTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testCapture() {
        val controller = CaptureController()
        val bitmaps = mutableListOf<ImageBitmap>()
        val contentHeight = 100.dp
        val contentWidth = 200.dp

        composeTestRule.setContent {
            Capturable(
                controller = controller,
                onCaptured = { bitmap, _ -> bitmaps.add(bitmap!!) }
            ) {
                Box(Modifier.size(contentWidth, contentHeight)) {
                    Text("Hello! Inside Capturable")
                }
            }
        }

        // When: Content is captured
        controller.capture()

        // Wait for some time to get a callback
        Thread.sleep(500)

        // Then: Content should be get captured ONLY ONCE and stored as bitmap
        assert(bitmaps.size == 1)

        // Then: Dimension of bitmap should be same as content's dimension
        val bitmap = bitmaps.first()

        val expectedHeight = with(composeTestRule.density) { contentHeight.toPx() }.roundToInt()
        val expectedWidth = with(composeTestRule.density) { contentWidth.toPx() }.roundToInt()

        val actualHeight = bitmap.height
        val actualWidth = bitmap.width

        assertEquals(expectedHeight, actualHeight)
        assertEquals(expectedWidth, actualWidth)
    }

    /**
     * Converts float value to the integer value by rounding up to ceiling.
     */
    private fun Float.roundToInt(): Int = toBigDecimal().setScale(0, RoundingMode.CEILING).toInt()
}