package ffc.airsync.db.temple

import ffc.entity.Link
import ffc.entity.System
import ffc.entity.place.Religion
import ffc.entity.place.ReligiousPlace
import me.piruin.geok.geometry.Point
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import org.jdbi.v3.sqlobject.statement.SqlQuery
import java.sql.ResultSet

interface QueryTemple {

    @SqlQuery(
        """
SELECT
	villagetemple.pcucode,
	villagetemple.villcode,
	villagetemple.templeno,
	villagetemple.templename as name,
	villagetemple.address,
	creligion.religionname as religion,
	villagetemple.xgis,
	villagetemple.ygis
FROM
	villagetemple
LEFT JOIN creligion ON
	creligion.religioncode=villagetemple.religion
    """
    )
    fun get(): List<ReligiousPlace>
}

class TempleMapper : RowMapper<ReligiousPlace> {
    override fun map(rs: ResultSet, ctx: StatementContext): ReligiousPlace {
        return ReligiousPlace().apply {
            name = rs.getString("name")
            no = rs.getString("address")
            religion = Religion.byName(rs.getString("religion"))

            val xgis = rs.getDouble("xgis")
            val ygis = rs.getDouble("ygis")
            if ((xgis != 0.0) && (ygis != 0.0))
                location = Point(ygis, xgis)

            link = Link(System.JHICS)
            link?.keys?.put("pcucode", rs.getString("pcucode"))
            link?.keys?.put("villcode", rs.getString("villcode"))
            link?.keys?.put("templeno", rs.getString("templeno"))
        }
    }
}
