package th.in.ffc.airsync.logfile;

import java.io.*;

public class ReadTextFile {
    ReadTextFile(String fileparth) throws FileNotFoundException {
        this(fileparth,true,2000);
    }
    public ReadTextFile(String logfile, boolean realtime) throws FileNotFoundException {
        this(logfile,realtime,2000);
    }
    ReadTextFile(String fileparth, boolean realtime,long delay) throws FileNotFoundException {
        {
            this.realtime =realtime;
            textfilepath = new File(fileparth);
            bufferReader = new BufferedReader(new FileReader(textfilepath));
        }
    }

    interface LogEvent{
        void process(QueryRecord record) throws IOException;
    }
    public long linenumber = 0;
    File textfilepath;
    BufferedReader bufferReader;
    private LogEvent listener;
    private boolean realtime;
    private long delay;

    public void setListener(LogEvent listener) {
        this.listener = listener;
    }

    public void stop(){
        realtime=false;
    }

    public void process() throws IOException {
        String line;
            do
            {
                while ((line = bufferReader.readLine()) != null) {
                    listener.process(new QueryRecord(line,linenumber++));
                }
                try {
                    if(realtime)Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }while(realtime);
        bufferReader.close();
    }
}
