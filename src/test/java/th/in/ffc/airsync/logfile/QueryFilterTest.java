package th.in.ffc.airsync.logfile;

import th.in.ffc.airsync.logfile.QueryRecord;
import th.in.ffc.airsync.logfile.filter.QueryFilter;
import org.junit.Assert;
import org.junit.Test;

public class QueryFilterTest {
    @Test
    public void testQueryFilter(){
        QueryFilter qt = new QueryFilter(Config.logpattern);
        Assert.assertEquals(qt.process(new QueryRecord("\t\t      1 Query       SHOW KEYS FROM `visit` FROM `jhcisdb`",12)).getLog(),"");
        Assert.assertEquals(qt.process(new QueryRecord("\t\t      4 Query       UPDATE `visit` SET `flagservice`='02'  WHERE `pcucode`='07934' AND `visitno`='188301'",12)).getLog(),"UPDATE `visit` SET `flagservice`='02'  WHERE `pcucode`='07934' AND `visitno`='188301'");
        Assert.assertEquals(qt.process(new QueryRecord("\t\t      1 Query       update visit set bmilevel ='3' where pcucode ='07934' AND visitno ='188301'",12)).getLog(),"update visit set bmilevel ='3' where pcucode ='07934' AND visitno ='188301'");
        Assert.assertEquals(qt.process(new QueryRecord("\t\t     12 Query       delete from _tmp_dmlap",12)).getLog(),"delete from _tmp_dmlap");
        //มี Now
        Assert.assertEquals(qt.process(new QueryRecord("\t\t      1 Query       INSERT INTO `visit` (`pcucode`,`visitno`,`visitdate`,`pcucodeperson`,`pid`,`flagservice`,`username`,`rightcode`,`rightno`,`hosmain`,`hossub`,`dateupdate`)  VALUES ('07934','188301','2018-02-15','07934','177','02','จตุรงค์','89','R89700026887797','10729','07934',now())",12)).getLog(),"INSERT INTO `visit` (`pcucode`,`visitno`,`visitdate`,`pcucodeperson`,`pid`,`flagservice`,`username`,`rightcode`,`rightno`,`hosmain`,`hossub`,`dateupdate`)  VALUES ('07934','188301','2018-02-15','07934','177','02','จตุรงค์','89','R89700026887797','10729','07934',now())");
    }
}
