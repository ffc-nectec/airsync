package ffc.airsync.ui

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

typealias KEY = String
typealias LookPcuCode = () -> String

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
    fun setLookPcuCode(pcuCode: LookPcuCode)
    fun setCallConfirmUninstall(callback: () -> Unit)

    var callGetOtp: () -> String
    var enableOtp: Boolean
    var enableSyncButton: Boolean

    data class ProgressData(val current: Int, val max: Int, val message: String? = null)
    data class Message(val message: String, val type: MESSAGE_TYPE = MESSAGE_TYPE.OK)
    data class CoutDown(val message: String, val count: Int)

    enum class MESSAGE_TYPE {
        OK, ERROR, INFO
    }
}

fun AirSyncGUI.createProgress(key: String, current: Int, max: Int, message: String? = null) {
    this.cretaeItemList(key to AirSyncGUI.ProgressData(current, max, message))
}

fun AirSyncGUI.createMessage(key: String, message: String, type: AirSyncGUI.MESSAGE_TYPE = AirSyncGUI.MESSAGE_TYPE.OK) {
    if (message.isNotBlank())
        this.cretaeItemList(key to AirSyncGUI.Message(message, type))
    else
        this.remove(key)
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
