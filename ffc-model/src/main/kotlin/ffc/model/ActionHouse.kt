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

package ffc.model

import org.joda.time.DateTime
import org.joda.time.LocalDate
import java.util.*

class ActionHouse(val orgUuid: UUID, val action: Address, val updateTo: UPDATETO) {

    val dateCreate = DateTime()
    val actionId = UUID.randomUUID()
    var status: STATUS = STATUS.NOT_COMPLETE

    enum class UPDATETO {
        MOBILE, ORG
    }

    enum class STATUS {
        COMPLETE, NOT_COMPLETE
    }

}
