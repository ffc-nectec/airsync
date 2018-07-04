package th.in.ffc.airsync.logreader.filter;

import org.junit.Assert;
import th.in.ffc.airsync.logreader.Config;
import th.in.ffc.airsync.logreader.QueryRecord;

public class GetTimeFilterTest {

    public void testGetTime(){
        GetTimeFilter gt = new GetTimeFilter(Config.timePattern);
        Assert.assertEquals("","");
        Assert.assertEquals(gt.process(new QueryRecord("180215 10:30:25\t      1 Query       SELECT * FROM person  WHERE pid = '177' and pcucodeperson ='07934'",12)).getTime(),"2018-02-15 10:30:25");
    }
}
