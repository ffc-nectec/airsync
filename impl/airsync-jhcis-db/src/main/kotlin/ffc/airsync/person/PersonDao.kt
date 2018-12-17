package ffc.airsync.person

import ffc.entity.Person

interface PersonDao {
    fun get(): List<Person>
    fun find(pcucode: String, pid: Long): List<Person>
}
