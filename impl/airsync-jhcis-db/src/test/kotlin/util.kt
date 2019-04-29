import ffc.airsync.JdbiDao
import ffc.airsync.MySqlJdbi
import ffc.airsync.db.DatabaseDao
import org.h2.jdbcx.JdbcConnectionPool
import java.sql.DriverManager

fun JdbiDao.createTest(): DatabaseDao {
    val jdbUrlString = "jdbc:h2:mem:test"
    val dbUser = "root"
    val dbPassword = "123456"

    val ds = JdbcConnectionPool.create(
        "$jdbUrlString;MODE=MySQL",
        dbUser,
        dbPassword
    )

    val conn = DriverManager.getConnection(jdbUrlString, dbUser, dbPassword)

    conn.createStatement().executeQuery("create database jhcisdb")

    return JdbiDao(MySqlJdbi(ds))
}
