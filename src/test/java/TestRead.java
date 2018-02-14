import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestRead {

    @Test
    public void testRead(){



        CreateHash ch= new CreateHash();
        ch.setListener(new CreateHash.onCreateHash() {
            @Override
            public void process(String line, long linenumber, String time, String hash) {
                System.out.println(hash+"\t"+time+"\t"+linenumber+"\t"+line);
            }
        });


        RepeatNow rn = new RepeatNow();
        rn.setListener(new Filter() {
            @Override
            public void process(String line, long linenumber, String time) {
                //System.out.println(time+"\t"+linenumber+"\t"+line);
                ch.process(line,linenumber,time);
            }
        });


        QueryFilter qt = new QueryFilter();
        qt.setListener(new Filter() {
            @Override
            public void process(String line, long linenumber, String time) {
                rn.process(line,linenumber,time);
                //System.out.println(time+"\t"+linenumber+"\t"+line);
            }
        });


        GetTime gt = new GetTime();
        gt.setListener(new Filter() {
            @Override
            public void process(String line, long linenumber, String time) {
                //System.out.println(time+"\t"+linenumber+"\t"+line);
                qt.process(line,linenumber,time);

            }
        });



        ReadLog rl = new ReadLog();
        rl.setListener(new Filter() {
            @Override
            public void process(String line,long ln,String time) {
                //System.out.println(line);
                gt.process(line,ln,time);
            }
        });
        rl.process();
    }




}
