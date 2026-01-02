package utils

import Setup
import Util
import java.io.File

object SSHUtil {

    // Helper to run SSH command, with special handling for killall Xcode
    fun ssh(
        command: String,
        user: String = Setup.macUser,
        host: String = Setup.macHost,
        port: Int = Setup.sshPort,
        keyPath: String = Setup.keyPath,
        ignoreExitCode: ((exitCode: Int, output: String) -> Boolean)? = null,
    ) {
        val sshCmd = mutableListOf("ssh", "-p", port.toString())
        if (File(keyPath).exists())
            sshCmd += listOf("-i", keyPath)
        sshCmd += listOf("$user@$host", command)
        Util.runOrThrow(
            cmd = sshCmd,
            errorMsg = "SSH command failed: $command",
            ignoreExitCode = ignoreExitCode
        )
    }
}