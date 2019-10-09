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
            put("visit", listOf("visit", "`visit`"))
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

        writer.println("""2018-08-09T08:49:32.213221Z	   19 Query	/* ApplicationName=IntelliJ IDEA 2018.2 */ UPDATE `jhcisdb`.`house` t SET t.`hno` = '3/88855' WHERE t.`pcucode` LIKE '07934' ESCAPE '#' AND t.`hcode` = 2""")

        writer.close()

        // table.get() `should be equal to` "`house`"
        // record.get().log `should be equal to` """UPDATE `jhcisdb`.`house` t SET t.`hno` = '3/88855' WHERE t.`pcucode` LIKE '07934' ESCAPE '#' AND t.`hcode` = 2"""
        // record.get().linenumber `should be equal to` 4
        /* ktlint-enable */
    }
}
