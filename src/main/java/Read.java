import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;



public class Read {
    public long init_linenumber = 0;
    public long ln = 0;

    File file;
    BufferedReader br;

    Read (long init_linenumber, File file){
        this.file=file;


    }
    Read(){
        file    = new File(Config.path);
        try{
            br = new BufferedReader(new FileReader(file));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public void read() {
        try {

            String line;
            String temp;
            String time;
            while(true)
            {
                while ((line = br.readLine()) != null) {
                    time=Markline.getTime(line);

                    temp=(ln++)+time+line;
                    //System.out.println(time+"\t"+line);

                    if(Markline.readActivity(line)){
                        line=Markline.repletNow(line,time);
                        System.out.println(Markline.createHash(temp)+"\t"+ln+"\t"+time+"\t"+Markline.readQuery(line));
                    }
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

    public void close(){
        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }





}
