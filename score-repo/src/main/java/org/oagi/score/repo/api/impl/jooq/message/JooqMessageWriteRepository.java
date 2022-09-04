package org.oagi.score.repo.api.impl.jooq.message;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.score.repo.api.base.ScoreDataAccessException;
import org.oagi.score.repo.api.impl.jooq.JooqScoreRepository;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.MessageRecord;
import org.oagi.score.repo.api.message.MessageWriteRepository;
import org.oagi.score.repo.api.message.model.SendMessageRequest;
import org.oagi.score.repo.api.message.model.SendMessageResponse;
import org.oagi.score.repo.api.user.model.ScoreUser;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.oagi.score.repo.api.impl.jooq.entity.Tables.MESSAGE;

public class JooqMessageWriteRepository
        extends JooqScoreRepository
        implements MessageWriteRepository {

    public JooqMessageWriteRepository(DSLContext dslContext) {
        super(dslContext);
    }

    @Override
    public SendMessageResponse sendMessage(SendMessageRequest request) throws ScoreDataAccessException {
        ScoreUser sender = request.getRequester();
        Map<ScoreUser, String> messageIds = new HashMap();
        for (ScoreUser recipient : request.getRecipients()) {
            MessageRecord message = new MessageRecord();
            message.setSenderId(sender.getUserId());
            message.setRecipientId(recipient.getUserId());
            message.setSubject(request.getSubject());
            message.setBody(request.getBody());
            message.setBodyContentType(request.getBodyContentType());
            message.setIsRead((byte) 0);
            message.setCreationTimestamp(LocalDateTime.now());
            String messageId = dslContext().insertInto(MESSAGE)
                    .set(message)
                    .returning(MESSAGE.MESSAGE_ID)
                    .fetchOne().getMessageId();
            messageIds.put(recipient, messageId);
        }

        return new SendMessageResponse(messageIds);
    }

}
