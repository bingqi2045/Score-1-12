package org.oagi.score.repo.api.businessterm.model;

import org.oagi.score.repo.api.base.Auditable;
import org.oagi.score.repo.api.user.model.ScoreUser;

import java.math.BigInteger;
import java.util.Date;
import java.util.Objects;

public class BieToAssign extends Auditable {

    private String bieId;

    private String bieType;

    public BieToAssign() {
    }

    public BieToAssign(String bieId, String bieType) {
        this.bieId = bieId;
        this.bieType = bieType;
    }

    public String getBieId() {
        return bieId;
    }

    public void setBieId(String bieId) {
        this.bieId = bieId;
    }

    public String getBieType() {
        return bieType;
    }

    public void setBieType(String bieType) {
        this.bieType = bieType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BieToAssign that = (BieToAssign) o;
        return bieId == that.bieId && bieType.equals(that.bieType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bieId, bieType);
    }

    @Override
    public String toString() {
        return "BieToAssign{" +
                "bieId=" + bieId +
                ", bieType='" + bieType + '\'' +
                '}';
    }
}
