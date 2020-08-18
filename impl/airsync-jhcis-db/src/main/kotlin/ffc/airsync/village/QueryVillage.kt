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

package ffc.airsync.village

import ffc.airsync.getLogger
import ffc.entity.Link
import ffc.entity.Place
import ffc.entity.System
import ffc.entity.Village
import ffc.entity.gson.toJson
import me.piruin.geok.geometry.Point
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.result.ResultSetException
import org.jdbi.v3.core.statement.StatementContext
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.UseRowMapper
import java.sql.ResultSet

private val logger by lazy { getLogger(QueryVillage::class) }

interface QueryVillage {
    @SqlQuery(
        """
SELECT
	village.pcucode,
	village.villcode,
	village.villname,
	village.latitude,
	village.longitude
FROM
	village
    """
    )
    @UseRowMapper(VillageMapper::class)
    fun get(): List<Village>
}

class VillageMapper : RowMapper<Village> {
    override fun map(rs: ResultSet, ctx: StatementContext): Village {
        val village = Village().apply {
            val place = Place().apply {
                villageName = rs.getString("villname") ?: ""

                try {
                    val longitude = rs.getString("longitude")?.toDoubleOrNull()
                    val latitude = rs.getString("latitude")?.toDoubleOrNull()

                    if (longitude != null && latitude != null)
                        if ((longitude != 0.0) && (latitude != 0.0)) {
                            if (latitude >= -90 && latitude <= 90) {
                                if (longitude >= -180 && longitude <= 180)
                                    location = Point(longitude, latitude)
                            } else
                                location = null

                            location = Point(longitude, latitude)
                        }
                } catch (ex: ResultSetException) {
                    logger.warn("Error xgis, ygix because convert error", ex)
                }
                link = Link(
                    System.JHICS,
                    "pcucode" to rs.getString("pcucode"),
                    "villcode" to rs.getString("villcode")
                )
            }

            name = place.villageName ?: ""
            places.add(place)
            link = place.link
        }
        logger.debug(village.toJson())
        return village
    }
}
