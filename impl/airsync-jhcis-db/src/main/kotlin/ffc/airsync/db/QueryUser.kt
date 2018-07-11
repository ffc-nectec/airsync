package ffc.airsync.db

import ffc.entity.Link
import ffc.entity.System
import ffc.entity.User
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import org.jdbi.v3.sqlobject.config.RegisterRowMapper
import org.jdbi.v3.sqlobject.statement.SqlQuery
import java.sql.ResultSet

interface QueryUser {
    @SqlQuery("""
        SELECT user.username, user.password FROM user
    """)
    @RegisterRowMapper(UserMapper::class)
    fun get(): List<User>
}

class UserMapper : RowMapper<User> {
    override fun map(rs: ResultSet, ctx: StatementContext): User {
        return User().update {
            name = rs.getString("username")
            password = rs.getString("password")
            link = Link(System.JHICS).apply {
                keys["username"] = name
            }
        }
    }
}
