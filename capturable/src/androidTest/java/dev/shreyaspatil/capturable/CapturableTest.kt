package dev.shreyaspatil.capturable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import dev.shreyaspatil.capturable.controller.CaptureController
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
                onCaptured = { bitmaps.add(it) }
            ) {
                Box(Modifier.size(contentWidth, contentHeight)) {
                    Text("Hello! Inside Capturable")
                }
            }
        }

        // When: Content is captured
        controller.capture()

        // Then: Content should be get captured ONLY ONCE and stored as bitmap
        assert(bitmaps.size == 1)

        // Then: Dimension of bitmap should be same as content's dimension
        val bitmap = bitmaps.first()

        val expectedHeight = with(composeTestRule.density) { contentHeight.toPx() }
        val expectedWidth = with(composeTestRule.density) { contentWidth.toPx() }

        val actualHeight = bitmap.height.toFloat()
        val actualWidth = bitmap.width.toFloat()

        assertEquals(actualHeight, expectedHeight)
        assertEquals(actualWidth, expectedWidth)
    }
}