package th.in.ffc.airsync.logreader.filter;

import org.junit.Assert;
import org.junit.Test;
import th.in.ffc.airsync.logreader.QueryRecord;

public class NowFilterTest {
    @Test
    public void testNowFilter(){
        NowFilter rn = new NowFilter();
        QueryRecord record = new QueryRecord("INSERT INTO `visit` (`pcucode`,`visitno`,`visitdate`,`pcucodeperson`,`pid`,`flagservice`,`username`,`rightcode`,`rightno`,`hosmain`,`hossub`,`dateupdate`)  VALUES ('07934','188301','2018-02-15','07934','177','02','จตุรงค์','89','R89700026887797','10729','07934',now())",12);
        record.setTime("2018-02-15 10:30:25");
        Assert.assertEquals(rn.process(record).getLog(),"INSERT INTO `visit` (`pcucode`,`visitno`,`visitdate`,`pcucodeperson`,`pid`,`flagservice`,`username`,`rightcode`,`rightno`,`hosmain`,`hossub`,`dateupdate`)  VALUES ('07934','188301','2018-02-15','07934','177','02','จตุรงค์','89','R89700026887797','10729','07934','2018-02-15 10:30:25')");
    }
}
