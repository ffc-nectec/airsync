package ffc.airsync.utils

import ffc.airsync.ui.AirSyncGUI
import ffc.airsync.ui.KEY
import ffc.airsync.ui.LookPcuCode

class EmptyGUI : AirSyncGUI {
    override fun cretaeItemList(data: Pair<KEY, Any>) {
    }

    override fun createMessageDelay(message: String, type: AirSyncGUI.MESSAGE_TYPE, delay: Long) {
    }

    override fun remove(key: KEY) {
    }

    override fun hideWindows() {
    }

    override fun showWIndows() {
    }

    override fun switchhHideShow() {
    }

    override fun setHeader(string: String) {
    }

    override fun setLocation(x: Int, y: Int) {
    }

    override fun createRightClick(x: Int, y: Int) {
    }

    override fun hideRightClick() {
    }

    override var enableSyncButton: Boolean
        get() = false
        set(value) {}

    override var enableOtp: Boolean
        get() = false
        set(value) {}

    override var callGetOtp: () -> String
        get() = { "" }
        set(value) {}

    override fun setLookPcuCode(pcuCode: LookPcuCode) {
    }

    override fun setCallConfirmUninstall(callback: () -> Unit) {
    }
}
