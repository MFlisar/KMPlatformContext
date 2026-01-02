import com.michaelflisar.kmplibrary.core.configs.Config
import com.michaelflisar.kmplibrary.core.configs.LibraryConfig
import com.michaelflisar.kmplibrary.core.utils.PackageRenamer
import com.michaelflisar.kmplibrary.core.utils.ScriptStep
import com.michaelflisar.kmplibrary.core.utils.ScriptUtil
import java.io.File
import java.util.Properties

fun main() {

    val rootDir = File(System.getProperty("user.dir"))
    val config = Config.readFromProject(rootDir)
    val libraryConfig = LibraryConfig.readFromProject(rootDir)

    val fileStateProperties = File(rootDir, "configs/state.properties")

    val state = Properties().apply {
        fileStateProperties.inputStream().use { load(it) }
    }

    val packageNameFrom = state.getProperty("packageNameFrom")!!
    val libraryNameFrom = state.getProperty("libraryNameFrom")!!

    val packageNameTo = libraryConfig.library.namespace
    val libraryNameTo = libraryConfig.library.name

    val details = mapOf(
        "Project Root" to rootDir.absolutePath,
        "Package Name From" to packageNameFrom,
        "Package Name To" to packageNameTo,
        "Library Name From" to libraryNameFrom,
        "Library Name To" to libraryNameTo
    )

    val steps = listOf(
        ScriptStep("Rename Package Names") {
            PackageRenamer.rename(
                root = rootDir,
                packageNameFrom = packageNameFrom,
                packageNameTo = packageNameTo,
                libraryNameFrom = libraryNameFrom,
                libraryNameTo = libraryNameTo,
            )
        },
        ScriptStep("Update iOS App") {

            val fileConfig = File(rootDir, "demo/iosApp/Configuration/Config.xcconfig")
            val content = fileConfig.readText()
            val newContent = content.replace(libraryNameFrom, libraryNameTo)
            fileConfig.writeText(newContent)

            val fileProject = File(rootDir, "demo/iosApp/iosApp.xcodeproj/project.pbxproj")
            val projectContent = fileProject.readText()
            val newProjectContent = projectContent.replace(libraryNameFrom, libraryNameTo)
            fileProject.writeText(newProjectContent)
        },
        ScriptStep("Save State") {
            state.setProperty("packageNameFrom", packageNameTo)
            state.setProperty("libraryNameFrom", libraryNameTo)
            fileStateProperties.outputStream().use { state.store(it, null) }
        }
    )

    ScriptUtil.runScript(
        name = "Rename Package Name",
        steps = steps,
        scriptInfos = {
            ScriptUtil.printDetails(details)
        }
    )
}