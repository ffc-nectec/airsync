package ffc.airsync.user

import ffc.entity.Link
import ffc.entity.System
import ffc.entity.User
import ffc.entity.User.Role.PROVIDER
import ffc.entity.User.Role.SURVEYOR
import ffc.entity.update
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import org.jdbi.v3.sqlobject.config.RegisterRowMapper
import org.jdbi.v3.sqlobject.statement.SqlQuery
import java.sql.ResultSet

interface QueryUser {
    @SqlQuery(
        """
SELECT
	user.username,
	user.password,
	user.pcucode,
	user.markdelete,
    user.officertype
FROM user
	WHERE
		user.password IS NOT NULL
		AND user.markdelete IS NULL
"""
    )
    @RegisterRowMapper(UserMapper::class)
    fun get(): List<User>
}

class UserMapper : RowMapper<User> {

    override fun map(rs: ResultSet, ctx: StatementContext): User {
        return User().update {
            val type = rs.getString("officertype") ?: "w"
            val role = if (type == "w") SURVEYOR else PROVIDER

            name = rs.getString("username")
            password = rs.getString("password")
            roles.add(role)
            link = Link(System.JHICS).apply {
                keys["username"] = name
                keys["pcucode"] = rs.getString("pcucode")
            }
        }
    }
}
