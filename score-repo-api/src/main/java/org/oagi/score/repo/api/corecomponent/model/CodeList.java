package org.oagi.score.repo.api.corecomponent.model;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Objects;

public class CodeList implements CoreComponent, Serializable {

    private String codeListId;

    private String guid;

    private String name;

    private String versionId;

    private String basedCodeListId;

    public String getBasedCodeListId() {
        return basedCodeListId;
    }

    public void setBasedCodeListId(String basedCodeListId) {
        this.basedCodeListId = basedCodeListId;
    }

    public String getListId() {
        return listId;
    }

    public void setListId(String listId) {
        this.listId = listId;
    }

    private String listId;

    private String agencyIdListValueId;

    public String getAgencyName() {
        return agencyName;
    }

    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }

    private String agencyName;

    private String prevCodeListId;

    private String nextCodeListId;

    public String getCodeListId() {
        return codeListId;
    }

    public void setCodeListId(String codeListId) {
        this.codeListId = codeListId;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public String getAgencyIdListValueId() {
        return agencyIdListValueId;
    }

    public void setAgencyIdListValueId(String agencyIdListValueId) {
        this.agencyIdListValueId = agencyIdListValueId;
    }

    public String getPrevCodeListId() {
        return prevCodeListId;
    }

    public void setPrevCodeListId(String prevCodeListId) {
        this.prevCodeListId = prevCodeListId;
    }

    public String getNextCodeListId() {
        return nextCodeListId;
    }

    public void setNextCodeListId(String nextCodeListId) {
        this.nextCodeListId = nextCodeListId;
    }

    @Override
    public String getId() {
        return getCodeListId();
    }

    @Override
    public String getGuid() {
        return guid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CodeList that = (CodeList) o;
        return Objects.equals(codeListId, that.codeListId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codeListId);
    }
}
