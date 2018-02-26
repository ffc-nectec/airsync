package th.`in`.ffc.airsync

import org.junit.Test
import th.`in`.ffc.airsync.api.RegisterElasticSearch
import th.`in`.ffc.module.struct.Pcu
import java.util.*

class RegisterElasticSearchTest {
    companion object {
        var pcu = Pcu("203","Sing-To", UUID.fromString("6ae2f41e-5df7-44d5-8e8d-e6bf08730cd1"),"ksdfkjfesdfdsfjhhjkoiii","127.0.0.1")
        var register = RegisterElasticSearch()
    }
    @Test
    fun registerPcuTest(){

        println("UUID="+pcu.uuid)
        register.registerPcu(pcu)
    }

    @Test
    fun unregisterPcuTest(){
        register.unregisterPcu(pcu)
    }

    @Test
    fun findPcuByUuidTest(){
        var pcu = register.findPcuByUuid(UUID.fromString("6ae2f41e-5df7-44d5-8e8d-e6bf08730cd1"))
        println("PCU="+pcu.Name)
    }

    @Test
    fun findPcuByIpAddressTest(){
        var pcu = register.findPcuByIpAddress("127.0.0.1")
        println("PCU="+pcu.Name)
    }
}
