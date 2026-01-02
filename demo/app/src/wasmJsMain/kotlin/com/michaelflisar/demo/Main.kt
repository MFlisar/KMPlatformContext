package com.michaelflisar.demo

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.michaelflisar.kmp.platformcontext.demo.DemoApp
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport(
        viewportContainer = document.body!!//getElementById("ComposeTarget")!!
    ) {
        DemoApp("WASM")
    }
}