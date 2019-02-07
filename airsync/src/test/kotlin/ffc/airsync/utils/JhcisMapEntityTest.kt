package ffc.airsync.utils

import ffc.entity.Link
import ffc.entity.Person
import ffc.entity.System
import ffc.entity.ThaiCitizenId
import ffc.entity.update
import org.amshove.kluent.`should equal`
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.junit.Test

class JhcisMapEntityTest {

    val person = Person("e079e175c75a44f180e8eaeb").update(DateTime.parse("2018-06-25T14:09:07.815+07:00")) {
        identities.add(ThaiCitizenId("1154801544875"))
        prename = "นาย"
        firstname = "พิรุณ"
        lastname = "พานิชผล"
        birthDate = LocalDate.parse("1993-06-29")
        avatarUrl = "https://avatars3.githubusercontent.com/u/783403?s=460&v=4"
        link = Link(
            System.JHICS, "pid" to "1234567", "cid" to "11014578451234",
            lastSync = DateTime.parse("2018-06-25T14:09:07.815+07:00")
        )
    }

    val person2 = Person("e079e175c75a44f180e8aaaa").update(DateTime.parse("2018-06-25T14:09:07.815+07:00")) {
        identities.add(ThaiCitizenId("1154801544875"))
        prename = "นาย"
        firstname = "พิรุณ"
        lastname = "พานิชผล"
        birthDate = LocalDate.parse("1993-06-29")
        avatarUrl = "https://avatars3.githubusercontent.com/u/783403?s=460&v=4"
        link = Link(
            System.JHICS, "pid" to "563", "cid" to "33333442345",
            lastSync = DateTime.parse("2018-06-25T14:09:07.815+07:00")
        )
    }

    val map = JhcisMapEntity(person.id, person.type, person.link)

    @Test
    fun equalsWithEntity() {
        map.equals(person) `should equal` true
        map.equals(person2) `should equal` false
    }

    @Test
    fun equalsWithLink() {
        map.equals(person.link) `should equal` true
        map.equals(person2.link) `should equal` false
    }
}
