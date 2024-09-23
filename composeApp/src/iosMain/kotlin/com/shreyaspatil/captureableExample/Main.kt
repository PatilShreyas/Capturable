package com.shreyaspatil.captureableExample

import androidx.compose.ui.window.ComposeUIViewController
import dev.shreyaspatil.capturableExample.App
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    return ComposeUIViewController { App() }
}