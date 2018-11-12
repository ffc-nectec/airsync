package ffc.airsync.db.visit

import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import org.jdbi.v3.sqlobject.config.RegisterRowMapper
import org.jdbi.v3.sqlobject.statement.SqlQuery
import java.sql.ResultSet

interface Query {
    @SqlQuery(
            """
        SELECT visitno FROM visit WHERE visit.visitno = (SELECT MAX(visit.visitno) FROM visit) LIMIT 1
    """
    )
    @RegisterRowMapper(MaxVisitNumberMapper::class)
    fun getMaxVisitNumber(): List<Long>
}

class MaxVisitNumberMapper : RowMapper<Long> {

    override fun map(rs: ResultSet?, ctx: StatementContext?): Long {
        if (rs == null) throw NullPointerException("MaxVisitNumberMapper result set is null")
        return rs.getLong("visitno")
    }
}
