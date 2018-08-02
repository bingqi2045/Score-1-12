package org.oagi.srt.gateway.http.bie_management.bie_edit;

public class BieEditAsbiepNodeDetail extends BieEditAsbiepNode {

    private int cardinalityMin;
    private int cardinalityMax;
    private boolean nillable;
    private String bizTerm;
    private String remark;

    private String contextDefinition;
    private String associationDefinition;
    private String componentDefinition;
    private String typeDefinition;

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

    public String getTypeDefinition() {
        return typeDefinition;
    }

    public void setTypeDefinition(String typeDefinition) {
        this.typeDefinition = typeDefinition;
    }

    public BieEditAsbiepNodeDetail append(BieEditAsbiepNode asbiepNode) {

        this.setTopLevelAbieId(asbiepNode.getTopLevelAbieId());
        this.setReleaseId(asbiepNode.getReleaseId());
        this.setType(asbiepNode.getType());
        this.setName(asbiepNode.getName());
        this.setUsed(asbiepNode.isUsed());
        this.setHasChild(asbiepNode.isHasChild());

        this.setAsbieId(asbiepNode.getAsbieId());
        this.setAsccId(asbiepNode.getAsccId());
        this.setAsbiepId(asbiepNode.getAsbiepId());
        this.setAsccpId(asbiepNode.getAsccpId());
        this.setAbieId(asbiepNode.getAbieId());
        this.setAccId(asbiepNode.getAccId());

        return this;
    }
}
