/*
 *
 *  * Copyright 2020 NECTEC
 *  *   National Electronics and Computer Technology Center, Thailand
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *    http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package ffc.airsync

import ffc.airsync.ncds.NCDscreenQuery
import ffc.airsync.specialpp.SpecialppQuery
import ffc.airsync.visit.HomeVisitIndividualQuery
import ffc.airsync.visit.VisitDiagQuery
import ffc.airsync.visit.VisitQuery
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
        internal val dbConfig: DatabaseConfig by lazy { DatabaseConfig() }
    }

    private fun setupJdbiInstant() {
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
        val dbHost: String = dbConfig.server
        val dbPort: String = dbConfig.port
        val dbName: String = dbConfig.databaseName
        val dbUsername: String = dbConfig.username
        val dbPassword: String = dbConfig.password

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

        createIndex { jdbi.extension<VisitQuery, Unit> { createIndex() } }
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
