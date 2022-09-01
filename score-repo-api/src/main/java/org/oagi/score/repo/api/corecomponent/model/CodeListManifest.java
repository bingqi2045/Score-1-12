package org.oagi.score.repo.api.corecomponent.model;

import org.oagi.score.repo.api.corecomponent.CcManifest;

import java.io.Serializable;
import java.math.BigInteger;

public class CodeListManifest implements CcManifest, Serializable {

    private BigInteger CodeListManifestId;

    private String releaseId;

    private BigInteger CodeListId;

    private BigInteger basedCodeListManifestId;

    private boolean conflict;

    private String logId;

    private BigInteger prevBccpManifestId;

    private BigInteger nextBccpManifestId;

    public BigInteger getCodeListManifestId() {
        return CodeListManifestId;
    }

    public void setCodeListManifestId(BigInteger codeListManifestId) {
        CodeListManifestId = codeListManifestId;
    }

    @Override
    public String getReleaseId() {
        return releaseId;
    }

    public void setReleaseId(String releaseId) {
        this.releaseId = releaseId;
    }

    public BigInteger getCodeListId() {
        return CodeListId;
    }

    public void setCodeListId(BigInteger codeListId) {
        CodeListId = codeListId;
    }

    public BigInteger getBasedCodeListManifestId() {
        return basedCodeListManifestId;
    }

    public void setBasedCodeListManifestId(BigInteger basedCodeListManifestId) {
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

    public BigInteger getPrevBccpManifestId() {
        return prevBccpManifestId;
    }

    public void setPrevBccpManifestId(BigInteger prevBccpManifestId) {
        this.prevBccpManifestId = prevBccpManifestId;
    }

    public BigInteger getNextBccpManifestId() {
        return nextBccpManifestId;
    }

    public void setNextBccpManifestId(BigInteger nextBccpManifestId) {
        this.nextBccpManifestId = nextBccpManifestId;
    }

    @Override
    public BigInteger getManifestId() {
        return this.getCodeListManifestId();
    }

    @Override
    public BigInteger getBasedCcId() {
        return this.getBasedCodeListManifestId();
    }
}
