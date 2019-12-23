package ffc.airsync.gui.drawable

import ffc.airsync.ui.AirSyncGUI
import ffc.airsync.ui.AirSyncGUI.CoutDown
import ffc.airsync.ui.AirSyncGUI.Message
import ffc.airsync.ui.AirSyncGUI.ProgressData
import ffc.airsync.ui.KEY
import ffc.airsync.ui.LookPcuCode
import ffc.airsync.ui.createCountDownMessage
import ffc.airsync.ui.createMessage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.awt.Component
import javax.swing.ImageIcon
import kotlin.random.Random
import kotlin.system.exitProcess

class AirSyncGUIController : AirSyncGUI {
    private lateinit var lookPcuCode: LookPcuCode
    // check.png designed by Smashicons from Flaticon
    val airsync = MainGUI()

    private val onOpenAirsync = RightClick.OnOpenAirsync {
        airsync.isVisible = true
    }

    private lateinit var _callConfirmUninstall: () -> Unit
    override fun setCallConfirmUninstall(callback: () -> Unit) {
        _callConfirmUninstall = callback
    }

    // เมื่อคลิกปุ่ม Uninstall
    private val onClickUninstall = UninstallUI.OnClickUninstall {
        val logger = getLogger(this)
        val pcucode = lookPcuCode()
        logger.info { "Call Uninstall $pcucode" }

        if (it!! == pcucode) {
            logger.info("Uninstall FFC $pcucode")
            _callConfirmUninstall()
            exitProcess(0)
        } else {
            return@OnClickUninstall false
        }
    }

    val rightClick = RightClick(airsync, onOpenAirsync, onClickUninstall)
    val random = Random(123182L)
    val listComponent = hashMapOf<KEY, Component>()
    val width = airsync.statusPanel.width - 10
    val height = 55
    var otp: () -> String = { "999 999" }

    init {
        val screenSize = getScreenSize()
        val windowsHeigh = screenSize.second - airsync.height
        airsync.setLocation(
            (screenSize.first - airsync.width) - 20,
            windowsHeigh - (windowsHeigh / 10)
        )
        airsync.isAlwaysOnTop = true
        setSyncIcon()
        setLogoIcon()
        airsync.otpButton.font = airsync.otpButton.font.deriveFont(22f)
        airsync.otpCallback = MainGUI.Callback {
            airsync.otpButton.isEnabled = false
            Thread {
                createCountDownMessage("otp", callGetOtp(), 60)
                runBlocking {
                    delay(65000)
                    airsync.otpButton.isEnabled = true
                }
            }.start()
        }
        val icon = "close.png".getImageScalingResource(airsync.closeButton.width, airsync.closeButton.height)
        airsync.closeButton.icon = ImageIcon(icon)
    }

    private fun setSyncIcon() {
        val openWebButton = airsync.openWeb
        // sync.png designed by prosymbols from Flaticon
        val image = "sync.png".getImageScalingResource(openWebButton.width, openWebButton.height)
        openWebButton.icon = ImageIcon(image)
    }

    private fun setLogoIcon() {
        val icon = airsync.icon
        val image = "logo.png".getImageScalingResource(icon.width, icon.height)
        icon.icon = ImageIcon(image)
    }

    override fun cretaeItemList(data: Pair<KEY, Any>) {
        when (data.second) {
            is ProgressData -> {
                CreateProgreassDataItem(listComponent, data, airsync).create(width, height)
            }
            is Message -> {
                CreateMessageItem(listComponent, data, airsync).create(width, height)
            }
            is CoutDown -> {
                CreateCountDownItem(listComponent, data, airsync) {
                    remove(data.first)
                }.create(width, height)
            }
        }
    }

    override fun remove(key: KEY) {
        val component = listComponent[key]
        if (component != null) {
            airsync.statusPanel.remove(component)
            listComponent.remove(key)
            airsync.statusPanel.updateUI()
        }
    }

    override fun createMessageDelay(message: String, type: AirSyncGUI.MESSAGE_TYPE, delayTime: Long) {
        val key = random.nextLong().toString()
        createMessage(key, message, type)
        GlobalScope.launch {
            delay(delayTime)
            remove(key)
        }
    }

    override fun createRightClick(x: Int, y: Int) {
        rightClick.setLocation(x - rightClick.width, y - rightClick.height)
        rightClick.isVisible = true
    }

    override fun hideRightClick() {
        rightClick.isVisible = false
    }

    override fun hideWindows() {
        airsync.isVisible = false
    }

    override fun showWIndows() {
        airsync.isVisible = true
    }

    override fun switchhHideShow() {
        airsync.isVisible = !airsync.isVisible
    }

    override fun setHeader(string: String) {
        airsync.headerLabel.text = string
    }

    override fun setLocation(x: Int, y: Int) {
        airsync.setLocation((getScreenSize().first - airsync.width) - 20, y - airsync.height - 50)
    }

    override var enableSyncButton: Boolean
        get() = airsync.openWeb.isEnabled
        set(value) {
            airsync.openWeb.isEnabled = value
        }
    override var enableOtp: Boolean
        get() = airsync.otpButton.isEnabled
        set(value) {
            airsync.otpButton.isEnabled = value
        }
    override var callGetOtp: () -> String
        get() = otp
        set(value) {
            otp = value
        }

    override fun setLookPcuCode(pcuCode: LookPcuCode) {
        this.lookPcuCode = pcuCode
    }
}
