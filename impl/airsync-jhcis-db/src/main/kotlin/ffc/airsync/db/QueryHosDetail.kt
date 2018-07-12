package ffc.airsync.db

import ffc.airsync.utils.printDebug
import ffc.entity.gson.toJson
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import org.jdbi.v3.sqlobject.config.RegisterRowMapper
import org.jdbi.v3.sqlobject.statement.SqlQuery
import java.sql.ResultSet

interface QueryHosDetail {
    @SqlQuery("""
SELECT
	office.offid as offid,
	office.tel as tel,
	chospital.hosname as hosname,
	cprovince.provname as provname
FROM office
	LEFT JOIN chospital
		ON office.offid = chospital.hoscode
	LEFT JOIN cprovince
		ON chospital.provcode = cprovince.provcode
    """
    )
    @RegisterRowMapper(HosDetailMapper::class)
    fun get(): List<HashMap<String, String>>
}

class HosDetailMapper : RowMapper<HashMap<String, String?>> {
    override fun map(rs: ResultSet, ctx: StatementContext): HashMap<String, String?> {
        val detailHos = HashMap<String, String?>()

        rs.next()
        detailHos["offid"] = rs.getString("offid")
        detailHos["tel"] = rs.getString("tel")
        detailHos["name"] = rs.getString("hosname")
        detailHos["province"] = rs.getString("provname")

        printDebug("DetailHos ${detailHos.toJson()}")
        return detailHos
    }
}
