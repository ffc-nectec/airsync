package ffc.airsync

import javax.sql.DataSource

class JhcisDBds {

    fun get(): DataSource {
        Class.forName("com.mysql.jdbc.Driver")

        val dsMySql = com.mysql.jdbc.jdbc2.optional.MysqlDataSource()

        val dbHost = "127.0.0.1"
        val dbPort = "3333"
        val dbName = "jhcisdb"
        val dbUsername = "root"
        val dbPassword = ""

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
        return dsMySql
    }
}
