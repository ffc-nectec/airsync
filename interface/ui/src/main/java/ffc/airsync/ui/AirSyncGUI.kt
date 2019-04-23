package ffc.airsync.ui

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

typealias KEY = String

interface AirSyncGUI {
    fun cretaeItemList(data: Pair<KEY, Any>)
    fun createMessageDelay(message: String, type: MESSAGE_TYPE = MESSAGE_TYPE.OK, delay: Long = 1000)
    fun remove(key: KEY)
    fun hideWindows()
    fun showWIndows()
    fun switchhHideShow()
    fun setHeader(string: String)
    fun setLocation(x: Int, y: Int)
    fun createRightClick(x: Int, y: Int)
    fun hideRightClick()
    var enableOtp: Boolean
    var enableSyncButton: Boolean

    data class ProgressData(val current: Int, val max: Int, val message: String? = null)
    data class Message(val message: String, val type: MESSAGE_TYPE = MESSAGE_TYPE.OK)
    data class CoutDown(val message: String, val count: Int)

    enum class MESSAGE_TYPE {
        OK, ERROR
    }
}

fun AirSyncGUI.createProgress(key: String, current: Int, max: Int, message: String? = null) {
    this.cretaeItemList(key to AirSyncGUI.ProgressData(current, max, message))
}

fun AirSyncGUI.createMessage(key: String, message: String, type: AirSyncGUI.MESSAGE_TYPE = AirSyncGUI.MESSAGE_TYPE.OK) {
    this.cretaeItemList(key to AirSyncGUI.Message(message, type))
}

fun AirSyncGUI.createCountDownMessage(key: String, message: String, count: Int) {
    this.cretaeItemList(key to AirSyncGUI.CoutDown(message, count))
}

fun AirSyncGUI.delayRemove(key: String, delayTime: Long = 500) {
    GlobalScope.launch {
        delay(delayTime)
        remove(key)
    }
}
