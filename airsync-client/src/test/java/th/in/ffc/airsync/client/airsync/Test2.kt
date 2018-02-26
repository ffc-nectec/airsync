package th.`in`.ffc.airsync.client.airsync

import com.google.gson.Gson
import org.junit.Test
import th.`in`.ffc.module.struct.Pcu
import th.`in`.ffc.module.struct.QueryAction
import java.util.*

class Test2 {
    val gson: Gson = Gson()
    @Test
    fun test1() {

        //val testobj: Pcu = Pcu("32432", "Nectec01", UUID.randomUUID())


        //System.out.println(
        //  gson.toJson(testobj))
        System.out.println(UUID.fromString("00000000-0000-0000-0000-000000000000"))
    }

    @Test
    fun test2(){
        val outstr: MutableList<String> = mutableListOf("sdf", "sdfasdfdsaf", "99999")
        //val testobj: QueryAction = QueryAction(outstr);

        //System.out.println(gson.toJson(testobj));
    }



}
