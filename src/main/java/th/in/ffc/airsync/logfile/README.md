# Use ReadLog

```java
public class Example {
    public void example(){
        ReadLogController ct = new ReadLogController();
        ct.setListener(record -> {
            System.out.print(record.getLinenumber()+"\t");
            System.out.print(record.getHash()+"\t");
            System.out.print(record.getLog()+"\t");
            System.out.print(record.getTime());
            System.out.println();
        });
        ct.run();
    }
}
```  

วิธีใช้ตัวอ่านไฟล์ CSV  

```java
public class Example2 {

    public void example{
        ToCSV tcv = new ToCSV();
        tcv.process();
    }
}
```
