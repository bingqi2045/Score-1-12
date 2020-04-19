package org.oagi.srt.repo;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.jooq.DSLContext;
import org.jooq.JSON;
import org.jooq.types.UInteger;
import org.jooq.types.ULong;
import org.oagi.srt.data.RevisionAction;
import org.oagi.srt.entity.jooq.tables.records.RevisionRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

import static org.oagi.srt.entity.jooq.Tables.REVISION;

@Repository
public class RevisionRepository {

    private DSLContext dslContext;

    public RevisionRepository(@Autowired DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    public class InsertRevisionArguments {
        final JsonNodeFactory nodeFactory = JsonNodeFactory.instance;

        private final ObjectNode content = nodeFactory.objectNode();
        private ULong revisionId;
        private UInteger revisionNum;
        private UInteger revisionTrackingNum;
        private RevisionAction revisionAction;
        private String reference;
        private ULong prevRevisionId;
        private ULong createdBy;
        private LocalDateTime creationTimestamp;

        public ULong getRevisionId() {
            return revisionId;
        }

        public InsertRevisionArguments setRevisionId(ULong revisionId) {
            this.revisionId = revisionId;
            return this;
        }

        public UInteger getRevisionNum() {
            return revisionNum;
        }

        public InsertRevisionArguments setRevisionNum(UInteger revisionNum) {
            this.revisionNum = revisionNum;
            return this;
        }

        public UInteger getRevisionTrackingNum() {
            return revisionTrackingNum;
        }

        public InsertRevisionArguments setRevisionTrackingNum(UInteger revisionTrackingNum) {
            this.revisionTrackingNum = revisionTrackingNum;
            return this;
        }

        public RevisionAction getRevisionAction() {
            return revisionAction;
        }

        public InsertRevisionArguments setRevisionAction(RevisionAction revisionAction) {
            this.revisionAction = revisionAction;
            return this;
        }

        public String getReference() {
            return reference;
        }

        public InsertRevisionArguments setReference(String reference) {
            this.reference = reference;
            return this;
        }

        public ULong getPrevRevisionId() {
            return prevRevisionId;
        }

        public InsertRevisionArguments setPrevRevisionId(ULong prevRevisionId) {
            this.prevRevisionId = prevRevisionId;
            return this;
        }

        public ULong getCreatedBy() {
            return createdBy;
        }

        public InsertRevisionArguments setCreatedBy(ULong createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public LocalDateTime getCreationTimestamp() {
            return creationTimestamp;
        }

        public InsertRevisionArguments setCreationTimestamp(LocalDateTime creationTimestamp) {
            this.creationTimestamp = creationTimestamp;
            return this;
        }

        public void addContent(String key, Object before, Object after) {
            final ObjectNode entry = nodeFactory.objectNode();
            entry.put("before", String.valueOf(before));
            entry.put("after",  String.valueOf(after));
            content.set(key, entry);
        }

        public ULong execute() {
            if (getPrevRevisionId() != null) {
                setRevisionNum(UInteger.valueOf(1));
                setRevisionTrackingNum(UInteger.valueOf(1));
            } else {
                RevisionRecord revisionRecord = getRevisionById(getPrevRevisionId());
                setRevisionNum(revisionRecord.getRevisionNum());
                setRevisionTrackingNum(revisionRecord.getRevisionTrackingNum().add(1));
            }

            return dslContext.insertInto(REVISION)
                    .set(REVISION.BODY, content  != null ? JSON.valueOf(content.toString()) : null)
                    .set(REVISION.PREV_REVISION_ID, getPrevRevisionId())
                    .set(REVISION.CREATED_BY, getCreatedBy())
                    .set(REVISION.REFERENCE, getReference())
                    .set(REVISION.CREATION_TIMESTAMP, getCreationTimestamp())
                    .set(REVISION.REVISION_ACTION, getRevisionAction().name())
                    .set(REVISION.REVISION_NUM, getRevisionNum())
                    .set(REVISION.REVISION_TRACKING_NUM, getRevisionTrackingNum())
                    .returning().fetchOne().getRevisionId();
        }
    }

    public InsertRevisionArguments insertRevisionArguments() {
        return new InsertRevisionArguments();
    }

    public RevisionRecord getRevisionById(ULong revisionId) {
        return dslContext.selectFrom(REVISION).where(REVISION.REVISION_ID.eq(revisionId)).fetchOne();
    }
}
