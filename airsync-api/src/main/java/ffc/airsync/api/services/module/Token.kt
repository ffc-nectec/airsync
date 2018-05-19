package ffc.airsync.api.services.module

import ffc.model.MobileToken
import ffc.model.Organization
import ffc.model.StorageOrg
import ffc.model.printDebug
import java.util.*
import javax.ws.rs.NotAuthorizedException
import javax.ws.rs.NotFoundException

fun getOrgByOrgToken(token: String, orgId: String): Organization {
    printDebug("getOrgByOrgToken $token")
    val org = orgDao.findByToken(token)
    if (org.id != orgId) {
        printDebug("org ไม่ตรงกัน")
        throw throw NotFoundException("Org ไม่ตรง")
    }

    return org
}

fun getOrgByMobileToken(token: UUID, orgId: String): StorageOrg<MobileToken> {
    printDebug("Befor check mobile token")
    val orgUuid = tokenMobile.find(token)
    if (orgUuid.id != orgId.toInt()) throw NotAuthorizedException("Not Auth")
    printDebug("Token pass ")

    return orgUuid
}
