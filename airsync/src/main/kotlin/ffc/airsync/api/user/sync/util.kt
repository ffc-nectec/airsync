package ffc.airsync.api.user.sync

import ffc.entity.User

/**
 * จับคู่ข้อมูล One กับ Two
 * พร้อมทั้ง check timestamp update
 */
internal fun mapUserOneWithTwo(one: List<User>, two: List<User>): List<Triple<User, User?, Boolean>> {
    val mapUser = hashMapOf<User, User?>()
    one.forEach { localItem ->
        mapUser[localItem] = two.find { cloudItem -> localItem.eq(cloudItem) }
    }
    val result = arrayListOf<Triple<User, User?, Boolean>>()
    mapUser.forEach { (localItem, cloudItem) ->
        val update =
            if (cloudItem == null)
                false
            else
                localItem.timestamp > cloudItem.timestamp

        result.add(Triple(localItem, cloudItem, update))
    }
    return result.toList()
}

internal fun User.eq(eq: User): Boolean {
    if (this.name == eq.name) return true
    val thisIdCard = this.getIdCard()
    val eqIdCard = eq.getIdCard()
    if (thisIdCard.isNullOrBlank()) return false
    if (eqIdCard.isNullOrBlank()) return false
    if (thisIdCard == eqIdCard) return true
    return false
}

internal fun User.getIdCard(): String? {
    return this.link?.keys?.get("idcard")?.toString()
}
