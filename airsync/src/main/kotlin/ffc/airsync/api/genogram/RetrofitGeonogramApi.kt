package ffc.airsync.api.genogram

import ffc.airsync.retrofit.RetofitApi
import ffc.airsync.utils.printDebug
import ffc.entity.Person

class RetrofitGeonogramApi : RetofitApi<GenogramUrl>(GenogramUrl::class.java), GeonogramApi {
    override fun put(personId: String, relationship: List<Person.Relationship>): List<Person.Relationship> {
        val relationLastUpdate = arrayListOf<Person.Relationship>()
        var syncccc = false
        var loop = 1
        while (!syncccc) {
            try {
                print("Sync rela loop ${loop++} ")
                val response = restService.updateRelationship(
                    orgId = organization.id,
                    authkey = tokenBarer,
                    personId = personId,
                    relationship = relationship
                ).execute()
                printDebug(" response ${response.code()} err:${response.errorBody()?.source()}")
                if (response.code() == 201 || response.code() == 200) {
                    relationLastUpdate.addAll(response.body() ?: arrayListOf())
                    syncccc = true
                } else {
                    syncccc = false
                }
            } catch (ex: Exception) {
                syncccc = false
                Thread.sleep(5000)
            }
        }
        return relationLastUpdate
    }
}
