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

package ffc.airsync.client;

import ffc.airsync.client.client.CentralMessageManage;
import ffc.airsync.client.client.CentralMessageManageV1;
import ffc.airsync.client.client.Config;
import ffc.airsync.client.client.module.DaoFactory;
import ffc.airsync.client.client.module.PcuSocket;
import ffc.airsync.client.client.module.PcuSocketAuthByToken;
import ffc.airsync.client.client.module.UserAuthDAO;
import ffc.model.Pcu;
import kotlin.Unit;

import java.util.UUID;

public class Main {

    public static Pcu pcuDataTest = new Pcu(UUID.fromString(Config.Companion.getPcuUuid()), "520", "Nectec", "", "", "", "");



    //Pcu pcuDataTest = new Pcu(UUID.fromString(Config.Companion.getPcuUuid()),"","");
    public static void main(String[] args) {
        //get config
        //check my.ini
        //check log resume
        //check database connection


        //register central
        UserAuthDAO userAuthDao = new DaoFactory().buildUserAuthDao();
        CentralMessageManage messageCentral = new CentralMessageManageV1();

        pcuDataTest = messageCentral.registerPcu(pcuDataTest, Config.Companion.getUrlRest());
        PcuSocket socket = new PcuSocketAuthByToken(message -> {
            if (message.equals("X")) {
                messageCentral.checkMobileRegisterAuth((String username, String password) -> {
                    System.out.println("Username = " + username + " Password = " + password);

                    if(userAuthDao.checkUserAurh(username,password)){
                        sdf
                    }
                    return Unit.INSTANCE;
                });


            }else {

            }
        });

        /*socket.setEventCallBack(new PcuSocket.OnEventCallbackMessageListener() {
            @Override
            public void EventCallBackMessage(@NotNull String message) {
                
            }
        });*/


        socket.connect(Config.Companion.getUri());
        socket.join();


        //heal connection to central
        //HealthConnection healConnection = new HealthConnection();
        //healConnection.start();
        //healConnection.join();
    }

}
