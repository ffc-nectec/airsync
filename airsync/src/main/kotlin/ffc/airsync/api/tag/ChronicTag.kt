package ffc.airsync.api.tag

import ffc.entity.Person

internal class ChronicTag {

    fun run(person: Person, addTag: () -> Unit) {
        val condition = person.chronics.firstOrNull { it.isActive } != null
        if (condition) {
            addTag()
        }
    }
}
