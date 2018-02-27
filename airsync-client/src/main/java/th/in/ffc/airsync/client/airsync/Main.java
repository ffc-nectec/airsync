package th.in.ffc.airsync.client.airsync;


import th.in.ffc.airsync.client.airsync.client.Config;
import th.in.ffc.airsync.client.airsync.client.HealthConnection;
import th.in.ffc.airsync.client.airsync.client.RegisterPcuToCentral;
import th.in.ffc.module.struct.obj.Pcu;

import java.util.UUID;

public class Main {

    static Pcu pcuDataTest = new Pcu("207", "คลองหลวง", UUID.fromString(Config.Companion.getPcuUuid()), "", "");

    public static void main(String[] args) {

        //get config
        //check my.ini
        //check log resume
        //check database connection
        //register central
        new RegisterPcuToCentral().register(pcuDataTest);
        //heal connection to central
        HealthConnection healConnection = new HealthConnection();
        healConnection.start();
        healConnection.join();
    }
}
