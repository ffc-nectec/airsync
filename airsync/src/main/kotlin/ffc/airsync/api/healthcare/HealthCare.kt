package ffc.airsync.api.healthcare

import ffc.airsync.Main
import ffc.airsync.db.DatabaseDao
import ffc.airsync.healthCareApi
import ffc.airsync.homeHealthTypeApi
import ffc.airsync.icd10Api
import ffc.airsync.persons
import ffc.airsync.specialPpApi
import ffc.airsync.users
import ffc.airsync.utils.checkNewDataCreate
import ffc.airsync.utils.getLogger
import ffc.airsync.utils.load
import ffc.airsync.utils.save
import ffc.entity.gson.toJson
import ffc.entity.healthcare.HealthCareService

fun ArrayList<HealthCareService>.initSync(progressCallback: (Int) -> Unit) {
    val cacheFile = arrayListOf<HealthCareService>().apply {
        addAll(load())
    }

    val jhcisVisit = getHealthCare(progressCallback)
    if (cacheFile.isEmpty()) {
        hashMapOf<String, Long>("maxvisit" to Main.instant.dao.queryMaxVisit()).save("maxvisit.json")
        val temp = listOf<HealthCareService>().load("healthTemp.json")
        if (temp.isEmpty()) {
            cacheFile.addAll(jhcisVisit)
            cacheFile.save("healthTemp.json")
        } else
            cacheFile.addAll(temp)
        addAll(healthCareApi.clearAndCreateHealthCare(cacheFile, progressCallback))
        save()
    } else {
        addAll(cacheFile)
        checkNewDataCreate(jhcisVisit, cacheFile, { jhcis, cloud ->
            val pcuCheck = runCatching { jhcis.link!!.keys["pcucode"] == cloud.link!!.keys["pcucode"] }
            val visitNoCheck = runCatching { jhcis.link!!.keys["visitno"] == cloud.link!!.keys["visitno"] }

            if (pcuCheck.isSuccess && visitNoCheck.isSuccess) {
                pcuCheck.getOrThrow() && visitNoCheck.getOrThrow()
            } else false
        }) {
            getLogger(this).info { "Create new visit ${it.toJson()}" }
            this.addAll(healthCareApi.clearAndCreateHealthCare(it, progressCallback, false))
            this.save()
        }
    }
    progressCallback(100)
}

private fun getHealthCare(progressCallback: (Int) -> Unit): List<HealthCareService> {
    /*return Main.instant.dao.getHealthCareService(
        lookupPatientId = { pid -> persons.find { it.link!!.keys["pid"] == pid }?.id ?: "" },
        lookupProviderId = { name -> (users.find { it.name == name } ?: users.last()).id }
    )*/

    return Main.instant.dao.getHealthCareService(
        lookupPatientId = { pid -> persons.find { it.link!!.keys["pid"] == pid }?.id ?: "" },
        lookupProviderId = { name ->
            val find = users.find { it.name == name }?.id
            if (find == null)
                getLogger(DatabaseDao::class).warn("ค้นหาเจ้าหน้าที่ $name ไม่พบ}")
            find ?: ""
        },
        lookupDisease = { icd10 -> icd10Api.lookup(icd10) },
        lookupServiceType = { serviceId -> homeHealthTypeApi.lookup(serviceId) },
        lookupSpecialPP = { ppCode -> specialPpApi.lookup(ppCode.trim()) },
        progressCallback = progressCallback
    )
}
