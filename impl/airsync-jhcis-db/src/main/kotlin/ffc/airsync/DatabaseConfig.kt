package ffc.airsync

import java.io.File
import java.io.FileInputStream
import java.util.Properties

internal class DatabaseConfig {
    private val jhcisConfigFile = "C:\\Program Files\\JHCIS\\database.properties"
    private val property = Properties()

    init {
        val file = File(jhcisConfigFile)
        require(file.isFile) { "ไม่พบไฟล์ $jhcisConfigFile ของตัวระบบ JHCIS" }
        property.load(FileInputStream(file))
    }

    val server get() = property.getProperty("SERVER")
    val port get() = property.getProperty("PORT")
    val username get() = property.getProperty("USERNAME")
    val password get() = property.getProperty("PASSWORD")
    val databaseName get() = property.getProperty("DATABASE")
    val currentOrganization get() = property.getProperty("PCUCODE")
}
