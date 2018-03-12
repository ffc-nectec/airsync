/*
 * Copyright (c) 2018 NECTEC
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

import java.util.*

data class Message(var from: UUID = UUID.randomUUID(),
                   var to: UUID = UUID.randomUUID(),
                   var status: Status = Status.DEFAULT,
                   val action: Action = Action.DEFAULT,
                   val message: String = "H") {

    enum class Action(code: Int) {
        DEFAULT(0),REGISTER(1), PING(10), GETUSER(2), CONFIRMUSER(3),SENDTO(4)
    }
    enum class Status(code: Int){
        DEFAULT(0),ERROR(500), SUCC(200),NOTFOUND(404)
    }
}
