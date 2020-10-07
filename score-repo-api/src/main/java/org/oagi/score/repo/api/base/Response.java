package org.oagi.score.repo.api.base;

import java.io.Serializable;

public class Response implements Serializable {

    private boolean succeed;

    private String message;

    public void setSucceed(boolean succeed) {
        this.succeed = succeed;
    }

    public void setFailure(boolean failure) {
        setSucceed(!failure);
    }

    public boolean isSucceed() {
        return succeed;
    }

    public void setFailureWithMessage(String message) {
        this.setFailure(true);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
