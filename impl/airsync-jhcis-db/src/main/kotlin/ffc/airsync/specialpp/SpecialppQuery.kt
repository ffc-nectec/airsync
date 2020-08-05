package ffc.airsync.specialpp

import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import org.jdbi.v3.sqlobject.config.RegisterRowMapper
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import java.sql.ResultSet

private const val specialQuery = """
SELECT
	f43specialpp.ppspecial,
	f43specialpp.dateupdate,
    f43specialpp.visitno
FROM
	f43specialpp
"""

private const val visitNumberIndex = """CREATE  INDEX visitnumber ON f43specialpp(visitno)"""

interface SpecialppQuery {

    @SqlUpdate(visitNumberIndex)
    fun createIndex()

    @SqlQuery(
        specialQuery + """
    WHERE f43specialpp.visitno = :visitnumber
    """
    )
    @RegisterRowMapper(SpecialPPMapper::class)
    fun getBy(@Bind("visitnumber") visitnumber: Long): List<String>

    @SqlQuery(
        specialQuery + """
    WHERE f43specialpp.visitno IS NOT NULL
    """
    )
    @RegisterRowMapper(SpecialPPMapperAll::class)
    fun getAll(): List<HashMap<Long, String>>
}

class SpecialPPMapper : RowMapper<String> {
    override fun map(rs: ResultSet, ctx: StatementContext?): String {
        return rs.getString("ppspecial")
    }
}

class SpecialPPMapperAll : RowMapper<HashMap<Long, String>> {
    override fun map(rs: ResultSet, ctx: StatementContext?): HashMap<Long, String> {
        return hashMapOf(rs.getLong("visitno") to rs.getString("ppspecial")!!)
    }
}
