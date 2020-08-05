package ffc.airsync

import ffc.airsync.api.person.findPersonId
import org.apache.logging.log4j.kotlin.logger

val lookupPersonId = { pid: String ->
    try {
        findPersonId(pid)
    } catch (ex: KotlinNullPointerException) {
        logger("Lookup").warn(ex) { "Lookup person Error ${ex.message}" }
        ""
    }
}
val lookupUserId = { name: String ->
    try {
        findProviderId(name)
    } catch (ex: KotlinNullPointerException) {
        logger("Lookup").warn(ex) { "Lookup user Error ${ex.message}" }
        ""
    }
}
val lookupDisease = { icd10: String -> icd10Api.lookup(icd10) }
val lookupServiceType = { serviceId: String -> homeHealthTypeApi.lookup(serviceId) }
val lookupSpecialPP = { ppCode: String -> specialPpApi.lookup(ppCode.trim()) }

private fun findProviderId(name: String): String {
    val id = (userManage.cloudUser.find { it.name == name })?.id
    return if (id == null) {
        userManage.sync()
        (userManage.cloudUser.find { it.name == name })?.id ?: ""
    } else
        id
}
