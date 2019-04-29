package ffc.airsync

import ffc.airsync.ncds.NCDscreenQuery
import ffc.airsync.specialpp.SpecialppQuery
import ffc.airsync.visit.HomeVisitIndividualQuery
import ffc.airsync.visit.VisitDiagQuery
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.sqlobject.SqlObjectPlugin
import org.jdbi.v3.sqlobject.kotlin.KotlinSqlObjectPlugin
import javax.sql.DataSource

class MySqlJdbi(
    var ds: DataSource? = null
) : Dao {

    init {
        setupJdbiInstant()
    }

    companion object {
        private lateinit var jdbiDao: Jdbi
        private var dbConfig: DatabaseConfig? = null
    }

    private fun setupJdbiInstant() {
        if (dbConfig == null) dbConfig = DatabaseConfig()
        try {
            jdbiDao.toString()
        } catch (ex: UninitializedPropertyAccessException) {
            jdbiDao = createJdbi()
        }
    }

    override val instant get() = jdbiDao

    private fun createJdbi(): Jdbi {
        Class.forName("com.mysql.jdbc.Driver")
        val jdbi: Jdbi
        val dbHost: String = dbConfig!!.server
        val dbPort: String = dbConfig!!.port
        val dbName: String = dbConfig!!.databaseName
        val dbUsername: String = dbConfig!!.username
        val dbPassword: String = dbConfig!!.password

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

        // createIndex { jdbi.extension<VisitQuery, Unit> { createIndex() } }
        createIndex { jdbi.extension<VisitDiagQuery, Unit> { createIndex() } }
        createIndex { jdbi.extension<SpecialppQuery, Unit> { createIndex() } }
        createIndex { jdbi.extension<NCDscreenQuery, Unit> { createIndex() } }
        createIndex { jdbi.extension<HomeVisitIndividualQuery, Unit> { createIndex() } }
        return jdbi
    }
}

private fun createIndex(f: () -> Unit) {
    try {
        f()
    } catch (ignore: org.jdbi.v3.core.statement.UnableToExecuteStatementException) {
    }
}
