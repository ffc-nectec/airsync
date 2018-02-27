package th.`in`.ffc.module.struct.obj

import java.util.*

data class MessageSync(var from :UUID, var to:UUID, var status :Int, val action:Int=0, val message:String="H")
