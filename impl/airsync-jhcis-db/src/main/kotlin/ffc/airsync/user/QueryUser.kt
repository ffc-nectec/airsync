package ffc.airsync.user

import com.google.gson.JsonParser
import ffc.entity.Link
import ffc.entity.System
import ffc.entity.User
import ffc.entity.User.Role.PROVIDER
import ffc.entity.User.Role.SURVEYOR
import ffc.entity.gson.ffcGson
import ffc.entity.gson.toJson
import ffc.entity.update
import max212.kotlin.util.hash.BCrypt
import max212.kotlin.util.hash.SHA265
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import org.jdbi.v3.sqlobject.config.RegisterRowMapper
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.joda.time.DateTime
import java.sql.ResultSet
import java.sql.Timestamp

interface QueryUser {
    @SqlQuery(
        """
SELECT
	user.username,
	user.password,
	user.pcucode,
	user.markdelete,
    user.officertype,
    user.dateupdate,
    user.idcard
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
    private val shA265 = SHA265()
    private val bCrypt = BCrypt(10)
    override fun map(rs: ResultSet, ctx: StatementContext): User {
        var user = User().update {
            val type = rs.getString("officertype") ?: "w"
            val role = if (type == "w") SURVEYOR else PROVIDER

            name = rs.getString("username")
            password = rs.getString("password")
            roles.add(role)
            link = Link(System.JHICS).apply {
                keys["username"] = name
                keys["pcucode"] = rs.getString("pcucode")
                keys["password"] = bCrypt.hash(password)
                rs.getString("idcard")?.let { keys["idcard"] = shA265.hash(it) }
            }
        }
        rs.getTimestamp("dateupdate").toTimestamp()?.let {
            // ปรับ timestamp ให้ตรงกับ dateupdate
            val jsonUser = JsonParser().parse(user.toJson()).asJsonObject
            jsonUser.addProperty("timestamp", it.toJson().replace("\"", "").trim())
            user = ffcGson.fromJson(jsonUser.toJson(), User::class.java)
        }
        return user
    }
}

private fun Timestamp.toTimestamp(): DateTime? {
    return kotlin.runCatching { DateTime(this.time) }.getOrNull()
}
