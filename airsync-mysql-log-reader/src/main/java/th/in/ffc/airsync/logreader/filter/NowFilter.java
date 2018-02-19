package th.in.ffc.airsync.logreader.filter;

import th.in.ffc.airsync.logreader.QueryRecord;

public class NowFilter implements Filters{

    @Override
    public QueryRecord process(QueryRecord record) {
        record.setLog(record.getLog().replaceAll("(now|NOW)\\(\\)","'"+record.getTime()+"'"));
        return record;
    }
}
