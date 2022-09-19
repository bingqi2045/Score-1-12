package org.oagi.score.repo.api.corecomponent.model;

import org.oagi.score.repo.api.corecomponent.CcManifest;

import java.io.Serializable;

public class CodeListManifest implements CcManifest, Serializable {

    private String codeListManifestId;

    private String releaseId;

    private String codeListId;

    private String basedCodeListManifestId;

    private boolean conflict;

    private String logId;

    private String prevBccpManifestId;

    private String nextBccpManifestId;

    public String getCodeListManifestId() {
        return codeListManifestId;
    }

    public void setCodeListManifestId(String codeListManifestId) {
        this.codeListManifestId = codeListManifestId;
    }

    @Override
    public String getReleaseId() {
        return releaseId;
    }

    public void setReleaseId(String releaseId) {
        this.releaseId = releaseId;
    }

    public String getCodeListId() {
        return this.codeListId;
    }

    public void setCodeListId(String codeListId) {
        this.codeListId = codeListId;
    }

    public String getBasedCodeListManifestId() {
        return basedCodeListManifestId;
    }

    public void setBasedCodeListManifestId(String basedCodeListManifestId) {
        this.basedCodeListManifestId = basedCodeListManifestId;
    }

    public boolean isConflict() {
        return conflict;
    }

    public void setConflict(boolean conflict) {
        this.conflict = conflict;
    }

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public String getPrevBccpManifestId() {
        return prevBccpManifestId;
    }

    public void setPrevBccpManifestId(String prevBccpManifestId) {
        this.prevBccpManifestId = prevBccpManifestId;
    }

    public String getNextBccpManifestId() {
        return nextBccpManifestId;
    }

    public void setNextBccpManifestId(String nextBccpManifestId) {
        this.nextBccpManifestId = nextBccpManifestId;
    }

    @Override
    public String getManifestId() {
        return this.getCodeListManifestId();
    }

    @Override
    public String getBasedCcId() {
        return getCodeListId();
    }
}
