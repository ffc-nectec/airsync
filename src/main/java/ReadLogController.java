public class ReadLogController {

    interface OnReciveLog{
        void onReciveLog(QueryRecord record);
    }

    private OnReciveLog listener;

    public void setListener(OnReciveLog listener) {
        this.listener = listener;
    }

    private String parthfile;
    private boolean readmode;

    ReadLogController(String parthfile, boolean readmode) {
        this.parthfile = parthfile;
        this.readmode = readmode;
    }

    ReadLogController(){
        this(Config.logpath,true);
    }

    public void run(){

        CreateHash ch= new CreateHash();
        NowFilter rn = new NowFilter();
        QueryFilter qt = new QueryFilter();
        GetTimeFilter gt = new GetTimeFilter();
        ReadTextFile rl = new ReadTextFile(parthfile,readmode);

        rl.setListener(new ReadTextFile.LogEvent() {
            @Override
            public void process(String line, long linenumber) {
                //System.out.println(line);
                String time=gt.process(line);
                line=qt.process(line);
                if(!line.equals("")){
                    line=rn.process(line,time);
                    String hash=ch.process(line,linenumber,time);
                    QueryRecord record=new QueryRecord(linenumber,hash,time,line);

                }
            }
        } );
        rl.process();

    }
}
