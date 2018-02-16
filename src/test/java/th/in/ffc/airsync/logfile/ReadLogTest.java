package th.in.ffc.airsync.logfile;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class ReadLogTest {


    @Test
    public void processTest(){
        ReadLog readLogModule = new ReadLog("src/test/resources/jlog_test.log",false);
        AtomicInteger linenumber= new AtomicInteger();
        AtomicReference<QueryRecord> recordTest = new AtomicReference<>();

        readLogModule.setListener( (QueryRecord record) -> {
            recordTest.set(record);
            linenumber.getAndIncrement();
        });
        try {
            readLogModule.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Assert.assertEquals(linenumber.getAndIncrement(),13);
        Assert.assertEquals(recordTest.get().getLog(),"update visit set timeservice ='1' where pcucode ='07934' AND visitno='188301'");
    }

    @Test
    public void testController(){

        Controller con = new Controller();
        //con.process();
    }
}
