package th.in.ffc.airsync.logreader;

import org.junit.Test;
import th.in.ffc.airsync.logreader.Controller;

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
    @Test
    public void processSingle(){
        //Controller controller = new Controller("src/test/resources/jlog_test.log","src/test/resources/jlog_test.csv",false);
        //controller.processSingle();

    }
}
