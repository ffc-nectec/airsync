package ffc.airsync.utils

private val mySqlStop = "net stop mysql_jhcis"
private val mySqlStart = "net start mysql_jhcis"

fun stopJhcisMySql() {
    val proc = Runtime.getRuntime().exec(mySqlStop)
    while (proc.isAlive) {
        Thread.sleep(200)
    }
}

fun startJhcisMySql() {
    val proc = Runtime.getRuntime().exec(mySqlStart)
    while (proc.isAlive) {
        Thread.sleep(200)
    }
}
