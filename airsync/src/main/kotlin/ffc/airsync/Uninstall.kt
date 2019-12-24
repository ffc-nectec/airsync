package ffc.airsync

import com.github.kittinunf.fuel.core.isSuccessful
import com.github.kittinunf.fuel.httpDownload
import ffc.airsync.utils.FFC_HOME
import ffc.airsync.utils.getLogger
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
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
        runBlocking {
            if (createUninstallFile())
                if (orgApi.deleteOrganization()) {
                    logger.info { "ทำการลบข้อมูล" }
                    isUninstall = true
                    rerunAirsync()
                } else {
                    logger.info { "ไม่สามารถลบข้อมูลบน Cloud ได้" }
                }
        }
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
        val command = "cmd /k start cmd /k \"\"${FFC_HOME}\\uninstall.bat\" " +
                "& type \"${FFC_HOME}\\uninstall.log\" & TIMEOUT 10 & " +
                "start \"\" " +
                "https://docs.google.com/forms/d/e/1FAIpQLScsvcWAWFxybLRRsk2QRItqcY21PaP4yphO7vh4icVR5Qy4vQ/viewform" +
                " & exit\""
        logger.info { "Delete file $command" }
        runtime.exec(command)
        exitProcess(0)
    }

    suspend fun createUninstallFile(retryFail: Int = 10): Boolean {
        val response = "https://raw.githubusercontent.com/ffc-nectec/airsync/master/uninstall.bat"
            .httpDownload()
            .fileDestination { response, request ->
                logger.info { "Uninstall status ${response.statusCode}" }
                File(FFC_HOME, "uninstall.bat")
            }
            .response()

        val successful = response.second.isSuccessful
        while (retryFail > 0 && !successful) {
            delay(2000)
            return createUninstallFile(retryFail - 1)
        }

        if (successful) {
            logger.info { "Download ไฟล์ดำเนินการลบ uninstall.bat เสร็จสมบูรณ์ รอบที่ $retryFail" }
            return true
        } else {
            logger.error { "Download ไฟล์ดำเนินการลบ uninstall.bat ไม่สมบูรณ์ รอบที่ $retryFail" }
            val error = response.third.component2()
            logger.error { "Content error body ${error?.errorData?.toString(Charsets.UTF_8)}" }
            throw error!!
        }
    }
}
