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

import java.io.*;

public class LogReaderV1 {
    private long linenumber = 0;
    private BufferedReader bufferReader;
    private LogEvent listener;
    private boolean realtime;
    private long delay;

    LogReaderV1(String fileparth) throws FileNotFoundException {
        this(fileparth, true, 2000);
    }

    public LogReaderV1(String logfile, boolean realtime) throws FileNotFoundException {
        this(logfile, realtime, 2000);
    }

    LogReaderV1(String fileparth, boolean realtime, long delay) throws FileNotFoundException {
        {
            this.realtime = realtime;
            this.delay = delay;
            File textfilepath = new File(fileparth);
            boolean openLog = false;
            // Wait Open Log
            while (!openLog) {
                try {
                    bufferReader = new BufferedReader(new FileReader(textfilepath));
                    openLog = true;
                } catch (java.io.FileNotFoundException ex) {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }


        }
    }

    public void setListener(LogEvent listener) {
        this.listener = listener;
    }

    public void stop() {
        realtime = false;
    }

    public void process() throws IOException {
        String line;
        do {
            while ((line = bufferReader.readLine()) != null) {
                listener.process(new QueryRecord(line, linenumber++));
            }
            try {
                if (realtime) Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } while (realtime);
        bufferReader.close();
    }

    interface LogEvent {
        void process(QueryRecord record);
    }
}
