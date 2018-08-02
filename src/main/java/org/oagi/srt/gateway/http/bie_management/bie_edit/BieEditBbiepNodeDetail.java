package org.oagi.srt.gateway.http.bie_management.bie_edit;

public class BieEditBbiepNodeDetail extends BieEditBbiepNode {

    private int cardinalityMin;
    private int cardinalityMax;
    private boolean nillable;
    private String fixedValue;
    private String bizTerm;
    private String remark;
    private long bdtId;

    private String contextDefinition;
    private String associationDefinition;
    private String componentDefinition;

    public int getCardinalityMin() {
        return cardinalityMin;
    }

    public void setCardinalityMin(int cardinalityMin) {
        this.cardinalityMin = cardinalityMin;
    }

    public int getCardinalityMax() {
        return cardinalityMax;
    }

    public void setCardinalityMax(int cardinalityMax) {
        this.cardinalityMax = cardinalityMax;
    }

    public boolean isNillable() {
        return nillable;
    }

    public void setNillable(boolean nillable) {
        this.nillable = nillable;
    }

    public String getFixedValue() {
        return fixedValue;
    }

    public void setFixedValue(String fixedValue) {
        this.fixedValue = fixedValue;
    }

    public String getBizTerm() {
        return bizTerm;
    }

    public void setBizTerm(String bizTerm) {
        this.bizTerm = bizTerm;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public long getBdtId() {
        return bdtId;
    }

    public void setBdtId(long bdtId) {
        this.bdtId = bdtId;
    }

    public String getContextDefinition() {
        return contextDefinition;
    }

    public void setContextDefinition(String contextDefinition) {
        this.contextDefinition = contextDefinition;
    }

    public String getAssociationDefinition() {
        return associationDefinition;
    }

    public void setAssociationDefinition(String associationDefinition) {
        this.associationDefinition = associationDefinition;
    }

    public String getComponentDefinition() {
        return componentDefinition;
    }

    public void setComponentDefinition(String componentDefinition) {
        this.componentDefinition = componentDefinition;
    }

    public BieEditBbiepNodeDetail append(BieEditBbiepNode bbiepNode) {

        this.setTopLevelAbieId(bbiepNode.getTopLevelAbieId());
        this.setReleaseId(bbiepNode.getReleaseId());
        this.setType(bbiepNode.getType());
        this.setName(bbiepNode.getName());
        this.setUsed(bbiepNode.isUsed());
        this.setHasChild(bbiepNode.isHasChild());

        this.setBbieId(bbiepNode.getBbieId());
        this.setBccId(bbiepNode.getBccId());
        this.setBbiepId(bbiepNode.getBbiepId());
        this.setBccpId(bbiepNode.getBccpId());
        this.setAttribute(bbiepNode.isAttribute());

        return this;
    }
}
