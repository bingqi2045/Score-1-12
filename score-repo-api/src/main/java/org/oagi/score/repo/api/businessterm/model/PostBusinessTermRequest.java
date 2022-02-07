package org.oagi.score.repo.api.businessterm.model;

import org.oagi.score.repo.api.base.Auditable;
import org.oagi.score.repo.api.user.model.ScoreUser;

import java.math.BigInteger;
import java.util.Date;

public class PostBusinessTermRequest extends Auditable {

    private BusinessTerm businessTerm;

    public BusinessTerm getBusinessTerm() {
        return businessTerm;
    }

    public void setBusinessTerm(BusinessTerm businessTerm) {
        this.businessTerm = businessTerm;
    }

    public PostBusinessTermRequest(BusinessTerm businessTerm) {
        this.businessTerm = businessTerm;
    }

    public PostBusinessTermRequest() {
    }
}
