package ffc.airsync.visit

import ffc.airsync.MySqlJdbi
import ffc.airsync.disease.QueryDisease
import ffc.airsync.extension
import ffc.airsync.healthtype.QueryHomeHealthType
import ffc.airsync.ncds.NCDscreenQuery
import ffc.airsync.specialpp.LookupSpecialPP
import ffc.airsync.specialpp.SpecialppQuery
import ffc.entity.Person
import ffc.entity.healthcare.CommunityService
import ffc.entity.healthcare.Diagnosis
import ffc.entity.healthcare.Disease
import ffc.entity.healthcare.HealthCareService
import ffc.entity.healthcare.HomeVisit
import ffc.entity.healthcare.NCDScreen
import ffc.entity.healthcare.SpecialPP
import ffc.entity.update
import org.joda.time.LocalDate
import java.util.LinkedList
import java.util.Queue
import javax.sql.DataSource
import kotlin.system.measureTimeMillis

class VisitJdbi(
    dbHost: String = "127.0.0.1",
    dbPort: String = "3333",
    dbName: String = "jhcisdb",
    dbUsername: String = "root",
    dbPassword: String = "123456",
    ds: DataSource? = null
) : MySqlJdbi(dbHost, dbPort, dbName, dbUsername, dbPassword, ds), VisitDao {
    override fun createHomeVisit(
        homeVisit: HomeVisit,
        healthCareService: HealthCareService,
        pcucode: String,
        pcucodePerson: String,
        patient: Person,
        username: String
    ): HealthCareService {

        val visitNum = getMaxVisit() + 1
        val rightcode = (patient.link?.keys?.get("rightcode")) as String?
        val rightno = (patient.link?.keys?.get("rightno")) as String?
        val hosmain = (patient.link?.keys?.get("hosmain")) as String?
        val hossub = (patient.link?.keys?.get("hossub")) as String?
        val visitData = healthCareService.buildInsertData(
            pcucode,
            visitNum,
            pcucodePerson,
            ((patient.link?.keys?.get("pid")) as String).toLong(),
            username,
            rightcode,
            rightno,
            hosmain,
            hossub
        )

        insertVisit(visitData)

        val insertDiagData = healthCareService.buildInsertDiag(pcucode, visitNum, username)
        jdbiDao.extension<VisitDiagQuery, Unit> {
            insertVisitDiag(insertDiagData)
        }

        val visitIndividualData = homeVisit.buildInsertIndividualData(healthCareService, pcucode, visitNum, username)
        jdbiDao.extension<HomeVisitIndividualQuery, Unit> { insertVitsitIndividual(visitIndividualData) }

        healthCareService.link!!.keys["pcucode"] = pcucode
        healthCareService.link!!.keys["visitno"] = visitNum.toString()

        ((patient.link?.keys?.get("pid")) as String?)?.let {
            healthCareService.link!!.keys["pid"] = it
        }
        rightcode?.let { healthCareService.link!!.keys["rightcode"] = it }
        rightno?.let { healthCareService.link!!.keys["rightno"] = it }
        hosmain?.let { healthCareService.link!!.keys["hosmain"] = it }
        hossub?.let { healthCareService.link!!.keys["hossub"] = it }
        healthCareService.link!!.isSynced = true

        return healthCareService
    }

    override fun updateHomeVisit(
        homeVisit: HomeVisit,
        healthCareService: HealthCareService,
        pcucode: String,
        pcucodePerson: String,
        patient: Person,
        username: String
    ): HealthCareService {

        val visitNum = getMaxVisit() + 1
        val rightcode = (patient.link?.keys?.get("rightcode")) as String?
        val rightno = (patient.link?.keys?.get("rightno")) as String?
        val hosmain = (patient.link?.keys?.get("hosmain")) as String?
        val hossub = (patient.link?.keys?.get("hossub")) as String?
        val visitData = healthCareService.buildInsertData(
            pcucode,
            visitNum,
            pcucodePerson,
            ((patient.link?.keys?.get("pid")) as String).toLong(),
            username,
            rightcode,
            rightno,
            hosmain,
            hossub
        )

        jdbiDao.extension<VisitQuery, Unit> { updateVisit(visitData) }

        healthCareService.buildInsertDiag(pcucode, visitNum, username).forEach {
            jdbiDao.extension<VisitDiagQuery, Unit> {
                updateVisitDiag(it)
            }
        }

        val visitIndividualData = homeVisit.buildInsertIndividualData(healthCareService, pcucode, visitNum, username)
        jdbiDao.extension<HomeVisitIndividualQuery, Unit> { updateVitsitIndividual(visitIndividualData) }

        healthCareService.link!!.keys["pcucode"] = pcucode
        healthCareService.link!!.keys["visitno"] = visitNum.toString()

        ((patient.link?.keys?.get("pid")) as String?)?.let {
            healthCareService.link!!.keys["pid"] = it
        }
        rightcode?.let { healthCareService.link!!.keys["rightcode"] = it }
        rightno?.let { healthCareService.link!!.keys["rightno"] = it }
        hosmain?.let { healthCareService.link!!.keys["hosmain"] = it }
        hossub?.let { healthCareService.link!!.keys["hossub"] = it }
        healthCareService.link!!.isSynced = true

        return healthCareService
    }

    override fun getMaxVisit(): Long {
        val listMaxVisit = jdbiDao.extension<VisitQuery, List<Long>> { getMaxVisitNumber() }
        return listMaxVisit.last()
    }

    fun insertVisit(insertData: InsertData) {
        val listVisitData = arrayListOf<InsertData>().apply {
            add(insertData)
        }
        jdbiDao.extension<VisitQuery, Unit> { insertVisit(listVisitData) }
    }

    override fun getHealthCareService(
        lookupPatientId: (pid: String) -> String,
        lookupProviderId: (name: String) -> String
    ): List<HealthCareService> {
        return getHealthCareService(
            lookupPatientId, lookupProviderId,
            lookupDisease = { icd10 ->
                jdbiDao.extension<QueryDisease, List<Disease>> {
                    get(icd10)
                }.firstOrNull()
            },
            lookupServiceType = { serviceId ->
                jdbiDao.extension<QueryHomeHealthType, List<CommunityService.ServiceType>> {
                    get(serviceId)
                }.firstOrNull()
            },
            lookupSpecialPP = { ppCode ->
                jdbiDao.extension<LookupSpecialPP, List<SpecialPP.PPType>> { get(ppCode) }.firstOrNull()
            }
        )
    }

    override fun getHealthCareService(
        lookupPatientId: (pid: String) -> String,
        lookupProviderId: (name: String) -> String,
        lookupDisease: (icd10: String) -> Disease?,
        lookupSpecialPP: (ppCode: String) -> SpecialPP.PPType?,
        lookupServiceType: (serviceId: String) -> CommunityService.ServiceType?
    ): List<HealthCareService> {
        return getHealthCareService(
            lookupPatientId,
            lookupProviderId,
            lookupDisease,
            lookupSpecialPP,
            lookupServiceType,
            ""
        )
    }

    override fun getHealthCareService(
        lookupPatientId: (pid: String) -> String,
        lookupProviderId: (name: String) -> String,
        lookupDisease: (icd10: String) -> Disease?,
        lookupSpecialPP: (ppCode: String) -> SpecialPP.PPType?,
        lookupServiceType: (serviceId: String) -> CommunityService.ServiceType?,
        whereString: String
    ): List<HealthCareService> {
        var i = 0
        val result = getVisit(whereString)
        val size = result.size
        val avgTimeRun: Queue<Long> = LinkedList()
        var sumTime = 0L

        val specialPpList = getSpecialPP()
        val ncdScreenList = getNcdScreen()
        val homeVisitList = getHomeVisit()

        return result.map { healthCare ->
            var outputVisit = HealthCareService("", "")

            val allRunTime = measureTimeMillis {
                i++

                var providerId = ""
                var patientId = ""

                val runtimeLookupUser = measureTimeMillis {
                    providerId = lookupProviderId(healthCare.providerId)
                    patientId = lookupPatientId(healthCare.patientId)
                }

                outputVisit = copyVisit(providerId, patientId, healthCare)
                outputVisit.link?.keys?.get("visitno")?.toString()?.toLong()?.let { visitNumber ->

                    var diagnosisIcd10: List<Diagnosis> = emptyList()
                    var specislPP: List<String> = emptyList()
                    var ncdScreen: List<NCDScreen> = emptyList()
                    var homeVisit: List<HomeVisit> = emptyList()

                    val runtimeQueryDb = measureTimeMillis {
                        diagnosisIcd10 = getVisitDiag(visitNumber)
                        specislPP = specialPpList[visitNumber] ?: emptyList()
                        ncdScreen = ncdScreenList[visitNumber] ?: emptyList()
                        homeVisit = homeVisitList[visitNumber] ?: emptyList()
                    }

                    val runtimeLookupApi = measureTimeMillis {
                        outputVisit.diagnosises = getDiagnosisIcd10(diagnosisIcd10, lookupDisease)

                        specislPP.forEach {
                            outputVisit.addSpecialPP(
                                lookupSpecialPP(it.trim()) ?: SpecialPP.PPType(it, it)
                            )
                        }

                        outputVisit.ncdScreen = ncdScreen.firstOrNull()?.let {
                            createNcdScreen(providerId, patientId, it)
                        }

                        homeVisit.firstOrNull()?.let { visit ->
                            visit.bundle["dateappoint"]?.let { outputVisit.nextAppoint = it as LocalDate }
                            outputVisit.communityServices.add(
                                HomeVisit(
                                    serviceType = lookupServiceType(visit.serviceType.id.trim()) ?: visit.serviceType,
                                    detail = visit.detail,
                                    plan = visit.plan,
                                    result = visit.result
                                )
                            )
                        }
                    }
                    if (i % 300 == 0 || i == size) {
                        print("Visit $i:$size")
                        print("\tLookupUser:$runtimeLookupUser")
                        print("\tRuntime DB:$runtimeQueryDb")
                        print("\tLookupApi:$runtimeLookupApi")
                    }
                }
            }

            if (avgTimeRun.size > 10000)
                sumTime -= avgTimeRun.poll()
            avgTimeRun.offer(allRunTime)
            sumTime += allRunTime

            val avgTime = sumTime / avgTimeRun.size

            if (i % 300 == 0 || i == size) {
                ((size - i) * avgTime).printTime()
                println()
            }
            outputVisit
        }
    }

    private fun createNcdScreen(
        providerId: String,
        patientId: String,
        ncdScreen: NCDScreen
    ): NCDScreen {
        return NCDScreen(
            providerId = providerId,
            patientId = patientId,
            hasDmInFamily = ncdScreen.hasDmInFamily,
            hasHtInFamily = ncdScreen.hasHtInFamily,
            smoke = ncdScreen.smoke,
            alcohol = ncdScreen.alcohol,
            bloodSugar = ncdScreen.bloodSugar,
            weight = ncdScreen.weight,
            height = ncdScreen.height,
            waist = ncdScreen.waist,
            bloodPressure = ncdScreen.bloodPressure,
            bloodPressure2nd = ncdScreen.bloodPressure2nd
        ).update(ncdScreen.timestamp) {
            time = ncdScreen.time
            endTime = ncdScreen.endTime
            location = ncdScreen.location
            link = ncdScreen.link
        }
    }

    private fun copyVisit(
        providerId: String,
        patientId: String,
        healthCare: HealthCareService
    ): HealthCareService {
        return HealthCareService(providerId, patientId).update(healthCare.timestamp) {
            nextAppoint = healthCare.nextAppoint
            syntom = healthCare.syntom
            suggestion = healthCare.suggestion
            weight = healthCare.weight
            height = healthCare.height
            waist = healthCare.waist
            ass = healthCare.ass
            bloodPressure = healthCare.bloodPressure
            bloodPressure2nd = healthCare.bloodPressure2nd
            pulseRate = healthCare.pulseRate
            respiratoryRate = healthCare.respiratoryRate
            bodyTemperature = healthCare.bodyTemperature
            diagnosises = healthCare.diagnosises
            note = healthCare.note
            photosUrl = healthCare.photosUrl
            communityServices = healthCare.communityServices
            ncdScreen = healthCare.ncdScreen
            specialPPs = healthCare.specialPPs
            principleDx = healthCare.principleDx
            link = healthCare.link
            time = healthCare.time
            endTime = healthCare.endTime
        }
    }

    private fun getDiagnosisIcd10(
        diagnosisIcd10: List<Diagnosis>,
        lookupDisease: (icd10: String) -> Disease?
    ): MutableList<Diagnosis> {
        return diagnosisIcd10.map {
            Diagnosis(
                disease = lookupDisease(it.disease.id.trim()) ?: it.disease,
                dxType = it.dxType,
                isContinued = it.isContinued
            )
        }.toMutableList()
    }

    private fun getVisitDiag(visitNumber: Long) =
        jdbiDao.extension<VisitDiagQuery, List<Diagnosis>> { getDiag(visitNumber) }

    private fun getHomeVisit(): HashMap<Long, List<HomeVisit>> {
        val homeVisitList = hashMapOf<Long, List<HomeVisit>>()
        jdbiDao.extension<HomeVisitIndividualQuery, List<Map<Long, HomeVisit>>> { getAll() }.forEach {
            mapList(it, homeVisitList)
        }
        return homeVisitList
    }

    private fun getNcdScreen(): HashMap<Long, List<NCDScreen>> {
        val ncdScreenList = hashMapOf<Long, List<NCDScreen>>()
        jdbiDao.extension<NCDscreenQuery, List<Map<Long, NCDScreen>>> { getAll() }.forEach {
            mapList(it, ncdScreenList)
        }
        return ncdScreenList
    }

    private fun getSpecialPP(): HashMap<Long, List<String>> {
        val specialPpList = hashMapOf<Long, List<String>>()
        jdbiDao.extension<SpecialppQuery, List<Map<Long, String>>> { getAll() }.forEach {
            mapList(it, specialPpList)
        }
        return specialPpList
    }

    private fun getVisit(whereString: String): List<HealthCareService> {
        return if (whereString.isBlank())
            jdbiDao.extension<VisitQuery, List<HealthCareService>> { get() }
        else
            jdbiDao.extension<VisitQuery, List<HealthCareService>> { get(whereString) }
    }
}

private fun Long.printTime() {
    if (this > 0) {
        val sec = (this / 1000) % 60
        val min = (this / 60000) % 60
        val hour = (this / 36e5).toInt()
        print("\t$hour:$min:$sec")
    }
}

private inline fun <reified T> mapList(
    it: Map<Long, T>,
    resut: HashMap<Long, List<T>>
) {
    val toList = it.toList()
    val key = toList[0].first
    val value = toList[0].second
    if (resut[key] == null || resut[key]?.isEmpty() == true)
        resut[key] = listOf(value)
    else {
        resut[key] =
                listOf(*resut[key]!!.toTypedArray(), value)
    }
}
