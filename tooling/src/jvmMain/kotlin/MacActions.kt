import com.michaelflisar.kmplibrary.core.utils.CMDUtil
import com.michaelflisar.kmplibrary.core.utils.SSHUtil
import com.michaelflisar.kmplibrary.core.utils.ScriptStep
import com.michaelflisar.kmplibrary.core.utils.ScriptUtil
import java.io.File

fun main() {

    // -------------------
    // Setup
    // -------------------

    val setup = Setup.create()
    val defaultInput = "1,2" // "a", "1-3", "1,2,3-5" usw.

    // -------------------
    // Script Steps + Run
    // -------------------

    val steps = listOf(
        ScriptStep("Sync to Mac") {
            syncToMac(setup)
        },
        ScriptStep("Build XCFramework on Mac (and copy it back)") {
            buildXCFramework(setup)
        },
        ScriptStep("Open XCode: App") {
            openXCode(setup.relativePaths.xcodeProjectFolder.getRemotePath(setup))
        },
        ScriptStep("Open XCode: XCFramework") {
            val index = ScriptUtil.printOptions(
                "Select XCFramework to open:",
                setup.xcFrameworks.map { it.name })
            if (index != null)
                openXCode(setup.xcFrameworks[index].xcodeproj.getRemotePath(setup))
        }
    )

    ScriptUtil.runScriptSteps(
        name = "Mac Actions",
        steps = steps,
        defaultInput = defaultInput
    )
}

private fun syncToMac(setup: Setup) {

    println("Syncing to Mac...")

    val exclude = listOf(
        "build",
        ".gradle",
        ".idea",
        "*.iml",
        ".kotlin",
        ".run"
    )

    // ----------------------
    // Script
    // ----------------------

    // Build tar exclude args
    val tarExArgs = exclude.flatMap { listOf("--exclude=$it") }

    println("\n--------------------------------")
    println("- Project:  ${setup.projectName}")
    println("- Mac User: ${Setup.macUser}")
    println("- Mac Host: ${Setup.macHost}")
    println("- Source:   ${setup.projectRootDirectory}")
    println("- Target:   ${setup.projectRemoteRootDirectory}")
    println("--------------------------------\n")

    // 1) Prepare remote dir (delete if exists, then create)
    println("1) Prepare Mac directory")
    val checkDirCmd =
        "[ -d '${setup.projectRemoteRootDirectory}' ] && rm -rf '${setup.projectRemoteRootDirectory}'"
    SSHUtil.ssh(
        "$checkDirCmd && mkdir -p '${setup.projectRemoteRootDirectory}' || mkdir -p '${setup.projectRemoteRootDirectory}'",
        Setup.sshSetup
    )
    SSHUtil.ssh("mkdir -p '${setup.projectRemoteRootDirectory}'", Setup.sshSetup)

    // 2) Create tar locally
    println("2) Create tar archive")
    val tmpTar = File.createTempFile("sync_", ".tar")
    val tarCmd = listOf(
        "tar",
        "-cf",
        tmpTar.absolutePath,
        "-C",
        setup.projectRootDirectory
    ) + tarExArgs + "."
    CMDUtil.runOrThrow(tarCmd, "tar create failed.")

    // 3) Copy tar to Mac
    println("3) Copy tar to Mac")
    if (!tmpTar.exists())
        error("TAR archive was not created: ${tmpTar.absolutePath}")
    val remoteTar = "${setup.projectRemoteRootDirectory}/__sync.tar"
    val scpCmd = mutableListOf("scp", "-P", Setup.sshPort.toString())
    if (File(Setup.keyPath).exists())
        scpCmd += listOf("-i", Setup.keyPath)
    scpCmd += listOf(tmpTar.absolutePath, "${Setup.macUser}@${Setup.macHost}:$remoteTar")
    CMDUtil.runOrThrow(scpCmd, "scp upload failed.")

    // 4) Extract tar on Mac and remove tar
    println("4) Extract tar on Mac")
    SSHUtil.ssh(
        "tar -xpf '$remoteTar' -C '${setup.projectRemoteRootDirectory}' && rm -f '$remoteTar'",
        Setup.sshSetup
    )

    // 5) Clean up local tar
    println("5) Delete local tar")
    tmpTar.delete()

    // 6) Close XCode on Mac
    println("6) Close XCode on Mac")
    SSHUtil.ssh(
        command = "killall Xcode >/dev/null 2>&1",
        sshSetup = Setup.sshSetup,
        ignoreExitCode = { exitCode, output ->
            exitCode == 1 && (output.isBlank() || output.contains("no matching processes", true))
        }
    )

    // 7) Set executable files
    println("7) Set executable files on Mac")
    for (file in setup.macExecutables.all) {
        val remotePath = file.getRemotePath(setup)
        SSHUtil.ssh("chmod +x '$remotePath'", Setup.sshSetup)
    }

    println("8) Done.")

}

