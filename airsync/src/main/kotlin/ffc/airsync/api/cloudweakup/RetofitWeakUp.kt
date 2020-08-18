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

package ffc.airsync.api.cloudweakup

import ffc.airsync.Config
import ffc.airsync.gui
import ffc.airsync.retrofit.ApiFactory
import ffc.airsync.ui.AirSyncGUI
import ffc.airsync.ui.createMessage
import ffc.airsync.utils.getLogger
import java.net.SocketTimeoutException

class RetofitWeakUp : WeakUpApi {

    private val restService = ApiFactory().buildApiClient(Config.baseUrlRest, WeakUpUrl::class.java, 128)
    override fun weakUp() {
        var count = 1
        val limitCount = 5
        var cloudStatusDown = true
        val logger = getLogger(this)
        while (cloudStatusDown && count++ <= limitCount) {
            try {
                logger.info("Wake cloud loop ${count - 1} in $limitCount")
                val response = restService.checkCloud().execute()
                gui.remove("Cloud Network error")
                if (response.code() == 200)
                    cloudStatusDown = false
            } catch (ignore: SocketTimeoutException) {
                cloudStatusDown = true
                Thread.sleep(3000)
            } catch (ex: java.net.UnknownHostException) {
                gui.createMessage(
                    "Cloud Network error",
                    "Network Error $ex",
                    AirSyncGUI.MESSAGE_TYPE.ERROR
                )
                Thread.sleep(3000)
            }
        }
    }
}
