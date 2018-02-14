import org.apache.commons.codec.digest.DigestUtils;

public class CreateHash implements Filter{

    interface onCreateHash{
        void process(String line, long linenumber, String time,String hash);
    }
    private onCreateHash listener;

    public void setListener(onCreateHash listener) {
        this.listener = listener;
    }

    @Override
    public void process(String line, long linenumber, String time) {
        listener.process(line,linenumber,time, DigestUtils.sha1Hex(line+time+linenumber));
    }
}
