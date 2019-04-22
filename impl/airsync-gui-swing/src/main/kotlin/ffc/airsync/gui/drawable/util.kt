package ffc.airsync.gui.drawable

import java.awt.Font
import java.awt.GraphicsEnvironment
import java.awt.Image
import java.awt.Toolkit
import javax.imageio.ImageIO

internal fun String.getFileResource() = Thread.currentThread().contextClassLoader.getResourceAsStream(this)

internal fun getScreenSize(): Pair<Int, Int> {
    val gd = GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice
    return Pair(gd.displayMode.width, gd.displayMode.height)
}

internal fun String.getImageResource(): Image {
    val resourceStream = getFileResource()
    val bufferImageIO = ImageIO.read(resourceStream)
    return Toolkit.getDefaultToolkit().createImage(bufferImageIO.source)
}

internal fun String.getImageScalingResource(width: Int, height: Int): Image {
    val image = this.getImageResource()
    return image.getScaledInstance(width, height, Image.SCALE_SMOOTH)
}

internal val kanitBold = Font.createFont(Font.TRUETYPE_FONT, "font/Kanit-Bold.otf".getFileResource())
internal val kanitMedium = Font.createFont(Font.TRUETYPE_FONT, "font/Kanit-Medium.otf".getFileResource())
