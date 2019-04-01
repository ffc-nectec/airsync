package ffc.airsync.business

import ffc.airsync.getLogger
import ffc.entity.Link
import ffc.entity.System
import ffc.entity.place.Business
import me.piruin.geok.geometry.Point
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.result.ResultSetException
import org.jdbi.v3.core.statement.StatementContext
import org.jdbi.v3.sqlobject.config.RegisterRowMapper
import org.jdbi.v3.sqlobject.statement.SqlQuery
import java.sql.ResultSet

private val logger by lazy { getLogger(QueryBusiness::class) }

interface QueryBusiness {

    @SqlQuery(
        """
SELECT
	villagebusiness.pcucode,
	villagebusiness.villcode,
	villagebusiness.businessno,
	villagebusiness.businessname as name,
	villagebusiness.address,
	cbusiness.businessdesc as type,
	villagebusiness.xgis,
	villagebusiness.ygis
FROM
	villagebusiness
LEFT JOIN cbusiness ON
	cbusiness.businesstypecode=villagebusiness.businesstype
    """
    )
    @RegisterRowMapper(BusinessMapper::class)
    fun get(): List<Business>
}

class BusinessMapper : RowMapper<Business> {
    override fun map(rs: ResultSet, ctx: StatementContext): Business {
        return Business().apply {
            name = rs.getString("name")
            businessType = rs.getString("type")
            no = rs.getString("address")

            try {
                val xgis = rs.getDouble("xgis")
                val ygis = rs.getDouble("ygis")
                if ((xgis != 0.0) && (ygis != 0.0))
                    location = Point(ygis, xgis)
            } catch (ex: ResultSetException) {
                logger.error("xgis, ygis Error", ex)
            }

            link = Link(System.JHICS)
            link?.keys?.put("pcucode", rs.getString("pcucode"))
            link?.keys?.put("villcode", rs.getString("villcode"))
            link?.keys?.put("businessno", rs.getString("businessno"))
        }
    }
}
