package ffc.airsync.api.person

import ffc.airsync.retrofit.RetofitApi
import ffc.airsync.utils.UploadSpliter
import ffc.entity.Person
import retrofit2.dsl.enqueue

class RetofitPersonApi : RetofitApi(), PersonApi {
    override fun putPerson(personList: List<Person>): List<Person> {
        val personLastUpdate = arrayListOf<Person>()
        var loop = 0
        restService.clearnPerson(orgId = organization.id, authkey = tokenBarer).execute()
        UploadSpliter.upload(200, personList) { it, index ->
            var syncc = true
            while (syncc) {
                try {
                    restService.unConfirmPersonBlock(
                        orgId = organization.id,
                        authkey = tokenBarer,
                        block = index
                    ).execute()

                    val response = restService.insertPersonBlock(
                        orgId = organization.id,
                        authkey = tokenBarer,
                        personList = it,
                        block = index
                    ).execute()

                    if (response.code() == 201 || response.code() == 200) {
                        personLastUpdate.addAll(response.body() ?: arrayListOf())
                        restService.confirmPersonBlock(
                            orgId = organization.id,
                            authkey = tokenBarer,
                            block = index
                        ).enqueue { }
                        syncc = false
                    } else {
                        println("Cannot Login ${response.code()}")
                    }
                } catch (ex: java.net.SocketTimeoutException) {
                    println("Time out loop ${++loop}")
                    ex.printStackTrace()
                }
            }
        }
        return personLastUpdate
    }
}
