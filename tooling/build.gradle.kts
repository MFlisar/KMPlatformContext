import com.michaelflisar.kmplibrary.core.configs.Config
import com.michaelflisar.kmplibrary.core.configs.LibraryConfig
import com.michaelflisar.kmplibrary.readme.ReadmeDefaults
import com.michaelflisar.kmplibrary.readme.UpdateReadmeUtil

plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

kotlin {

    jvm()

    sourceSets {
        jvmMain.dependencies {
            implementation(deps.kmplibrary.core)
        }
    }
}

// -------------------------------------
// Custom task: updateMarkdownFiles
// -------------------------------------

/**
 * Updates the markdown files (README.md, etc.) based on the current configuration.
 *
 * functions DOES NOT need any adjustments!
 */
tasks.register("updateMarkdownFiles") {

    // disable configuration cache for this task
    notCompatibleWithConfigurationCache("updateMarkdownFiles uses dynamic file readings")

    val root = rootDir
    val config = Config.read(rootProject)
    val libraryConfig = LibraryConfig.read(rootProject)

    doLast {
        UpdateReadmeUtil.update(
            rootDir = root,
            readmeTemplate = ReadmeDefaults.DefaultReadmeTemplate,
            config = config,
            libraryConfig = libraryConfig
        )
    }
}