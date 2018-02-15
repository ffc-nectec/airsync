public class NowFilter{


    public String process(String line, String time) {
        return(line.replaceAll("(now|NOW)\\(\\)","'"+time+"'"));
    }
}
