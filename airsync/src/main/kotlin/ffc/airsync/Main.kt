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
import ffc.airsync.ui.createProgress
import ffc.airsync.utils.ApiLoopException
import ffc.airsync.utils.EmptyGUI
import ffc.airsync.utils.createErrorMessage
import ffc.airsync.utils.getLogger
import ffc.airsync.utils.getPathJarDir
import max.kotlin.checkdupp.CheckDupplicate
import max.kotlin.checkdupp.CheckDupplicateWithRest
import org.kohsuke.args4j.CmdLineException
import org.kohsuke.args4j.CmdLineParser
import org.kohsuke.args4j.Option
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.io.File
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.TimeZone
import javax.ws.rs.NotAuthorizedException
import kotlin.system.exitProcess

private val logger = getLogger(Main::class.java)
private var shutdown = false
var countSync = -100

var isShutdown
    set(value) {
        if (!shutdown) shutdown = value
    }
    get() = shutdown

internal class Main constructor(args: Array<String>) {
    @Option(name = "-api", usage = "Api url Ex. https://ffc-nectec.herokuapp.com ")
    private var api = API

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
                createErrorMessage("Duplicate", "Duplicate process", ex, logger)
                Thread.sleep(2000)
                exitProcess(1)
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
            createErrorMessage("Init Error", "Init Error", ex, logger)
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
        Config.logfilepath = dbLogfilePath().absolutePath
        MainController(dao).run()
    }

    private fun dbLogfilePath(): File {
        val dbLocation = dao.getDatabaseLocaion()
        val dataDir = File(dbLocation, "data")
        return File(dataDir, "jlog.log")
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
        createErrorMessage(
            "Init Controller Error",
            "ไม่สามารถเชื่อมต่อ Database ตรวจสอบการตั้งค่า ปิดแล้วเปิด FFC Airsync ใหม่อีกครั้ง", ex, logger
        )
        throw ex
    } catch (ex: ApiLoopException) {
        createErrorMessage("Api Error", "เกิดข้อผิดพลาด Api", ex, logger)
        throw ex
    } catch (ex: java.net.SocketTimeoutException) {
        createErrorMessage("Network Error", "ไม่สามารถเชื่อมต่อกับ Cloud ได้", ex, logger)
        throw ex
    } catch (ex: java.net.SocketException) {
        createErrorMessage("Socket Error", "Network Socket Error", ex, logger)
        throw ex
    } catch (ex: NotAuthorizedException) {
        createErrorMessage("Auth Error", "Server ปฏิเสทการเชื่อมต่อ Cannot auth ${ex.message}", ex, logger)
        throw ex
    } catch (ex: Exception) {
        createErrorMessage("Error Message", "Init Error $ex", ex, logger)
        throw ex
    }
}

val gui: AirSyncGUI = try {
    createArisyncGui()
} catch (ex: java.awt.HeadlessException) {
    EmptyGUI()
}
