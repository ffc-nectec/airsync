package ffc.airsync.db.visit

import ffc.entity.Person
import ffc.entity.User
import ffc.entity.healthcare.Diagnosis
import ffc.entity.healthcare.Disease
import ffc.entity.healthcare.HomeVisit

internal class VisitUtil {
    fun mapId(
        user: List<User>,
        current: HomeVisit,
        person: List<Person>
    ): HomeVisit {
        val providerId = (user.find { it.name == current.providerId } ?: user.last()).id
        val patientId = (person.find { it.link!!.keys["pid"] == current.patientId })!!.id

        val homeVisit = HomeVisit(
            providerId = providerId,
            id = current.id,
            patientId = patientId,
            serviceType = current.serviceType
        ).apply {
            syntom = current.syntom
            detail = current.detail
            result = current.result
            plan = current.plan
            nextAppoint = current.nextAppoint
            diagnosises = current.diagnosises
            time = current.time
            weight = current.weight
            height = current.height
            waist = current.waist
            ass = current.ass
            bloodPressure = current.bloodPressure
            bodyTemperature = current.bodyTemperature
            pulseRate = current.pulseRate
            respiratoryRate = current.respiratoryRate
            link = current.link
        }
        return homeVisit
    }

    fun `ใสข้อมูล Disease`(
        current: HomeVisit,
        lookupDisease: (icd10: String) -> Disease
    ) {
        val diagnosises = current.diagnosises.first()
        val disease = lookupDisease(diagnosises.disease.icd10!!)
        val diagnosisesNew = Diagnosis(disease, diagnosises.dxType, diagnosises.isContinued)
        current.diagnosises.clear()
        current.diagnosises.add(diagnosisesNew)
    }

    fun checkDuplicateVisitDiag(
        it: HomeVisit,
        currentVisit: HomeVisit
    ): Boolean {
        val checkPcuCode = it.link!!.keys["pcucode"] == currentVisit.link!!.keys["pcucode"]
        val checkVisitNo = it.link!!.keys["visitno"] == currentVisit.link!!.keys["visitno"]
        return (checkPcuCode && checkVisitNo)
    }
}
