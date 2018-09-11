import ffc.airsync.utils.cleanFile
import ffc.airsync.utils.getClassNameInList
import ffc.airsync.utils.load
import ffc.airsync.utils.save
import ffc.entity.Link
import ffc.entity.Person
import ffc.entity.System
import ffc.entity.ThaiCitizenId
import ffc.entity.update
import org.amshove.kluent.`should be equal to`
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.junit.After
import org.junit.Before
import org.junit.Test

class DataStoreTest {

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
    }

    @Before
    fun setUp() {
        arrayListOf<Person>().cleanFile()
    }

    @After
    fun tearDown() {
        arrayListOf<Person>().cleanFile()
    }

    @Test
    fun getClassNameInList() {
        val persons = arrayListOf<Person>().apply {
            add(person)
        }

        getClassNameInList(persons) `should be equal to` "Person"
    }

    @Test
    fun clearFile() {
        arrayListOf<Person>().apply {
            add(person)
        }.save()

        arrayListOf<Person>().cleanFile()
    }

    @Test
    fun loadFile() {
        val person = arrayListOf<Person>().load()
        person.isEmpty() `should be equal to` true
    }

    @Test
    fun savePerson() {
        arrayListOf<Person>().apply {
            add(person)
        }.save()

        val persons = arrayListOf<Person>().load()

        persons.count() `should be equal to` 1
        persons.last().id `should be equal to` "e079e175c75a44f180e8eaeb"
    }
}
