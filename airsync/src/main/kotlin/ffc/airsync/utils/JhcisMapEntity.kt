package ffc.airsync.utils

import ffc.entity.Entity
import ffc.entity.Link

data class JhcisMapEntity(
    val id: String,
    val type: String,
    val link: Link?
) {
    override fun equals(other: Any?): Boolean {

        if (other == this) return true
        if (other is Entity) {
            return (other.id == id && other.type == type)
        }

        if (other is Link) {
            if (link == null) return false
            if (other.system == link.system) {
                val otherKey = other.keys.toList()
                for (i in 0 until otherKey.size) {
                    if (link.keys[otherKey[i].first] != otherKey[i].second)
                        return false
                }
                return true
            }
        }
        return false
    }
}
