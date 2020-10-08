package org.oagi.score.repo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.JSON;
import org.jooq.types.UInteger;
import org.jooq.types.ULong;
import org.oagi.score.data.LogAction;
import org.oagi.score.gateway.http.api.cc_management.data.CcAction;
import org.oagi.score.gateway.http.api.common.data.PageRequest;
import org.oagi.score.gateway.http.api.common.data.PageResponse;
import org.oagi.score.gateway.http.api.log_management.data.Log;
import org.oagi.score.gateway.http.api.log_management.data.LogListRequest;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.*;
import org.oagi.score.repo.domain.LogSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.oagi.score.gateway.http.api.log_management.helper.LogUtils.generateHash;
import static org.oagi.score.repo.api.impl.jooq.entity.Tables.*;

@Repository
public class LogRepository {

    private DSLContext dslContext;

    private LogSerializer serializer;

    public LogRepository(@Autowired DSLContext dslContext,
                         @Autowired LogSerializer serializer) {
        this.dslContext = dslContext;
        this.serializer = serializer;
    }

    public PageResponse<Log> getLogByReference(LogListRequest request) {
        if (request.getReference().isEmpty()) {
            return null;
        }

        PageRequest pageRequest = request.getPageRequest();
        PageResponse response = new PageResponse<Log>();
        List<Condition> conditions = new ArrayList();
        conditions.add(LOG.REFERENCE.eq(request.getReference()));

        int length = dslContext.selectCount()
                .from(LOG)
                .where(conditions)
                .fetchOptionalInto(Integer.class).orElse(0);

        List<Log> list = dslContext.select(
                LOG.LOG_ID,
                LOG.HASH,
                LOG.REVISION_NUM,
                LOG.REVISION_TRACKING_NUM,
                LOG.LOG_ACTION,
                LOG.PREV_LOG_ID,
                LOG.CREATION_TIMESTAMP.as("timestamp"),
                APP_USER.LOGIN_ID.as("loginId")
        )
                .from(LOG)
                .join(APP_USER)
                .on(LOG.CREATED_BY.eq(APP_USER.APP_USER_ID))
                .where(conditions)
                .orderBy(LOG.LOG_ID.desc())
                .limit(pageRequest.getOffset(), pageRequest.getPageSize())
                .fetchInto(Log.class);

        response.setList(list);
        response.setPage(pageRequest.getPageIndex());
        response.setSize(pageRequest.getPageSize());
        response.setLength(length);

        return response;
    }

    public String getSnapshotById(AuthenticatedPrincipal user, BigInteger logId) {
        if (logId == null || logId.longValue() <= 0L) {
            return "{}";
        }

        return serializer.deserialize(
                dslContext.select(LOG.SNAPSHOT)
                        .from(LOG)
                        .where(LOG.LOG_ID.eq(ULong.valueOf(logId)))
                        .fetchOptionalInto(String.class).orElse(null)
        ).toString();
    }

    public class InsertLogArguments {
        final JsonNodeFactory nodeFactory = JsonNodeFactory.instance;

        private final ObjectNode content = nodeFactory.objectNode();
        private ULong logId;
        private UInteger logNum;
        private UInteger logTrackingNum;
        private LogAction logAction;
        private String reference;
        private ULong prevLogId;
        private ULong createdBy;
        private LocalDateTime creationTimestamp;

        public ULong getLogId() {
            return logId;
        }

        public InsertLogArguments setLogId(ULong logId) {
            this.logId = logId;
            return this;
        }

        public UInteger getRevisionNum() {
            return logNum;
        }

        public InsertLogArguments setRevisionNum(UInteger logNum) {
            this.logNum = logNum;
            return this;
        }

        public UInteger getRevisionTrackingNum() {
            return logTrackingNum;
        }

        public InsertLogArguments setRevisionTrackingNum(UInteger logTrackingNum) {
            this.logTrackingNum = logTrackingNum;
            return this;
        }

