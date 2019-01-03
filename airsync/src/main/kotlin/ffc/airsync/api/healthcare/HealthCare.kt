package ffc.airsync.api.healthcare

import ffc.airsync.Main
import ffc.airsync.api.homehealthtype.homeHealthTypeApi
import ffc.airsync.api.icd10.icd10Api
import ffc.airsync.api.icd10.specialPpApi
import ffc.airsync.api.person.persons
import ffc.airsync.api.user.users
import ffc.airsync.utils.load
import ffc.airsync.utils.save
import ffc.entity.healthcare.HealthCareService

fun ArrayList<HealthCareService>.initSync() {
    val localHealthCare = arrayListOf<HealthCareService>().apply {
        addAll(load())
    }

    if (localHealthCare.isEmpty()) {
        hashMapOf<String, Long>("maxvisit" to Main.instant.dao.queryMaxVisit()).save("maxvisit.json")
        val temp = listOf<HealthCareService>().load("healthTemp.json")
        if (temp.isEmpty()) {
            localHealthCare.addAll(getHealthCare())
            localHealthCare.save("healthTemp.json")
        }
        localHealthCare.addAll(temp)
        addAll(healthCareApi.clearAndCreateHealthCare(localHealthCare))
        save()
    } else {
        addAll(localHealthCare)
    }
}

private fun getHealthCare(): List<HealthCareService> {
    /*return Main.instant.dao.getHealthCareService(
        lookupPatientId = { pid -> persons.find { it.link!!.keys["pid"] == pid }?.id ?: "" },
        lookupProviderId = { name -> (users.find { it.name == name } ?: users.last()).id }
    )*/

    return Main.instant.dao.getHealthCareService(
        lookupPatientId = { pid -> persons.find { it.link!!.keys["pid"] == pid }?.id ?: "" },
        lookupProviderId = { name -> (users.find { it.name == name } ?: users.last()).id },
        lookupDisease = { icd10 -> icd10Api.lookup(icd10) },
        lookupServiceType = { serviceId -> homeHealthTypeApi.lookup(serviceId) },
        lookupSpecialPP = { ppCode -> specialPpApi.lookup(ppCode.trim()) }
    )
}
