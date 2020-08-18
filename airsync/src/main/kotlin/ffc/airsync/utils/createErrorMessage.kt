/*
 *
 *  * Copyright 2020 NECTEC
 *  *   National Electronics and Computer Technology Center, Thailand
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *    http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

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
