import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ReadTextFile {
    interface LogEvent{
        void process(String line,long linenumber);
    }
    public long ln = 0;
    File file;
    BufferedReader br;
    private LogEvent listener;
    private boolean runmode;


    public void setListener(LogEvent listener) {
        this.listener = listener;
    }

    ReadTextFile(){
        this(Config.logpath);
    }
    ReadTextFile(String fileparth){
           this(fileparth,true);
    }

    ReadTextFile(String fileparth, boolean alwalloop){
        {
            this.runmode =alwalloop;
            file  = new File(Config.logpath);
            try{
                br = new BufferedReader(new FileReader(file));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void process() {
        String line;
        try {
            while(runmode)
            {
                while ((line = br.readLine()) != null) {
                    listener.process(line,ln++);
                }
                Thread.sleep(2000);

            }
            //br.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
