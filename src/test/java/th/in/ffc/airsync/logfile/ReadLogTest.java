package th.in.ffc.airsync.logfile;

import org.junit.Assert;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class ReadLogTest {


    @Test
    public void notRealtime() {
        ReadLog readLogModule = new ReadLog("src/test/resources/jlog_test.log", false);
        AtomicInteger linenumber = new AtomicInteger();
        AtomicReference<QueryRecord> recordTest = new AtomicReference<>();

        readLogModule.setListener((QueryRecord record) -> {
            recordTest.set(record);
            linenumber.getAndIncrement();
        });
        try {
            readLogModule.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Assert.assertEquals(linenumber.getAndIncrement(), 13);
        Assert.assertEquals(recordTest.get().getLog(), "update visit set timeservice ='1' where pcucode ='07934' AND visitno='188301'");
    }

    @Test
    public void realTime() throws Exception {

        ReadLog readLog;
        String logfilepart = "src/test/resources/readLogRT.txt";
        AtomicReference<QueryRecord> recordTest = new AtomicReference<>();
        PrintWriter writer = new PrintWriter(logfilepart, "UTF-8");
        readLog = new ReadLog(logfilepart, true, 100);
        readLog.setListener(record -> {
            //System.out.println(record.getLog());
            recordTest.set(record);
        });
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    readLog.run();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        writer.println("180215 10:29:20\t      1 Connect     root@localhost on jhcisdb");
        writer.flush();
        writer.println("\t\t      1 Query       update office set dateverupdate ='2016-10-11'");
        writer.flush();
        writer.println("\t\t      1 Query       INSERT INTO `visit` (`pcucode`,`visitno`,`visitdate`,`pcucodeperson`,`pid`,`flagservice`,`username`,`rightcode`,`rightno`,`hosmain`,`hossub`,`dateupdate`)  VALUES ('07934','188301','2018-02-15','07934','177','02','จตุรงค์','89','R89700026887797','10729','07934',now())");
        writer.flush();
        Thread.sleep(200);


        writer.close();
        Assert.assertEquals(recordTest.get().getLog(), "INSERT INTO `visit` (`pcucode`,`visitno`,`visitdate`,`pcucodeperson`,`pid`,`flagservice`,`username`,`rightcode`,`rightno`,`hosmain`,`hossub`,`dateupdate`)  VALUES ('07934','188301','2018-02-15','07934','177','02','จตุรงค์','89','R89700026887797','10729','07934','2018-02-15 10:29:20')");
    }


    public void run() {
        Controller controller = new Controller();
        controller.processSingle();
    }
}
