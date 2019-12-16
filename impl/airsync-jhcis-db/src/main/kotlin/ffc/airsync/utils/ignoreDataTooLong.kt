package ffc.airsync.utils

import com.mysql.jdbc.MysqlDataTruncation

fun ignoreDataTooLong(callback: () -> Unit) {
    try {
        callback()
    } catch (ex: MysqlDataTruncation) {
        val message = ex.message ?: ""
        if (!message.contains("Data too long for column")) throw ex
    }
}
