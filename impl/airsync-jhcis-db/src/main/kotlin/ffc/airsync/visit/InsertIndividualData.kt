package ffc.airsync.visit

import ffc.entity.healthcare.HealthCareService
import ffc.entity.healthcare.HomeVisit
import org.joda.time.DateTime
import java.sql.Timestamp

class InsertIndividualData(
    homeVisit: HomeVisit,
    healthCareService: HealthCareService,
    val pcucode: String,
    val visitno: Long,
    val username: String
) {
    val patientsign = healthCareService.syntom
    val homehealthdetail = homeVisit.detail
    val homehealthresult = homeVisit.result
    val homehealthplan = homeVisit.plan
    val dateappoint =
        if (healthCareService.nextAppoint != null)
            Timestamp(healthCareService.nextAppoint!!.toDate().time)
        else
            null

    val user = username
    val dateupdate = Timestamp(DateTime.now().plusHours(7).millis)

    val homehealthtype = homeVisit.serviceType.id
}

fun HomeVisit.buildInsertIndividualData(
    healthCareService: HealthCareService,
    pcucode: String,
    visitno: Long,
    username: String
): InsertIndividualData {
    return InsertIndividualData(this, healthCareService, pcucode, visitno, username)
}
