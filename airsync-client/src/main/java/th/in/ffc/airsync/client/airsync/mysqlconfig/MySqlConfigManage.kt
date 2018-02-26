package th.`in`.ffc.airsync.client.airsync.mysqlconfig

import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.util.*

class MySqlConfigManage {

    fun setLog(logfilepath : String){
        val bufferedReader : BufferedReader = BufferedReader(FileReader(File(logfilepath)))

        var line :String?
        var i :Int = 0

        line=bufferedReader.readLine()
        while (line != null){
            System.out.println(line)
            line=bufferedReader.readLine()
        }


    }
}
