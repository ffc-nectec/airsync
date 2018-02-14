import org.apache.commons.codec.digest.DigestUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Markline {


    public static boolean readActivity(String line){

        Matcher m = Config.pattern.matcher(line);
        if (m.find( )) {
            return true;
        }else {
            return false;
        }
    }

    public static String readQuery(String line){
        return line.replaceFirst("^.*Query( {7}|\\t)","");
    }


    //180212 11:39:52
    private static String temp_time;

    public static String getTime(String line){

        Matcher m = Config.timePattern.matcher(line);
        String out="";


        if (m.find( )) {
            temp_time ="20"+ m.group(0).substring(0,2)+"-";
            temp_time += m.group(0).substring(2,4)+"-";
            temp_time += m.group(0).substring(4,6)+" ";
            temp_time += m.group(0).substring(7);
        }
        out=temp_time;

        return out;
    }

    public static String repletNow(String line,String time){
        return line.replaceAll("(now|NOW)\\(\\)","'"+time+"'");
    }

    public static String createHash(String data){
        return DigestUtils.sha1Hex(data);
    }
}
