/*
 *
 *  * Copyright 2020 NECTEC
 *  *   National Electronics and Computer Technology Center, Thailand
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *    http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package th.`in`.ffc.airsync.logreader

import org.amshove.kluent.`should be equal to`
import org.junit.Test
import java.io.File
import java.io.IOException
import java.io.PrintWriter
import java.util.concurrent.atomic.AtomicReference

class LogReaderTest {

    private val logfile = "ReadTextFileRT.txt"

    @Test
    fun fullTest() {
        val readLogFile: LogReader
        val table = AtomicReference<String>()
        val key = AtomicReference<String>()
        val writer = PrintWriter(logfile, "UTF-8")

        File("logTest.cfg").delete()

        val filter = hashMapOf<String, List<String>>().apply {
            put("house", listOf("house", "`house`", "`jhcisdb`.`house`"))
            put("visit", listOf("visit", "`visit`", " visit ", "visitdrug", "visithomehealthindividual"))
            put("person", listOf("`person`", " person ", "`jhcisdb`.`person`", "person"))
            put("user", listOf("`user`", " user ", "`jhcisdb`.`user`"))
        }

        readLogFile = LogReader(
            logfile,
            isTest = true,
            delay = 100,
            tablesPattern = filter,
            onLogInput = { tableName,
                keyWhere ->

                table.set(tableName)
                key.set(keyWhere.first())
            }
        )

        Thread {
            try {
                readLogFile.start()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }.start()

        /* ktlint-disable */
        writer.println("180215 10:29:20\t      1 Connect     root@localhost on jhcisdb")
        writer.flush()
        writer.println("\t\t      1 Query       update office set dateverupdate ='2016-10-11'")
        writer.flush()
        writer.println("""181210  1:28:32	      1 Query       UPDATE `jhcisdb`.`visit` SET `symptoms`='What is it.6' WHERE  `pcucode`='01092' AND `visitno`=289783""")
        writer.println("\t\t      1 Query       INSERT INTO `visit` (`pcucode`,`visitno`,`visitdate`,`pcucodeperson`,`pid`,`flagservice`,`username`,`rightcode`,`rightno`,`hosmain`,`hossub`,`dateupdate`)  VALUES ('07934','188301','2018-02-15','07934','177','02','จตุรงค์','89','R89700026887797','10729','07934',now())")
        writer.flush()
        Thread.sleep(200)

        table.get() `should be equal to` "visit"
        key.get() `should be equal to` """`pcucode`,`visitno`,`visitdate`,`pcucodeperson`,`pid`,`flagservice`,`username`,`rightcode`,`rightno`,`hosmain`,`hossub`,`dateupdate`"""

        writer.print("""		      3 Query       UPDATE `house` SET `hno`='78/5' WHERE  `pcucode`='07934' AND `hcode`=305""")
        writer.flush()
        Thread.sleep(200)

        table.get() `should be equal to` "house"
        key.get() `should be equal to` """`pcucode`='07934' AND `hcode`=305"""

        writer.println("""181210  3:38:46       1 Query       UPDATE `jhcisdb`.`house` SET `hno`='cmyk' WHERE  `pcucode`='01092' AND `hcode`=7364""")
        writer.flush()
        Thread.sleep(200)
        table.get() `should be equal to` "house"
        key.get() `should be equal to` """`pcucode`='01092' AND `hcode`=7364"""

        writer.println(
            """		      1 Query       UPDATE house SET pets='0', ygis=NULL, houseairflow='0', hid=NULL, foodgarbageware='0', foodcook='0', iodeinuse='0', pcucode='06651', nearhouse=NULL, pid=NULL, garbageerase='0', dateupdate=NULL, foodsanitation='0', wateruse=NULL, whjrk='0', housepic=NULL, road=NULL, area='1', houselight='0', hno='999999', pcucodeperson=NULL, slpp='0', controlmqt='0', cht='0', flag18fileexpo='2', toilet='0', controlcockroach='0', controlinsetdisease='0', housesanitation='0', waterassuage='0', housesurveydate=NULL, hcode='2659', telephonehouse=NULL, foodkeepsafe='0', villcode='57010501', foodwarewash='0', usernamedoc=NULL, waterdrink=NULL, housecomplete='0', waterdrinkeno='0', headhealthhouse=NULL, communityno=NULL, housechar='1', controlrat='0', foodwarekeep='0', xgis=NULL, iodeinsalt='0', pidvola=NULL, controlhousefly='0', pcucodepersonvola=NULL, houseendur='0', petsdung='0', foodcookroom='0', iodeinmaterial='0', foodware='0', kmch='0', garbageware='0', wateruseeno='0', ftlj='0', housecharground=NULL, dateregister=NULL, houseclean='0' , flag18fileexpo='2'  WHERE pcucode='06651' AND hcode='2659' AND 1"""
        )
        writer.flush()
        Thread.sleep(200)
        table.get() `should be equal to` "house"
        key.get() `should be equal to` """pcucode='06651' AND hcode='2659' AND 1"""

        writer.println("""		      1 Query       INSERT INTO person (fname,pcucodeperson,pid,hcode,provcodemoi,distcodemoi,subdistcodemoi,prename,familyno,typelive,nation,origin,religion,occupa,hnomoi,mumoi,sex,dateupdate,flagoffline) VALUES ('','06651','18075','2659','57','01','05','','1','1','99','99','01','001','999999','1','1',now(),null)""")
        writer.flush()
        Thread.sleep(200)
        table.get() `should be equal to` "person"

        writer.println("""		      1 Query       UPDATE person SET typelive ='1' WHERE (typelive ='4' or typelive is NULL or typelive ='') AND pid ='18075' AND pcucodeperson ='06651'""")
        writer.flush()
        Thread.sleep(200)
        table.get() `should be equal to` "person"
        key.get() `should be equal to` """(typelive ='4' or typelive is NULL or typelive ='') AND pid ='18075' AND pcucodeperson ='06651'"""

        writer.println("""		      1 Query       update person set fname ='วิชญาพร',lname ='หนูทอง' where pcucodeperson ='06651' and pid ='18075'""")
        writer.flush()
        Thread.sleep(200)
        table.get() `should be equal to` "person"
        key.get() `should be equal to` """pcucodeperson ='06651' and pid ='18075'"""

        writer.println("""		      1 Query       update person set person.idcard ='3412542335125' where pcucodeperson ='06651' and pid =18075""")
        writer.flush()
        Thread.sleep(200)
        table.get() `should be equal to` "person"
        key.get() `should be equal to` """pcucodeperson ='06651' and pid =18075"""

        writer.println("""2018-08-09T08:49:32.213221Z	   19 Query	/* ApplicationName=IntelliJ IDEA 2018.2 */ UPDATE `jhcisdb`.`house` t SET t.`hno` = '3/88855' WHERE t.`pcucode` LIKE '07934' ESCAPE '#' AND t.`hcode` = 2""")

        writer.close()

        // table.get() `should be equal to` "`house`"
        // record.get().log `should be equal to` """UPDATE `jhcisdb`.`house` t SET t.`hno` = '3/88855' WHERE t.`pcucode` LIKE '07934' ESCAPE '#' AND t.`hcode` = 2"""
        // record.get().linenumber `should be equal to` 4
        /* ktlint-enable */
    }
}
