import com.michaelflisar.kmplibrary.Targets
import com.michaelflisar.kmplibrary.core.configs.Config
import com.michaelflisar.kmplibrary.core.configs.LibraryConfig
import com.michaelflisar.kmplibrary.setups.AndroidLibrarySetup

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.hotreload)
    alias(deps.plugins.kmplibrary.buildplugin)
}

// ------------------------
// Setup
// ------------------------

val buildTargets = Targets(
    // mobile
    android = true,
    iOS = true,
    // desktop
    windows = true,
    macOS = true,
    // web
    wasm = true
)
val androidSetup = AndroidLibrarySetup(
    compileSdk = app.versions.compileSdk,
    minSdk = app.versions.minSdk,
    enableAndroidResources = true
)

val config = Config.read(rootProject)
val libraryConfig = LibraryConfig.read(rootProject)

// -------------------
// Setup
// -------------------

compose.resources {
    packageOfResClass = "${libraryConfig.library.namespace}.shared.resources"
    publicResClass = true
}

kotlin {

    //-------------
    // Targets
    //-------------

    buildTargets.setupTargetsLibrary(project, config, libraryConfig, androidSetup)

    // ------------------------
    // Source Sets
    // ------------------------

    sourceSets {

        // ---------------------
        // custom source sets
        // ---------------------

        // --

        // ---------------------
        // dependencies
        // ---------------------

        commonMain.dependencies {

            // resources
            api(compose.components.resources)

            // Kotlin
            // ..

            // Compose
            api(libs.compose.material3)
            //implementation(libs.compose.material.icons.core)
            //implementation(libs.compose.material.icons.extended)

            // demo ui composables
            implementation(deps.kmp.democomposables)

            // ------------------------
            // Library
            // ------------------------

            api(project(":kmpplatformcontext:core"))

        }

        androidMain.dependencies {
            api(project(":kmpplatformcontext:initializer"))
        }
    }
}