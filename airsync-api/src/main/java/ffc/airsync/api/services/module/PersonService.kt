package ffc.airsync.api.services.module

import ffc.model.Chronic
import ffc.model.Person
import ffc.model.printDebug
import java.util.*

object PersonService {

    fun get(token: String, orgId: String): List<Person> {
        val tokenObj = getOrgByMobileToken(UUID.fromString(token.trim()), orgId)
        val personList = personDao.find(orgUuid = tokenObj.uuid)
        val personReturn = arrayListOf<Person>()


        var lmitLoop = 0
        personList.forEach {
            if (lmitLoop < 100) {
                lmitLoop++


                val person = it.data
                val chronicPerson = chronicDao.filterByPersonPid(tokenObj.uuid, it.data.pid!!.toInt())
                val chronicList = arrayListOf<Chronic>()


                if (chronicPerson.isNotEmpty())
                    chronicPerson.forEach {
                        printDebug("It pid = ${it.data.pid} Person pid = ${person.pid}")
                        chronicList.add(it.data)
                    }
                person.chronics = chronicList


                if (person.houseId != null) {
                    val housePerson = houseDao.findByHouseId(tokenObj.uuid, person.houseId!!)
                    person.house = housePerson?.data
                }


                personReturn.add(person)
            }
        }


        return personReturn
    }


    fun create(token: String, orgId: String, personList: List<Person>) {
        val org = getOrgByOrgToken(token, orgId)
        personDao.insert(org.uuid, personList)
    }
}
