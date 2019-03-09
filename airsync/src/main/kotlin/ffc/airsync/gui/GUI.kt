package ffc.airsync.gui

import java.awt.GraphicsEnvironment

fun getScreenSize(): Pair<Int, Int> {
    val gd = GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice
    return Pair(gd.displayMode.width, gd.displayMode.height)
}
