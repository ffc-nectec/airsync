package ffc.airsync

import ffc.airsync.api.person.findPersonId
import ffc.airsync.api.user.findProviderId

val lookupPersonId = { pid: String ->
    try {
        findPersonId(pid)
    } catch (ex: KotlinNullPointerException) {
        ""
    }
}
val lookupUserId = { name: String ->
    try {
        findProviderId(name)
    } catch (ex: KotlinNullPointerException) {
        ""
    }
}
val lookupDisease = { icd10: String -> icd10Api.lookup(icd10) }
val lookupServiceType = { serviceId: String -> homeHealthTypeApi.lookup(serviceId) }
val lookupSpecialPP = { ppCode: String -> specialPpApi.lookup(ppCode.trim()) }
