package com.michaelflisar.kmp.platformcontext

object PlatformContextProvider {

    private var context: PlatformContext? = null

    fun init(context: PlatformContext) {
        this.context = context
    }

    fun get() : PlatformContext {
        if (context == null) {
            context = getDefaultPlatformContext()
        }
        return context ?: throw IllegalStateException("PlatformContext not initialized. Either add the `initializer` module to the android project or initialise the context via PlatformContextProvider.init(context) inside your android application first.")

    }
}