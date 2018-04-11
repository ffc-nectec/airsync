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


    protected static final String HOSTNAMEDB = "127.0.0.1";
    protected static final String HOSTPORTDB = "3333";
    protected static final String HOSTDBNAME = "jhcisdb";
    protected static final String HOSTUSERNAME = "root";
    protected static final String HOSTPASSWORD = "123456";
    protected static final String ORGUUID = "00000000-0000-0000-0000-000000000001";
    protected static final String ORGNAME = "NECTEC";
    protected static final String ORGCODE = "589";
    private static Main instance;
    @Option(name = "-dbhost", usage = "Database hostserver Ex. 127.0.0.1 ")
    protected String dbhost = HOSTNAMEDB;

    @Option(name = "-dbpot", usage = "Database port Ex. 3333 ")
    protected String dbpot = HOSTPORTDB;

    @Option(name = "-dbname", usage = "Database name Ex. jhcisdb ")
    protected String dbname = HOSTDBNAME;

    @Option(name = "-dbusername", usage = "Database name Ex. root ")
    protected String dbusername = HOSTUSERNAME;

    @Option(name = "-dbpassword", usage = "Database name Ex. 111111 ")
    protected String dbpassword = HOSTPASSWORD;


    @Option(name = "-orguuid", usage = "Org uuid Ex. 00000000-0000-0000-0000-000000000001 ")
    protected String orgUuid = ORGUUID;

    @Option(name = "-orgname", usage = "Database name Ex. NECTEC ")
    protected String orgName = ORGNAME;

    @Option(name = "-orgcode", usage = "Database name Ex. 9843 ")
    protected String orgCode = ORGCODE;


    public Main(String[] args) {
        try {
            CmdLineParser parser = new CmdLineParser(this);
            parser.parseArgument(args);
        } catch (CmdLineException cmd) {
            cmd.printStackTrace();
        }
    }


    public static void main(String[] args) {
        instance = new Main(args);
        instance.run();
    }


    public void run() {
        new MainContraller().main(dbhost, dbpot, dbname, dbusername, dbpassword, orgUuid, orgName, orgCode);
    }

}
