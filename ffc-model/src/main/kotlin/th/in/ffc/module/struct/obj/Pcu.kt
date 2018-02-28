package th.`in`.ffc.module.struct.obj

import java.util.*

data class Pcu(val uuid: UUID = UUID.randomUUID()) {
    var code: String = "099912"
    var name: String = "NECTEC"

    constructor(uuid: UUID, code: String, name: String) : this(uuid) {
        this.code = code
        this.name = name
    }

    var session: String? = null
    var lastKnownIp: String? = null
}
