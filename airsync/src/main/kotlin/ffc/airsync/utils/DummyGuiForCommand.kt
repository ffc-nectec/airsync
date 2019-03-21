package ffc.airsync.utils

import ffc.airsync.ui.AirSyncGUI
import ffc.airsync.ui.KEY

class DummyGuiForCommand : AirSyncGUI {
    override fun set(data: Pair<KEY, Any>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun createMessageDelay(message: String, type: AirSyncGUI.MESSAGE_TYPE, delay: Long) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun remove(key: KEY) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun hideWindows() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showWIndows() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun switchhHideShow() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setHeader(string: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setLocation(x: Int, y: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun createRightClick(x: Int, y: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun hideRightClick() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override var enableSyncButton: Boolean
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        set(value) {}
}
