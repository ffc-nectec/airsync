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

package ffc.airsync.client.client

import ffc.airsync.client.client.module.DaoFactory
import ffc.airsync.client.client.module.PcuSocket
import ffc.airsync.client.client.module.PcuSocketAuthByToken
import ffc.airsync.client.client.module.UserAuthDAO
import ffc.model.Organization
import java.util.*

class MainContraller {


    var orgDataTest = Organization(UUID.fromString(Config.pcuUuid),"-1", "520", "Nectec")


    fun main(args: Array<String>) {
        //get config
        //check my.ini
        //check log resume
        //check database connection


        //register central
        val userAuthDao : UserAuthDAO = DaoFactory().buildUserAuthDao()
        val messageCentral : CentralMessageManage = CentralMessageMaorgUpdatenageV1()

        orgDataTest = messageCentral.registerOrganization(orgDataTest, Config.baseUrlRest)



        val socket = PcuSocketAuthByToken(object : PcuSocket.OnEventCallbackMessageListener {
            override fun EventCallBackMessage(message: String) {
                if (message == "X") {



                    try {
                        messageCentral.checkMobileRegisterAuth({ mobileUserAuth ->
                            //Call back with post get data
                            println("Username = " + mobileUserAuth.user.user + " Password = " + mobileUserAuth.user.pass)
/*
                            if (userAuthDao.checkUserAurh(mobileUserAuth.user, mobileUserAuth.pass)) {
                                println("UserInfo pass")
                                mobileUserAuth.checkUser = UserInfo.UserStatus.PASS
                            } else {
                                println("UserInfo not pass.")
                                mobileUserAuth.checkUser = UserInfo.UserStatus.NOTPASS
                            }*/
                            //mobileUserAuth.toJson().httpPost(Config.baseUrlRest)
                            /*val message = Message<UserInfo>(
                              mobileUserAuth.orgUuid.uuid,
                              mobileUserAuth.mobileUuid,
                              Message.Status.DEFAULT,
                              Message.Action.CONFIRMUSER,
                              mobileUserAuth)*/
                            println("Send Userpas ownAction central")
                            //message.toJson().httpPost(Config.baseUrlRest)
                        })
                    }catch (e :Exception){
                        println(e)
                    }




                    messageCentral.getData()




                } else {// Cannot X

                }
            }

        },orgDataTest)




        socket.connect(Config.baseUrlSocket)
        socket.join()


        //heal connection ownAction central
        //HealthConnection healConnection = new HealthConnection();
        //healConnection.start();
        //healConnection.join();
    }
}
