package ffc.airsync.api.village

import ffc.airsync.retrofit.RetofitApi
import ffc.airsync.utils.ApiLoopException
import ffc.airsync.utils.callApi
import ffc.entity.Village

class RetofitVillageApi : RetofitApi<VillageUrl>(VillageUrl::class.java), VillageApi {
    override fun toCloud(villages: List<Village>): List<Village> {
        return callApi {
            callApi { restService.deleteOrg(organization.id, tokenBarer) }
            val response = restService.create(
                orgId = organization.id,
                authkey = tokenBarer,
                villages = villages
            ).execute()

            if (response.code() == 201 || response.code() == 200) {
                response.body() ?: arrayListOf()
            } else
                throw ApiLoopException("create village error code ${response.code()}")
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
