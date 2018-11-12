package ffc.airsync.db

import ffc.airsync.db.visit.InsertData
import ffc.airsync.utils.timeZone
import ffc.entity.Link
import ffc.entity.System
import ffc.entity.healthcare.BloodPressure
import ffc.entity.healthcare.CommunityServiceType
import ffc.entity.healthcare.Diagnosis
import ffc.entity.healthcare.Disease
import ffc.entity.healthcare.HomeVisit
import me.piruin.geok.geometry.Point
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should equal`
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import java.sql.Timestamp
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.TimeZone

class QueryTest {

    lateinit var fullInsertData: InsertData

    val homeVisit = HomeVisit(
            "999999",
            "000000",
            CommunityServiceType("654321", "ให้อดข้าว")
    ).apply {
        detail = "ทดสอบ ปกติทุกอย่าง ราบรื่น มากๆ"
        nextAppoint = LocalDate(1536218895967).plusMonths(5)
        plan = "กินอื่ม นอนหลับ ทานของมันให้น้อยลง ออกกำลังกาย"
        bloodPressure = BloodPressure(110.0, 90.0)
        respiratoryRate = 20.4
        pulseRate = 62.2
        bodyTemperature = 37.1

        val disease = Disease(
                "9981abcdeeeffdeab",
                "สมองเสื่อม",
                "I10.1",
                true,
                true,
                false
        )
        diagnosises.add(Diagnosis(disease, Diagnosis.Type.OTHER))

        height = 190.5
        weight = 80.3
        location = Point(13.0, 113.34)
        principleDx = disease
        suggestion = "suggestion"
        syntom = "sleep"
        time = DateTime(2018, 9, 6, 14, 28, 15, timeZone(7))

        val myLink = Link(System.JHICS)
        myLink.keys["pcucode"] = "xxxxxx"
        myLink.keys["visitno"] = "yyyyyy"

        link = myLink
    }

    @Before
    fun setUp() {
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.ofOffset("UTC", ZoneOffset.ofHours(7))))
        fullInsertData = InsertData(
                homeVisit,
                "01088",
                54321,
                "01088",
                16840,
                "คนดีช่วยรอด",
                "",
                "",
                "",
                ""
        )
    }

    @Test
    fun bp() {
        val bp = fullInsertData.bp!!

        bp.systolic `should equal` 110.0
        bp.diastolic `should equal` 90.0
        fullInsertData.pressure `should equal` "110/90"
    }

    @Test
    fun visitcode() {
        fullInsertData.pcucode `should be equal to` "01088"
    }

    @Test
    fun visitnumber() {
        fullInsertData.visitno `should be equal to` 54321
    }

    @Test
    fun pcucodeperson() {
        fullInsertData.pcucodeperson `should be equal to` "01088"
    }

    @Test
    fun inTimeServiceFun() {
        fullInsertData.timeservice `should be equal to` 1
    }

    @Test
    fun outTimeServiceFun() {
        fullInsertData.getTimeService(1) `should be equal to` 2
        fullInsertData.getTimeService(19) `should be equal to` 2
    }

    @Test
    fun getTimeServe() {
        val visitTime = DateTime(2018, 9, 6, 14, 28, 15, timeZone(7))
        fullInsertData.getTimeService(visitTime.withZone(timeZone(7)).hourOfDay) `should be equal to` 1
        fullInsertData.getTimeService(visitTime.withZone(timeZone(8)).hourOfDay) `should be equal to` 1
        fullInsertData.getTimeService(visitTime.withZone(timeZone(9)).hourOfDay) `should be equal to` 2
    }

    @Test
    fun visitdate() {
        fullInsertData.visitdate `should equal` Timestamp.valueOf("2018-09-06 14:28:15.0")
    }

    @Ignore("Get max visit in real db")
    fun getMaxVisit() {
        val maxVisitNmber = JdbiDao().queryMaxVisit()

        maxVisitNmber `should be equal to` 238489
    }

    @Ignore("InsertUpdate visit in real db")
    fun insertVisitTest() {
        JdbiDao().insertVisit(fullInsertData)
    }
}
