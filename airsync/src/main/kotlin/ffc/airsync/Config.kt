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

package ffc.airsync

class Config private constructor() {

    companion object {
        var baseUrlRest = "https://ffc-nectec.herokuapp.com/v0/org/"
        // var baseUrlRest = "http://127.0.0.1:8080/v0/org/"
        var logfilepath = "C:\\Program Files\\JHCIS\\MySQL\\data\\jlog.log"
    }

    init {
        val url = System.getenv("FFC_CLOUD")
        val logfilepath = System.getenv("FFC_LOG_PART")

        if (logfilepath != null)
            Config.logfilepath = logfilepath
        if (url != null)
            baseUrlRest = url
    }
}
