package ffc.airsync.mysqlvariable

interface GetMySqlVariable {
    fun mysqlLocation(): String
    fun mysqlDataDirectory(): String
}
