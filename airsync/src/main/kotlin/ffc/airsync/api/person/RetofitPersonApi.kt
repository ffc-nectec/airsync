package ffc.airsync.api.person

import ffc.airsync.retrofit.RetofitApi
import ffc.airsync.utils.ApiLoopException
import ffc.airsync.utils.UploadSpliter
import ffc.airsync.utils.callApi
import ffc.airsync.utils.callApiNoReturn
import ffc.entity.Person
import retrofit2.dsl.enqueue

class RetofitPersonApi : RetofitApi<PersonUrl>(PersonUrl::class.java), PersonApi {
    override fun putPerson(personList: List<Person>, progressCallback: (Int) -> Unit): List<Person> {
        val personLastUpdate = arrayListOf<Person>()
        callApiNoReturn { restService.clearnPerson(orgId = organization.id, authkey = tokenBarer).execute() }

        val fixSizeCake = 200
        val sizeOfLoop = personList.size / fixSizeCake
        UploadSpliter.upload(fixSizeCake, personList) { it, index ->

            val result = callApi {
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
                    restService.confirmPersonBlock(
                        orgId = organization.id,
                        authkey = tokenBarer,
                        block = index
                    ).enqueue { }
                    response.body() ?: arrayListOf()
                } else {
                    throw ApiLoopException("Cannot Login ${response.code()}")
                }
            }
            personLastUpdate.addAll(result)
            progressCallback(((index * 50) / sizeOfLoop) + 50)
        }
        return personLastUpdate
    }
}
