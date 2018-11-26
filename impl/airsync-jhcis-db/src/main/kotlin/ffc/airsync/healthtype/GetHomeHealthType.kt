package ffc.airsync.healthtype

import ffc.entity.Lang
import ffc.entity.Link
import ffc.entity.System
import ffc.entity.healthcare.CommunityService.ServiceType
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import org.jdbi.v3.sqlobject.config.RegisterRowMapper
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlQuery
import java.sql.ResultSet

private const val homeHealtyTypeQuery = """
SELECT
	chomehealthtype.homehealthcode as code,
	chomehealthtype.homehealthmeaning as mean,
	chomehealthtype.homehealthmap as map
FROM
	chomehealthtype

    """

interface QueryHomeHealthType {
    @SqlQuery(homeHealtyTypeQuery + "WHERE chomehealthtype.homehealthcode = :healthcode")
    @RegisterRowMapper(GetHomeHealthMapper::class)
    fun get(@Bind("healthcode") healthcode: String): List<ServiceType>
}

internal class GetHomeHealthMapper : RowMapper<ServiceType> {
    override fun map(rs: ResultSet?, ctx: StatementContext?): ServiceType {
        if (rs == null) throw ClassNotFoundException()

        return ServiceType(
            rs.getString("code"),
            rs.getString("mean")
        ).apply {

            translation[Lang.th] = rs.getString("mean")
            link = Link(System.JHICS).apply {
                keys["code"] = rs.getString("code")
                try {
                    keys["map"] = rs.getString("map")
                } catch (ignore: IllegalStateException) {
                }
            }
        }
    }
}
