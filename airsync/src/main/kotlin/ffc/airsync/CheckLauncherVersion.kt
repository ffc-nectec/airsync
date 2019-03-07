package ffc.airsync

import max.download.zip.ZIpDownload
import max.githubapi.GitHubLatestApi
import java.io.File
import java.io.FileNotFoundException
import java.io.FileReader
import java.net.URL

class CheckLauncherVersion {
    fun check() {
        printDebug("Check Launcher Version")
        var launcherVersion = ""
        try {
            val fr = FileReader("launcher.version")
            launcherVersion = fr.readText().trim()
            fr.close()
        } catch (ignore: FileNotFoundException) {
        }

        val gh = GitHubLatestApi("ffc-nectec/AirSyncLauncher").getLastRelease()
        printDebug("Check launcher local version $launcherVersion and git version ${gh.tag_name}")
        if (gh.tag_name != launcherVersion) {
            val ass = gh.assets.find { it.name == "ffc-airsync.zip" }
            val downloadUrl = ass?.browser_download_url
            if (downloadUrl != null) {
                println("Launcher download...")
                val zipD = ZIpDownload(URL(downloadUrl)) {
                    printDebug("Launcher download ${((it / ass.size) * 100)} %")
                }
                zipD.download(File(""))
            }
        }
    }
}
