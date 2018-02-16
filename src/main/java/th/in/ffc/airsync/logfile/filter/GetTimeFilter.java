package th.in.ffc.airsync.logfile.filter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import th.in.ffc.airsync.logfile.QueryRecord;

public class GetTimeFilter implements Filters{

    private Pattern timePattern;

    private static String time;

    public GetTimeFilter(Pattern timePattern) {
        this.timePattern = timePattern;
    }

    @Override
    public QueryRecord process(QueryRecord record) {
        Matcher matcher = timePattern.matcher(record.getLog());
        if (matcher.find( )) {
            time ="20"+ matcher.group(0).substring(0,2)+"-";
            time += matcher.group(0).substring(2,4)+"-";
            time += matcher.group(0).substring(4,6)+" ";
            time += matcher.group(0).substring(7);
        }
        record.setTime(time);
        return record;
    }
}
