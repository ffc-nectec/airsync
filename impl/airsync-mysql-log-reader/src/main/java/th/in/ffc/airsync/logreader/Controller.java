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

import th.in.ffc.airsync.logreader.filter.CreateHash;
import th.in.ffc.airsync.logreader.filter.Filters;
import th.in.ffc.airsync.logreader.filter.GetTimeFilter;
import th.in.ffc.airsync.logreader.filter.NowFilter;
import th.in.ffc.airsync.logreader.filter.QueryFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Controller {
    List<Filters> filters = Arrays.asList(
            new GetTimeFilter(Config.timePattern),
            new QueryFilter(Config.logpattern),
            new NowFilter(),
            new CreateHash()
    );
    String logfilepath;
    String csvfilepath;
    boolean realtime;
    CsvLogWriter csvwritemodule;
    private onLogFileException onLogFileExceptionListener;
    private onCsvFileException onCsvFileExceptionListener;

    public Controller(onLogFileException onLogFileExceptionListener, onCsvFileException onCsvFileExceptionListener, String logfilepath, String csvfilepath, boolean realtime) {
        this.onLogFileExceptionListener = onLogFileExceptionListener;
        this.onCsvFileExceptionListener = onCsvFileExceptionListener;
        this.logfilepath = logfilepath;
        this.csvfilepath = csvfilepath;
        this.realtime = realtime;
    }

    public Controller() {
        this(ex -> {
        }, ex -> {
        }, Config.logfilepath, Config.csvfilepath, true);
    }

    public Controller(String logfilepath, String csvfilepath, boolean realtime) {
        this(ex -> {
        }, ex -> {
        }, logfilepath, csvfilepath, realtime);
    }

    public void process() {
        Thread thread = new Thread(() -> {
            processSingle();
        });
        thread.start();

    }

    public void processSingle() {

        try {
            csvwritemodule = new CsvLogWriter(csvfilepath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            ReadTextFile readTextFile = new ReadTextFile(Config.logfilepath);
            readTextFile.setListener(record -> {
                for (Filters filter : filters) {
                    filter.process(record);
                    if (record.getLog().equals("")) break;
                }
                try {
                    if (!record.getLog().equals(""))
                        csvwritemodule.write(record);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            readTextFile.process();
        } catch (IOException e) {
            e.printStackTrace();
            onLogFileExceptionListener.ioException(e);
        }
    }

    interface onLogFileException {
        void ioException(IOException ex);
    }

    interface onCsvFileException {
        void ioException(IOException ex);
    }
}
