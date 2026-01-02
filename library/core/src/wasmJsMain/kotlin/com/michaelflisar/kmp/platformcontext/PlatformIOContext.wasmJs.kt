package com.michaelflisar.kmp.platformcontext

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

// In WASM, there is no separate IO dispatcher, so we use Main
actual fun PlatformIOContext(): CoroutineDispatcher = Dispatchers.Main
