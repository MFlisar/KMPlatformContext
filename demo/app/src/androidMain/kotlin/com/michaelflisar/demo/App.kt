package com.michaelflisar.demo

import android.app.Application
import com.michaelflisar.kmp.platformcontext.PlatformContextProvider

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        // If you do not use the initializer module, call the init function below here:
        // init()

    }

    private fun init() {
        // begin-snippet: PlatformContextProvider::init
        PlatformContextProvider.init(this)
        // end-snippet
    }
}