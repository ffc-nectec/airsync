import java.util.regex.Matcher;

public class QueryFilter implements  Filters{

    @Override
    public String process(String line) {
        Matcher m = Config.logpattern.matcher(line);
        if (m.find( )) {
            return(line.replaceFirst("^.*Query( {7}|\\t)",""));
        }return "";
    }
}
