package ffc.airsync.db

import ffc.entity.Link
import ffc.entity.System
import ffc.entity.place.Businsess
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import org.jdbi.v3.sqlobject.config.RegisterRowMapper
import org.jdbi.v3.sqlobject.statement.SqlQuery
import java.sql.ResultSet

interface QueryBusiness {

    @SqlQuery(
        """
SELECT
	villagebusiness.pcucode,
	villagebusiness.villcode,
	villagebusiness.businessno,
	villagebusiness.businessname as name,
	cbusiness.businessdesc as type
FROM
	villagebusiness
LEFT JOIN cbusiness ON
	cbusiness.businesstypecode=villagebusiness.businesstype
    """
    )
    @RegisterRowMapper(BusinessMapper::class)
    fun get(): List<Businsess>
}

class BusinessMapper : RowMapper<Businsess> {
    override fun map(rs: ResultSet, ctx: StatementContext): Businsess {
        return Businsess().apply {
            name = rs.getString("name")
            businessType = rs.getString("type")

            link = Link(System.JHICS)
            link?.keys?.put("pcucode", rs.getString("pcucode"))
            link?.keys?.put("villcode", rs.getString("villcode"))
            link?.keys?.put("businessno", rs.getString("businessno"))
        }
    }
}
