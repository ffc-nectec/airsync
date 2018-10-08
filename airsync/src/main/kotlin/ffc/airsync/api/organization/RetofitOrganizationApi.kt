package ffc.airsync.api.organization

import ffc.airsync.retrofit.RetofitApi
import ffc.airsync.utils.printDebug
import ffc.entity.Organization
import ffc.entity.Token
import ffc.entity.gson.toJson
import javax.xml.bind.DatatypeConverter

class RetofitOrganizationApi(org: Organization, serviceUrl: String, token: Token) : RetofitApi(org, serviceUrl, token),
    OrganizationApi {
    override fun registerOrganization(
        localOrganization: Organization,
        onSuccessRegister: (organization: Organization, token: Token) -> Unit
    ) {
        val token: Token
        wakeCloud()

        val orgRegis = regisOrgToCloud(localOrganization)

        val user = orgRegis.users[0]
        val authStr = user.name + ":" + user.password
        val authEncoded = DatatypeConverter.printBase64Binary(authStr.toByteArray())
        val authorization = "Basic $authEncoded"
        val tokenFromServer = restService.loginOrg(orgRegis.id, authorization).execute().body()
            ?: throw Exception("ไม่สามารถ Login org ได้")
        printDebug("\tToken = ${tokenFromServer.toJson()}")
        token = tokenFromServer
        orgRegis.bundle["token"] = tokenFromServer

        printDebug("Client update registerOrg from cloud ${orgRegis.toJson()}")

        onSuccessRegister(orgRegis, token)
    }

    private fun regisOrgToCloud(organization: Organization): Organization {
        val restOrg: Organization? = restService.regisOrg(organization).execute().body()
        check(restOrg != null) { "ลงทะเบียนชื่อหน่วยงานซ้ำ" }

        return restOrg!!
    }
}
