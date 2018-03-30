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

class Config (){
    companion object {
        var pcuUuid ="00000000-0000-0000-0000-000000000010"
        //val baseUrlSocket = URI.create("ws://127.0.0.1:8080/airsync")
        //val baseUrlRest = "http://127.0.0.1:8080/v0/org/"
        val baseUrlRest = "http://188.166.249.72/v0/org/"
        //val baseUrlSocket = URI.create("ws://188.166.249.72:80/airsync")
    }
}
