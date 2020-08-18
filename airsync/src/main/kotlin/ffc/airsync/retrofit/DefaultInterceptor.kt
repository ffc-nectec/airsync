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

package ffc.airsync.retrofit

import ffc.airsync.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.lang.String.format

internal class DefaultInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        val builder = original.newBuilder()
            .addHeader("User-Agent", USER_AGENT)
            .addHeader("Accept", "application/json; charset=utf-8")
            .addHeader("Accept-Charset", "utf-8")
            .addHeader("X-Requested-By", "ffc-airsync")

        return chain.proceed(builder.build())
    }

    companion object {
        private val OS = format("%s; %s",
            System.getProperty("os.name"),
            System.getProperty("os.arch"))
        private val JAVA = format("Java/%s (%s)",
            System.getProperty("java.version"),
            System.getProperty("sun.arch.data.model"))
        private val USER_AGENT = "FFC-AirSync/${BuildConfig.VERSION} ($OS) $JAVA"
    }
}
