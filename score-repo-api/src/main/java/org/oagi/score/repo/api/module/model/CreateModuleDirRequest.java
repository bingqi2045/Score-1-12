package org.oagi.score.repo.api.module.model;

import org.oagi.score.repo.api.base.Request;
import org.oagi.score.repo.api.user.model.ScoreUser;

import java.math.BigInteger;

public class CreateModuleDirRequest extends Request {

    private BigInteger parentModuleDirId;

    private String name;

    public CreateModuleDirRequest(ScoreUser requester) {
        super(requester);
    }

    public BigInteger getParentModuleDirId() {
        return parentModuleDirId;
    }

    public void setParentModuleDirId(BigInteger parentModuleDirId) {
        this.parentModuleDirId = parentModuleDirId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
