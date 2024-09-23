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
package dev.shreyaspatil.capturable.controller

import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest

@Suppress("DeferredResultUnused")
class CaptureControllerTest {

    private val controller = CaptureController(mockk())

    @Test
    fun captureAsync_validConfig_withNoParameters() = runTest {
        // Before capturing, make sure to collect flow request eagerly
        val captureRequestDeferred = asyncOnUnconfinedDispatcher { getRecentCaptureRequest() }

        // When: Captured
        controller.captureAsync()

        val captureRequest = captureRequestDeferred.await()

        // Then: Capture request should be get emitted with graphics layer
        assertEquals(captureRequest.imageBitmapDeferred.isActive, true)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun <T> TestScope.asyncOnUnconfinedDispatcher(block: suspend CoroutineScope.() -> T) =
        async(UnconfinedTestDispatcher(testScheduler), block = block)

    private suspend fun getRecentCaptureRequest() = controller.captureRequests.take(1).first()
}