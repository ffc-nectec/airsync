package ffc.airsync.api.analyzer

import ffc.entity.healthcare.analyze.HealthAnalyzer

interface AnalyzerSyncApi {
    fun insert(
        healtyAnalyzer: Map<String, HealthAnalyzer>,
        progressCallback: (Int) -> Unit
    ): Map<String, HealthAnalyzer>
}
