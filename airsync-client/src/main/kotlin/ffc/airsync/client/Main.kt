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

package ffc.airsync.client

import ffc.airsync.client.module.daojdbi.JdbiDatabaseDao
import ffc.entity.Link
import ffc.entity.Organization
import ffc.entity.System
import org.kohsuke.args4j.CmdLineException
import org.kohsuke.args4j.CmdLineParser
import org.kohsuke.args4j.Option

class Main private constructor(args: Array<String>) {

    @Option(name = "-dbhost", usage = "Database hostserver Ex. 127.0.0.1 ")
    protected var dbhost = HOSTNAMEDB

    @Option(name = "-dbport", usage = "Database port Ex. 3333 ")
    protected var dbport = HOSTPORTDB

    @Option(name = "-dbname", usage = "Database name Ex. jhcisdb ")
    protected var dbname = HOSTDBNAME

    @Option(name = "-dbusername", usage = "Database name Ex. root ")
    protected var dbusername = HOSTUSERNAME

    @Option(name = "-dbpassword", usage = "Database name Ex. 111111 ")
    protected var dbpassword = HOSTPASSWORD

    @Option(name = "-orguuid", usage = "Org uuid Ex. 00000000-0000-0000-0000-000000000001 ")
    protected var orgUuid = ORGUUID

    @Option(name = "-orgname", usage = "Database name Ex. NECTEC ")
    protected var orgName = ORGNAME

    @Option(name = "-orgcode", usage = "Database name Ex. 9843 ")
    protected var orgCode = ORGCODE

    init {
        try {
            val parser = CmdLineParser(this)
            parser.parseArgument(*args)
        } catch (cmd: CmdLineException) {
            cmd.printStackTrace()
        }

    }

    private fun run() {
        val dao = JdbiDatabaseDao(dbhost, dbport, dbname, dbusername, dbpassword)
        var org = Organization().apply {
            link = Link(System.JHICS, "pcucode" to orgCode)
            name = orgName
        }
        MainContraller(org, dao).run()
    }

    companion object {

        protected val HOSTNAMEDB = "127.0.0.1"
        protected val HOSTPORTDB = "3333"
        protected val HOSTDBNAME = "jhcisdb"
        protected val HOSTUSERNAME = "root"
        protected val HOSTPASSWORD = "123456"
        protected val ORGUUID = "00000000-0000-0000-0000-000000000001"
        protected val ORGNAME = "NECTEC"
        protected val ORGCODE = "589"
        private var instance: Main? = null

        @JvmStatic
        fun main(args: Array<String>) {
            Main(args).run()
        }
    }

}
