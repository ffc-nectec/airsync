package th.`in`.ffc.airsync.client.airsync

import org.junit.Test
import th.`in`.ffc.airsync.client.airsync.mysqlconfig.MySqlConfigManage

class MySqlConfigManageTest {

    @Test
    fun readLog(){
        var test = MySqlConfigManage()
        test.setLog("C:\\Program Files\\JHCIS\\MySQL\\my.ini")
    }
}
