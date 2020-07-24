package ffc.airsync.api.genogram

import ffc.airsync.api.genogram.lib.GenogramProcessWatcarakorn
import ffc.airsync.api.person.lock
import ffc.airsync.geonogramApi
import ffc.airsync.persons
import ffc.airsync.utils.getLogger
import ffc.airsync.utils.load
import ffc.airsync.utils.save
import ffc.entity.Person

private interface GenogramUtil

private val logger by lazy { getLogger(GenogramUtil::class) }

fun ArrayList<Person>.initRelation(progressCallback: (Int) -> Unit) {
    persons.lock {
        val localRelation = arrayListOf<Person>().apply {
            addAll(load("relation.json"))
        }

        if (localRelation.isEmpty()) {
            this.addAll(persons)
            this.forEach {
                it.relationships.clear()
            }
            GenogramProcessWatcarakorn(FFCAdapterPersonDetailInterface(this)).process(this)
            updateToCloud(progressCallback)
            save("relation.json")
        } else {
            this.addAll(localRelation)
        }
        progressCallback(100)
    }
}

private fun List<Person>.updateToCloud(progressCallback: (Int) -> Unit) {
    val personBlock = hashMapOf<String, List<Person.Relationship>>()

    forEach {
        if (it.relationships.isNotEmpty()) {
            personBlock[it.id] = it.relationships
        }
    }
    geonogramApi.putBlock(personBlock, progressCallback).forEach { personId, relation ->
        this.find { it.id == personId }?.relationships = relation.toMutableList()
    }
}
