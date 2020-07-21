package ffc.airsync

import com.wix.mysql.EmbeddedMysql
import com.wix.mysql.EmbeddedMysql.anEmbeddedMysql
import com.wix.mysql.ScriptResolver
import com.wix.mysql.config.Charset.UTF8
import com.wix.mysql.config.MysqldConfig.aMysqldConfig
import com.wix.mysql.distribution.Version.v5_6_23
import java.sql.Connection
import java.sql.DriverManager
import java.util.concurrent.TimeUnit

class MySqlUnitTestServer(
    private val databaseName: String,
    private val initSqlResource: String,
    private val port: Int = mySqlPort,
    private val user: String = mySqlUser,
    private val password: String = mySqlPass
) {
    val connection: Connection
    private val config = aMysqldConfig(v5_6_23)
        .withCharset(UTF8)
        .withPort(port)
        .withUser(user, password)
        .withTimeZone("Europe/Vilnius")
        .withTimeout(2, TimeUnit.MINUTES)
        .withServerVariable("max_connect_errors", 666)
        .build()

    private val mysqld: EmbeddedMysql = anEmbeddedMysql(config)
        .addSchema(databaseName, ScriptResolver.classPathScript(initSqlResource))
        .start()

    init {
        connection = DriverManager.getConnection("jdbc:mysql://localhost:$port/$databaseName", user, password)
    }

    fun stop() {
        mysqld.stop()
    }
}
