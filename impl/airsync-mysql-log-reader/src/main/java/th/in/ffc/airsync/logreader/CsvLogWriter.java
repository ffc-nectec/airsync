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

import com.csvreader.CsvWriter;

import java.io.FileWriter;
import java.io.IOException;

public class CsvLogWriter {
    private static final char SEPARATOR = ',';
    private CsvWriter csvOutput;

    CsvLogWriter(String csvfilepath) throws IOException {
        csvOutput = new CsvWriter(new FileWriter(csvfilepath, false), SEPARATOR);
        csvOutput.write("linenumber");
        csvOutput.write("hash");
        csvOutput.write("query");
        csvOutput.endRecord();

    }

    public void write(QueryRecord record) throws IOException {
        csvOutput.write(record.getLinenumber() + "");
        csvOutput.write(record.getHash());
        csvOutput.write(record.getLog());
        csvOutput.endRecord();
        csvOutput.flush();
    }
}
