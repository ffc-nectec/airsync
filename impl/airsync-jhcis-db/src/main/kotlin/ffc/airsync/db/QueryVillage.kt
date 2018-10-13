package ffc.airsync.db

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
	village.latitude as ygis,
	village.longitude as xgis
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
                val xgis = rs.getDouble("xgis")
                val ygis = rs.getDouble("ygis")

                villageName = rs.getString("villname") ?: ""

                if ((xgis != 0.0) && (ygis != 0.0))
                    location = Point(xgis, ygis)

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
