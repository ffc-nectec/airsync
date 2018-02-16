package th.in.ffc.airsync.logfile;

import org.junit.Test;

public class ControllerTest {
    @Test
    public void process(){
        Controller controller = new Controller("src/test/resources/jlog_test.log","src/test/resources/jlog_test.csv",false);
        controller.process();
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
