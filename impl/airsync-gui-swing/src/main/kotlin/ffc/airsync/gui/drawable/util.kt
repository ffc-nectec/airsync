package ffc.airsync.gui.drawable

import org.apache.logging.log4j.kotlin.KotlinLogger
import org.apache.logging.log4j.kotlin.logger
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

inline fun <reified T> getLogger(clazz: T): KotlinLogger {
    return logger(T::class.java.simpleName)
}
