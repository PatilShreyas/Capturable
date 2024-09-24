import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import dev.shreyaspatil.capturableExample.App
import java.awt.Dimension

fun main() = application {
    Window(
        title = "Capturable Example",
        state = rememberWindowState(width = 400.dp, height = 711.dp),
        onCloseRequest = ::exitApplication,
    ) {
        window.minimumSize = Dimension(300, 533)
        App()
    }
}

@Preview
@Composable
fun AppPreview() { App() }