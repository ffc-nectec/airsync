package ffc.airsync

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.sqlobject.SqlObjectPlugin
import org.jdbi.v3.sqlobject.kotlin.KotlinSqlObjectPlugin
import javax.sql.DataSource

abstract class MySqlJdbi(
    val dbHost: String = "127.0.0.1",
    val dbPort: String = "3333",
    val dbName: String = "jhcisdb",
    val dbUsername: String = "root",
    val dbPassword: String = "123456",
    var ds: DataSource? = null
) {
    companion object {
        lateinit var jdbiDao: Jdbi
    }

    init {
        jdbiDao = createJdbi()
    }

    private fun createJdbi(): Jdbi {
        Class.forName("com.mysql.jdbc.Driver")
        val jdbi: Jdbi

        if (ds == null) {
            val dsMySql = com.mysql.jdbc.jdbc2.optional.MysqlDataSource()

            dsMySql.setURL(
                "jdbc:mysql://$dbHost:$dbPort/$dbName?" +
                        "autoReconnect=true&" +
                        "useSSL=false&" +
                        "maxReconnects=2&" +
                        "autoReconnectForPools=true&" +
                        "connectTimeout=10000&" +
                        "socketTimeout=10000"
            )
            dsMySql.databaseName = dbName
            dsMySql.user = dbUsername
            dsMySql.setPassword(dbPassword)
            dsMySql.port = dbPort.toInt()
            ds = dsMySql
            // pool.add(dsMySql.connection)
            jdbi = Jdbi.create(dsMySql)
        } else {
            jdbi = Jdbi.create(ds)
        }

        jdbi.installPlugin(KotlinPlugin())
        jdbi.installPlugin(SqlObjectPlugin())
        jdbi.installPlugin(KotlinSqlObjectPlugin())
        return jdbi
    }
}
