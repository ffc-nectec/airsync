/*
 * Copyright (c) 2561 NECTEC
 *   National Electronics and Computer Technology Center, Thailand
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ffc.airsync.api

import ffc.airsync.api.dao.EsOrgDao
import ffc.model.Organization
import org.junit.Test
import java.util.*

class EsOrganizationDaoTest {
    companion object {
        var pcu = Organization(UUID.fromString("6ae2f41e-5df7-44d5-8e8d-e6bf08730cd1"),"-1", "203", "Sing-To").apply {
            session = "ksdfkjfesdfdsfjhhjkoiii"
            lastKnownIp = "127.0.0.1"
        }
        var pcu2 = Organization(UUID.fromString("6ae2f41e-5df7-44d5-8e8d-e6bf08730111"),"-1", "208", "Sing-To208").apply {
            session = "ijkjsskkjsdsnksnsd"
            lastKnownIp = "192.0.0.1"
        }
        var register = EsOrgDao()
    }

    @Test
    fun registerPcuTest(){

        println("UUID="+ pcu.uuid)
        register.insert(pcu)
    }
    @Test
    fun registerPcuTest2(){

        println("UUID="+ pcu2.uuid)
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
        //println("PCU=" + pcu.name)
    }

    @Test
    fun getAllPcuTest(){
        var pculist = register.find()
        pculist.forEach { pcu -> println("name " + pcu.name) }
    }
}
