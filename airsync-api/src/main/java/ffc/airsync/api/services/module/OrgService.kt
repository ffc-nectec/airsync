package ffc.airsync.api.services.module

import ffc.model.Organization
import ffc.model.printDebug
import java.util.*
import javax.ws.rs.NotAuthorizedException
import javax.ws.rs.NotFoundException

object OrgService {

    fun register(organization: Organization, lastKnownIp: String): Organization {

        organization.token = UUID.randomUUID()
        organization.lastKnownIp = lastKnownIp
        organization.socketUrl = "ws://127.0.0.1:8080/airsync"
        //organization.socketUrl="ws://188.166.249.72/airsync"

        orgDao.insert(organization)
        return organization
    }


    fun remove(token: UUID, orgId: String) {
        val org = getOrgByOrgToken(token, orgId)

        orgDao.findById(orgId)

        printDebug("Remove org id = $orgId == ${org.id}")
        if (org.id != orgId) throw NotAuthorizedException("ไม่เจอ Org")
        val uuidForRemove = UUID.fromString(org.uuid.toString())
        orgDao.removeByOrgUuid(uuidForRemove)
        orgUser.removeByOrgUuid(uuidForRemove)
        houseDao.removeByOrgUuid(uuidForRemove)
        tokenMobile.removeByOrgUuid(uuidForRemove)
        personDao.removeByOrgUuid(uuidForRemove)
    }


    fun getMy(ipAddress: String): List<Organization> {

        printDebug("Get my org $ipAddress")
        val pcuReturn = orgDao.findByIpAddress(ipAddress)
        pcuReturn.forEach {
            printDebug("\tOrg list Name ${it.name} ${it.lastKnownIp}")
        }

        if (pcuReturn.isEmpty())
            throw NotFoundException("ไม่มีข้อมูลลงทะเบียน")



        return hideOrgPrivate(pcuReturn)
    }

    fun get(): List<Organization> {
        printDebug("Get all org")
        val pcuReturn = orgDao.find()
        pcuReturn.forEach {
            printDebug("\tOrg list Name ${it.name} ${it.lastKnownIp}")
        }
        if (pcuReturn.isEmpty()) throw NotFoundException("ไม่มีข้อมูลลงทะเบียน")

        return hideOrgPrivate(pcuReturn)
    }


    private fun hideOrgPrivate(org: List<Organization>): List<Organization> {

        val orgCloneList = arrayListOf<Organization>()
        org.forEach {
            val orgClone = it.clone()
            orgClone.token = null
            orgClone.lastKnownIp = null
            orgClone.firebaseToken = null
            orgCloneList.add(orgClone)
        }
        return orgCloneList
    }
}
