import java.io.IOException

object Util {

    // Helper to run a command and throw if it fails, returns output
    fun runOrThrow(
        cmd: List<String>,
        errorMsg: String,
        ignoreExitCode: ((exitCode: Int, output: String) -> Boolean)? = null,
    ): String {
        val proc = ProcessBuilder(cmd)
            .redirectErrorStream(true)
            .start()
        val output = proc.inputStream.bufferedReader().readText()
        val exit = proc.waitFor()
        if (exit != 0 && (ignoreExitCode == null || !ignoreExitCode(exit, output))) {
            throw IOException("$errorMsg\nExit code: $exit\nOutput:\n$output")
        }
        return output
    }
}