package com.michaelflisar.demo

import androidx.compose.ui.window.ComposeUIViewController
import com.michaelflisar.kmp.platformcontext.demo.DemoApp

/**
 * iOS entry point used by the Xcode demo project (demo/xcode).
 */
fun MainViewController() = ComposeUIViewController {
    DemoApp("iOS")
}
