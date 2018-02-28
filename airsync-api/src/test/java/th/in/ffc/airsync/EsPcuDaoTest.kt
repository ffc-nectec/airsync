package th.`in`.ffc.airsync

import org.junit.Test
import th.`in`.ffc.airsync.api.dao.EsPcuDao
import th.`in`.ffc.module.struct.obj.Pcu
import java.util.*

class EsPcuDaoTest {
    companion object {
        var pcu = Pcu(UUID.fromString("6ae2f41e-5df7-44d5-8e8d-e6bf08730cd1"), "203", "Sing-To").apply {
            session = "ksdfkjfesdfdsfjhhjkoiii"
            lastKnownIp = "127.0.0.1"
        }
        var pcu2 = Pcu(UUID.fromString("6ae2f41e-5df7-44d5-8e8d-e6bf08730111"), "208", "Sing-To208").apply {
            session = "ijkjsskkjsdsnksnsd"
            lastKnownIp = "192.0.0.1"
        }
        var register = EsPcuDao()
    }

    @Test
    fun registerPcuTest(){

        println("UUID="+pcu.uuid)
        register.insert(pcu)
    }
    @Test
    fun registerPcuTest2(){

        println("UUID="+pcu2.uuid)
        register.insert(pcu2)
    }

    @Test
    fun unregisterPcuTest(){
        register.remove(pcu)
        register.remove(pcu2)
    }

    @Test
    fun findPcuByUuidTest(){
        var pcu = register.findByUuid(UUID.fromString("6ae2f41e-5df7-44d5-8e8d-e6bf08730cd1"))
        println("PCU=" + pcu.name)
    }

    @Test
    fun findPcuByIpAddressTest(){
        var pcu = register.findByIpAddress("127.0.0.1")
        println("PCU=" + pcu.name)
    }

    @Test
    fun getAllPcuTest(){
        var pculist = register.find()
        pculist.forEach { pcu -> println("name " + pcu.name) }
    }
}
