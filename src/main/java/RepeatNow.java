public class RepeatNow implements Filter {
    private Filter listener;

    public void setListener(Filter listener) {
        this.listener = listener;
    }

    @Override
    public void process(String line, long linenumber, String time) {
        listener.process(line.replaceAll("(now|NOW)\\(\\)","'"+time+"'"),linenumber,time);
    }
}
