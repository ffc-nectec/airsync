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

import ffc.airsync.client.client.MainContraller;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public class Main {


    protected static final String HOSTNAMEDB="127.0.0.1";
    protected static final String HOSTPORTDB="3333";
    protected static final String HOSTDB="jhcisdb";
    protected static final String HOSTUSERNAME="root";
    protected static final String HOSTPASSWORD="123456";



    //@Option(name = "-hostdb", usage = "Database hostserver Ex. 127.0.0.1 ")
    //protected int port = DEFAULT_PORT;

    public static void main(String[] args) {

        new MainContraller().main(args);
    }

}
