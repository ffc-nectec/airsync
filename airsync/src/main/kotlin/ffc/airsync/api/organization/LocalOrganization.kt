package ffc.airsync.api.organization

import ffc.airsync.db.DatabaseDao
import ffc.entity.Link
import ffc.entity.Organization
import ffc.entity.System
import ffc.entity.User
import ffc.entity.update
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.Properties
import java.util.UUID

class LocalOrganization(
    val dao: DatabaseDao,
    var logConfig: String = "C:\\Program Files\\JHCIS\\MySQL\\data\\ffcProperty.cnf"
) {

    private lateinit var properties: Properties
    val organization: Organization

    init {
        loadProperty()
        organization = getOrganizationDetail(orgId)
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

    private fun getOrganizationDetail(orgId: String): Organization {
        val org: Organization
        if (orgId.isNotEmpty()) {
            org = Organization(orgId)
        } else {
            org = Organization()
        }
        with(org) {
            val detail = dao.getDetail()
            val hosId = detail["pcucode"] ?: ""

            // pcucode.append(hosId)

            name = detail["name"] ?: ""
            name = name.replace(Regex("""[\.\,\|\(\)\ ]"""), "")
            displayName = detail["name"] ?: ""
            tel = detail["tel"]
            address = detail["province"]
            link = Link(System.JHICS).apply {
                keys["pcucode"] = hosId
            }
            users.add(createAirSyncUser(hosId))
            update { }
        }
        return org
    }

    private fun createAirSyncUser(hosId: String): User = User().update {
        name = "airsync$hosId"
        password = UUID.randomUUID().toString().replace("-", "")
        role = User.Role.ORG
    }
}
