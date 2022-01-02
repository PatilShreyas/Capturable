package dev.shreyaspatil.capturable.controller

import android.graphics.Bitmap
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Test

class CaptureControllerTest {

    private val controller = CaptureController()

    @Test
    fun testCapture_withNoParameters() = runBlockingTest {
        // Before capturing, make sure to collect flow request eagerly
        val captureRequest = async { getRecentCaptureRequest() }

        // When: Captured
        controller.capture()

        val actualConfig = captureRequest.await()
        val expectedConfig = Bitmap.Config.ARGB_8888

        // Then: Capture request should be get emitted with default bitmap config
        assertEquals(actualConfig, expectedConfig)
    }

    @Test
    fun testCapture_withCustomParameters() = runBlockingTest {
        // Before capturing, make sure to collect flow request eagerly
        val captureRequest = async { getRecentCaptureRequest() }

        // Given: The customized config
        val expectedConfig = Bitmap.Config.RGB_565

        // When: Captured
        controller.capture(expectedConfig)

        val actualConfig = captureRequest.await()

        // Then: Capture request should be get emitted with default bitmap config
        assertEquals(actualConfig, expectedConfig)
    }

    private suspend fun getRecentCaptureRequest() = controller.captureRequests.take(1).first()
}