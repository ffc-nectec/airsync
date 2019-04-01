package ffc.airsync

import ffc.airsync.ui.AirSyncGUI
import ffc.airsync.ui.createMessage
import ffc.airsync.ui.createProgress
import ffc.airsync.utils.getLogger
import ffc.airsync.utils.getPathJarDir
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import max.download.zip.ZIpDownload
import max.githubapi.GitHubLatestApi
import java.io.File
import java.io.FileNotFoundException
import java.io.FileReader
import java.net.URL

@Deprecated("Disable because move to ffc-launcher.")
class CheckLauncherVersion(val gui: AirSyncGUI) {
    private val logger by lazy { getLogger(this) }
    fun check() {
        gui.createProgress("Check Launcher", 0, 100)
        logger.trace("Check Launcher Version")
        var launcherVersion = ""
        try {
            val fr = FileReader("${getPathJarDir()}/launcher.version")
            launcherVersion = fr.readText().trim()
            fr.close()
        } catch (ignore: FileNotFoundException) {
        }
        var isFinish = false

        while (!isFinish)
            try {
                val gh = GitHubLatestApi("ffc-nectec/AirSyncLauncher").getLastRelease()
                gui.remove("Launcher Network Error")
                kotlin.run {
                    val message = "Check launcher local version $launcherVersion and git version ${gh.tag_name}"
                    gui.createProgress("Check Launcher", 0, 100, message)
                    logger.info(message)
                }
                if (gh.tag_name != launcherVersion) {
                    val ass = gh.assets.find { it.name == "ffc-airsync.zip" }
                    val downloadUrl = ass?.browser_download_url
                    if (downloadUrl != null) {
                        gui.createProgress("Check Launcher", 0, 100, "Launcher download...")
                        val message = "Launcher download..."
                        logger.trace(message)
                        val zipD = ZIpDownload(URL(downloadUrl)) {
                            val percenDownload = (it / ass.size) * 100
                            kotlin.run {
                                gui.createProgress(
                                    "Check Launcher",
                                    percenDownload.toInt(),
                                    100,
                                    message
                                )
                            }
                        }
                        zipD.download(File(getPathJarDir(), ""))
                    }
                }
                isFinish = true
            } catch (ex: java.net.UnknownHostException) {
                gui.createMessage(
                    "Launcher Network Error",
                    "Network Error $ex",
                    AirSyncGUI.MESSAGE_TYPE.ERROR
                )
            }
        GlobalScope.launch {
            delay(1000)
            gui.remove("Check Launcher")
        }
    }
}
