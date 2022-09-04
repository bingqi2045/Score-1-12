package org.oagi.score.repo.api.businessterm.model;

import org.oagi.score.repo.api.base.Auditable;

import java.math.BigInteger;

public class CreateBusinessTermResponse extends Auditable {

    private final String businessTermId;

    public CreateBusinessTermResponse(String businessTermId) {
        this.businessTermId = businessTermId;
    }

    public String getBusinessTermId() {
        return businessTermId;
    }

}
