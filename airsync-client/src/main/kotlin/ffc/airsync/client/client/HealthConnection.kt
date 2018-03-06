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

package ffc.airsync.client.client

import ffc.airsync.client.Main
import ffc.model.TokenMessage
import ffc.model.toJson

class HealthConnection() {





    companion object {
        private var runLoop = false
        private val thread = Thread(Runnable {
            var state=0
            while (true) {
                if(runLoop) {
                    if (state==0)
                    {
                        NetworkClient.client.sendText(TokenMessage(Main.pcuDataTest.pcuToken!!).toJson())
                        state=1
                    }
                    NetworkClient.client.sendText("H")
                }
                Thread.sleep(5000)
            }
        })
    }

    fun start(){
        runLoop =true
    }
    fun stop(){
        runLoop =false
    }

    fun join(){
        thread.join()
    }

    init {
        thread.start()
    }


}
