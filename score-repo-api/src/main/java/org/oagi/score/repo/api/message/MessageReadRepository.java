package org.oagi.score.repo.api.message;

import org.oagi.score.repo.api.base.ScoreDataAccessException;
import org.oagi.score.repo.api.message.model.GetMessageRequest;
import org.oagi.score.repo.api.message.model.GetMessageResponse;

public interface MessageReadRepository {

    GetMessageResponse getMessage(
            GetMessageRequest request) throws ScoreDataAccessException;

}
