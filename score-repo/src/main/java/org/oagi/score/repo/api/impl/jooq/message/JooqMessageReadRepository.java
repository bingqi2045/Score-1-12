package org.oagi.score.repo.api.impl.jooq.message;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectJoinStep;
import org.jooq.types.ULong;
import org.oagi.score.repo.api.base.ScoreDataAccessException;
import org.oagi.score.repo.api.impl.jooq.JooqScoreRepository;
import org.oagi.score.repo.api.message.MessageReadRepository;
import org.oagi.score.repo.api.message.model.GetCountOfUnreadMessagesRequest;
import org.oagi.score.repo.api.message.model.GetCountOfUnreadMessagesResponse;
import org.oagi.score.repo.api.message.model.GetMessageRequest;
import org.oagi.score.repo.api.message.model.GetMessageResponse;
import org.oagi.score.repo.api.user.model.ScoreUser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.oagi.score.repo.api.impl.jooq.entity.Tables.APP_USER;
import static org.oagi.score.repo.api.impl.jooq.entity.Tables.MESSAGE;
import static org.oagi.score.repo.api.user.model.ScoreRole.DEVELOPER;
import static org.oagi.score.repo.api.user.model.ScoreRole.END_USER;

public class JooqMessageReadRepository
        extends JooqScoreRepository
        implements MessageReadRepository {

    public JooqMessageReadRepository(DSLContext dslContext) {
        super(dslContext);
    }

    @Override
    public GetMessageResponse getMessage(GetMessageRequest request) throws ScoreDataAccessException {
        ScoreUser requester = request.getRequester();
        Record message = dslContext().select(MESSAGE.RECIPIENT_ID,
                MESSAGE.SUBJECT, MESSAGE.BODY, MESSAGE.BODY_CONTENT_TYPE,
                MESSAGE.CREATION_TIMESTAMP, MESSAGE.IS_READ,
                APP_USER.APP_USER_ID, APP_USER.LOGIN_ID, APP_USER.IS_DEVELOPER)
                .from(MESSAGE)
                .join(APP_USER).on(MESSAGE.SENDER_ID.eq(APP_USER.APP_USER_ID))
                .where(MESSAGE.MESSAGE_ID.eq(ULong.valueOf(request.getMessageId())))
                .fetchOptional().orElse(null);
        if (message == null) {
            throw new ScoreDataAccessException("Message with ID: '" + request.getMessageId() + "' does not exist.");
        }
        if (!message.get(MESSAGE.RECIPIENT_ID).equals(ULong.valueOf(requester.getUserId()))) {
            throw new ScoreDataAccessException("You do not have a permission to access this message.");
        }

        // Mark as read
        if (message.get(MESSAGE.IS_READ) == (byte) 0) {
            dslContext().update(MESSAGE)
                    .set(MESSAGE.IS_READ, (byte) 1)
                    .where(MESSAGE.MESSAGE_ID.eq(ULong.valueOf(request.getMessageId())))
                    .execute();
        }

        return new GetMessageResponse(new ScoreUser(
                message.get(APP_USER.APP_USER_ID).toBigInteger(),
                message.get(APP_USER.LOGIN_ID),
                (byte) 1 == message.get(APP_USER.IS_DEVELOPER) ? DEVELOPER : END_USER),
                message.get(MESSAGE.SUBJECT),
                message.get(MESSAGE.BODY), message.get(MESSAGE.BODY_CONTENT_TYPE),
                message.get(MESSAGE.CREATION_TIMESTAMP));
    }

    @Override
    public GetCountOfUnreadMessagesResponse getCountOfUnreadMessages(
            GetCountOfUnreadMessagesRequest request) throws ScoreDataAccessException {
        ScoreUser requester = request.getRequester();
        List<Condition> conds = new ArrayList();
        conds.add(MESSAGE.RECIPIENT_ID.eq(ULong.valueOf(requester.getUserId())));
        conds.add(MESSAGE.IS_READ.eq((byte) 0));

        List<ULong> senderUserIds =
                request.getSenders().stream().map(e -> ULong.valueOf(e.getUserId())).collect(Collectors.toList());
        if (!senderUserIds.isEmpty()) {
            conds.add(senderUserIds.size() == 1 ?
                    MESSAGE.SENDER_ID.eq(senderUserIds.get(0)) :
                    MESSAGE.SENDER_ID.in(senderUserIds));
        }

        int countOfUnreadMessages = dslContext().selectCount()
                .from(MESSAGE)
                .where(conds)
                .fetchOptionalInto(Integer.class).orElse(0);
        return new GetCountOfUnreadMessagesResponse(countOfUnreadMessages);
    }

}
