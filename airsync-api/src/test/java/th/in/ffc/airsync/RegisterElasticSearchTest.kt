package th.`in`.ffc.airsync

import org.junit.Test
import th.`in`.ffc.airsync.api.dao.RegisterElasticSearch
import th.`in`.ffc.module.struct.obj.Pcu
import java.util.*

class RegisterElasticSearchTest {
    companion object {
        var pcu = Pcu("203", "Sing-To", UUID.fromString("6ae2f41e-5df7-44d5-8e8d-e6bf08730cd1"), "ksdfkjfesdfdsfjhhjkoiii", "127.0.0.1")
        var pcu2 = Pcu("208", "Sing-To208", UUID.fromString("6ae2f41e-5df7-44d5-8e8d-e6bf08730111"), "ijkjsskkjsdsnksnsd", "192.0.0.1")
        var register = RegisterElasticSearch()
    }
    @Test
    fun registerPcuTest(){

        println("UUID="+pcu.uuid)
        register.registerPcu(pcu)
    }
    @Test
    fun registerPcuTest2(){

        println("UUID="+pcu2.uuid)
        register.registerPcu(pcu2)
    }

    @Test
    fun unregisterPcuTest(){
        register.unregisterPcu(pcu)
        register.unregisterPcu(pcu2)
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

    @Test
    fun getAllPcuTest(){
        var pculist= register.getAllPcu()
        pculist.forEach { pcu -> println("Name "+pcu.Name) }
    }
}
