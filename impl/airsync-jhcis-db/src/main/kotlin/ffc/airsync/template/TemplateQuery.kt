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

package ffc.airsync.template

import ffc.entity.Template
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import org.jdbi.v3.sqlobject.config.RegisterRowMapper
import org.jdbi.v3.sqlobject.statement.SqlQuery
import java.sql.ResultSet

private const val syntom = """
SELECT syshomehealth1.hhsign as hint
FROM syshomehealth1
"""

private const val homeVisitDetail = """
SELECT syshomehealth2.hhservicecare as hint
FROM syshomehealth2
"""

private const val homeVisitResult = """
SELECT syshomehealth3.hhevalplan as hint
FROM syshomehealth3
"""

interface TemplateQuery {
    @SqlQuery(syntom)
    @RegisterRowMapper(SyntomHintMapper::class)
    fun getSyntom(): List<Template>

    @SqlQuery(homeVisitDetail)
    @RegisterRowMapper(HomeVisitDetailHintMapper::class)
    fun getHomeVisitDetail(): List<Template>

    @SqlQuery(homeVisitResult)
    @RegisterRowMapper(HomeVisitResultHintMapper::class)
    fun getHomeVisitResult(): List<Template>
}

internal class SyntomHintMapper : RowMapper<Template> {
    override fun map(rs: ResultSet, ctx: StatementContext?): Template {
        return Template(rs.getString("hint"), "HealthCareService.syntom")
    }
}

internal class HomeVisitDetailHintMapper : RowMapper<Template> {
    override fun map(rs: ResultSet, ctx: StatementContext?): Template {
        return Template(rs.getString("hint"), "HomeVisit.detail")
    }
}

internal class HomeVisitResultHintMapper : RowMapper<Template> {
    override fun map(rs: ResultSet, ctx: StatementContext?): Template {
        return Template(rs.getString("hint"), "HomeVisit.result")
    }
}
