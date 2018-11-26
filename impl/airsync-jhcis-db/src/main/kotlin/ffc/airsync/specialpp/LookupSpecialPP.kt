package ffc.airsync.specialpp

import ffc.entity.healthcare.SpecialPP.PPType
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import org.jdbi.v3.sqlobject.config.RegisterRowMapper
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlQuery
import java.sql.ResultSet

private const val specialPPquery = """
SELECT
	cspecialpp.ppcode,
	cspecialpp.ppname
FROM
	cspecialpp

    """

interface LookupSpecialPP {
    @SqlQuery(specialPPquery + "WHERE cspecialpp.ppcode = :ppcode")
    @RegisterRowMapper(SpecialPpMapperType::class)
    fun get(@Bind("ppcode") ppcode: String): List<PPType>
}

internal class SpecialPpMapperType : RowMapper<PPType> {
    override fun map(rs: ResultSet, ctx: StatementContext?): PPType {
        return PPType(
            id = rs.getString("ppcode"),
            name = rs.getString("ppname")
        )
    }
}