        public LogAction getLogAction() {
            return logAction;
        }

        public InsertLogArguments setLogAction(LogAction logAction) {
            this.logAction = logAction;
            return this;
        }

        public String getReference() {
            return reference;
        }

        public InsertLogArguments setReference(String reference) {
            this.reference = reference;
            return this;
        }

        public ULong getPrevLogId() {
            return prevLogId;
        }

        public InsertLogArguments setPrevLogId(ULong prevLogId) {
            this.prevLogId = prevLogId;
            return this;
        }

        public ULong getCreatedBy() {
            return createdBy;
        }

        public InsertLogArguments setCreatedBy(ULong createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public LocalDateTime getCreationTimestamp() {
            return creationTimestamp;
        }

        public InsertLogArguments setCreationTimestamp(LocalDateTime creationTimestamp) {
            this.creationTimestamp = creationTimestamp;
            return this;
        }

        public void addContent(String key, Object before, Object after) {
            final ObjectNode entry = nodeFactory.objectNode();
            if (!String.valueOf(before).equals(String.valueOf(after))) {
                entry.put("before", String.valueOf(before));
                entry.put("after", String.valueOf(after));
                content.set(key, entry);
            }
        }

        public void setAction(CcAction action) {
            content.put("ActionDescription", action.toString());
        }

        public ULong execute() {
            if (getPrevLogId() == null) {
                setRevisionNum(UInteger.valueOf(1));
                setRevisionTrackingNum(UInteger.valueOf(1));
            } else {
                LogRecord logRecord = getLogById(getPrevLogId());
                setRevisionNum(logRecord.getRevisionNum());
                setRevisionTrackingNum(logRecord.getRevisionTrackingNum().add(1));
            }

            return dslContext.insertInto(LOG)
                    .set(LOG.SNAPSHOT, content != null ? JSON.valueOf(content.toString()) : null)
                    .set(LOG.PREV_LOG_ID, getPrevLogId())
                    .set(LOG.CREATED_BY, getCreatedBy())
                    .set(LOG.CREATION_TIMESTAMP, getCreationTimestamp())
                    .set(LOG.LOG_ACTION, getLogAction().name())
                    .set(LOG.REVISION_NUM, getRevisionNum())
                    .set(LOG.REVISION_TRACKING_NUM, getRevisionTrackingNum())
                    .returning().fetchOne().getLogId();
        }
    }

    public class UpdateLogArguments {
        final JsonNodeFactory nodeFactory = JsonNodeFactory.instance;

        private ObjectNode content;
        private ULong logId;
        private String reference;

        UpdateLogArguments(ULong logId) {
            LogRecord logRecord = dslContext
                    .selectFrom(LOG)
                    .where(LOG.LOG_ID.eq(logId))
                    .fetchOne();
            this.logId = logRecord.getLogId();
            try {
                this.content = (ObjectNode) new ObjectMapper().readTree(logRecord.getSnapshot().toString());
            } catch (JsonProcessingException e) {
                this.content = nodeFactory.objectNode();
            }
        }

        public UpdateLogArguments addContent(String key, Object before, Object after) {
            final ObjectNode entry = nodeFactory.objectNode();
            if (String.valueOf(before) != String.valueOf(after)) {
                entry.put("before", String.valueOf(before));
                entry.put("after", String.valueOf(after));
                content.set(key, entry);
            }
            return this;
        }

        public UpdateLogArguments setAction(CcAction action) {
            content.put("ActionDescription", action.toString());
            return this;
        }

        public UpdateLogArguments setReference(String reference) {
            this.reference = reference;
            return this;
        }

        public void execute() {
            if (this.logId == null) {
                return;
            }

            dslContext.update(LOG)
                    .set(LOG.SNAPSHOT, content != null ? JSON.valueOf(content.toString()) : null)
                    .where(LOG.LOG_ID.eq(this.logId))
                    .returning().fetchOne();
        }
    }

