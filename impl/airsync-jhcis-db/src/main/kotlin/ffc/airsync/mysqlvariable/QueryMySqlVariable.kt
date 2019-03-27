package ffc.airsync.mysqlvariable

import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import org.jdbi.v3.sqlobject.config.RegisterRowMapper
import org.jdbi.v3.sqlobject.statement.SqlQuery
import java.sql.ResultSet

interface QueryMySqlVariable {
    @SqlQuery("SHOW VARIABLES LIKE 'basedir'")
    @RegisterRowMapper(VariableMapping::class)
    fun getBaseDir(): List<String>

    @SqlQuery("SHOW VARIABLES LIKE 'datadir'")
    @RegisterRowMapper(VariableMapping::class)
    fun getDataDir(): List<String>
}

internal class VariableMapping : RowMapper<String> {
    override fun map(rs: ResultSet, ctx: StatementContext): String {
        return rs.getString("Value")
    }
}
