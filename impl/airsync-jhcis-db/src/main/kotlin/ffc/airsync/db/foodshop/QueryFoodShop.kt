package ffc.airsync.db.foodshop

import ffc.entity.Link
import ffc.entity.System
import ffc.entity.place.Business
import me.piruin.geok.geometry.Point
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import org.jdbi.v3.sqlobject.config.RegisterRowMapper
import org.jdbi.v3.sqlobject.statement.SqlQuery
import java.sql.ResultSet

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

            val xgis = rs.getDouble("xgis")
            val ygis = rs.getDouble("ygis")
            if ((xgis != 0.0) && (ygis != 0.0))
                location = Point(ygis, xgis)

            link = Link(System.JHICS)
            link?.keys?.put("pcucode", rs.getString("pcucode"))
            link?.keys?.put("villcode", rs.getString("villcode"))
            link?.keys?.put("foodshopno", rs.getString("foodshopno"))
        }
    }
}
