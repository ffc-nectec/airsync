package ffc.airsync.utils

import ffc.airsync.gui
import ffc.airsync.ui.AirSyncGUI
import ffc.airsync.ui.createMessage
import org.apache.logging.log4j.kotlin.KotlinLogger

fun createErrorMessage(key: String, message: String, ex: java.lang.Exception, logger: KotlinLogger) {
    var exMessage = "\n"
    ex.stackTrace.forEach {
        exMessage += "$it\n}"
    }
    logger.error(message, ex)
    gui.createMessage(
        key,
        message + ex,
        AirSyncGUI.MESSAGE_TYPE.ERROR
    )
}
