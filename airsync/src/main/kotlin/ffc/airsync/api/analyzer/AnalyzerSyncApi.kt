package ffc.airsync.api.analyzer

import ffc.entity.healthcare.analyze.HealthAnalyzer

interface AnalyzerSyncApi {
    fun insert(healtyAnalyzer: Map<String, HealthAnalyzer>): Map<String, HealthAnalyzer>
}

val analyzerSyncApi: AnalyzerSyncApi by lazy { RetofitAnalyzerSyncApi() }
