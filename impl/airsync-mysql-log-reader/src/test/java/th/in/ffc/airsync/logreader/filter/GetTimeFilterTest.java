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
import th.in.ffc.airsync.logreader.Config;
import th.in.ffc.airsync.logreader.QueryRecord;

public class GetTimeFilterTest {

    public void testGetTime() {
        GetTimeFilter gt = new GetTimeFilter(Config.timePattern);
        Assert.assertEquals("", "");
        Assert.assertEquals(gt.process(new QueryRecord("180215 10:30:25\t      1 Query       SELECT * FROM person  WHERE pid = '177' and pcucodeperson ='07934'", 12)).getTime(), "2018-02-15 10:30:25");
    }
}
