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

const val API = "https://api.ffc.in.th"
const val APIVERSION = "v1"

// const val API = "https://ffcmaekawtom.herokuapp.com"
// const val API = "https://ffc-beta.herokuapp.com"
// const val API = "https://ffc-staging.herokuapp.com"
// const val API = "http://127.0.0.1:8080"
const val MYSQLLOG = "C:\\Program Files\\JHCIS\\MySQL\\data\\jlog.log"

class Config private constructor() {

    companion object {
        lateinit var baseUrlRest: String
        lateinit var logfilepath: String
    }
}
