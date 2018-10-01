package ffc.airsync.api

import ffc.entity.Person

interface PersonApi {
    fun putPerson(personList: List<Person>): List<Person>
}
