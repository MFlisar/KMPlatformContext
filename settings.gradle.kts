dependencyResolutionManagement {

    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
        maven("https://jitpack.io")
        mavenLocal()
    }

    versionCatalogs {
        create("app") {
            from(files("gradle/app.versions.toml"))
        }
        create("deps") {
            from(files("gradle/deps.versions.toml"))
        }
    }
}

pluginManagement {

    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
        maven("https://jitpack.io")
        mavenLocal()
    }
}

// --------------
// Settings Plugin
// --------------

plugins {
    // version catalogue does not work here!
    id("io.github.mflisar.kmpdevtools.plugins-settings-gradle") version "6.2.1" //apply false
}

// --------------
// Functions
// --------------

fun includeModule(path: String, name: String) {
    include(name)
    project(name).projectDir = file(path)
}

// --------------
// Library
// --------------

val libraryConfig = com.michaelflisar.kmpdevtools.core.configs.LibraryConfig.read(rootProject)
val libraryId = libraryConfig.library.name.lowercase()

// Modules
includeModule("library", ":$libraryId")
includeModule("library/core", ":$libraryId:core")
includeModule("library/initializer", ":$libraryId:initializer")

// Modules
// --

// Dokka
include(":dokka")

// --------------
// App
// --------------

include(":demo:shared")
include(":demo:app")