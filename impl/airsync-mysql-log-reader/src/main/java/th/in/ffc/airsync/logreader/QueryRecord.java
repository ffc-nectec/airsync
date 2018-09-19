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

public class QueryRecord {

    private String log;
    private String hash;
    private long linenumber;
    private String time;

    public QueryRecord(long linenumber, String hash, String time, String log) {
        this.log = log;
        this.hash = hash;
        this.linenumber = linenumber;
        this.time = time;
    }

    public QueryRecord(String log, long linenumber) {
        this(linenumber, "", "", log);
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public long getLinenumber() {
        return linenumber;
    }

    public void setLinenumber(long linenumber) {
        this.linenumber = linenumber;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
