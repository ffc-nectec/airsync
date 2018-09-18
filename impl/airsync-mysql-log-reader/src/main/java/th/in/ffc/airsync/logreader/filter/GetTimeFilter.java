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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import th.in.ffc.airsync.logreader.QueryRecord;

public class GetTimeFilter implements Filters {
    private static String time;
    private Pattern timePattern;

    public GetTimeFilter(Pattern timePattern) {
        this.timePattern = timePattern;
    }

    @Override
    public QueryRecord process(QueryRecord record) {
        Matcher matcher = timePattern.matcher(record.getLog());
        if (matcher.find()) {
            time = "20" + matcher.group(0).substring(0, 2) + "-";
            time += matcher.group(0).substring(2, 4) + "-";
            time += matcher.group(0).substring(4, 6) + " ";
            time += matcher.group(0).substring(7);
        }
        record.setTime(time);
        return record;
    }
}
