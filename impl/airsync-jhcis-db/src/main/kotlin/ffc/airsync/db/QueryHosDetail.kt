package ffc.airsync.db

import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import org.jdbi.v3.sqlobject.config.RegisterRowMapper
import org.jdbi.v3.sqlobject.statement.SqlQuery
import java.sql.ResultSet

interface QueryHosDetail {
    @SqlQuery("""
SELECT
	office.offid,
	office.tel,
	chospital.hosname,
	cprovince.provname
FROM office
	LEFT JOIN chospital
		ON office.offid = chospital.hoscode
	LEFT JOIN cprovince
		ON chospital.provcode = cprovince.provcode ;
    """
    )
    @RegisterRowMapper(HouseDetailMapper::class)
    fun get(): List<HashMap<String, String>>
}

class HouseDetailMapper : RowMapper<HashMap<String, String>> {
    override fun map(rs: ResultSet, ctx: StatementContext): HashMap<String, String> {
        val detailHos = HashMap<String, String>()

        detailHos["hosId"] = rs.getString("offid")
        detailHos["tel"] = rs.getString("tel")
        detailHos["name"] = rs.getString("hosname")
        detailHos["province"] = rs.getString("provname")

        return detailHos
    }
}
