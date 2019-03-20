package ffc.airsync.gui.drawable

import ffc.airsync.ui.AirSyncGUI
import ffc.airsync.ui.AirSyncGUI.ProgressData
import ffc.airsync.ui.KEY
import java.awt.Component
import java.awt.GraphicsEnvironment
import java.awt.Image
import java.awt.Toolkit
import javax.imageio.ImageIO
import javax.swing.ImageIcon

class AirSyncGUIController : AirSyncGUI {

    val airsync = MainGUI()
    val listComponent = hashMapOf<KEY, Component>()

    init {
        val screenSize = getScreenSize()
        airsync.setLocation(
            (screenSize.first / 2) - (airsync.width / 2),
            (screenSize.second / 2) - (airsync.height / 2)
        )
        configSyncIcon()
        configLogoIcon()
    }

    private fun configSyncIcon() {
        val openWebButton = airsync.openWeb
        val image = "sync.png".getImageResource()
        val newImage = image.getScaledInstance(openWebButton.width, openWebButton.height, Image.SCALE_SMOOTH)
        openWebButton.icon = ImageIcon(newImage)
    }

    private fun configLogoIcon() {
        val icon = airsync.icon
        val image = "logo.png".getImageResource()
        val newImage = image.getScaledInstance(icon.width, icon.height, Image.SCALE_SMOOTH)
        icon.icon = ImageIcon(newImage)
    }

    override fun set(data: Pair<KEY, Any>) {

        var component = listComponent[data.first]
        if (component == null) {
            if (data.second is AirSyncGUI.ProgressData) {
                component = StatusProgress()
                listComponent[data.first] = component
                airsync.statusPanel.add(component)
            }
        }

        when (component) {
            is StatusProgress -> {
                val progressData = data.second as ProgressData
                component.jProgressBar.maximum = progressData.all
                component.jProgressBar.value = progressData.current
            }
        }
    }

    override fun remove(key: KEY) {
        val component = listComponent[key]
        if (component != null) {
            airsync.statusPanel.remove(component)
            listComponent.remove(key)
        }
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

    override fun setLogo(image: Image) {
        val icon = airsync.icon
        val newImage = image.getScaledInstance(icon.width, icon.height, java.awt.Image.SCALE_SMOOTH)
        icon.icon = ImageIcon(newImage)
    }

    override fun setHeader(string: String) {
        airsync.headerLabel.text = string
    }

    private fun String.getImageResource(): Image {
        val resourceStream = Thread.currentThread().contextClassLoader.getResourceAsStream(this)
        val bufferImageIO = ImageIO.read(resourceStream)
        return Toolkit.getDefaultToolkit().createImage(bufferImageIO.source)
    }

    fun getScreenSize(): Pair<Int, Int> {
        val gd = GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice
        return Pair(gd.displayMode.width, gd.displayMode.height)
    }
}
