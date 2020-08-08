package ffc.airsync.person

import ffc.entity.Person
import ffc.entity.healthcare.Chronic
import ffc.entity.healthcare.Disability
import ffc.entity.healthcare.Icd10

interface PersonDao {
    interface Lookup {
        fun lookupDisease(icd10: String): Icd10
        fun lookupChronic(pcuCode: String, pid: String): List<Chronic>
        fun lookupDisability(pcuCode: String, pid: String): List<Disability>
    }

    fun get(lookup: () -> Lookup): List<Person>
    fun findBy(pcuCode: String, pid: String, lookup: () -> Lookup): Person
}
