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

package ffc.airsync.client.client.module

import ffc.model.printDebug
import org.apache.commons.codec.digest.DigestUtils
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.WebSocketAdapter

class BaseNetworkSocket : WebSocketAdapter() {

    interface OnWebSocketText{
        fun onWebSocketText(message: String?, count: Long)
    }

    var onWebSocketText :OnWebSocketText? = null


    var session: String = ""
    var count : Long = 0

    override fun onWebSocketConnect(sess: Session?) {
        super.onWebSocketConnect(sess)
        printDebug("Socket Connected: " + sess)
        this.session = DigestUtils.sha1Hex(sess.toString())
        printDebug("Session= " + this.session)
    }

    override fun onWebSocketText(message: String?) {
        super.onWebSocketText(message)
        printDebug("onWebSocketText")
        printDebug("Count:" + (count++) + "\tReceived TEXT data: " + message)
        onWebSocketText?.onWebSocketText(message,count)

    }

    override fun onWebSocketClose(statusCode: Int, reason: String?) {
        super.onWebSocketClose(statusCode, reason)
        printDebug("Socket Closed: [" + statusCode + "] " + reason)
    }

    override fun onWebSocketError(cause: Throwable?) {
        super.onWebSocketError(cause)
        cause!!.printStackTrace(System.err)
    }
}
