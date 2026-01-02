package com.michaelflisar.kmp.platformcontext

actual typealias PlatformContext = android.content.Context

internal actual fun getDefaultPlatformContext(): PlatformContext? = null