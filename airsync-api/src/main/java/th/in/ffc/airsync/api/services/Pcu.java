package th.in.ffc.airsync.api.services;

import javax.xml.bind.annotation.XmlRootElement;

public class Pcu {

    private String pcuCode;
    private String pcuName;
    private String pcuUuid;


    public Pcu(String pcuName, String pcuUuid) {
        this.pcuName = pcuName;
        this.pcuUuid = pcuUuid;
    }

    public Pcu(String pcuCode, String pcuName, String pcuUuid) {
        this.pcuCode = pcuCode;
        this.pcuName = pcuName;
        this.pcuUuid = pcuUuid;
    }

    public String getPcuCode() {
        return pcuCode;
    }

    public void setPcuCode(String pcuCode) {
        this.pcuCode = pcuCode;
    }

    public String getPcuName() {
        return pcuName;
    }

    public void setPcuName(String pcuName) {
        this.pcuName = pcuName;
    }

    public String getPcuUuid() {
        return pcuUuid;
    }

    public void setPcuUuid(String pcuUuid) {
        this.pcuUuid = pcuUuid;
    }
}
