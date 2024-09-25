import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import dev.shreyaspatil.capturableExample.App
import org.jetbrains.skiko.wasm.onWasmReady
import kotlinx.browser.document
import kotlinx.browser.window

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    window.onload = {
        onWasmReady {
            val body = document.body ?: return@onWasmReady
            ComposeViewport(body) {
                App()
            }
        }
    }
}
