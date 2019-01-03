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
