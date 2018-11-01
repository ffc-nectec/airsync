package ffc.airsync.api.genogram

import ffc.entity.Person
import ffc.entity.Person.Relationship

interface GeonogramApi {
    fun put(personId: String, relationship: List<Relationship>): List<Person.Relationship>
}
