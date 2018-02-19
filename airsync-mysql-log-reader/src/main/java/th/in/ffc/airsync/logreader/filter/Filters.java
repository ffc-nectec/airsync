package th.in.ffc.airsync.logreader.filter;

import th.in.ffc.airsync.logreader.QueryRecord;

public interface Filters {
    QueryRecord process(QueryRecord record);
}
