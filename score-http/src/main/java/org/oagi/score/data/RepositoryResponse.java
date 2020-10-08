package org.oagi.score.data;

public class RepositoryResponse {

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
