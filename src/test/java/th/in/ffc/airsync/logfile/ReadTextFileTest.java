package th.in.ffc.airsync.logfile;

import org.junit.Assert;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.atomic.AtomicReference;

public class ReadTextFileTest {
    @Test
    public void read() throws Exception {
        ReadTextFile readTextFile;
        String logfilepart = "src/test/resources/ReadTextFileRT.txt";
        AtomicReference<QueryRecord> recordTest = new AtomicReference<>();
        PrintWriter writer = null;
        writer = new PrintWriter(logfilepart, "UTF-8");
        readTextFile = new ReadTextFile(logfilepart, true, 100);
        readTextFile.setListener(record -> {
            //System.out.println(record.getLog());
            recordTest.set(record);
        });
        new Thread(() -> {

            try {
                readTextFile.process();
            } catch (IOException e) {
                e.printStackTrace();
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
        Assert.assertEquals(recordTest.get().getLog(), "\t\t      1 Query       INSERT INTO `visit` (`pcucode`,`visitno`,`visitdate`,`pcucodeperson`,`pid`,`flagservice`,`username`,`rightcode`,`rightno`,`hosmain`,`hossub`,`dateupdate`)  VALUES ('07934','188301','2018-02-15','07934','177','02','จตุรงค์','89','R89700026887797','10729','07934',now())");
    }
}
