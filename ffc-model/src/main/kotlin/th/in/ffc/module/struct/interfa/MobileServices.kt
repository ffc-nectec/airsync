package th.`in`.ffc.module.struct.interfa

import th.`in`.ffc.module.struct.obj.Pcu

interface MobileServices {
    fun getAll() : List<Pcu>
    fun getMyPcu(): List<Pcu>


}
