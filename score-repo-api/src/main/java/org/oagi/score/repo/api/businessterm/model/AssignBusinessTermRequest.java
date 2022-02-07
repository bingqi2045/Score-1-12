package org.oagi.score.repo.api.businessterm.model;

import org.oagi.score.repo.api.bie.model.Abie;

import java.math.BigInteger;

public class AssignBusinessTermRequest {

    private Abie abie;
    private BigInteger businessTermId;

    public Abie getAbie() {
        return abie;
    }

    public void setAbie(Abie abie) {
        this.abie = abie;
    }

    public BigInteger getBusinessTermId() {
        return businessTermId;
    }

    public void setBusinessTermId(BigInteger businessTermId) {
        this.businessTermId = businessTermId;
    }

    public AssignBusinessTermRequest(Abie abie, BigInteger businessTermId) {
        this.abie = abie;
        this.businessTermId = businessTermId;
    }

    public AssignBusinessTermRequest() {
    }
}
