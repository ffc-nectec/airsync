import ffc.airsync.retrofit.airSyncGson
import ffc.entity.Link
import ffc.entity.Person
import ffc.entity.System
import ffc.entity.ThaiCitizenId
import ffc.entity.gson.parseTo
import ffc.entity.gson.toJson
import ffc.entity.update
import org.amshove.kluent.`should contain`
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.junit.Test

class EntityBundleTest {

    val person = Person("e079e175c75a44f180e8eaeb").update(DateTime.parse("2018-06-25T14:09:07.815+07:00")) {
        identities.add(ThaiCitizenId("1154801544875"))
        prename = "นาย"
        firstname = "พิรุณ"
        lastname = "พานิชผล"
        birthDate = LocalDate.parse("1993-06-29")
        link = Link(
            System.JHICS, "pid" to "1234567", "cid" to "11014578451234",
            lastSync = DateTime.parse("2018-06-25T14:09:07.815+07:00")
        )
        bundle["orgId"] = "00108"
        bundle["houseId"] = "12321237896"
    }

    @Test
    fun bundle() {
        val json = person.toJson(airSyncGson)
        val personConvert = json.parseTo<Person>(airSyncGson)

        personConvert.bundle.toJson() `should contain` "houseId"
    }
}
