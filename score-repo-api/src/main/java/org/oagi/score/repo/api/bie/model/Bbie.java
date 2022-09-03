package org.oagi.score.repo.api.bie.model;

import java.math.BigInteger;

public class Bbie implements BieAssociation {

    private String bbieId;

    private String guid;

    private BigInteger basedBccManifestId;

    private String path;

    private String hashPath;

    private String fromAbieId;

    private String toBbiepId;

    private BigInteger bdtPriRestriId;

    private String codeListId;

    private String agencyIdListId;

    private String defaultValue;

    private String fixedValue;

    private int cardinalityMin;

    private int cardinalityMax;

    private boolean nillable;

    private String definition;

    private String remark;

    private String example;

    private boolean used;

    private String ownerTopLevelAsbiepId;

    public String getBbieId() {
        return bbieId;
    }

    public void setBbieId(String bbieId) {
        this.bbieId = bbieId;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public BigInteger getBasedBccManifestId() {
        return basedBccManifestId;
    }

    public void setBasedBccManifestId(BigInteger basedBccManifestId) {
        this.basedBccManifestId = basedBccManifestId;
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

    public String getToBbiepId() {
        return toBbiepId;
    }

    public void setToBbiepId(String toBbiepId) {
        this.toBbiepId = toBbiepId;
    }

    public BigInteger getBdtPriRestriId() {
        return bdtPriRestriId;
    }

    public void setBdtPriRestriId(BigInteger bdtPriRestriId) {
        this.bdtPriRestriId = bdtPriRestriId;
    }

    public String getCodeListId() {
        return codeListId;
    }

    public void setCodeListId(String codeListId) {
        this.codeListId = codeListId;
    }

    public String getAgencyIdListId() {
        return agencyIdListId;
    }

    public void setAgencyIdListId(String agencyIdListId) {
        this.agencyIdListId = agencyIdListId;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getFixedValue() {
        return fixedValue;
    }

    public void setFixedValue(String fixedValue) {
        this.fixedValue = fixedValue;
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

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
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
        return false;
    }

    @Override
    public boolean isBbie() {
        return true;
    }
}
