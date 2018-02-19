package th.in.ffc.airsync.logreader.filter;

import org.apache.commons.codec.digest.DigestUtils;
import th.in.ffc.airsync.logreader.QueryRecord;

public class CreateHash implements Filters {

    @Override
    public QueryRecord process(QueryRecord record) {
        record.setHash(DigestUtils.sha1Hex(record.getLog()+ record.getTime()+ record.getLinenumber()));
        return record;
    }
}
