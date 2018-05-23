package ffc.airsync.api.services.module

import ffc.model.Chronic
import ffc.model.Person
import ffc.model.printDebug
import java.util.*

object PersonService {

    fun get(token: String, orgId: String, page: Int, per_page: Int): List<Person> {
        val tokenObj = getOrgByMobileToken(UUID.fromString(token.trim()), orgId)
        val personList = personDao.find(orgUuid = tokenObj.uuid)
        val personReturn = arrayListOf<Person>()


        val count = personList.count()

        itemRenderPerPage(page, per_page, count, object : AddItmeAction {
            override fun onAddItemAction(it: Int) {

                val person = personList[it].data


                if (person.houseId != null) {
                    val housePerson = houseDao.findByHouseId(tokenObj.uuid, person.houseId!!)
                    person.house = housePerson?.data
                }

                personReturn.add(person)

            }
        })

        return personReturn
    }


    fun create(token: UUID, orgId: String, personList: List<Person>) {
        val org = getOrgByOrgToken(token, orgId)
        personDao.insert(org.uuid, personList)
    }
}
