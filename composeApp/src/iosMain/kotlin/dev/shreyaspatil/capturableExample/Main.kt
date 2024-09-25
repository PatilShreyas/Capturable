package dev.shreyaspatil.capturableExample

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

fun mainViewController(): UIViewController {
    return ComposeUIViewController { App() }
}