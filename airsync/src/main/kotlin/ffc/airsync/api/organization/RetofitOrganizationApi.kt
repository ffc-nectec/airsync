package ffc.airsync.api.organization

import ffc.airsync.retrofit.RetofitApi
import ffc.airsync.utils.printDebug
import ffc.entity.Organization
import ffc.entity.Token
import ffc.entity.gson.toJson
import javax.xml.bind.DatatypeConverter

class RetofitOrganizationApi : RetofitApi(), OrganizationApi {
    override fun registerOrganization(
        localOrganization: Organization,
        onSuccessRegister: (organization: Organization, token: Token) -> Unit
    ) {
        wakeCloud()

        if (isEverRegister(organization)) {
            token = organization.bundle["token"] as Token
            organization = organization
        } else {

            organization = regisOrgToCloud(localOrganization)

            val user = organization.users[0]
            val authStr = user.name + ":" + user.password
            val authEncoded = DatatypeConverter.printBase64Binary(authStr.toByteArray())
            val authorization = "Basic $authEncoded"
            val tokenFromServer = restService.loginOrg(organization.id, authorization).execute().body()
                ?: throw Exception("ไม่สามารถ Login org ได้")
            printDebug("\tToken = ${tokenFromServer.toJson()}")
            token = tokenFromServer
            organization.bundle["token"] = tokenFromServer

            printDebug("Client update registerOrg from cloud ${organization.toJson()}")
        }
        onSuccessRegister(organization, token)
    }

    private fun isEverRegister(organization: Organization) = organization.bundle["token"] != null

    private fun regisOrgToCloud(organization: Organization): Organization {
        val restOrg: Organization? = restService.regisOrg(organization).execute().body()
        check(restOrg != null) { "ลงทะเบียนชื่อหน่วยงานซ้ำ" }

        return restOrg!!
    }
}
