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

package ffc.airsync.api.retrofit

import com.fatboyindustrial.gsonjodatime.DateTimeConverter
import com.fatboyindustrial.gsonjodatime.LocalDateConverter
import com.fatboyindustrial.gsonjodatime.LocalDateTimeConverter
import com.google.gson.GsonBuilder
import ffc.entity.Identity
import ffc.entity.User
import ffc.entity.gson.HealthCareJsonAdapter
import ffc.entity.gson.IdentityJsonAdapter
import ffc.entity.gson.UserJsonAdapter
import ffc.entity.gson.ffcGson
import ffc.entity.healthcare.HealthCareService
import me.piruin.geok.LatLng
import me.piruin.geok.geometry.Geometry
import me.piruin.geok.gson.GeometrySerializer
import me.piruin.geok.gson.LatLngSerializer
import me.piruin.geok.gson.adapterFor
import okhttp3.OkHttpClient
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class APIClient {
    fun getCientStd(baseUrl: String): Retrofit? {
        val client = OkHttpClient.Builder()
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(ffcGson))
            .client(client.build())
            .build()
        return retrofit
    }

    fun getCientAirsync(baseUrl: String): Retrofit? {
        val ffcGson = GsonBuilder()
            .adapterFor<User>(UserJsonAdapter())
            .adapterFor<Identity>(IdentityJsonAdapter())
            .adapterFor<HealthCareService>(HealthCareJsonAdapter())
            .adapterForExtLibrary()
            .create()

        val client = OkHttpClient.Builder()
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(ffcGson))
            .client(client.build())
            .build()
        return retrofit
    }

    private fun GsonBuilder.adapterForExtLibrary(): GsonBuilder {
        adapterFor<Geometry>(GeometrySerializer())
        adapterFor<LatLng>(LatLngSerializer())
        adapterFor<DateTime>(DateTimeConverter())
        adapterFor<LocalDate>(LocalDateConverter())
        adapterFor<LocalDateTime>(LocalDateTimeConverter())
        return this
    }
}
