package ffc.airsync.client

import ffc.model.Address
import ffc.model.printDebug

object SplitUpload {


    fun <T> upload(count: Int, fixrow: Int, list: List<T>) {

        printDebug("SplitUpload upload size $count Row per req $fixrow")

        val split = count / fixrow  //แบ่งได้กี่ชุด
        val splitmod = count % fixrow  //เคษเหลือ


        for (pageCount in 0..(split - 1)) {


            val slotUpload = arrayListOf<T>()
            val startRow = pageCount * fixrow
            for (itemCount in 0..fixrow) {
                //slotUpload.add(list[startRow + itemCount])
            }


        }


    }
}
