package ffc.airsync.api.village

import ffc.airsync.retrofit.RetofitApi
import ffc.airsync.utils.ApiLoopException
import ffc.airsync.utils.callApi
import ffc.entity.Village

class VillageServiceApi : RetofitApi<VillageService>(VillageService::class.java), VillageApi {
    override fun toCloud(villages: List<Village>): List<Village> {
        return callApi {
            val output = arrayListOf<Village>()
            callApi { restService.deleteOrg(organization.id, tokenBarer).execute() }
            /*val response = restService.create(
                orgId = organization.id,
                authkey = tokenBarer,
                villages = villages
            ).execute()*/

            villages.forEach {
                it.places.clear()

                val response = restService.createSingel(
                    orgId = organization.id,
                    authkey = tokenBarer,
                    village = it
                ).execute()

                if (response.code() == 201 || response.code() == 200) {
                    output.add(response.body()!!)
                } else
                    throw ApiLoopException("create village error code ${response.code()}")
            }

            output
        }
    }

    override fun get(): List<Village> {
        return callApi {
            val cloud = restService.getHouse(organization.id, tokenBarer).execute()
            cloud.body() ?: arrayListOf()
        }
    }

    override fun editCloud(village: Village): Village {
        return callApi {
            val response = restService.edit(
                organization.id, tokenBarer,
                villageId = village.id,
                villages = village
            ).execute()
            if (response.code() == 201 || response.code() == 200) {
                response.body()
            } else
                throw ApiLoopException("Edit village error code ${response.code()}")
        }
    }
}
