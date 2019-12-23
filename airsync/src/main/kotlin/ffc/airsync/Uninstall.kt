package ffc.airsync

import com.github.kittinunf.fuel.httpDownload
import ffc.airsync.utils.FFC_HOME
import ffc.airsync.utils.getLogger
import java.io.File
import kotlin.system.exitProcess

class Uninstall {
    private val logger = getLogger(this)
    private val airsyncFile by lazy { File(FFC_HOME, "airsync.jar") }
    private val javawFile by lazy { File(FFC_HOME, "jre/bin/javaw.exe") }
    var isUninstall: Boolean
        get() = File("$FFC_HOME", "uninstall.ffc").isFile
        set(value) {
            if (value) {
                if (!isUninstall) {
                    File("$FFC_HOME", "uninstall.ffc").createNewFile()
                }
            } else {
                File("$FFC_HOME", "uninstall.ffc").deleteRecursively()
            }
        }

    fun confirmRemoveOrganization() {
        logger.info { "ทำการลบข้อมูล" }
        isUninstall = true
        orgApi.deleteOrganization()
        rerunAirsync()
    }

    private fun rerunAirsync() {
        val command = "\"${javawFile.absolutePath}\" -Xms1G -Xmx4G -jar -Dfile.encoding=UTF-8 " +
                "-jar \"${airsyncFile.absolutePath}\""
        runtime.exec(command)
        exitProcess(0)
    }

    private val runtime: Runtime
        get() = Runtime.getRuntime()

    fun removeFile() {
        require(isUninstall) { "ยังไม่ได้กำหนดค่าสำหรับการลบ" }
        createUninstallFile()
        val command = "cmd /k start cmd /k \"${FFC_HOME}\\uninstall.bat\""
        logger.info { "Delete file $command" }
        runtime.exec(command)
        exitProcess(0)
    }

    fun createUninstallFile() {
        "https://raw.githubusercontent.com/ffc-nectec/airsync/Uninstaller/uninstall.bat"
            .httpDownload()
            .fileDestination { response, request ->
                logger.info { "Uninstall status ${response.statusCode}" }
                File(FFC_HOME, "uninstall.bat")
            }
            .response()
    }
}
