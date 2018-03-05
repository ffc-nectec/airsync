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

import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.client.WebSocketClient
import java.net.URI
import java.util.concurrent.Future

class PcuSocketEventManage(uri: URI) {
    //http://188.166.249.72

    //val uri = URI.create("ws://188.166.249.72:80/airsync")
    val client = WebSocketClient()
    val socket = PcuSocketEvent()
    var session: Session? = null

    fun sendText(message: String) {
        if (session != null) {
            session!!.getRemote().sendString(message)
        }else{
            throw NoSuchFieldException("Session Null")
        }
    }
    fun close(){
        // Close session
        this.session?.close()
        client.stop()
    }

    init {
        try {
            try {
                client.start()
                // Attempt Connect
                val fut: Future<Session> = client.connect(socket, uri)
                // Wait for Connect
                this.session = fut.get()

            } finally {

            }
        } catch (t: Throwable) {
            t.printStackTrace(System.err)
        }
    }

}
