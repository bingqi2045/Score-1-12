package org.oagi.score.repo.api.message.model;

import org.oagi.score.repo.api.base.Request;
import org.oagi.score.repo.api.user.model.ScoreUser;

public class GetMessageRequest extends Request {

    private final String messageId;

    public GetMessageRequest(ScoreUser requester, String messageId) {
        super(requester);
        this.messageId = messageId;
    }

    public String getMessageId() {
        return messageId;
    }
}
