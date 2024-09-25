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

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.test.junit4.createComposeRule
import com.shreyaspatil.capturable.TestContent
import com.shreyaspatil.capturable.contentHeight
import com.shreyaspatil.capturable.contentWidth
import com.shreyaspatil.capturable.roundToInt
import dev.shreyaspatil.capturable.controller.CaptureController
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class CapturableTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testCapture_withModifier() {
        val controller = CompletableDeferred<CaptureController>()
        composeTestRule.setContent {
            val captureController = rememberCaptureController()
            TestContent(captureController)
            LaunchedEffect(Unit) {
                controller.complete(captureController)
            }
        }

        // When: Content is captured
        val bitmap = runBlocking { controller.await().captureAsync().await() }

        val expectedHeight = with(composeTestRule.density) { contentHeight.toPx() }.roundToInt()
        val expectedWidth = with(composeTestRule.density) { contentWidth.toPx() }.roundToInt()

        val actualHeight = bitmap.height
        val actualWidth = bitmap.width

        assertEquals(expectedHeight, actualHeight)
        assertEquals(expectedWidth, actualWidth)
    }
}