package ffc.airsync

import ffc.airsync.ui.AirSyncGUI
import ffc.airsync.utils.getPathJarDir
import max.download.zip.ZIpDownload
import max.githubapi.GitHubLatestApi
import java.io.File
import java.io.FileNotFoundException
import java.io.FileReader
import java.net.URL

@Deprecated("Disable because move to ffc-launcher.")
class CheckLauncherVersion(val gui: AirSyncGUI) {
    fun check() {
        gui.set("Check Launcher" to AirSyncGUI.ProgressData(0, 100))
        printDebug("Check Launcher Version")
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
                    gui.set("Check Launcher" to AirSyncGUI.ProgressData(0, 100, message))
                    printDebug(message)
                }
                if (gh.tag_name != launcherVersion) {
                    val ass = gh.assets.find { it.name == "ffc-airsync.zip" }
                    val downloadUrl = ass?.browser_download_url
                    if (downloadUrl != null) {
                        gui.set("Check Launcher" to AirSyncGUI.ProgressData(0, 100, "Launcher download..."))
                        println("Launcher download...")
                        val zipD = ZIpDownload(URL(downloadUrl)) {
                            val percenDownload = (it / ass.size) * 100
                            kotlin.run {
                                val message = "Launcher download"
                                printDebug(message)
                                gui.set(
                                    "Check Launcher" to AirSyncGUI.ProgressData(
                                        percenDownload.toInt(),
                                        100,
                                        message
                                    )
                                )
                            }
                        }
                        zipD.download(File(getPathJarDir(), ""))
                    }
                }
                isFinish = true
            } catch (ex: java.net.UnknownHostException) {
                gui.set(
                    "Launcher Network Error" to AirSyncGUI.CheckData(
                        "Network Error $ex",
                        AirSyncGUI.MESSAGE_TYPE.ERROR
                    )
                )
            }
        Thread {
            Thread.sleep(1000)
            gui.remove("Check Launcher")
        }.start()
    }
}
