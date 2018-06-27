package ffc.airsync.client.mysqlconfig

import java.io.BufferedReader
import java.io.File
import java.io.FileReader

class MySqlConfigManage {

    fun setLog(logfilepath : String){
        val bufferedReader : BufferedReader = BufferedReader(FileReader(File(logfilepath)))

        var line :String?

        line=bufferedReader.readLine()
        while (line != null){
            System.out.println(line)
            line=bufferedReader.readLine()
        }


    }
}