    public InsertLogArguments insertLogArguments() {
        return new InsertLogArguments();
    }

    public UpdateLogArguments updateLogArguments(ULong logId) {
        return new UpdateLogArguments(logId);
    }

    public LogRecord getLogById(ULong logId) {
        return dslContext.selectFrom(LOG).where(LOG.LOG_ID.eq(logId)).fetchOne();
    }

    /*
     * Begins ACC
     */
    public LogRecord insertAccLog(AccRecord accRecord,
                                  LogAction logAction,
                                  ULong requesterId,
                                  LocalDateTime timestamp) {
        return insertAccLog(accRecord, null, logAction, requesterId, timestamp);
    }

    private String serialize(AccRecord accRecord) {
        List<AsccRecord> asccRecords = dslContext.selectFrom(ASCC)
                .where(ASCC.FROM_ACC_ID.eq(accRecord.getAccId()))
                .fetch();

        List<BccRecord> bccRecords = dslContext.selectFrom(BCC)
                .where(BCC.FROM_ACC_ID.eq(accRecord.getAccId()))
                .fetch();

        List<SeqKeyRecord> seqKeyRecords = dslContext.selectFrom(SEQ_KEY)
                .where(SEQ_KEY.FROM_ACC_ID.eq(accRecord.getAccId()))
                .fetch();

        return serializer.serialize(accRecord, asccRecords, bccRecords, seqKeyRecords);
    }

    public LogRecord insertAccLog(AccRecord accRecord,
                                  ULong prevLogId,
                                  LogAction logAction,
                                  ULong requesterId,
                                  LocalDateTime timestamp) {

        LogRecord prevLogRecord = null;
        if (prevLogId != null) {
            prevLogRecord = dslContext.selectFrom(LOG)
                    .where(LOG.LOG_ID.eq(prevLogId))
                    .fetchOne();
        }

        LogRecord logRecord = new LogRecord();
        logRecord.setHash(generateHash());
        if (LogAction.Revised.equals(logAction)) {
            assert (prevLogRecord != null);
            logRecord.setRevisionNum(prevLogRecord.getRevisionNum().add(1));
            logRecord.setRevisionTrackingNum(UInteger.valueOf(1));
        } else if (LogAction.Canceled.equals(logAction)) {
            assert (prevLogRecord != null);
            logRecord.setRevisionNum(prevLogRecord.getRevisionNum().subtract(1));
            logRecord.setRevisionTrackingNum(UInteger.valueOf(1));
        } else {
            if (prevLogRecord != null) {
                logRecord.setRevisionNum(prevLogRecord.getRevisionNum());
                logRecord.setRevisionTrackingNum(prevLogRecord.getRevisionTrackingNum().add(1));
            } else {
                logRecord.setRevisionNum(UInteger.valueOf(1));
                logRecord.setRevisionTrackingNum(UInteger.valueOf(1));
            }
        }
        logRecord.setLogAction(logAction.name());

        logRecord.setSnapshot(JSON.valueOf(serialize(accRecord)));
        logRecord.setReference(accRecord.getGuid());
        logRecord.setCreatedBy(requesterId);
        logRecord.setCreationTimestamp(timestamp);
        if (prevLogRecord != null) {
            logRecord.setPrevLogId(prevLogRecord.getLogId());
        }

        logRecord.setLogId(dslContext.insertInto(LOG)
                .set(logRecord)
                .returning(LOG.LOG_ID).fetchOne().getLogId());
        if (prevLogRecord != null) {
            prevLogRecord.setNextLogId(logRecord.getLogId());
            prevLogRecord.update(LOG.NEXT_LOG_ID);
        }

        return logRecord;
    }

    /*
     * Begins ASCCP
     */
    public LogRecord insertAsccpLog(AsccpRecord asccpRecord,
                                    LogAction logAction,
                                    ULong requesterId,
                                    LocalDateTime timestamp) {
        return insertAsccpLog(asccpRecord, null, logAction, requesterId, timestamp);
    }

