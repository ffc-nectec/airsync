package ffc.airsync.db.visit

import ffc.entity.healthcare.Diagnosis
import ffc.entity.healthcare.HomeVisit
import org.joda.time.DateTime
import java.sql.Timestamp

class InsertDiagData(
    val homeVisit: HomeVisit,
    val pcucode: String,
    val visitno: Long,
    val username: String
) {
    lateinit var diagcode: String
    lateinit var conti: String
    lateinit var dxtype: String
    val dateupdate: Timestamp = Timestamp(DateTime.now().plusHours(7).millis)
    val doctordiag = username
    val appointdate =
            if (homeVisit.nextAppoint != null)
                Timestamp(homeVisit.nextAppoint!!.toDate().time)
            else
                null
}

fun HomeVisit.buildInsertDiag(
    pcucode: String,
    visitno: Long,
    username: String
): Iterable<InsertDiagData> {
    return this.diagnosises.map {
        InsertDiagData(this, pcucode, visitno, username).apply {
            diagcode = it.disease.icd10!!.trim()
            conti = if (it.isContinued) "1" else "0"
            dxtype = when (it.dxType) {
                Diagnosis.Type.PRINCIPLE_DX -> "01"
                Diagnosis.Type.CO_MORBIDITY -> "02"
                Diagnosis.Type.COMPLICATION -> "03"
                Diagnosis.Type.OTHER -> "04"
                else -> "05"
            }.trim()
        }
    }
}
