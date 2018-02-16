package th.in.ffc.airsync.logfile;

import java.io.IOException;

public class Controller {
    private ReadLog readlogmodule;
    private CsvLogWriter csvwritemodule;

    interface onLogFileException {
        void ioException(IOException ex);
    }
    interface onCsvFileException{
        void ioException(IOException ex);
    }

    private onLogFileException onLogFileExceptionListener;
    private onCsvFileException onCsvFileExceptionListener;

    Controller(onLogFileException onLogFileExceptionListener, onCsvFileException onCsvFileExceptionListener,String logfilepath,String csvfilepath,boolean realtime) {
        this.onLogFileExceptionListener = onLogFileExceptionListener;
        this.onCsvFileExceptionListener = onCsvFileExceptionListener;
        readlogmodule = new ReadLog(logfilepath,realtime);
        try {
            csvwritemodule = new CsvLogWriter(csvfilepath);
        } catch (IOException e) {
            e.printStackTrace();
            onCsvFileExceptionListener.ioException(e);
        }
        readlogmodule.setListener(record -> {

            try {
                csvwritemodule.write(record);
            } catch (IOException e) {
                e.printStackTrace();
                onCsvFileExceptionListener.ioException(e);
            }

        });
    }

    Controller() {
        this(ex -> {},ex -> {},Config.logfilepath,Config.csvfilepath,true);
    }
    Controller(String logfilepath,String csvfilepath,boolean realtime){
        this(ex -> {},ex -> {},logfilepath,csvfilepath,realtime);
    }



    public void process(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    readlogmodule.run();
                } catch (IOException e) {
                    e.printStackTrace();
                    onLogFileExceptionListener.ioException(e);
                }
            }
        });
        thread.start();

    }
}
