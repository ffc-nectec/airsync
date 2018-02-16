package th.in.ffc.airsync.logfile;

import th.in.ffc.airsync.logfile.filter.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ReadLog {

    List<Filters> filters = Arrays.asList(
            new GetTimeFilter(Config.timePattern),
            new QueryFilter(Config.logpattern),
            new NowFilter(),
            new CreateHash()
    );

    interface OnReciveLog{
        void onReciveLog(QueryRecord record);
    }

    private OnReciveLog listener;

    public void setListener(OnReciveLog listener) {
        this.listener = listener;
    }

    private String logfile;
    private boolean realtime;
    private long delay;

    /**
     * สร้างโดยการระบุพารามิเตอร์
     * @param logfile ตำแหน่งของไฟล์ log mysql
     * @param realtime กำหนดเป็น true เมื่อต้องการให้อ่านตลอดเวลาอัพเดทเวลามีข้อมูลใหม่ ถ้ากำหนดเป็น false เมื่อต้องการอ่านรอบเดียวจบ
     */
    public ReadLog(String logfile, boolean realtime,long delay) {
        this.logfile = logfile;
        this.realtime = realtime;
        this.delay=delay;
    }
    public ReadLog(String logfile, boolean realtime){
        this(logfile,realtime,1500);
    }

    public void run() throws IOException {
        ReadTextFile readTextFile = new ReadTextFile(logfile, realtime,delay);
        readTextFile.setListener(record -> {
                for(Filters filter : filters){
                    filter.process(record);
                    if(record.getLog().equals(""))return;
                }
                listener.onReciveLog(record);
            });
        readTextFile.process();
    }
}
