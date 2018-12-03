package ffc.airsync.api.analyzer

import ffc.airsync.utils.load
import ffc.airsync.utils.save
import ffc.entity.healthcare.HealthCareService
import ffc.entity.healthcare.analyze.HealthAnalyzer
import kotlin.system.measureTimeMillis

fun HashMap<String, HealthAnalyzer>.initSync(healthCareService: List<HealthCareService>) {

    val localAnalyzer = hashMapOf<String, HealthAnalyzer>()
    localAnalyzer.putAll(localAnalyzer.load("analyzer.json"))

    if (localAnalyzer.isEmpty()) {
        val processSet = HashSet<String>()
        healthCareService.forEach {
            processSet.add(it.patientId)
        }

        val size = processSet.size
        var sum = 0L

        analyzerApi.deleteAll()
        println()
        processSet.forEachIndexed { index, patientId ->
            val runtime = measureTimeMillis {
                print("analytic $index:$size")
                val analyzer = HealthAnalyzer()
                val visit = healthCareService.filter { it.patientId == patientId }
                analyzer.analyze(*visit.toTypedArray())
                localAnalyzer[patientId] = analyzerApi.insert(patientId, analyzer)
            }
            sum += runtime
            val avgTime = sum / (index + 1)
            val timeMill = avgTime * (size - index)
            val sec = (timeMill / 1000) % 60
            val min = (timeMill / 60000) % 60
            val hour = (timeMill / 36e5).toInt()
            println("\tTime:$runtime Avg:$avgTime CountTime $hour:$min:$sec")
        }

        localAnalyzer.save("analyzer.json")
    } else {
        putAll(localAnalyzer)
    }
}

fun HashMap<String, HealthAnalyzer>.initSync2(healthCareService: List<HealthCareService>) {

    val localAnalyzer = hashMapOf<String, HealthAnalyzer>()
    localAnalyzer.putAll(localAnalyzer.load("analyzer.json"))

    if (localAnalyzer.isEmpty()) {
        val processSet = HashSet<String>()
        healthCareService.forEach {
            processSet.add(it.patientId)
        }

        println()
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
