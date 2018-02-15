import org.apache.commons.codec.digest.DigestUtils;

public class CreateHash {

    public String process(String line, long linenumber, String time) {
        return(DigestUtils.sha1Hex(line+time+linenumber));
    }
}