    public LogRecord insertAsccpLog(AsccpRecord asccpRecord,
                                    ULong prevLogId,
                                    LogAction logAction,
                                    ULong requesterId,
                                    LocalDateTime timestamp) {

        LogRecord prevLogRecord = null;
        if (prevLogId != null) {
            prevLogRecord = dslContext.selectFrom(LOG)
                    .where(LOG.LOG_ID.eq(prevLogId))
                    .fetchOne();
        }

        LogRecord logRecord = new LogRecord();
        logRecord.setHash(generateHash());
        if (LogAction.Revised.equals(logAction)) {
            assert (prevLogRecord != null);
            logRecord.setRevisionNum(prevLogRecord.getRevisionNum().add(1));
            logRecord.setRevisionTrackingNum(UInteger.valueOf(1));
        } else if (LogAction.Canceled.equals(logAction)) {
            assert (prevLogRecord != null);
            logRecord.setRevisionNum(prevLogRecord.getRevisionNum().subtract(1));
            logRecord.setRevisionTrackingNum(UInteger.valueOf(1));
        } else {
            if (prevLogRecord != null) {
                logRecord.setRevisionNum(prevLogRecord.getRevisionNum());
                logRecord.setRevisionTrackingNum(prevLogRecord.getRevisionTrackingNum().add(1));
            } else {
                logRecord.setRevisionNum(UInteger.valueOf(1));
                logRecord.setRevisionTrackingNum(UInteger.valueOf(1));
            }
        }
        logRecord.setLogAction(logAction.name());
        logRecord.setSnapshot(JSON.valueOf(serializer.serialize(asccpRecord)));
        logRecord.setReference(asccpRecord.getGuid());
        logRecord.setCreatedBy(requesterId);
        logRecord.setCreationTimestamp(timestamp);
        if (prevLogRecord != null) {
            logRecord.setPrevLogId(prevLogRecord.getLogId());
        }

        logRecord.setLogId(dslContext.insertInto(LOG)
                .set(logRecord)
                .returning(LOG.LOG_ID).fetchOne().getLogId());
        if (prevLogRecord != null) {
            prevLogRecord.setNextLogId(logRecord.getLogId());
            prevLogRecord.update(LOG.NEXT_LOG_ID);
        }

        return logRecord;
    }

    /*
     * Begins BCCP
     */
    public LogRecord insertBccpLog(BccpRecord bccpRecord,
                                   LogAction logAction,
                                   ULong requesterId,
                                   LocalDateTime timestamp) {
        return insertBccpLog(bccpRecord, null, logAction, requesterId, timestamp);
    }

    public LogRecord insertBccpLog(BccpRecord bccpRecord,
                                   ULong prevLogId,
                                   LogAction logAction,
                                   ULong requesterId,
                                   LocalDateTime timestamp) {

        LogRecord prevLogRecord = null;
        if (prevLogId != null) {
            prevLogRecord = dslContext.selectFrom(LOG)
                    .where(LOG.LOG_ID.eq(prevLogId))
                    .fetchOne();
        }

        LogRecord logRecord = new LogRecord();
        logRecord.setHash(generateHash());
        if (LogAction.Revised.equals(logAction)) {
            assert (prevLogRecord != null);
            logRecord.setRevisionNum(prevLogRecord.getRevisionNum().add(1));
            logRecord.setRevisionTrackingNum(UInteger.valueOf(1));
        } else if (LogAction.Canceled.equals(logAction)) {
            assert (prevLogRecord != null);
            logRecord.setRevisionNum(prevLogRecord.getRevisionNum().subtract(1));
            logRecord.setRevisionTrackingNum(UInteger.valueOf(1));
        } else {
            if (prevLogRecord != null) {
                logRecord.setRevisionNum(prevLogRecord.getRevisionNum());
                logRecord.setRevisionTrackingNum(prevLogRecord.getRevisionTrackingNum().add(1));
            } else {
                logRecord.setRevisionNum(UInteger.valueOf(1));
                logRecord.setRevisionTrackingNum(UInteger.valueOf(1));
            }
        }
        logRecord.setLogAction(logAction.name());
        logRecord.setSnapshot(JSON.valueOf(serializer.serialize(bccpRecord)));
        logRecord.setReference(bccpRecord.getGuid());
        logRecord.setCreatedBy(requesterId);
        logRecord.setCreationTimestamp(timestamp);
        if (prevLogRecord != null) {
            logRecord.setPrevLogId(prevLogRecord.getLogId());
        }

        logRecord.setLogId(dslContext.insertInto(LOG)
                .set(logRecord)
                .returning(LOG.LOG_ID).fetchOne().getLogId());
        if (prevLogRecord != null) {
            prevLogRecord.setNextLogId(logRecord.getLogId());
            prevLogRecord.update(LOG.NEXT_LOG_ID);
        }

        return logRecord;
    }

