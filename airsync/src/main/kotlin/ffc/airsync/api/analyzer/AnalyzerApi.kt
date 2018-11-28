package ffc.airsync.api.analyzer

import ffc.entity.healthcare.analyze.HealthAnalyzer

interface AnalyzerApi {
    fun insert(personId: String, healthAnalyzer: HealthAnalyzer): HealthAnalyzer
    fun delete(personId: String)
}
