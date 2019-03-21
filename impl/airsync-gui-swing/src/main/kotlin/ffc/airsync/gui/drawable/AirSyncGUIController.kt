package ffc.airsync.gui.drawable

import ffc.airsync.ui.AirSyncGUI
import ffc.airsync.ui.AirSyncGUI.CheckData
import ffc.airsync.ui.AirSyncGUI.ProgressData
import ffc.airsync.ui.KEY
import java.awt.Component
import java.awt.Dimension
import java.awt.Font
import java.awt.GraphicsEnvironment
import java.awt.Image
import java.awt.Toolkit
import javax.imageio.ImageIO
import javax.swing.ImageIcon

class AirSyncGUIController : AirSyncGUI {
    // check.png designed by Smashicons from Flaticon
    val airsync = MainGUI()
    val rightClick = RightClick(RightClick.OnOpenAirsync {
        airsync.isVisible = true
    })
    val listComponent = hashMapOf<KEY, Component>()
    val width = airsync.statusPanel.width - 10
    val height = 55
    val kanitBold = Font.createFont(Font.TRUETYPE_FONT, "font/Kanit-Bold.otf".getFileResource())
    val kanitMedium = Font.createFont(Font.TRUETYPE_FONT, "font/Kanit-Medium.otf".getFileResource())

    init {
        val screenSize = getScreenSize()
        val windowsHeigh = screenSize.second - airsync.height
        airsync.setLocation(
            (screenSize.first - airsync.width) - 20,
            windowsHeigh - (windowsHeigh / 10)
        )
        configSyncIcon()
        configLogoIcon()
        val icon = "close.png".getImageScalingResource(airsync.closeButton.width, airsync.closeButton.height)
        airsync.closeButton.icon = ImageIcon(icon)
        airsync.headerLabel.font = kanitMedium.deriveFont(airsync.headerLabel.font.size2D)
    }

    private fun configSyncIcon() {
        val openWebButton = airsync.openWeb
        // sync.png designed by prosymbols from Flaticon
        val image = "sync.png".getImageScalingResource(openWebButton.width, openWebButton.height)
        openWebButton.icon = ImageIcon(image)
    }

    private fun configLogoIcon() {
        val icon = airsync.icon
        val image = "logo.png".getImageScalingResource(icon.width, icon.height)
        icon.icon = ImageIcon(image)
    }

    override fun set(data: Pair<KEY, Any>) {
        when (data.second) {
            is ProgressData -> {
                if (listComponent[data.first] == null) {
                    val newStatusProgress = StatusProgress()
                    newStatusProgress.preferredSize = Dimension(width, height)
                    listComponent[data.first] = newStatusProgress
                    newStatusProgress.label.font = kanitMedium.deriveFont(newStatusProgress.label.font.size.toFloat())
                    airsync.statusPanel.add(newStatusProgress)
                }

                val progressData = data.second as ProgressData
                val statusProgress = listComponent[data.first] as StatusProgress
                statusProgress.jProgressBar.minimum = 0
                statusProgress.jProgressBar.maximum = progressData.max
                statusProgress.jProgressBar.value = progressData.current
                statusProgress.label.text =
                    data.first + if (progressData.message != null) ":${progressData.message}" else ""
            }
            is CheckData -> {
                if (listComponent[data.first] == null) {
                    val newCheckData = SuccessConfirm()
                    newCheckData.preferredSize = Dimension(width, height)
                    newCheckData.text.font = kanitMedium.deriveFont(18f)
                    listComponent[data.first] = newCheckData
                    airsync.statusPanel.add(newCheckData)
                }
                val checkData = data.second as CheckData
                val checkDataConfirm = listComponent[data.first] as SuccessConfirm
                checkDataConfirm.icon.icon = when (checkData.type) {
                    AirSyncGUI.MESSAGE_TYPE.OK -> {
                        ImageIcon(
                            "check.png".getImageScalingResource(
                                height - 10,
                                height - 10
                            )
                        )
                    }
                    AirSyncGUI.MESSAGE_TYPE.ERROR -> {
                        ImageIcon(
                            "error.png".getImageScalingResource(
                                height - 10,
                                height - 10
                            )
                        )
                    }
                }
                checkDataConfirm.text.text = checkData.message
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

    private fun String.getImageResource(): Image {
        val resourceStream = getFileResource()
        val bufferImageIO = ImageIO.read(resourceStream)
        return Toolkit.getDefaultToolkit().createImage(bufferImageIO.source)
    }

    private fun String.getImageScalingResource(width: Int, height: Int): Image {
        val image = this.getImageResource()
        return image.getScaledInstance(width, height, Image.SCALE_SMOOTH)
    }

    fun getScreenSize(): Pair<Int, Int> {
        val gd = GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice
        return Pair(gd.displayMode.width, gd.displayMode.height)
    }

    override fun setLocation(x: Int, y: Int) {
        airsync.setLocation((getScreenSize().first - airsync.width) - 20, y - airsync.height - 50)
    }

    override var enableSyncButton: Boolean
        get() = airsync.openWeb.isEnabled
        set(value) {
            airsync.openWeb.isEnabled = value
        }
}
