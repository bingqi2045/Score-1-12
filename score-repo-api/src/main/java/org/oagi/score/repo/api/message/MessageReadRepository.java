package org.oagi.score.repo.api.message;

import org.oagi.score.repo.api.base.ScoreDataAccessException;
import org.oagi.score.repo.api.message.model.GetCountOfUnreadMessagesRequest;
import org.oagi.score.repo.api.message.model.GetCountOfUnreadMessagesResponse;
import org.oagi.score.repo.api.message.model.GetMessageRequest;
import org.oagi.score.repo.api.message.model.GetMessageResponse;

public interface MessageReadRepository {

    GetCountOfUnreadMessagesResponse getCountOfUnreadMessages(
            GetCountOfUnreadMessagesRequest request) throws ScoreDataAccessException;

    GetMessageResponse getMessage(
            GetMessageRequest request) throws ScoreDataAccessException;

}
