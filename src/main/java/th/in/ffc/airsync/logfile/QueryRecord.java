package th.in.ffc.airsync.logfile;

public class QueryRecord {
    private String log;
    private String hash;
    private long linenumber;
    private String time;


    public QueryRecord(long linenumber,String hash,String time,String log) {
        this.log = log;
        this.hash = hash;
        this.linenumber = linenumber;
        this.time = time;
    }

    public QueryRecord(String log, long linenumber) {
        this(linenumber,"","",log);
    }

    public void setLog(String log) {
        this.log = log;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void setLinenumber(long linenumber) {
        this.linenumber = linenumber;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLog() {
        return log;
    }
    public String getHash() {
        return hash;
    }

    public long getLinenumber() {
        return linenumber;
    }

    public String getTime() {
        return time;
    }
}