private fun buildXCFramework(setup: Setup) {

    println("Building XCFramework on Mac...")

    for (xcFramework in setup.xcFrameworks) {

        println("Building XCFramework: ${xcFramework.name}")

        val buildXCFrameworkPath = setup.relativePaths.buildXCFrameworkFile

        val relativeScriptPath = buildXCFrameworkPath.path
        val localXCFrameworkFile = xcFramework.xcframework.getLocalFile(setup)
        val remoteXCFrameworkPath = xcFramework.xcframework.getRemotePath(setup)
        val relativeXCodeProjPath = xcFramework.xcodeproj.path

        val projectName = xcFramework.name
        val scheme = xcFramework.scheme
        val includeMacFlag = if (xcFramework.targets.contains("macos")) 1 else 0

        // 1) sh script auf mac ausführen
        // im root ausführen, dann passt auch project path relative zum root
        val env = "PROJECT_NAME=\"$projectName\" " +
                "SCHEME=\"$scheme\" " +
                "PROJECT_PATH=\"${relativeXCodeProjPath}\" " +
                "CONFIGURATION=\"Release\" " +
                "INCLUDE_MAC=$includeMacFlag " +
                "INCLUDE_CATALYST=0"

        val command1 =
            "cd '${setup.projectRemoteRootDirectory}' && chmod +x '$relativeScriptPath' && $env './$relativeScriptPath'"
        SSHUtil.ssh(command1, Setup.sshSetup)

        // 2) xcframework zurück kopieren
        println("Copying XCFramework back to local machine...")
        if (localXCFrameworkFile.exists()) {
            localXCFrameworkFile.deleteRecursively()
        }
        val scpCommand = listOf(
            "scp",
            "-r",
            "${Setup.macUser}@${Setup.macHost}:$remoteXCFrameworkPath",
            localXCFrameworkFile.parentFile.absolutePath
        )
        val process = ProcessBuilder(scpCommand)
            .redirectErrorStream(true)
            .start()

        val output = process.inputStream.bufferedReader().readText()
        val exitCode = process.waitFor()
        if (exitCode != 0) {
            throw RuntimeException("scp failed with exit code $exitCode. Output:\n$output")
        }
        if (!localXCFrameworkFile.exists()) {
            throw RuntimeException("Kopieren fehlgeschlagen: ${localXCFrameworkFile.absolutePath} existiert nicht!")
        }
    }
}

private fun openXCode(remoteXCodeProject: String) {
    println("Opening XCode...")
    SSHUtil.ssh(
        command = "open -a Xcode '$remoteXCodeProject' >/dev/null 2>&1 &",
        sshSetup = Setup.sshSetup
    )
}

private fun openTerminalInScriptDir(setup: Setup) {
    println("Opening Terminal in script dir...")
    val remotePath = setup.relativePaths.scriptsFolder.getRemotePath(setup)
    SSHUtil.ssh(
        command = "open -a Terminal '$remotePath' >/dev/null 2>&1 &",
        sshSetup = Setup.sshSetup
    )
}


