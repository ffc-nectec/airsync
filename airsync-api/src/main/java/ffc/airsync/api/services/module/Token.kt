package ffc.airsync.api.services.module

import ffc.model.TokenMessage
import ffc.model.Organization
import ffc.model.StorageOrg
import ffc.model.printDebug
import java.util.*
import javax.ws.rs.NotAuthorizedException
import javax.ws.rs.NotFoundException

fun getOrgByOrgToken(token: UUID, orgId: String): Organization {
    printDebug("getOrgByOrgToken $token")
    val org = orgDao.findByToken(token)
    if (org.id != orgId) {
        printDebug("org ไม่ตรงกัน")
        throw throw NotFoundException("Org ไม่ตรง")
    }

    return org
}

fun getOrgByMobileToken(token: UUID, orgId: String): StorageOrg<TokenMessage> {
    printDebug("Befor check mobile token")
    val tokenMessage = tokenMobile.find(token)

    if (tokenMessage.orgId != orgId) throw NotAuthorizedException("Not Auth")
    printDebug("Token pass ")

    if (tokenMessage.data.checkExpireTokem()) throw NotAuthorizedException("Token expire ${tokenMessage.data.expireDate}")


    return tokenMessage
}
