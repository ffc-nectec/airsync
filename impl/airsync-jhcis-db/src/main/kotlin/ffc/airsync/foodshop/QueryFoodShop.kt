package ffc.airsync.foodshop

import ffc.airsync.getLogger
import ffc.airsync.utils.getLocation
import ffc.entity.Link
import ffc.entity.System
import ffc.entity.place.Business
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import org.jdbi.v3.sqlobject.config.RegisterRowMapper
import org.jdbi.v3.sqlobject.statement.SqlQuery
import java.sql.ResultSet

private val logger by lazy { getLogger(QueryFoodShop::class) }

interface QueryFoodShop {
    @SqlQuery(
        """
SELECT
	villagefoodshop.pcucode,
	villagefoodshop.villcode,
	villagefoodshop.foodshopno,
	villagefoodshop.foodshopname as name,
	villagefoodshop.address,
	cbusiness.businessdesc as type,
	villagefoodshop.xgis,
	villagefoodshop.ygis
FROM
	villagefoodshop
LEFT JOIN cbusiness ON
	cbusiness.businesstypecode=villagefoodshop.foodshopno

    """
    )
    @RegisterRowMapper(FoodShopMapper::class)
    fun get(): List<Business>
}

class FoodShopMapper : RowMapper<Business> {
    override fun map(rs: ResultSet, ctx: StatementContext): Business {
        return Business().apply {
            name = rs.getString("name")
            businessType = rs.getString("type")
            no = rs.getString("address")
            location = getLocation(rs)
            link = Link(System.JHICS)
            link?.keys?.put("pcucode", rs.getString("pcucode"))
            link?.keys?.put("villcode", rs.getString("villcode"))
            link?.keys?.put("foodshopno", rs.getString("foodshopno"))
        }
    }
}
