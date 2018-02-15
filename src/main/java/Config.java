import java.util.regex.Pattern;

public class Config {

    public static final Pattern logpattern = Pattern.compile("( {7}|\\t)(UPDATE|INSERT|DELETE|CREATE|ALTER|insert|update|delete|create|alter) ");
    public static final String logpath = "C:\\Program Files\\JHCIS\\MySQL\\data\\jlog.log";
    public static final Pattern timePattern = Pattern.compile("(^\\d{6} .{8})");


    private Config(){

    }
}
