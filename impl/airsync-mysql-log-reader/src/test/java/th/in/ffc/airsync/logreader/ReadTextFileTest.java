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

package th.in.ffc.airsync.logreader;

import org.junit.Assert;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicReference;

public class ReadTextFileTest {

    public void read() throws Exception {
        LogReaderV1 readLogFile;
        String logfilepart = "src/test/resources/ReadTextFileRT.txt";
        AtomicReference<QueryRecord> recordTest = new AtomicReference<>();
        PrintWriter writer = null;
        writer = new PrintWriter(logfilepart, "UTF-8");
        readLogFile = new LogReaderV1(logfilepart, true, 100);
        readLogFile.setListener(record -> {
            //System.out.println(record.getLog());
            recordTest.set(record);
        });
        new Thread(() -> {

            try {
                readLogFile.process();
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
