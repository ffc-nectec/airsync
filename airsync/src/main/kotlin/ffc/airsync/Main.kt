/*
 * Copyright (c) 2018 NECTEC
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

package ffc.airsync

import ffc.airsync.db.DatabaseDao
import ffc.airsync.provider.databaseDaoModule
import org.kohsuke.args4j.CmdLineException
import org.kohsuke.args4j.CmdLineParser
import org.kohsuke.args4j.Option
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.TimeZone
import kotlin.system.exitProcess

private const val VERSION = "0.0.2"
private const val HOSTNAMEDB = "127.0.0.1"
private const val HOSTPORTDB = "3333"
private const val HOSTDBNAME = "jhcisdb"
private const val HOSTUSERNAME = "root"
private const val HOSTPASSWORD = "123456"
// private const val API = "https://ffc-nectec.herokuapp.com"
private const val API = "https://ffcmaekawtom.herokuapp.com"
// private const val API = "https://ffc-beta.herokuapp.com"
// private const val API = "https://ffc-nectec-staging.herokuapp.com"
// private const val API = "http://127.0.0.1:8080"
private const val MYSQLLOG = "C:\\Program Files\\JHCIS\\MySQL\\data\\jlog.log"

internal class Main constructor(args: Array<String>) {
    @Option(name = "-dbhost", usage = "Database hostserver Ex. 127.0.0.1 ")
    private var dbhost = HOSTNAMEDB

    @Option(name = "-dbport", usage = "Database port Ex. 3333 ")
    private var dbport = HOSTPORTDB

    @Option(name = "-dbname", usage = "Database name Ex. jhcisdb ")
    private var dbname = HOSTDBNAME

    @Option(name = "-dbusername", usage = "Database name Ex. root ")
    private var dbusername = HOSTUSERNAME

    @Option(name = "-dbpassword", usage = "Database name Ex. 111111 ")
    private var dbpassword = HOSTPASSWORD

    @Option(name = "-api", usage = "Api url Ex. https://ffc-nectec.herokuapp.com ")
    private var api = API

    @Option(name = "-mysqllog", usage = "MySQL query log file Ex. C:\\Program Files\\JHCIS\\MySQL\\data\\jlog.log ")
    private var mysqlLog = MYSQLLOG

    init {
        if (args.contains("-v")) {
            print(VERSION)
            exitProcess(0)
        }
        try {
            TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.ofOffset("UTC", ZoneOffset.ofHours(7))))
            val parser = CmdLineParser(this)
            parser.parseArgument(*args)
        } catch (cmd: CmdLineException) {
            cmd.printStackTrace()
        }
    }

    val dao: DatabaseDao by lazy { databaseDaoModule(dbhost, dbport, dbname, dbusername, dbpassword) }

    fun run() {
        instant = this
        Config.baseUrlRest = api
        Config.logfilepath = mysqlLog
        MainController(dao).run()
    }

    companion object {
        lateinit var instant: Main
    }
}

fun main(args: Array<String>) {
    Main(args).run()
}
