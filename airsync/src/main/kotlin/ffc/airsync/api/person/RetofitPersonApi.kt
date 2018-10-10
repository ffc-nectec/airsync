package ffc.airsync.api.person

import ffc.airsync.retrofit.RetofitApi
import ffc.airsync.utils.UploadSpliter
import ffc.entity.Person

class RetofitPersonApi : RetofitApi(), PersonApi {
    override fun putPerson(personList: List<Person>): List<Person> {
        val personLastUpdate = arrayListOf<Person>()
        UploadSpliter.upload(200, personList) {
            val respond = restService.createPerson(
                orgId = organization.id,
                authkey = tokenBarer,
                personList = it
            ).execute()
            if (respond.code() != 201) throw IllegalAccessException("Cannot Login ${respond.code()}")
            personLastUpdate.addAll(respond.body() ?: arrayListOf())
        }
        return personLastUpdate
    }
}
