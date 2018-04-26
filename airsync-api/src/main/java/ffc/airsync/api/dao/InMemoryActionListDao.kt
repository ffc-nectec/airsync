/*
 * Copyright (c) 2561 NECTEC
 *   National Electronics and Computer Technology Center, Thailand
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ffc.airsync.api.dao

import ffc.model.ActionHouse
import ffc.model.printDebug
import java.util.*
import javax.ws.rs.NotFoundException

class InMemoryActionListDao private constructor() : ActionListDao {

    val actionList = arrayListOf<ActionHouse>()

    companion object {
        val instant = InMemoryActionListDao()
    }


    override fun insert(actionHouse: ActionHouse) {
        printDebug("Insert action house update to ${actionHouse.updateTo}")
        actionList.add(actionHouse)

    }

    override fun get(orgUUID: UUID, updateTo: ActionHouse.UPDATETO): List<ActionHouse> {


        printDebug("Get house by orgUUID:$orgUUID update to $updateTo")


        val listActionOrg = actionList.filter {
            it.orgUuid == orgUUID &&
              it.status == ActionHouse.STATUS.NOT_COMPLETE
        }
        return listActionOrg
    }

    override fun updateStatusComplete(actionId: UUID) {
        printDebug("Update action status action id $actionId")
        val action = actionList.find {
            it.actionId == actionId
        } ?: throw NotFoundException("ไม่พบ Action id ให้ Update complete")
        action.status = ActionHouse.STATUS.COMPLETE
    }
}
