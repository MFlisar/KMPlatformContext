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
// Functions
// --------------

fun includeModule(path: String, name: String) {
    include(name)
    project(name).projectDir = file(path)
}

// --------------
// Library
// --------------

val libraryId = "kmpplatformcontext"

// Modules
includeModule("library", ":$libraryId")
includeModule("library/core", ":$libraryId:core")
includeModule("library/initializer", ":$libraryId:initializer")

// Modules
// --

// --------------
// App
// --------------

include(":demo:shared")
include(":demo:app")

// developer tools (for local tasks only)
include(":tooling")