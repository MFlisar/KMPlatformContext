import com.michaelflisar.kmpdevtools.core.configs.LibraryConfig

plugins {
    kotlin("jvm") apply false
    alias(libs.plugins.dokka)
}

dependencies {
    dokka(project(":kmpplatformcontext:core"))
    dokka(project(":kmpplatformcontext:initializer"))
}

dokka {
    val libraryConfig = LibraryConfig.read(rootProject)
    moduleName.set(libraryConfig.library.name)
}
