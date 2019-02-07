package ffc.airsync.api.analyzer

import ffc.airsync.printDebug
import ffc.airsync.utils.load
import ffc.airsync.utils.save
import ffc.entity.healthcare.HealthCareService
import ffc.entity.healthcare.analyze.HealthAnalyzer

fun HashMap<String, HealthAnalyzer>.initSync2(healthCareService: List<HealthCareService>) {

    val localAnalyzer = hashMapOf<String, HealthAnalyzer>()
    localAnalyzer.putAll(localAnalyzer.load("analyzer.json"))

    if (localAnalyzer.isEmpty()) {
        val processSet = HashSet<String>()
        healthCareService.forEach {
            processSet.add(it.patientId)
        }

        printDebug("")
        processSet.forEachIndexed { index, patientId ->
            val analyzer = HealthAnalyzer()
            val visit = healthCareService.filter { it.patientId == patientId }
            analyzer.analyze(*visit.toTypedArray())
            localAnalyzer[patientId] = analyzer
        }

        val processCloud = hashMapOf<String, HealthAnalyzer>()
        processCloud.putAll(analyzerSyncApi.insert(localAnalyzer))

        localAnalyzer.clear()
        localAnalyzer.putAll(processCloud)

        localAnalyzer.save("analyzer.json")
    } else {
        putAll(localAnalyzer)
    }
}
