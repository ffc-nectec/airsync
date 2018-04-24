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

package ffc.model

import me.piruin.geok.LatLng
import me.piruin.geok.geometry.Geometry


data class ProperitsGeoJson(val id: Int? = null) {


    var haveChronics: Boolean = false
    var type: String = "House"
    var no: String? = null
    var road: String? = null
    var tambon: String? = null
    var ampur: String? = null
    var changwat: String? = null
    var coordinates: LatLng? = null
    var people: ArrayList<People>? = arrayListOf()
    var hid: Int? = null
    var identity: Identity? = null
}

data class People(val id: String, val name: String)
data class MyGeo(override val type: String, val coordinates: LatLng) : Geometry


