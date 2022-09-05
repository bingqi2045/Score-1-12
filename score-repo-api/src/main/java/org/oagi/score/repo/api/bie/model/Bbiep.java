package org.oagi.score.repo.api.bie.model;

import org.oagi.score.repo.api.base.Auditable;

import java.math.BigInteger;

public class Bbiep extends Auditable {

    private String bbiepId;

    private String guid;

    private String basedBccpManifestId;

    private String path;

    private String hashPath;

    private String definition;

    private String remark;

    private String bizTerm;

    private String ownerTopLevelAsbiepId;

    public String getBbiepId() {
        return bbiepId;
    }

    public void setBbiepId(String bbiepId) {
        this.bbiepId = bbiepId;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getBasedBccpManifestId() {
        return basedBccpManifestId;
    }

    public void setBasedBccpManifestId(String basedBccpManifestId) {
        this.basedBccpManifestId = basedBccpManifestId;
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
