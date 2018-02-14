import java.util.regex.Matcher;

public class QueryFilter implements  Filter{


    private Filter listener;

    public void setListener(Filter listener) {
        this.listener = listener;
    }


    @Override
    public void process(String line, long linenumber, String time) {
        Matcher m = Config.pattern.matcher(line);
        if (m.find( )) {
            listener.process(line.replaceFirst("^.*Query( {7}|\\t)",""),linenumber,time);
        }
    }
}
