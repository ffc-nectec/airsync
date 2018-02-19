package th.in.ffc.airsync.logreader.filter;

import org.junit.Assert;
import org.junit.Test;
import th.in.ffc.airsync.logreader.QueryRecord;

public class CreateHashTest {
    @Test
    public void testNowFilter(){
        CreateHash ch = new CreateHash();
        QueryRecord record = new QueryRecord("INSERT INTO `visit` (`pcucode`,`visitno`,`visitdate`,`pcucodeperson`,`pid`,`flagservice`,`username`,`rightcode`,`rightno`,`hosmain`,`hossub`,`dateupdate`)  VALUES ('07934','188301','2018-02-15','07934','177','02','จตุรงค์','89','R89700026887797','10729','07934',now())",12);
        record.setTime("2018-02-15 10:30:25");
        Assert.assertTrue(ch.process(record).getHash().length()>5);

    }
}
