package ffc.airsync.mysql

private val mySqlStop = "net stop mysql_jhcis"
private val mySqlStart = "net start mysql_jhcis"

internal fun stopJhcisMySql() {
    val proc = Runtime.getRuntime().exec(mySqlStop)
    while (proc.isAlive) {
        Thread.sleep(200)
    }
}

internal fun startJhcisMySql() {
    val proc = Runtime.getRuntime().exec(mySqlStart)
    while (proc.isAlive) {
        Thread.sleep(200)
    }
}
