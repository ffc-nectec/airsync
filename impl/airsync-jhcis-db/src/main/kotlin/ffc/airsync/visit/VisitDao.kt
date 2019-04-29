package ffc.airsync.visit

import ffc.entity.Person
import ffc.entity.healthcare.CommunityService
import ffc.entity.healthcare.Disease
import ffc.entity.healthcare.HealthCareService
import ffc.entity.healthcare.HomeVisit
import ffc.entity.healthcare.SpecialPP

interface VisitDao {
    fun createHomeVisit(
        homeVisit: HomeVisit,
        healthCareService: HealthCareService,
        pcucode: String,
        pcucodePerson: String,
        patient: Person,
        username: String
    ): HealthCareService

    fun updateHomeVisit(
        homeVisit: HomeVisit,
        healthCareService: HealthCareService,
        pcucode: String,
        pcucodePerson: String,
        patient: Person,
        username: String
    ): HealthCareService

    fun getHealthCareService(
        lookupPatientId: (pid: String) -> String,
        lookupProviderId: (name: String) -> String,
        lookupDisease: (icd10: String) -> Disease?,
        lookupSpecialPP: (ppCode: String) -> SpecialPP.PPType?,
        lookupServiceType: (serviceId: String) -> CommunityService.ServiceType?,
        whereString: String = "",
        progressCallback: (Int) -> Unit = {}
    ): List<HealthCareService>

    fun getMaxVisit(): Long
}
