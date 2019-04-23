package ffc.airsync.api.analyzer

import ffc.airsync.analyzerSyncApi
import ffc.airsync.utils.load
import ffc.airsync.utils.save
import ffc.entity.healthcare.HealthCareService
import ffc.entity.healthcare.analyze.HealthAnalyzer

fun HashMap<String, HealthAnalyzer>.initSync(
    healthCareService: List<HealthCareService>,
    progressCallback: (Int) -> Unit
) {

    val localAnalyzer = hashMapOf<String, HealthAnalyzer>()
    localAnalyzer.putAll(localAnalyzer.load("analyzer.json"))

    if (localAnalyzer.isEmpty()) {
        val processSet = HashSet<String>()
        healthCareService.forEach {
            processSet.add(it.patientId)
        }
        progressCallback(10)
        val sizeOfLoop = processSet.size

        processSet.forEachIndexed { index, patientId ->
            val analyzer = HealthAnalyzer()
            val visit = healthCareService.filter { it.patientId == patientId }
            analyzer.analyze(*visit.toTypedArray())
            localAnalyzer[patientId] = analyzer
            progressCallback(((index * 40) / sizeOfLoop) + 10)
        }

        val processCloud = hashMapOf<String, HealthAnalyzer>()
        processCloud.putAll(analyzerSyncApi.insert(localAnalyzer, progressCallback))

        localAnalyzer.clear()
        localAnalyzer.putAll(processCloud)

        localAnalyzer.save("analyzer.json")
    } else {
        putAll(localAnalyzer)
    }
    progressCallback(100)
}
