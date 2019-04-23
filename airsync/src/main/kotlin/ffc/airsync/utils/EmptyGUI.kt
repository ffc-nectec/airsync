package ffc.airsync.utils

import ffc.airsync.ui.AirSyncGUI
import ffc.airsync.ui.KEY

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
}
