package ffc.airsync.specialpp

import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import org.jdbi.v3.sqlobject.config.RegisterRowMapper
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlQuery
import java.sql.ResultSet

private const val specialQuery = """
SELECT
	f43specialpp.ppspecial,
	f43specialpp.dateupdate
FROM
	f43specialpp
"""

private const val visitNumberIndex = """CREATE  INDEX visitnumber ON f43specialpp(visitno)"""

interface SpecialppQuery {
    @SqlQuery(
        specialQuery + """
    WHERE f43specialpp.visitno = :visitnumber
    """
    )
    @RegisterRowMapper(SpecialPPMapper::class)
    fun get(@Bind("visitnumber") visitnumber: Int): List<String>
}

class SpecialPPMapper : RowMapper<String> {
    override fun map(rs: ResultSet, ctx: StatementContext?): String {
        return rs.getString("ppspecial")
    }
}
