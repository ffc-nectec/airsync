package ffc.airsync.api.analyzer

import ffc.airsync.retrofit.RetofitApi
import ffc.airsync.utils.ApiLoopException
import ffc.airsync.utils.UploadSpliterMap
import ffc.airsync.utils.callApi
import ffc.airsync.utils.callApiNoReturn
import ffc.entity.healthcare.analyze.HealthAnalyzer

class RetofitAnalyzerSyncApi : RetofitApi<AnalyzerUrl>(AnalyzerUrl::class.java), AnalyzerSyncApi {

    override fun insert(healtyAnalyzer: Map<String, HealthAnalyzer>): Map<String, HealthAnalyzer> {
        val output = hashMapOf<String, HealthAnalyzer>()
        callApiNoReturn { restService.cleanHealthAnalyzeOrgId(organization.id, tokenBarer).execute() }

        UploadSpliterMap.upload(200, healtyAnalyzer) { analyzer, block ->

            val result = callApi {
                restService.unConfirmBlock(organization.id, tokenBarer, block).execute()
                val response = restService.insertBlock(
                    organization.id, tokenBarer,
                    block = block,
                    healtyAnalyzer = analyzer
                ).execute()
                if (response.code() == 201 || response.code() == 200) {
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
