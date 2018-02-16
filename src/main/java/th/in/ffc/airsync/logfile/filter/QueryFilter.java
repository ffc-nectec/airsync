package th.in.ffc.airsync.logfile.filter;

import th.in.ffc.airsync.logfile.QueryRecord;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryFilter implements  Filters{

    private Pattern logpattern;

    public QueryFilter(Pattern logpattern) {
        this.logpattern = logpattern;
    }

    @Override
    public QueryRecord process(QueryRecord record) {
        Matcher matcher = logpattern.matcher(record.getLog());
        if (matcher.find( )) {
            record.setLog(record.getLog().replaceFirst("^.*Query( {7}|\\t)",""));
        }else {
            record.setLog("");
        }
        return record;
    }
}
