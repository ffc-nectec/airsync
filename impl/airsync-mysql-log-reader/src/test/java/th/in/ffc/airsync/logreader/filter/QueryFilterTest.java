/*
 * Copyright (c) 2018 NECTEC
 *   National Electronics and Computer Technology Center, Thailand
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package th.in.ffc.airsync.logreader.filter;

import org.junit.Assert;
import org.junit.Test;
import th.in.ffc.airsync.logreader.Config;
import th.in.ffc.airsync.logreader.QueryRecord;

public class QueryFilterTest {

    @Test
    public void testQueryFilter() {
        QueryFilter qt = new QueryFilter(Config.logpattern);
        Assert.assertEquals(qt.process(new QueryRecord("\t\t      1 Query       SHOW KEYS FROM `visit` FROM `jhcisdb`", 12)).getLog(), "");
        Assert.assertEquals(qt.process(new QueryRecord("\t\t      4 Query       UPDATE `visit` SET `flagservice`='02'  WHERE `pcucode`='07934' AND `visitno`='188301'", 12)).getLog(), "UPDATE `visit` SET `flagservice`='02'  WHERE `pcucode`='07934' AND `visitno`='188301'");
        Assert.assertEquals(qt.process(new QueryRecord("\t\t      1 Query       update visit set bmilevel ='3' where pcucode ='07934' AND visitno ='188301'", 12)).getLog(), "update visit set bmilevel ='3' where pcucode ='07934' AND visitno ='188301'");
        Assert.assertEquals(qt.process(new QueryRecord("\t\t     12 Query       delete from _tmp_dmlap", 12)).getLog(), "delete from _tmp_dmlap");
        //มี Now
        Assert.assertEquals(qt.process(new QueryRecord("\t\t      1 Query       INSERT INTO `visit` (`pcucode`,`visitno`,`visitdate`,`pcucodeperson`,`pid`,`flagservice`,`username`,`rightcode`,`rightno`,`hosmain`,`hossub`,`dateupdate`)  VALUES ('07934','188301','2018-02-15','07934','177','02','จตุรงค์','89','R89700026887797','10729','07934',now())", 12)).getLog(), "INSERT INTO `visit` (`pcucode`,`visitno`,`visitdate`,`pcucodeperson`,`pid`,`flagservice`,`username`,`rightcode`,`rightno`,`hosmain`,`hossub`,`dateupdate`)  VALUES ('07934','188301','2018-02-15','07934','177','02','จตุรงค์','89','R89700026887797','10729','07934',now())");
    }
}
