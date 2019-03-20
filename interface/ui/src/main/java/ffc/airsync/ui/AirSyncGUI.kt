package ffc.airsync.ui

import java.awt.Image

typealias KEY = String

interface AirSyncGUI {
    fun set(data: Pair<KEY, Any>)
    fun remove(key: KEY)
    fun hideWindows()
    fun showWIndows()
    fun switchhHideShow()
    fun setLogo(image: Image)
    fun setHeader(string: String)

    data class ProgressData(val current: Int, val all: Int)
}
