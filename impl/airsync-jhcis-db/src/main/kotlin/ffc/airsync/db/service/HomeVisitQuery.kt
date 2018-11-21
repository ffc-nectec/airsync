package ffc.airsync.db.service

import ffc.entity.healthcare.CommunityService
import ffc.entity.healthcare.HomeVisit
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import org.joda.time.LocalDate
import java.sql.ResultSet

private const val homeHealthQuery = """
SELECT
	visithomehealthindividual.homehealthtype,
	visithomehealthindividual.homehealthdetail,
	visithomehealthindividual.homehealthresult,
	visithomehealthindividual.dateappoint,
	visithomehealthindividual.homehealthplan
FROM
    visithomehealthindividual
"""

private const val homehealthIndex = """CREATE  INDEX visitnumber ON visithomehealthindividual(visitno)"""

interface HomeVisitQuery {
    @SqlUpdate(homehealthIndex)
    fun createIndex()

    @SqlQuery(
        homeHealthQuery + """
WHERE visithomehealthindividual.visitno = :visitnumber
    """
    )
    fun get(@Bind("visitnumber") visitnumber: Int): List<HomeVisit>
}

class VisitHomeHealthMapper : RowMapper<HomeVisit> {
    override fun map(rs: ResultSet, ctx: StatementContext?): HomeVisit {
        return HomeVisit(
            serviceType = CommunityService.ServiceType(
                id = rs.getString("homehealthtype"),
                name = ""
            )
        ).apply {
            rs.getString("homehealthdetail")?.let { detail = it }
            rs.getString("homehealthresult")?.let { result = it }
            rs.getString("homehealthplan")?.let { plan = it }
            rs.getDate("dateappoint")?.let { bundle.put("dateappoint", LocalDate(it.time)) }
        }
    }
}
