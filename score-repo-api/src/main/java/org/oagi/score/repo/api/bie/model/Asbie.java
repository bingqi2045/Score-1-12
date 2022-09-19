package org.oagi.score.repo.api.bie.model;

public class Asbie implements BieAssociation {

    private String asbieId;

    private String guid;

    private String basedAsccManifestId;

    private String path;

    private String hashPath;

    private String fromAbieId;

    private String toAsbiepId;

    private int cardinalityMin;

    private int cardinalityMax;

    private boolean nillable;

    private String definition;

    private String remark;

    private boolean used;

    private String ownerTopLevelAsbiepId;

    public String getAsbieId() {
        return asbieId;
    }

    public void setAsbieId(String asbieId) {
        this.asbieId = asbieId;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getBasedAsccManifestId() {
        return basedAsccManifestId;
    }

    public void setBasedAsccManifestId(String basedAsccManifestId) {
        this.basedAsccManifestId = basedAsccManifestId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getHashPath() {
        return hashPath;
    }

    public void setHashPath(String hashPath) {
        this.hashPath = hashPath;
    }

    public String getFromAbieId() {
        return fromAbieId;
    }

    public void setFromAbieId(String fromAbieId) {
        this.fromAbieId = fromAbieId;
    }

    public String getToAsbiepId() {
        return toAsbiepId;
    }

    public void setToAsbiepId(String toAsbiepId) {
        this.toAsbiepId = toAsbiepId;
    }

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

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public String getOwnerTopLevelAsbiepId() {
        return ownerTopLevelAsbiepId;
    }

    public void setOwnerTopLevelAsbiepId(String ownerTopLevelAsbiepId) {
        this.ownerTopLevelAsbiepId = ownerTopLevelAsbiepId;
    }

    @Override
    public boolean isAsbie() {
        return true;
    }

    @Override
    public boolean isBbie() {
        return false;
    }
}
