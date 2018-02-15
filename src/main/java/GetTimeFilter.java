import java.util.regex.Matcher;

public class GetTimeFilter implements Filters{


    private static String time;

    @Override
    public String process(String line) {
        Matcher m = Config.timePattern.matcher(line);
        String out="";


        if (m.find( )) {
            time ="20"+ m.group(0).substring(0,2)+"-";
            time += m.group(0).substring(2,4)+"-";
            time += m.group(0).substring(4,6)+" ";
            time += m.group(0).substring(7);
        }
        return time;
    }
}
