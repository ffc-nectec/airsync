package ffc.airsync.utils

import ffc.airsync.Main
import java.io.File
import java.net.URLDecoder

fun getPathJarDir(): String {
    val jarFile = File(Main.javaClass.protectionDomain.codeSource.location.path!!)
    return URLDecoder.decode(jarFile.parentFile.path, "UTF-8")
}
