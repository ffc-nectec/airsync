package ffc.airsync.utils

import ffc.airsync.Main
import hii.log.print.easy.EasyPrintLogGUI

val debug = System.getenv("FFC_DEBUG")
private val logPrint: EasyPrintLogGUI? = try {
    EasyPrintLogGUI(
        "AirSync to cloud...",
        lineLimit = 1000
    )
} catch (ex: java.awt.HeadlessException) {
    null
}

fun printDebug(infoDebug: String) {
    if (debug == null)
        try {
            if (Main.instant.noGUI)
                println(infoDebug)
            else
                logPrint?.text = (infoDebug)
        } catch (ex: kotlin.UninitializedPropertyAccessException) {
            println(infoDebug)
        } catch (ex: java.awt.HeadlessException) {
            println(infoDebug)
        }
}
