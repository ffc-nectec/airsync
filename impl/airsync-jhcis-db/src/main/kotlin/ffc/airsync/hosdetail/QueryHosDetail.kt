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
