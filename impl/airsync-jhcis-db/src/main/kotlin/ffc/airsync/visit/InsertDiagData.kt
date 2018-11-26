package ffc.airsync.visit

import ffc.entity.healthcare.Diagnosis
import ffc.entity.healthcare.HealthCareService
import ffc.entity.healthcare.Icd10
import org.joda.time.DateTime
import java.sql.Timestamp

class InsertDiagData(
    val healthCareService: HealthCareService,
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
        if (healthCareService.nextAppoint != null)
            Timestamp(healthCareService.nextAppoint!!.toDate().time)
        else
            null
}

fun HealthCareService.buildInsertDiag(
    pcucode: String,
    visitno: Long,
    username: String
): Iterable<InsertDiagData> {
    return this.diagnosises.map {
        InsertDiagData(this, pcucode, visitno, username).apply {
            if (it.disease is Icd10)
                diagcode = (it.disease as Icd10).icd10.trim()
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
