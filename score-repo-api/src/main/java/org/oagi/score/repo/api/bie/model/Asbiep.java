package org.oagi.score.repo.api.bie.model;

import org.oagi.score.repo.api.base.Auditable;

import java.math.BigInteger;

public class Asbiep extends Auditable {

    private String asbiepId;

    private String guid;

    private BigInteger basedAsccpManifestId;

    private String path;

    private String hashPath;

    private String roleOfAbieId;

    private String definition;

    private String remark;

    private String bizTerm;

    private String ownerTopLevelAsbiepId;

    public String getAsbiepId() {
        return asbiepId;
    }

    public void setAsbiepId(String asbiepId) {
        this.asbiepId = asbiepId;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public BigInteger getBasedAsccpManifestId() {
        return basedAsccpManifestId;
    }

    public void setBasedAsccpManifestId(BigInteger basedAsccpManifestId) {
        this.basedAsccpManifestId = basedAsccpManifestId;
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

    public String getRoleOfAbieId() {
        return roleOfAbieId;
    }

    public void setRoleOfAbieId(String roleOfAbieId) {
        this.roleOfAbieId = roleOfAbieId;
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

    public String getBizTerm() {
        return bizTerm;
    }

    public void setBizTerm(String bizTerm) {
        this.bizTerm = bizTerm;
    }

    public String getOwnerTopLevelAsbiepId() {
        return ownerTopLevelAsbiepId;
    }

    public void setOwnerTopLevelAsbiepId(String ownerTopLevelAsbiepId) {
        this.ownerTopLevelAsbiepId = ownerTopLevelAsbiepId;
    }
}
