package th.in.ffc.airsync.logfile;

import com.csvreader.CsvWriter;

import java.io.FileWriter;
import java.io.IOException;

public class CsvLogWriter {
    public static final char SEPARATOR = ',';
    private CsvWriter csvOutput;


    public CsvLogWriter(String csvfilepath) throws IOException {

            csvOutput = new CsvWriter(new FileWriter(csvfilepath, false), SEPARATOR);
            csvOutput.write("linenumber");
            csvOutput.write("hash");
            csvOutput.write("query");
            csvOutput.endRecord();

    }
    public CsvLogWriter() throws IOException {
        this(Config.csvfilepath);
    }
    public void write(QueryRecord record) throws IOException {

            csvOutput.write(record.getLinenumber()+"");
            csvOutput.write(record.getHash());
            csvOutput.write(record.getLog());
            csvOutput.endRecord();
            csvOutput.flush();


    }
}
