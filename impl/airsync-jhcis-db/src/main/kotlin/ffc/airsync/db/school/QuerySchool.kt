package ffc.airsync.db.school

import ffc.entity.Link
import ffc.entity.System
import ffc.entity.place.Education
import ffc.entity.place.School
import me.piruin.geok.geometry.Point
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import org.jdbi.v3.sqlobject.config.RegisterRowMapper
import org.jdbi.v3.sqlobject.statement.SqlQuery
import java.sql.ResultSet

interface QuerySchool {

    @SqlQuery(
        """
SELECT
	villageschool.pcucode,
	villageschool.villcode,
	villageschool.schoolno,
	villageschool.schoolname as name,
	cschoolclass.classname as maxclass,
	cschooldepend.schooldependname as depen,
	villageschool.address,
	villageschool.xgis,
	villageschool.ygis
FROM
	villageschool
LEFT JOIN cschoolclass ON
	cschoolclass.classcode=villageschool.maxclass
LEFT JOIN cschooldepend ON
	cschooldepend.schooldependcode=villageschool.depend
    """
    )
    @RegisterRowMapper(SchoolMapper::class)
    fun get(): List<School>
}

class SchoolMapper : RowMapper<School> {
    override fun map(rs: ResultSet, ctx: StatementContext): School {
        return School().apply {

            name = rs.getString("name")
            educationLevel = Education.byName(rs.getString("maxclass"))
            depend = rs.getString("depen")
            no = rs.getString("address")

            val xgis = rs.getDouble("xgis")
            val ygis = rs.getDouble("ygis")
            if ((xgis != 0.0) && (ygis != 0.0))
                location = Point(ygis, xgis)

            link = Link(System.JHICS)
            link?.keys?.put("pcucode", rs.getString("pcucode"))
            link?.keys?.put("villcode", rs.getString("villcode"))
            link?.keys?.put("schoolno", rs.getString("schoolno"))
        }
    }
}
