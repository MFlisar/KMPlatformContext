import com.codingfeline.buildkonfig.compiler.FieldSpec.Type
import com.michaelflisar.kmplibrary.BuildFileUtil
import com.michaelflisar.kmplibrary.Targets
import com.michaelflisar.kmplibrary.core.configs.Config
import com.michaelflisar.kmplibrary.core.configs.LibraryConfig
import com.michaelflisar.kmplibrary.setupWindowsApp
import com.michaelflisar.kmplibrary.setups.DesktopAppSetup
import com.michaelflisar.kmplibrary.setups.WasmAppSetup

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.hotreload)
    alias(libs.plugins.buildkonfig)
    alias(deps.plugins.kmplibrary.buildplugin)
}

// ------------------------
// Setup
// ------------------------

val config = Config.read(rootProject)
val libraryConfig = LibraryConfig.read(rootProject)

val appName = "${libraryConfig.library.name} Demo"
val appNamespace = "com.michaelflisar.demo"
val appVersionName = "1.0.0"
val appVersionCode = 1

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
val desktopSetup = DesktopAppSetup(
    appName = appName,
    appVersionName = appVersionName,
    mainClass = "$appNamespace.MainKt",
    author = config.developer.name,
    ico = "icon.ico"
)
val wasmSetup = WasmAppSetup(
    moduleName = "demo",
    outputFileName = "demo.js"
)

// ------------------------
// Kotlin
// ------------------------

buildkonfig {
    packageName = appNamespace
    defaultConfigs {
        buildConfigField(Type.STRING, "versionName", appVersionName)
        buildConfigField(Type.INT, "versionCode", appVersionCode.toString())
        buildConfigField(Type.STRING, "packageName", appNamespace)
        buildConfigField(Type.STRING, "appName", appName)
    }
}

kotlin {

    //-------------
    // Targets
    //-------------

    buildTargets.setupTargetsApp(project, config, wasmSetup)

    // ------------------------
    // Source Sets
    // ------------------------

    sourceSets {

        // ---------------------
        // custom source sets
        // ---------------------

        // --

        // ------------------------
        // dependencies
        // ------------------------

        commonMain.dependencies {

            // resources
            api(compose.components.resources)

            // Modules
            implementation(project(":demo:shared"))
        }

        androidMain.dependencies {

            // AndroidX/Compose/Material
            implementation(libs.androidx.activity.compose)

        }

        jvmMain.dependencies {

            implementation(compose.desktop.currentOs) {
                exclude(group = "org.jetbrains.compose.material", module = "material")
            }

        }
    }
}

// -------------------
// Configurations
// -------------------

// android configuration
android {

    BuildFileUtil.setupAndroidApp(
        project = project,
        config = config,
        androidNamespace = appNamespace,
        compileSdk = app.versions.compileSdk,
        minSdk = app.versions.minSdk,
        targetSdk = app.versions.targetSdk,
        versionCode = appVersionCode,
        versionName = appVersionName,
        buildConfig = true,
        checkDebugKeyStoreProperty = true
    )

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
            applicationIdSuffix = ".debug"
        }
    }
}

// windows configuration
compose.desktop {
    application {

        // BuildFilePlugin Extension
        setupWindowsApp(
            project = project,
            setup = desktopSetup
        )
    }
}
