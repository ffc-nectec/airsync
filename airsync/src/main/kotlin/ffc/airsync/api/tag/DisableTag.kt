package ffc.airsync.api.tag

import ffc.entity.Person

internal class DisableTag {
    fun run(person: Person, addTag: (person: Person) -> Unit) {
        val condition = person.disabilities.isNotEmpty()
        if (condition) {
            addTag(person)
        }
    }
}
