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
import ffc.airsync.gui.TryIcon
import ffc.airsync.provider.createArisyncGui
import ffc.airsync.provider.databaseDaoModule
import ffc.airsync.ui.AirSyncGUI
import ffc.airsync.ui.createMessage
import ffc.airsync.ui.createProgress
import ffc.airsync.utils.ApiLoopException
import ffc.airsync.utils.EmptyGUI
import ffc.airsync.utils.getLogger
import ffc.airsync.utils.getPathJarDir
import max.kotlin.checkdupp.CheckDupplicate
import max.kotlin.checkdupp.CheckDupplicateWithRest
import org.kohsuke.args4j.CmdLineException
import org.kohsuke.args4j.CmdLineParser
import org.kohsuke.args4j.Option
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.TimeZone
import javax.ws.rs.NotAuthorizedException
import kotlin.system.exitProcess

const val APIVERSION = "v1"

private const val API = "https://api.ffc.in.th"
// private const val API = "https://ffcmaekawtom.herokuapp.com"
// private const val API = "https://ffc-beta.herokuapp.com"
// private const val API = "https://ffc-staging.herokuapp.com"
// private const val API = "http://127.0.0.1:8080"
private const val MYSQLLOG = "C:\\Program Files\\JHCIS\\MySQL\\data\\jlog.log"
private val logger = getLogger(Main::class.java)
private var shutdown = false

var isShutdown
    set(value) {
        if (!shutdown) shutdown = value
    }
    get() = shutdown

internal class Main constructor(args: Array<String>) {
    @Option(name = "-api", usage = "Api url Ex. https://ffc-nectec.herokuapp.com ")
    private var api = API

    @Option(name = "-mysqllog", usage = "MySQL query log file Ex. C:\\Program Files\\JHCIS\\MySQL\\data\\jlog.log ")
    private var mysqlLog = MYSQLLOG

    var skipConfigMyIni = false

    var noGUI = false

    private val processDupplicate: CheckDupplicate = CheckDupplicateWithRest("airsync")

    val tryIcon: TryIcon

    init {
        instant = this
        if (args.contains("-v")) {
            print(BuildConfig.VERSION)
            exitProcess(0)
        }

        if (args.contains("-getpath")) {
            print(getPathJarDir())
            exitProcess(0)
        }
        logger.info("Run with ${BuildConfig.VERSION}")
        gui.setHeader(BuildConfig.VERSION)
        logger.trace("Show gui")
        gui.showWIndows()
        logger.trace("Create small icon")
        tryIcon = CreateTryIcon()
        try {

            if (args.contains("-nogui")) {
                logger.info("No gui mode")
                noGUI = true
            }

            if (args.contains("-skipcon")) {
                logger.info("Skip check my.ini")
                skipConfigMyIni = true
            }
            try {
                logger.debug("Check process duplicate.")
                processDupplicate.register()
            } catch (ex: max.kotlin.checkdupp.DupplicateProcessException) {
                errMessage("Duplicate", "Duplicate process", ex)
                Thread.sleep(2000)
                System.exit(1)
            }

            try {
                TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.ofOffset("UTC", ZoneOffset.ofHours(7))))
                val parser = CmdLineParser(this)
                val dd = arrayListOf<String>()
                dd.addAll(args)
                dd.remove("-runnow")
                parser.parseArgument(dd.toList())
            } catch (cmd: CmdLineException) {
                logger.warn(cmd, cmd)
            }
        } catch (ex: Exception) {
            errMessage("Init Error", "Init Error", ex)
            throw ex
        }
    }

    private fun CreateTryIcon(): TryIcon {
        return TryIcon("FFC Airsync", "icon.png") {
            object : MouseListener {
                override fun mouseReleased(e: MouseEvent?) {
                }

                override fun mouseEntered(e: MouseEvent?) {
                }

                override fun mouseClicked(e: MouseEvent) {
                    if (e.button == MouseEvent.BUTTON3) {
                        gui.createRightClick(e.point.x, e.point.y)
                    } else {
                        gui.switchhHideShow()
                        gui.setLocation(e.point.x, e.point.y)
                    }
                }

                override fun mouseExited(e: MouseEvent?) {
                }

                override fun mousePressed(e: MouseEvent?) {
                }
            }
        }
    }

    val dao: DatabaseDao by lazy { databaseDaoModule() }

    fun run() {
        gui.createProgress("Init Dao", 50, 100, "กำลังตรวจสอบการตั้งค่า Mysql JHCIS")
        if (!skipConfigMyIni) dao.init()
        gui.createProgress("Init Dao", 100, 100, "กำลังตรวจสอบการตั้งค่า Mysql JHCIS")
        gui.remove("Init Dao")
        Config.baseUrlRest = api
        Config.logfilepath = mysqlLog
        MainController(dao).run()
    }

    companion object {
        lateinit var instant: Main
    }
}

fun main(args: Array<String>) {
    // Runtime.getRuntime().addShutdownHook(ShutdownHook())
    try {
        Main(args).run()
    } catch (ex: org.jdbi.v3.core.ConnectionException) {
        gui.remove("Database")
        errMessage(
            "Init Controller Error",
            "ไม่สามารถเชื่อมต่อ Database ตรวจสอบการตั้งค่า ปิดแล้วเปิด FFC Airsync ใหม่อีกครั้ง", ex
        )
        throw ex
    } catch (ex: ApiLoopException) {
        errMessage("Api Error", "เกิดข้อผิดพลาด Api", ex)
        throw ex
    } catch (ex: java.net.SocketTimeoutException) {
        errMessage("Network Error", "ไม่สามารถเชื่อมต่อกับ Cloud ได้", ex)
        throw ex
    } catch (ex: java.net.SocketException) {
        errMessage("Socket Error", "Network Socket Error", ex)
        throw ex
    } catch (ex: NotAuthorizedException) {
        errMessage("Auth Error", "Server ปฏิเสทการเชื่อมต่อ Cannot auth ${ex.message}", ex)
        throw ex
    } catch (ex: Exception) {
        errMessage("Error Message", "Init Error $ex", ex)
        throw ex
    }
}

fun errMessage(key: String, message: String, ex: java.lang.Exception) {
    var exMessage = "\n"
    ex.stackTrace.forEach {
        exMessage += "$it\n}"
    }
    logger.error(message, ex)
    gui.createMessage(
        key,
        message + ex,
        AirSyncGUI.MESSAGE_TYPE.ERROR
    )
}

val gui: AirSyncGUI = try {
    createArisyncGui()
} catch (ex: java.awt.HeadlessException) {
    EmptyGUI()
}
