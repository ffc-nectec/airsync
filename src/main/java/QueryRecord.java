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
