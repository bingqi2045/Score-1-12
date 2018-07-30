package org.oagi.srt.gateway.http.bie_management;

public class BieEditNode {

    private String type;
    private long bieId;
    private long ccId;
    private String name;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getBieId() {
        return bieId;
    }

    public void setBieId(long bieId) {
        this.bieId = bieId;
    }

    public long getCcId() {
        return ccId;
    }

    public void setCcId(long ccId) {
        this.ccId = ccId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
