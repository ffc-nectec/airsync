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

import ffc.model.Pcu
import ffc.model.QueryAction
import ffc.model.fromJson
import ffc.model.toJson
import okhttp3.*

class CentralDataSeed : CentralData {

    val JSON = MediaType.parse("application/json; charset=utf-8")
    val client = OkHttpClient()

    override fun registerPcu(pcu: Pcu,url :String) :Pcu {
        val pcu2:Pcu  = putToServer(url,pcu.toJson()).body()!!.string().fromJson()
        return pcu2
    }

    override fun getData(): QueryAction {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun postToServer(url :String, json:String) :Response{
        val body = RequestBody.create(JSON, json)
        val request = Request.Builder()
          .url(url)
          .post(body)
          .build()
        val response = client.newCall(request).execute();
        return response
    }
    private fun putToServer(url :String, json:String) :Response{
        val body = RequestBody.create(JSON, json)
        val request = Request.Builder()
          .url(url)
          .put(body)
          .build()
        val response = client.newCall(request).execute();
        return response
    }
}
