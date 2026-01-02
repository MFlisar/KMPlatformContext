import com.michaelflisar.kmplibrary.core.utils.ScriptStep
import com.michaelflisar.kmplibrary.core.utils.ScriptUtil
import java.io.File

fun main() {

    val root = File(System.getProperty("user.dir"))

    // -------------------
    // Setup
    // -------------------

    val projectPath = File("M:\\dev\\11 - libs (mine)\\FeedbackManager")

    val foldersToCopy = listOf(
        // Folders to copy
        UpdateAction.CopyFolder(".github"),
        UpdateAction.CopyFolder(".run"),
        UpdateAction.CopyFolder("gradle"),
        UpdateAction.CopyFolder("demo"),
        UpdateAction.CopyFolder("documentation"),
        UpdateAction.CopyFolder("library"),
        UpdateAction.CopyFolder("tooling"),
        // Specials
        UpdateAction.DeleteRootFiles,
        UpdateAction.CopyRootFiles,
    )

    // -------------------
    // Script steps
    // -------------------

    val steps = listOf(
        ScriptStep(
            name = "Update Project",
        ) {
            println("Updating project at: ${projectPath.absolutePath}")

            if (!projectPath.exists() || !projectPath.isDirectory) {
                println("Project path does not exist or is not a directory!")
                return@ScriptStep
            }

            for (action in foldersToCopy) {
                when (action) {
                    is UpdateAction.CopyFolder -> {
                        val fromDir = File(root, action.relativePath)
                        val toDir = File(projectPath, action.relativePath)
                        if (fromDir.exists() && fromDir.isDirectory) {
                            if (action.deleteTargetBeforeCopy) {
                                toDir.deleteRecursively()
                            }
                            fromDir
                                .walkTopDown()
                                .filter { file ->
                                    val relativePath = file.relativeTo(fromDir).path
                                    val targetFile = File(toDir, relativePath)
                                    !action.excludeFile(file, targetFile)
                                }.forEach { file ->
                                    val relativePath = file.relativeTo(fromDir).path
                                    val targetFile = File(toDir, relativePath)
                                    if (file.isDirectory) {
                                        if (!targetFile.exists()) {
                                            targetFile.mkdirs()
                                        }
                                    } else {
                                        file.copyTo(targetFile, overwrite = true)
                                    }
                                }
                            println("Copied folder: ${action.relativePath}")
                        } else {
                            println("Source folder does not exist: ${action.relativePath}")
                        }
                    }

                    is UpdateAction.DeleteRootFiles -> {
                        val filesToDelete =
                            projectPath.listFiles()?.filter { it.isFile } ?: emptyList()
                        filesToDelete.forEach { file ->
                            if (file.exists()) {
                                file.delete()
                            }
                        }
                        println("Deleted ${filesToDelete.size} root files")
                    }

                    is UpdateAction.CopyRootFiles -> {
                        val filesToCopy = root.listFiles()?.filter { it.isFile } ?: emptyList()
                        filesToCopy.forEach { file ->
                            val targetFile = File(projectPath, file.name)
                            file.copyTo(targetFile, overwrite = true)
                        }
                        println("Copied ${filesToCopy.size} root files")
                    }
                }
            }
        }
    )

    // -------------------
    // Run script
    // -------------------

    var run = true
    ScriptUtil.runScript(
        name = "Update Project",
        steps = steps,
        scriptInfos = {
            println("Informations:")
            println("Project path must be changed in the script manually!")
            println("Current project path:")
            println("${projectPath.absolutePath}")
            run = ScriptUtil.getUserInputYesNo("Continue?", defaultYes = false)
        },
        executeStep = { run }
    )
}

sealed class UpdateAction {

    class CopyFolder(
        val relativePath: String,
        val deleteTargetBeforeCopy: Boolean = false,
        val excludeFile: (from: File, to: File) -> Boolean = { _, _ -> false },
    ) : UpdateAction()

    object DeleteRootFiles : UpdateAction()

    object CopyRootFiles : UpdateAction()

}