package ffc.airsync.utils

import ffc.entity.User
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.Properties

class PropertyStore(var logConfig: String = "C:\\Program Files\\JHCIS\\MySQL\\data\\ffcProperty.cnf") {

    private lateinit var properties: Properties

    init {
        loadProperty()
    }

    var token: String
        get() = getProperty("token")
        set(value) = setProperty("token", value)

    var orgId: String
        get() = getProperty("orgId")
        set(value) = setProperty("orgId", value)

    var userOrg: User
        get() = User(getProperty("userIdOrg")).apply {
            name = getProperty("userOrg")
        }
        set(value) {
            setProperty("userIdOrg", value.id)
            setProperty("userOrg", value.name)
        }

    var everPutData: Boolean
        get() = getProperty("EverPutData").toBoolean()
        set(value) {
            setProperty("EverPutData", value.toString())
        }

    fun getProperty(key: String): String {
        loadProperty()
        return properties.getProperty(key, "")
    }

    fun setProperty(key: String, value: String) {
        properties.setProperty(key, value)
        saveProperty()
    }

    private fun saveProperty() {
        properties.store(FileOutputStream(logConfig), null)
    }

    private fun loadProperty() {
        val conf = Properties()
        try {
            conf.load(FileInputStream(logConfig))
        } catch (ignore: java.io.FileNotFoundException) {
        }
        properties = conf
    }
}
