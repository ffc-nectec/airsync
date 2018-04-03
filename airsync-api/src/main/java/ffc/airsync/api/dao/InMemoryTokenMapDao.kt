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

import ffc.model.TokenMap
import java.util.*
import javax.ws.rs.NotFoundException

class InMemoryTokenMapDao : TokenMapDao<UUID> {

    val tokenMap = arrayListOf<TokenMap<UUID>>()

    override fun insert(token: TokenMap<UUID>) {

        //Device 1 per 1 token
        tokenMap.removeIf { it.user == token.user && it.uuid == token.uuid }
        tokenMap.add(token)

    }

    override fun find(token: String): UUID {
        val device = tokenMap.find { it.token == token }

        if (device != null) return device.uuid
        else
            throw NotFoundException()

    }

    override fun remove(token: String) {
        tokenMap.removeIf { it.token == token }

    }

}
