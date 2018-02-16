package th.in.ffc.airsync.logfile.filter;

import th.in.ffc.airsync.logfile.QueryRecord;

public interface Filters {
    QueryRecord process(QueryRecord record);
}
