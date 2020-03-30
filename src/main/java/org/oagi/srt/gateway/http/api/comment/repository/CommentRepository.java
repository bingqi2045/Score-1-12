package org.oagi.srt.gateway.http.api.comment.repository;

import lombok.Data;
import org.jooq.DSLContext;
import org.jooq.UpdateSetMoreStep;
import org.jooq.tools.StringUtils;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.tables.records.CommentRecord;
import org.oagi.srt.gateway.http.api.comment.data.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.oagi.srt.entity.jooq.Tables.APP_USER;
import static org.oagi.srt.entity.jooq.Tables.COMMENT;

@Repository
public class CommentRepository {

    @Autowired
    private DSLContext dslContext;

    public List<Comment> getCommentsByReference(String reference) {
        return dslContext.select(
                COMMENT.COMMENT_ID, APP_USER.LOGIN_ID, COMMENT.COMMENT_,
                COMMENT.LAST_UPDATE_TIMESTAMP,
                COMMENT.IS_HIDDEN, COMMENT.PREV_COMMENT_ID)
                .from(COMMENT)
                .join(APP_USER).on(COMMENT.CREATED_BY.eq(APP_USER.APP_USER_ID))
                .where(COMMENT.REFERENCE.eq(reference))
                .orderBy(COMMENT.LAST_UPDATE_TIMESTAMP.asc())
                .fetchStream()
                .map(e -> {
                    Comment comment = new Comment();
                    comment.setCommentId(e.get(COMMENT.COMMENT_ID).longValue());
                    comment.setLoginId(e.get(APP_USER.LOGIN_ID));
                    comment.setText(e.get(COMMENT.COMMENT_));
                    comment.setTimestamp(e.get(COMMENT.LAST_UPDATE_TIMESTAMP));
                    comment.setHidden(e.get(COMMENT.IS_HIDDEN) == (byte) 1);
                    ULong prevCommentId = e.get(COMMENT.PREV_COMMENT_ID);
                    if (prevCommentId != null) {
                        comment.setPrevCommentId(prevCommentId.longValue());
                    }
                    return comment;
                })
                .collect(Collectors.toList());
    }

    @Data
    public class InsertCommentArguments {

        private String reference;
        private String text;
        private ULong prevCommentId;
        private ULong createdBy;

        public InsertCommentArguments setReference(String reference) {
            this.reference = reference;
            return this;
        }

        public InsertCommentArguments setText(String text) {
            this.text = text;
            return this;
        }

        public InsertCommentArguments setPrevCommentId(Long prevCommentId) {
            if (prevCommentId == null || prevCommentId <= 0L) {
                return this;
            }
            return setPrevCommentId(ULong.valueOf(prevCommentId));
        }

        public InsertCommentArguments setPrevCommentId(ULong prevCommentId) {
            this.prevCommentId = prevCommentId;
            return this;
        }

        public InsertCommentArguments setCreatedBy(long createdBy) {
            return setCreatedBy(ULong.valueOf(createdBy));
        }

        public InsertCommentArguments setCreatedBy(ULong createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public void execute() {
            executeInsertComment(this);
        }
    }

    public InsertCommentArguments insertComment() {
        return new InsertCommentArguments();
    }

    private void executeInsertComment(InsertCommentArguments arguments) {
        CommentRecord record = new CommentRecord();
        LocalDateTime timestamp = LocalDateTime.now();

        record.setReference(arguments.getReference());
        record.setComment(arguments.getText());
        record.setIsHidden((byte) 0);
        if (arguments.getPrevCommentId() != null) {
            record.setPrevCommentId(arguments.getPrevCommentId());
        }
        record.setCreatedBy(arguments.getCreatedBy());
        record.setCreationTimestamp(timestamp);
        record.setLastUpdateTimestamp(timestamp);

        dslContext.insertInto(COMMENT)
                .set(record)
                .execute();
    }

    public Long getOwnerIdByCommentId(long commentId) {
        return dslContext.select(COMMENT.CREATED_BY)
                .from(COMMENT)
                .where(COMMENT.COMMENT_ID.eq(ULong.valueOf(commentId)))
                .fetchOptionalInto(Long.class).orElse(null);
    }

    @Data
    public class UpdateCommentArguments {

        private final ULong userId;
        private ULong commentId;

        private String text;
        private Boolean hide;
        private Boolean delete;

        public UpdateCommentArguments(long userId) {
            this(ULong.valueOf(userId));
        }

        public UpdateCommentArguments(ULong userId) {
            this.userId = userId;
        }

        public UpdateCommentArguments setCommentId(Long commentId) {
            if (commentId == null || commentId <= 0L) {
                return this;
            }
            return setCommentId(ULong.valueOf(commentId));
        }

        public UpdateCommentArguments setCommentId(ULong commentId) {
            this.commentId = commentId;
            return this;
        }

        public UpdateCommentArguments setText(String text) {
            this.text = text;
            return this;
        }

        public UpdateCommentArguments setHide(Boolean hide) {
            if (hide != null) {
                this.hide = hide;
            }
            return this;
        }

        public UpdateCommentArguments setDelete(Boolean delete) {
            if (delete != null) {
                this.delete = delete;
            }
            return this;
        }

        public void execute() {
            if (isDirty()) {
                executeUpdateComment(this);
            }
        }

        private boolean isDirty() {
            return (this.text != null || this.hide != null || this.delete != null);
        }
    }

    public UpdateCommentArguments updateComment(long userId) {
        return new UpdateCommentArguments(userId);
    }

    private void executeUpdateComment(UpdateCommentArguments arguments) {
        LocalDateTime timestamp = LocalDateTime.now();

        UpdateSetMoreStep<CommentRecord> step = dslContext.update(COMMENT)
                .set(COMMENT.LAST_UPDATE_TIMESTAMP, timestamp);

        String text = arguments.getText();
        if (text != null) {
            if (StringUtils.isEmpty(text)) {
                step = step.setNull(COMMENT.COMMENT_);
            } else {
                step = step.set(COMMENT.COMMENT_, text);
            }
        }

        if (arguments.getHide() != null) {
            step = step.set(COMMENT.IS_HIDDEN, (byte) (arguments.getHide() ? 1 : 0));
        }

        if (arguments.getDelete() != null) {
            step = step.set(COMMENT.IS_DELETED, (byte) (arguments.getDelete() ? 1 : 0));
        }

        step.execute();
    }

}
