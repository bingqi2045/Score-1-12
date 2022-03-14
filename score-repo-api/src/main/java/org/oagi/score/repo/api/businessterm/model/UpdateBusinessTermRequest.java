package org.oagi.score.repo.api.businessterm.model;

import org.oagi.score.repo.api.base.Request;
import org.oagi.score.repo.api.businesscontext.model.BusinessContextValue;
import org.oagi.score.repo.api.user.model.ScoreUser;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;

public class UpdateBusinessTermRequest extends Request {

    private BigInteger businessTermId;

    private String businessTerm;

    private String definition;

    private String externalReferenceUri;

    private String externalReferenceId;

    public UpdateBusinessTermRequest(ScoreUser requester) {
        super(requester);
    }

    public BigInteger getBusinessTermId() {
        return businessTermId;
    }

    public void setBusinessTermId(BigInteger businessTermId) {
        this.businessTermId = businessTermId;
    }

    public UpdateBusinessTermRequest withBusinessTermId(BigInteger businessTermId) {
        this.setBusinessTermId(businessTermId);
        return this;
    }

    public String getBusinessTerm() {
        return businessTerm;
    }

    public void setBusinessTerm(String businessTerm) {
        this.businessTerm = businessTerm;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getExternalReferenceUri() {
        return externalReferenceUri;
    }

    public void setExternalReferenceUri(String externalReferenceUri) {
        this.externalReferenceUri = externalReferenceUri;
    }

    public String getExternalReferenceId() {
        return externalReferenceId;
    }

    public void setExternalReferenceId(String externalReferenceId) {
        this.externalReferenceId = externalReferenceId;
    }
}
