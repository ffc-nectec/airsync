/*
 *
 *  * Copyright 2020 NECTEC
 *  *   National Electronics and Computer Technology Center, Thailand
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *    http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package ffc.airsync.api.icd10

import ffc.airsync.retrofit.RetofitApi
import ffc.airsync.utils.callApi
import ffc.airsync.utils.getLogger
import ffc.entity.healthcare.Icd10

class Icd10ServiceApi : RetofitApi<Icd10Service>(Icd10Service::class.java, 10240), Icd10Api {
    private val logger by lazy { getLogger(this) }
    private val cacheIcd404 = HashSet<String>()
    private var count404 = 0
    override fun lookup(icd10: String): Icd10 {

        val icd10Upper = icd10.toUpperCase()
        return callApi {
            if (cacheIcd404.contains(icd10Upper)) {
                count404++
                printIcd10NotFoundLog(icd10Upper)
                return@callApi Icd10(icd10 = icd10Upper, id = icd10Upper, name = "")
            }

            val response = restService.lookupIcd10(
                authkey = tokenBarer,
                id = icd10Upper
            ).execute()
            if (response.code() == 404) {
                count404++
                cacheIcd404.add(icd10Upper)
                printIcd10NotFoundLog(icd10Upper)
            }
            if (response.code() != 200) {
                val errorBody = response.errorBody()?.byteStream()?.reader()?.readLines()
                val message = "Error LookupICD10=$icd10Upper error=${response.code()} body=$errorBody"
                logger.warn(Exception(message)) { message }
            }
            response.body() ?: Icd10(icd10 = icd10Upper, id = icd10Upper, name = "")
        }
    }

    private fun printIcd10NotFoundLog(icd10Upper: String) {
        logger.warn { "icd10 $icd10Upper is 404. Count all 404 = $count404" }
    }
}
