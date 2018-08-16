package th.`in`.ffc.airsync.logreader

import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.Properties

class LineManage(var logConfig: String = "C:\\Program Files\\JHCIS\\MySQL\\data\\log.cnf") {
    private lateinit var properties: Properties
    private var lastLineNumber: Long = 0

    init {
        loadProperty()
        val lastLineStr = properties.getProperty("lastline") ?: "0"
    }

    fun getProperty(key: String): String {
        loadProperty()
        return properties.getProperty(key, "")
    }

    fun setProperty(key: String, value: String) {
        properties.setProperty(key, value)
        saveProperty()
    }

    fun setLastLineNumber(lineNumber: Long) {
        setProperty("lastline", lineNumber.toString())
    }

    fun getLastLineNumber(): Long {
        val lineNumberStr = getProperty("lastline")
        return if (lineNumberStr != "") {
            lineNumberStr.toLong()
        } else {
            0
        }
    }

    private fun saveProperty() {
        properties.store(FileOutputStream(logConfig), null)
    }

    private fun loadProperty() {
        val conf = Properties()
        try {
            conf.load(FileInputStream(logConfig))
        } catch (ignore: java.lang.NullPointerException) {
        }
        properties = conf
    }
}
