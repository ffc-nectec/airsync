/*
 *
 *  * Copyright 2020 NECTEC
 *  *   National Electronics and Computer Technology Center, Thailand
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *    http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

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
