package ffc.airsync.api.person

import ffc.entity.Person

interface PersonApi {
    fun putPerson(personList: List<Person>, progressCallback: (Int) -> Unit): List<Person>
}

val personApi: PersonApi by lazy { RetofitPersonApi() }
val persons = arrayListOf<Person>()