    /*
     * Begins Code List
     */
    public LogRecord insertCodeListLog(CodeListRecord codeListRecord,
                                       LogAction logAction,
                                       ULong requesterId,
                                       LocalDateTime timestamp) {
        return insertCodeListLog(codeListRecord, null, logAction, requesterId, timestamp);
    }

    public LogRecord insertCodeListLog(CodeListRecord codeListRecord,
                                       ULong prevLogId,
                                       LogAction logAction,
                                       ULong requesterId,
                                       LocalDateTime timestamp) {

        LogRecord prevLogRecord = null;
        if (prevLogId != null) {
            prevLogRecord = dslContext.selectFrom(LOG)
                    .where(LOG.LOG_ID.eq(prevLogId))
                    .fetchOne();
        }

        LogRecord logRecord = new LogRecord();
        logRecord.setHash(generateHash());
        if (LogAction.Revised.equals(logAction)) {
            assert (prevLogRecord != null);
            logRecord.setRevisionNum(prevLogRecord.getRevisionNum().add(1));
            logRecord.setRevisionTrackingNum(UInteger.valueOf(1));
        } else if (LogAction.Canceled.equals(logAction)) {
            assert (prevLogRecord != null);
            logRecord.setRevisionNum(prevLogRecord.getRevisionNum().subtract(1));
            logRecord.setRevisionTrackingNum(UInteger.valueOf(1));
        } else {
            if (prevLogRecord != null) {
                logRecord.setRevisionNum(prevLogRecord.getRevisionNum());
                logRecord.setRevisionTrackingNum(prevLogRecord.getRevisionTrackingNum().add(1));
            } else {
                logRecord.setRevisionNum(UInteger.valueOf(1));
                logRecord.setRevisionTrackingNum(UInteger.valueOf(1));
            }
        }
        logRecord.setLogAction(logAction.name());

        List<CodeListValueRecord> codeListValueRecords = dslContext.selectFrom(CODE_LIST_VALUE)
                .where(CODE_LIST_VALUE.CODE_LIST_ID.eq(codeListRecord.getCodeListId()))
                .fetch();

        logRecord.setSnapshot(JSON.valueOf(serializer.serialize(codeListRecord, codeListValueRecords)));
        logRecord.setReference(codeListRecord.getGuid());
        logRecord.setCreatedBy(requesterId);
        logRecord.setCreationTimestamp(timestamp);
        if (prevLogRecord != null) {
            logRecord.setPrevLogId(prevLogRecord.getLogId());
        }

        logRecord.setLogId(dslContext.insertInto(LOG)
                .set(logRecord)
                .returning(LOG.LOG_ID).fetchOne().getLogId());
        if (prevLogRecord != null) {
            prevLogRecord.setNextLogId(logRecord.getLogId());
            prevLogRecord.update(LOG.NEXT_LOG_ID);
        }

        return logRecord;
    }
}
