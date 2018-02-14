import java.util.regex.Matcher;

public class GetTime implements Filter{


   private Filter listener;
    //180212 11:39:52
    private static String temp_time;


    public void setListener(Filter listener) {
        this.listener = listener;
    }


    @Override
    public void process(String line,long ln,String time) {
        Matcher m = Config.timePattern.matcher(line);
        String out="";


        if (m.find( )) {
            temp_time ="20"+ m.group(0).substring(0,2)+"-";
            temp_time += m.group(0).substring(2,4)+"-";
            temp_time += m.group(0).substring(4,6)+" ";
            temp_time += m.group(0).substring(7);
        }
        listener.process(line,ln,temp_time);
    }
}
