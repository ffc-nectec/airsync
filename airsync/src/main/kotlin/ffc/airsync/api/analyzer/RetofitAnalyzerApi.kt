package ffc.airsync.api.analyzer

import ffc.airsync.retrofit.RetofitApi
import ffc.airsync.utils.callApi
import ffc.entity.healthcare.analyze.HealthAnalyzer

class RetofitAnalyzerApi : RetofitApi(), AnalyzerApi {

    override fun insert(personId: String, healthAnalyzer: HealthAnalyzer): HealthAnalyzer {

        return callApi(
            cleanAll = { restService.cleanHealthAnalyzeOrgId(organization.id, tokenBarer) }
        ) {
            restService.removeHealthAnalyze(
                orgId = organization.id,
                authkey = tokenBarer,
                personId = personId
            )
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
}
