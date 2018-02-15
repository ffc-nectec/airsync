import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ReadLog{
    public long ln = 0;
    File file;
    BufferedReader br;
    private Filter listener;
    private boolean alloop;

    public void setListener(Filter listener) {
        this.listener = listener;
    }

    ReadLog(){
        this(Config.path);
    }
    ReadLog(String fileparth){
           this(fileparth,true);
    }

    ReadLog(String fileparth,boolean alwalloop){
        {
            this.alloop=alwalloop;
            file  = new File(Config.path);
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
            while(alloop)
            {
                while ((line = br.readLine()) != null) {
                    listener.process(line,ln++,"");
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
