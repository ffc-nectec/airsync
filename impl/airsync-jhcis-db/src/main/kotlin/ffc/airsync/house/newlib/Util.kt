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

package ffc.airsync.house.newlib

import me.piruin.geok.geometry.Point

internal class Util {
    fun getLocation(xgis: Double, ygis: Double): Point? {
        val point = try {

            if ((xgis != 0.0) && (ygis != 0.0))
                if (xgis < ygis)
                    Point(xgis, ygis)
                else
                    Point(ygis, xgis)
            else
                null
        } catch (ex: AssertionError) {
            null
        }
        if (point != null) {
            val latitude = point.coordinates.latitude
            val longitude = point.coordinates.longitude
            if (latitude >= -90 && latitude <= 90) {
                if (longitude >= -180 && longitude <= 180)
                    return point
            } else
                return null
        }
        return point
    }

    fun jhcisGeoToDouble(str: String?): Double {
        if (str.isNullOrEmpty()) return 0.0
        val rex = Regex("""^(\d+\.\d+).*""")
        val filter = rex.matchEntire(str)?.groupValues
        return filter?.lastOrNull()?.toDoubleOrNull() ?: 0.0
    }
}
