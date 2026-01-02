package com.michaelflisar.kmp.platformcontext

import android.content.Context
import androidx.startup.Initializer

class PlatformContextInitializer : Initializer<Unit> {

    override fun create(context: Context): Unit {
        PlatformContextProvider.init(context.applicationContext)
        return Unit
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        // No dependencies on other libraries.
        return emptyList()
    }
}