package ffc.airsync.api.analyzer

import ffc.airsync.retrofit.RetofitApi
import ffc.airsync.utils.callApi
import ffc.airsync.utils.callApiNoReturn
import ffc.entity.healthcare.analyze.HealthAnalyzer

class RetofitAnalyzerApi : RetofitApi<AnalyzerUrl>(AnalyzerUrl::class.java), AnalyzerApi {

    override fun insert(personId: String, healthAnalyzer: HealthAnalyzer): HealthAnalyzer {
        return callApi {
            restService.removeHealthAnalyze(
                orgId = organization.id,
                authkey = tokenBarer,
                personId = personId
            ).execute()
            val respond = restService.createHealthAnalyze(
                orgId = organization.id,
                authkey = tokenBarer,
                personId = personId,
                healtyAnalyzer = healthAnalyzer
            ).execute()
            if (respond.code() == 201 || respond.code() == 200) {
                respond.body()
            } else {
                null
            }
        }
    }

    override fun delete(personId: String) {
        callApi {
            restService.removeHealthAnalyze(
                orgId = organization.id,
                authkey = tokenBarer,
                personId = personId
            )
        }
    }

    override fun deleteAll() {
        callApiNoReturn {
            restService.cleanHealthAnalyzeOrgId(organization.id, tokenBarer).execute()
        }
    }
}
