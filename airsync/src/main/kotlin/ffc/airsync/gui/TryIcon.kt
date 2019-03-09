package ffc.airsync.gui

import java.awt.PopupMenu
import java.awt.SystemTray
import java.awt.Toolkit
import java.awt.TrayIcon
import java.awt.TrayIcon.MessageType
import java.awt.event.MouseListener
import javax.imageio.ImageIO

class TryIcon(
    private val name: String,
    private val imageParth: String,
    private val toolTip: String? = null,
    private val popupMenu: PopupMenu? = null,
    private val mouseListener: () -> MouseListener
) {

    lateinit var trayIcon: TrayIcon
    val isSupport get() = SystemTray.isSupported()

    init {
        if (SystemTray.isSupported()) {
            configTry()
        }
    }

    fun infoNotificationMessage(caption: String, message: String) {
        trayIcon.displayMessage(caption, message, MessageType.INFO)
    }

    fun erroroNotificationMessage(caption: String, message: String) {
        trayIcon.displayMessage(caption, message, MessageType.ERROR)
    }

    fun warningNotificationMessage(caption: String, message: String) {
        trayIcon.displayMessage(caption, message, MessageType.WARNING)
    }

    fun notificationMessage(caption: String, message: String) {
        trayIcon.displayMessage(caption, message, MessageType.NONE)
    }

    private fun configTry() {
        val tray = SystemTray.getSystemTray()

        // If the icon is a file
        val classloader = Thread.currentThread().contextClassLoader
        val bufferImageIO = ImageIO.read(classloader.getResourceAsStream("icon.png"))
        val image = Toolkit.getDefaultToolkit().createImage(bufferImageIO.source)
        trayIcon = TrayIcon(image, name)
        // Let the system resize the image if needed
        trayIcon.isImageAutoSize = true
        trayIcon.addMouseListener(mouseListener.invoke())
        if (popupMenu != null) trayIcon.popupMenu = popupMenu
        if (toolTip != null) trayIcon.toolTip = toolTip
        tray.add(trayIcon)
    }
}
