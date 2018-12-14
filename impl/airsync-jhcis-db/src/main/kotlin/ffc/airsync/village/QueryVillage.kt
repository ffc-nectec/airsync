package ffc.airsync.village

import ffc.entity.Link
import ffc.entity.Place
import ffc.entity.System
import ffc.entity.Village
import me.piruin.geok.geometry.Point
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.UseRowMapper
import java.sql.ResultSet

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
        return Village().apply {
            val place = Place().apply {
                val longitude = rs.getString("longitude")?.toDoubleOrNull()
                val latitude = rs.getString("latitude")?.toDoubleOrNull()

                villageName = rs.getString("villname") ?: ""

                if (longitude != null && latitude != null)
                    if ((longitude != 0.0) && (latitude != 0.0))
                        location = Point(longitude, latitude)

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
    }
}
