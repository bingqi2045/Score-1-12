package org.oagi.srt.gateway.http.bie_management.bie_edit;

public class BieEditBbieScNodeDetail extends BieEditBbieScNode {

    private int cardinalityMin;
    private int cardinalityMax;
    private String fixedValue;
    private String defaultValue;
    private String bizTerm;
    private String remark;

    private String contextDefinition;
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

    public String getFixedValue() {
        return fixedValue;
    }

    public void setFixedValue(String fixedValue) {
        this.fixedValue = fixedValue;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
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

    public String getContextDefinition() {
        return contextDefinition;
    }

    public void setContextDefinition(String contextDefinition) {
        this.contextDefinition = contextDefinition;
    }

    public String getComponentDefinition() {
        return componentDefinition;
    }

    public void setComponentDefinition(String componentDefinition) {
        this.componentDefinition = componentDefinition;
    }

    public BieEditBbieScNodeDetail append(BieEditBbieScNode bbieScNode) {

        this.setTopLevelAbieId(bbieScNode.getTopLevelAbieId());
        this.setReleaseId(bbieScNode.getReleaseId());
        this.setType(bbieScNode.getType());
        this.setName(bbieScNode.getName());
        this.setUsed(bbieScNode.isUsed());
        this.setHasChild(bbieScNode.isHasChild());

        this.setBbieScId(bbieScNode.getBbieScId());
        this.setDtScId(bbieScNode.getDtScId());

        return this;
    }
}
