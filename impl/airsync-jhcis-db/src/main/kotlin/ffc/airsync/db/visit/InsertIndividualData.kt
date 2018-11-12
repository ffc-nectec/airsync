package ffc.airsync.db.visit

import ffc.entity.healthcare.HomeVisit
import org.joda.time.DateTime
import java.sql.Timestamp

class InsertIndividualData(
    homeVisit: HomeVisit,
    val pcucode: String,
    val visitno: Long,
    val username: String
) {
    val patientsign = homeVisit.syntom
    val homehealthdetail = homeVisit.detail
    val homehealthresult = homeVisit.result
    val homehealthplan = homeVisit.plan
    val dateappoint =
            if (homeVisit.nextAppoint != null)
                Timestamp(homeVisit.nextAppoint!!.toDate().time)
            else
                null

    val user = username
    val dateupdate = Timestamp(DateTime.now().plusHours(7).millis)

    val homehealthtype = homeVisit.serviceType.id
}

fun HomeVisit.buildInsertIndividualData(
    pcucode: String,
    visitno: Long,
    username: String
): InsertIndividualData {
    return InsertIndividualData(this, pcucode, visitno, username)
}
