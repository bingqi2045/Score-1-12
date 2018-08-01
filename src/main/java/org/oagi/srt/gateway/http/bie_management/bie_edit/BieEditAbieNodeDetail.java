package org.oagi.srt.gateway.http.bie_management.bie_edit;

public class BieEditAbieNodeDetail extends BieEditAbieNode {
    private String version;
    private String status;
    private String remark;
    private String bizTerm;
    private String definition;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getBizTerm() {
        return bizTerm;
    }

    public void setBizTerm(String bizTerm) {
        this.bizTerm = bizTerm;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public BieEditAbieNodeDetail append(BieEditAbieNode abieNode) {

        this.setTopLevelAbieId(abieNode.getTopLevelAbieId());
        this.setReleaseId(abieNode.getReleaseId());
        this.setType(abieNode.getType());
        this.setName(abieNode.getName());
        this.setUsed(abieNode.isUsed());
        this.setHasChild(abieNode.isHasChild());

        this.setAsbiepId(abieNode.getAsbiepId());
        this.setAbieId(abieNode.getAbieId());
        this.setAsccpId(abieNode.getAsccpId());
        this.setAccId(abieNode.getAccId());

        return this;
    }
}
