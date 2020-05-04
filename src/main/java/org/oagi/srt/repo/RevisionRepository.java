package org.oagi.srt.repo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.jooq.DSLContext;
import org.jooq.JSON;
import org.jooq.types.UInteger;
import org.jooq.types.ULong;
import org.oagi.srt.data.RevisionAction;
import org.oagi.srt.entity.jooq.tables.records.RevisionRecord;
import org.oagi.srt.gateway.http.api.cc_management.data.CcAction;
import org.oagi.srt.gateway.http.api.revision_management.data.Revision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static org.jooq.impl.DSL.condition;
import static org.oagi.srt.entity.jooq.Tables.APP_USER;
import static org.oagi.srt.entity.jooq.Tables.REVISION;

@Repository
public class RevisionRepository {

    private DSLContext dslContext;

    public RevisionRepository(@Autowired DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    public List<Revision> getRevisionByReference(String reference) {
        if (reference.isEmpty()) {
            return null;
        }

        return dslContext.select(
                REVISION.REVISION_ID,
                REVISION.REVISION_NUM,
                REVISION.REVISION_TRACKING_NUM,
                REVISION.REVISION_ACTION,
                REVISION.SNAPSHOT,
                REVISION.PREV_REVISION_ID,
                REVISION.CREATION_TIMESTAMP.as("timestamp"),
                APP_USER.NAME.as("loginId")
        )
                .from(REVISION)
                .join(APP_USER)
                .on(REVISION.CREATED_BY.eq(APP_USER.APP_USER_ID))
                .where(condition("snapshop->\"$.guid\" = '" + reference + "'"))
                .orderBy(REVISION.REVISION_ID.desc())
                .fetchInto(Revision.class);
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
            if (!String.valueOf(before).equals(String.valueOf(after))) {
                entry.put("before", String.valueOf(before));
                entry.put("after",  String.valueOf(after));
                content.set(key, entry);
            }
        }

        public void setAction(CcAction action) {
            content.put("ActionDescription", action.toString());
        }

        public ULong execute() {
            if (getPrevRevisionId() == null) {
                setRevisionNum(UInteger.valueOf(1));
                setRevisionTrackingNum(UInteger.valueOf(1));
            } else {
                RevisionRecord revisionRecord = getRevisionById(getPrevRevisionId());
                setRevisionNum(revisionRecord.getRevisionNum());
                setRevisionTrackingNum(revisionRecord.getRevisionTrackingNum().add(1));
            }

            return dslContext.insertInto(REVISION)
                    .set(REVISION.SNAPSHOT, content  != null ? JSON.valueOf(content.toString()) : null)
                    .set(REVISION.PREV_REVISION_ID, getPrevRevisionId())
                    .set(REVISION.CREATED_BY, getCreatedBy())
                    .set(REVISION.CREATION_TIMESTAMP, getCreationTimestamp())
                    .set(REVISION.REVISION_ACTION, getRevisionAction().name())
                    .set(REVISION.REVISION_NUM, getRevisionNum())
                    .set(REVISION.REVISION_TRACKING_NUM, getRevisionTrackingNum())
                    .returning().fetchOne().getRevisionId();
        }
    }

    public class UpdateRevisionArguments {
        final JsonNodeFactory nodeFactory = JsonNodeFactory.instance;

        private ObjectNode content;
        private ULong revisionId;
        private String reference;

        UpdateRevisionArguments(ULong revisionId)  {
            RevisionRecord revisionRecord = dslContext
                    .selectFrom(REVISION)
                    .where(REVISION.REVISION_ID.eq(revisionId))
                    .fetchOne();
            this.revisionId = revisionRecord.getRevisionId();
            try {
                this.content = (ObjectNode) new ObjectMapper().readTree(revisionRecord.getSnapshot().toString());
            } catch (JsonProcessingException e) {
                this.content = nodeFactory.objectNode();
            }
        }

        public UpdateRevisionArguments addContent(String key, Object before, Object after) {
            final ObjectNode entry = nodeFactory.objectNode();
            if (String.valueOf(before) != String.valueOf(after)) {
                entry.put("before", String.valueOf(before));
                entry.put("after",  String.valueOf(after));
                content.set(key, entry);
            }
            return this;
        }

        public UpdateRevisionArguments setAction(CcAction action) {
            content.put("ActionDescription", action.toString());
            return this;
        }

        public UpdateRevisionArguments setReference(String reference) {
            this.reference = reference;
            return this;
        }

        public void execute() {
            if (this.revisionId == null) {
                return;
            }

            dslContext.update(REVISION)
                    .set(REVISION.SNAPSHOT, content  != null ? JSON.valueOf(content.toString()) : null)
                    .where(REVISION.REVISION_ID.eq(this.revisionId))
                    .returning().fetchOne();
        }
    }

    public InsertRevisionArguments insertRevisionArguments() {
        return new InsertRevisionArguments();
    }
    public UpdateRevisionArguments updateRevisionArguments(ULong revisionId) {
        return new UpdateRevisionArguments(revisionId);
    }

    public RevisionRecord getRevisionById(ULong revisionId) {
        return dslContext.selectFrom(REVISION).where(REVISION.REVISION_ID.eq(revisionId)).fetchOne();
    }
}
