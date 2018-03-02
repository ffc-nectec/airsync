package th.`in`.ffc.module.struct.obj

import java.util.*

data class MessageSync(var from: UUID,
                       var to: UUID,
                       var status: Int,
                       val action: Action = Action.NULL,
                       val message: String = "H") {

    enum class Action(code: Int) {
        REGISTER(1), PING(10), NULL(0)
    }
    enum class Status(code: Int){
        ERROR(500), SUCC(200),NOTFOUND(404)
    }
}
