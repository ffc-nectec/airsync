package ffc.airsync.hosdetail

import ffc.airsync.getLogger
import ffc.entity.gson.toJson
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import org.jdbi.v3.sqlobject.config.RegisterRowMapper
import org.jdbi.v3.sqlobject.statement.SqlQuery
import java.sql.ResultSet

private val logger by lazy { getLogger(QueryHosDetail::class) }

interface QueryHosDetail {
    @SqlQuery(
        """
SELECT
	office.offid as offid,
	office.tel as tel,
	chospital.hosname as hosname,
	cprovince.provname as provname
FROM office
	INNER JOIN chospital
		ON office.offid = chospital.hoscode
	INNER JOIN cprovince
		ON chospital.provcode = cprovince.provcode
    """
    )
    @RegisterRowMapper(HosDetailMapper::class)
    fun get(): List<HashMap<String, String>>
}

class HosDetailMapper : RowMapper<HashMap<String, String?>> {
    override fun map(rs: ResultSet, ctx: StatementContext): HashMap<String, String?> {
        val detailHos = HashMap<String, String?>()

        detailHos["pcucode"] = rs.getString("offid")
        detailHos["tel"] = rs.getString("tel")
        detailHos["name"] = rs.getString("hosname")
        detailHos["province"] = rs.getString("provname")

        logger.info("DetailHos ${detailHos.toJson()}")
        return detailHos
    }
}
