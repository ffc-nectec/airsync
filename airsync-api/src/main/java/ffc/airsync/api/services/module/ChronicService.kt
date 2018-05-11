package ffc.airsync.api.services.module

import ffc.model.Chronic

object ChronicService {
    fun create(token: String, orgId: String, chronicList: List<Chronic>) {
        val org = getOrgByOrgToken(token, orgId)
        chronicDao.insert(org.uuid, chronicList)
    }
}
