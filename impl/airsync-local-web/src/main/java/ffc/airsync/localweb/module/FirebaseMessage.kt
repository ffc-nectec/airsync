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

package ffc.airsync.client.webservice.module

import ffc.airsync.notification.Notification
import ffc.entity.Messaging

class FirebaseMessage private constructor() : Notification {
    private lateinit var identifierChange: ((HashMap<String, String>) -> Unit)
    private lateinit var onDataChange: ((type: String, id: String) -> Unit)

    override fun onTokenChange(callback: (HashMap<String, String>) -> Unit) {
        identifierChange = callback
    }

    override fun onReceiveDataUpdate(callback: (type: String, id: String) -> Unit) {
        onDataChange = callback
    }

    companion object {
        val instant by lazy { FirebaseMessage() }
    }

    fun updateToken(firebaseToken: HashMap<String, String>) {
        identifierChange.invoke(firebaseToken)
    }

    fun update(data: Messaging) {
        onDataChange.invoke(data.type, data.id)
    }
}
