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

import ffc.model.QueryAction
import java.util.*

class InMemoryMessageActionDao : MessageActionDao {

    private constructor()
    val actionList = arrayListOf<QueryAction>()


    companion object {
        val instance = InMemoryMessageActionDao()

    }

    override fun insert(action: QueryAction) {
        actionList.add(action)
    }

    override fun next(to: UUID): QueryAction {
        val action =  actionList.find { it.ownAction == to }

        if(action != null)
            return action
        else
            return QueryAction()
    }

    override fun remove(action: QueryAction) {
        actionList.removeIf { it.equals(action) }
    }

}
