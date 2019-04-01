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

package ffc.airsync.retrofit

import ffc.airsync.utils.getLogger
import ffc.entity.gson.ffcGson
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

internal class RetofitAPIClient {

    private val logger by lazy { getLogger(this) }
    fun getCient(baseUrl: String, cacheKbyte: Int, prefix: String): Retrofit {

        val createTempDir = createTempDir(prefix, "airsync")
        logger.debug("Retofit temp dir ${createTempDir.absolutePath}")
        val cacheSize = Cache(createTempDir, cacheKbyte * 1024L)

        val client = OkHttpClient
            .Builder()
            .cache(cacheSize)
            .addInterceptor(DefaultInterceptor())
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(ffcGson))
            .build()
    }
}
