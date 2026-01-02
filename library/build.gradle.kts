import com.michaelflisar.kmplibrary.BuildFileUtil
import com.michaelflisar.kmplibrary.Targets
import com.michaelflisar.kmplibrary.core.Platform
import com.michaelflisar.kmplibrary.core.configs.Config
import com.michaelflisar.kmplibrary.core.configs.LibraryConfig
import com.michaelflisar.kmplibrary.setups.AndroidLibrarySetup

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.dokka)
    alias(libs.plugins.gradle.maven.publish.plugin)
    alias(libs.plugins.binary.compatibility.validator)
    alias(deps.plugins.kmplibrary.buildplugin)
}

// -------------------
// Informations
// -------------------

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
    minSdk = app.versions.minSdk
)

val config = Config.read(rootProject)
val libraryConfig = LibraryConfig.read(rootProject)

// -------------------
// Setup
// -------------------

kotlin {

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    //-------------
    // Targets
    //-------------

    buildTargets.setupTargetsLibrary(project, config, libraryConfig, androidSetup)

    // -------
    // Sources
    // -------

    sourceSets {

        // ---------------------
        // custom source sets
        // ---------------------

        val notAndroidMain by creating { dependsOn(commonMain.get()) }

        buildTargets.setupDependencies(notAndroidMain, sourceSets, buildTargets, listOf(Platform.ANDROID), platformsNotSupported = true)

        // ---------------------
        // dependencies
        // ---------------------

        commonMain.dependencies {
            implementation(deps.kotlinx.coroutines.core)
        }

        androidMain.dependencies {
            implementation(libs.androidx.core)
        }
    }
}

// -------------------
// Publish
// -------------------

// maven publish configuration
if (BuildFileUtil.checkGradleProperty(project, "publishToMaven") != false)
    BuildFileUtil.setupMavenPublish(project, config, libraryConfig)