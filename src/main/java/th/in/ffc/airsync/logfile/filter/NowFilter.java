package th.in.ffc.airsync.logfile.filter;

import th.in.ffc.airsync.logfile.QueryRecord;

public class NowFilter implements Filters{

    @Override
    public QueryRecord process(QueryRecord record) {
        record.setLog(record.getLog().replaceAll("(now|NOW)\\(\\)","'"+record.getTime()+"'"));
        return record;
    }
}
