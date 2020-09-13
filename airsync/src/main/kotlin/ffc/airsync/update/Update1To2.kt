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

package ffc.airsync.update

import ffc.airsync.Main
import ffc.airsync.analyzer
import ffc.airsync.healthCare
import ffc.airsync.houseManage
import ffc.airsync.personManage
import ffc.airsync.relation
import ffc.airsync.userManage
import ffc.airsync.utils.syncCloud
import ffc.airsync.villages

internal class Update1To2 {
    fun runUpdate() {
        if (userManage.cloudUser.isNotEmpty()) {
            userManage.sync(true)
            syncCloud.sync(Main.instant.dao)
            houseManage.sync(true)
            personManage.sync(true)
        }
        relation.clear()
        analyzer.clear()
        healthCare.clear()
        villages.clear()
        houseManage.clear()
        personManage.clear()
        DeleteFileInData("Village.json").delete()
        DeleteFileInData("healthTemp.json").delete()
        DeleteFileInData("HealthCareService.json").delete()
        DeleteFileInData("analyzer.json").delete()
        DeleteFileInData("relation.json").delete()
    }
}
