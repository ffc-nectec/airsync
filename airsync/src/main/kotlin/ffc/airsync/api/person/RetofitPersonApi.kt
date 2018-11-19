package ffc.airsync.api.person

import ffc.airsync.retrofit.RetofitApi
import ffc.airsync.utils.UploadSpliter
import ffc.entity.Person

class RetofitPersonApi : RetofitApi(), PersonApi {
    override fun putPerson(personList: List<Person>): List<Person> {
        val personLastUpdate = arrayListOf<Person>()
        var syncccc = true
        var loop = 0
        while (syncccc) {
            try {
                println("Loop putPerson ${++loop}")
                personLastUpdate.clear()
                restService.clearnPerson(orgId = organization.id, authkey = tokenBarer).execute()
                UploadSpliter.upload(200, personList) { it, index ->
                    val respond = restService.createPerson(
                        orgId = organization.id,
                        authkey = tokenBarer,
                        personList = it
                    ).execute()
                    if (respond.code() != 201) throw IllegalAccessException("Cannot Login ${respond.code()}")
                    personLastUpdate.addAll(respond.body() ?: arrayListOf())
                }
                syncccc = false
            } catch (ex: java.net.SocketTimeoutException) {
                println("Time out loop $loop")
                ex.printStackTrace()
            }
        }
        return personLastUpdate
    }
}
