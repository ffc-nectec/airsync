package ffc.airsync.api.person

import ffc.airsync.api.ApiV1
import ffc.airsync.retrofit.RetofitApi
import ffc.airsync.utils.UploadSpliter
import ffc.airsync.utils.persons
import ffc.entity.Person

class RetofitPersonApi : RetofitApi(), PersonApi {
    override fun putPerson(personList: List<Person>): List<Person> {
        val personLastUpdate = arrayListOf<Person>()
        UploadSpliter.upload(200, persons, object : UploadSpliter.HowToSendCake<Person> {
            override fun send(cakePlate: ArrayList<Person>) {
                val respond = restService.createPerson(
                    orgId = ApiV1.organization.id,
                    authkey = tokenBarer,
                    personList = cakePlate
                ).execute()
                if (respond.code() != 201) throw IllegalAccessException("Cannot Login ${respond.code()}")
                personLastUpdate.addAll(respond.body() ?: arrayListOf())
            }
        })
        return personLastUpdate
    }
}
