package org.oagi.score.repo.api.businessterm.model;

import org.oagi.score.repo.api.base.Auditable;
import org.oagi.score.repo.api.user.model.ScoreUser;

import java.math.BigInteger;
import java.util.Date;

public class BieToAssign extends Auditable {

    private BigInteger bieId;

    private String bieType;

    public BieToAssign() {
    }

    public BieToAssign(BigInteger bieId, String bieType) {
        this.bieId = bieId;
        this.bieType = bieType;
    }

    public BigInteger getBieId() {
        return bieId;
    }

    public void setBieId(BigInteger bieId) {
        this.bieId = bieId;
    }

    public String getBieType() {
        return bieType;
    }

    public void setBieType(String bieType) {
        this.bieType = bieType;
    }
}
