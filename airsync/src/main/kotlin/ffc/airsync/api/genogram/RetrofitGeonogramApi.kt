package ffc.airsync.api.genogram

import ffc.airsync.retrofit.RetofitApi
import ffc.airsync.utils.ApiLoopException
import ffc.airsync.utils.UploadSpliterMap
import ffc.airsync.utils.callApi
import ffc.airsync.utils.callApiNoReturn
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

    override fun putBlock(
        relationship: Map<String, List<Person.Relationship>>
    ): Map<String, List<Person.Relationship>> {
        val output = hashMapOf<String, List<Person.Relationship>>()
        callApiNoReturn { restService.cleanAll(organization.id, tokenBarer).execute() }

        UploadSpliterMap.upload(100, relationship) { list, block ->

            val result = callApi {
                restService.unConfirmBlock(organization.id, tokenBarer, block).execute()

                val response = restService.insertBlock(
                    organization.id, tokenBarer,
                    block = block,
                    relationship = list
                ).execute()

                if (response.code() == 201 || response.code() == 200) {
                    restService.confirmBlock(
                        organization.id, tokenBarer,
                        block = block
                    )
                    response.body()
                } else {
                    throw ApiLoopException("Response code wrong.")
                }
            }
            output.putAll(result)
        }
        return output
    }
}
