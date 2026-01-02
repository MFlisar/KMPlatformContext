import com.michaelflisar.kmplibrary.core.configs.Config
import com.michaelflisar.kmplibrary.core.configs.LibraryConfig
import com.michaelflisar.kmplibrary.core.utils.SSHSetup
import java.io.File

class Setup(
    // general
    val projectName: String,
    val projectRootDirectory: String,
    val projectRemoteRootDirectory: String,
    // relative paths
    val relativePaths: RelativePaths,
    // XCFramework
    val xcFrameworks: List<XCFrameworkSetup>,
    // executables on Mac
    val macExecutables: MacExecutables,
) {
    class RelativePath(val path: String) {

        fun getName() = path.substringAfterLast("/")

        fun getRemotePath(setup: Setup) = "${setup.projectRemoteRootDirectory}/$path"
        fun getRemotePathParent(setup: Setup) = getRemotePath(setup).substringBeforeLast("/")

        fun getLocalPath(setup: Setup) = "${setup.projectRootDirectory}/$path"
        fun getLocalFile(setup: Setup) = File(getLocalPath(setup))
    }

    class RelativePaths(
        val buildXCFrameworkFile: RelativePath,
        val xcodeProjectFolder: RelativePath,
        val buildOnMacFile: RelativePath,
        val gradlewFile: RelativePath,
        val scriptsFolder: RelativePath
    )

    companion object {

        val macHost = "macmini.local"           // Hostname or IP
        val macUser = "mflisar"                 // macOS User
        val macTargetDir = "/Users/mflisar/dev" // Base target dir on Mac
        val sshPort = 22
        val keyPath = System.getProperty("user.home") + "/.ssh/id_ed25519"

        val sshSetup = SSHSetup(
            host = macHost,
            user = macUser,
            port = sshPort,
            keyPath = keyPath
        )

        fun create(): Setup {

            val root = File(System.getProperty("user.dir"))
            val config = Config.readFromProject(root)
            val libraryConfig = LibraryConfig.readFromProject(root)

            // 1) general
            val rootDir = root                                  // /Users/mflisar/dev/LibraryTemplate
            val projectName = libraryConfig.library.name        // LibraryTemplate
            val rootRemoteDir = "${macTargetDir}/$projectName"  // /Users/mflisar/dev/LibraryTemplate

            // 2) project paths (relative to root)
            val buildXCFrameworkPath = RelativePath("tooling/scripts/build_xcframework.sh")
            val xcodeProjectPath = RelativePath("demo/iosApp/iosApp.xcodeproj")
            val buildOnMacPath = RelativePath("tooling/scripts/build_on_mac.sh")
            val gradlewPath = RelativePath("gradlew")
            val scriptsFolderPath = RelativePath("tooling/scripts")

            // 3) xcframeworks // todo: alle XCFramework definieren
            val xcFrameworks = libraryConfig.xcframeworks.map {
                val name = it.name
                val path = it.path
                XCFrameworkSetup(
                    name = name,
                    scheme = name,
                    targets = it.targets,
                    xcodeproj = RelativePath("$path/$name.xcodeproj"),
                    xcframework = RelativePath("$path/$name.xcframework")
                )
            }

            return Setup(
                projectName = projectName,
                projectRootDirectory = rootDir.absolutePath,
                projectRemoteRootDirectory = rootRemoteDir,
                relativePaths = RelativePaths(
                    buildXCFrameworkFile = buildXCFrameworkPath,
                    xcodeProjectFolder = xcodeProjectPath,
                    buildOnMacFile = buildOnMacPath,
                    gradlewFile = gradlewPath,
                    scriptsFolder = scriptsFolderPath
                ),
                xcFrameworks = xcFrameworks,
                macExecutables = MacExecutables(
                    buildOnMacPath = buildOnMacPath,
                    buildXCFrameworkPath = buildXCFrameworkPath,
                    gradlewPath = gradlewPath,
                )
            )
        }
    }

    class XCFrameworkSetup(
        val name: String,
        val scheme: String,
        val targets: List<String>,
        val xcodeproj: RelativePath,
        val xcframework: RelativePath
    )

    class MacExecutables(
        val buildOnMacPath: RelativePath,
        val buildXCFrameworkPath: RelativePath,
        val gradlewPath: RelativePath,
    ) {
        val all: List<RelativePath>
            get() = listOf(buildOnMacPath, buildXCFrameworkPath, gradlewPath)
    }
}