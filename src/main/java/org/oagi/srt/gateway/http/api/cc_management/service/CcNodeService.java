package org.oagi.srt.gateway.http.api.cc_management.service;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.data.BCCEntityType;
import org.oagi.srt.data.RevisionAction;
import org.oagi.srt.entity.jooq.Tables;
import org.oagi.srt.entity.jooq.tables.records.AsccRecord;
import org.oagi.srt.entity.jooq.tables.records.BccRecord;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;
import org.oagi.srt.gateway.http.api.cc_management.data.node.*;
import org.oagi.srt.gateway.http.api.cc_management.repository.CcNodeRepository;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.oagi.srt.gateway.http.helper.SrtGuid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

import static org.jooq.impl.DSL.and;
import static org.jooq.impl.DSL.max;
import static org.oagi.srt.entity.jooq.Tables.*;

@Service
@Transactional(readOnly = true)
public class CcNodeService {

    @Autowired
    private CcNodeRepository repository;

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private SessionService sessionService;

    public CcAccNode getAccNode(User user, long accId, Long releaseId) {
        return repository.getAccNodeByAccId(accId, releaseId);
    }

    public CcAsccpNode getAsccpNode(User user, long asccpId, Long releaseId) {
        return repository.getAsccpNodeByAsccpId(asccpId, releaseId);
    }

    public CcBccpNode getBccpNode(User user, long bccpId, Long releaseId) {
        return repository.getBccpNodeByBccpId(bccpId, releaseId);
    }

    public CcAccNode getExtensionNode(User user, long extensionId, Long releaseId) {
        CcAccNode ueAcc = repository.getAccNodeByAccId(extensionId, null);
        CcAsccpNode asccpNode = repository.getAsccpNodeByRoleOfAccId(ueAcc.getAccId(), null);
        return repository.getAccNodeByAsccpIdFromAscc(asccpNode.getAsccpId(), releaseId);
    }

    @Transactional
    public void appendAsccp(User user, long extensionId, Long releaseId, long asccpId) {
        int nextSeqKey = getNextSeqKey(extensionId);

        asccpId = dslContext.select(ASCCP.CURRENT_ASCCP_ID)
                .from(ASCCP).where(ASCCP.ASCCP_ID.eq(ULong.valueOf(asccpId)))
                .fetchOneInto(Long.class);

        AsccRecord ascc = createASCC(user, extensionId, asccpId, nextSeqKey, releaseId);

        int revisionNum = dslContext.select(ACC.REVISION_NUM)
                .from(ACC).where(ACC.ACC_ID.eq(ULong.valueOf(extensionId)))
                .fetchOneInto(Integer.class);

        createASCCHistory(ascc, revisionNum);
    }

    private AsccRecord createASCC(User user, long accId, long asccpId, int seqKey, long releaseId) {
        String accObjectClassTerm = dslContext.select(ACC.OBJECT_CLASS_TERM)
                .from(ACC).where(ACC.ACC_ID.eq(ULong.valueOf(accId)))
                .fetchOneInto(String.class);
        String asccpDen = dslContext.select(ASCCP.DEN)
                .from(ASCCP).where(ASCCP.ASCCP_ID.eq(ULong.valueOf(asccpId)))
                .fetchOneInto(String.class);

        ULong userId = ULong.valueOf(sessionService.userId(user));
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        return dslContext.insertInto(Tables.ASCC,
                Tables.ASCC.GUID,
                Tables.ASCC.CARDINALITY_MIN,
                Tables.ASCC.CARDINALITY_MAX,
                Tables.ASCC.SEQ_KEY,
                Tables.ASCC.FROM_ACC_ID,
                Tables.ASCC.TO_ASCCP_ID,
                Tables.ASCC.DEN,
                Tables.ASCC.IS_DEPRECATED,
                Tables.ASCC.CREATED_BY,
                Tables.ASCC.LAST_UPDATED_BY,
                Tables.ASCC.OWNER_USER_ID,
                Tables.ASCC.CREATION_TIMESTAMP,
                Tables.ASCC.LAST_UPDATE_TIMESTAMP,
                Tables.ASCC.RELEASE_ID,
                Tables.ASCC.STATE,
                Tables.ASCC.REVISION_NUM,
                Tables.ASCC.REVISION_TRACKING_NUM,
                Tables.ASCC.REVISION_ACTION).values(
                SrtGuid.randomGuid(),
                0,
                -1,
                seqKey,
                ULong.valueOf(accId),
                ULong.valueOf(asccpId),
                accObjectClassTerm + ". " + asccpDen,
                Byte.valueOf((byte) 0),
                userId,
                userId,
                userId,
                timestamp,
                timestamp,
                ULong.valueOf(releaseId),
                CcState.Editing.getValue(),
                0,
                0,
                null
        ).returning().fetchOne();
    }

    private void createASCCHistory(AsccRecord ascc, int revisionNum) {
        dslContext.insertInto(Tables.ASCC,
                Tables.ASCC.GUID,
                Tables.ASCC.CARDINALITY_MIN,
                Tables.ASCC.CARDINALITY_MAX,
                Tables.ASCC.SEQ_KEY,
                Tables.ASCC.FROM_ACC_ID,
                Tables.ASCC.TO_ASCCP_ID,
                Tables.ASCC.DEN,
                Tables.ASCC.IS_DEPRECATED,
                Tables.ASCC.CREATED_BY,
                Tables.ASCC.LAST_UPDATED_BY,
                Tables.ASCC.OWNER_USER_ID,
                Tables.ASCC.CREATION_TIMESTAMP,
                Tables.ASCC.LAST_UPDATE_TIMESTAMP,
                Tables.ASCC.RELEASE_ID,
                Tables.ASCC.STATE,
                Tables.ASCC.REVISION_NUM,
                Tables.ASCC.REVISION_TRACKING_NUM,
                Tables.ASCC.REVISION_ACTION,
                Tables.ASCC.CURRENT_ASCC_ID).values(
                ascc.getGuid(),
                ascc.getCardinalityMin(),
                ascc.getCardinalityMax(),
                ascc.getSeqKey(),
                ascc.getFromAccId(),
                ascc.getToAsccpId(),
                ascc.getDen(),
                ascc.getIsDeprecated(),
                ascc.getCreatedBy(),
                ascc.getLastUpdatedBy(),
                ascc.getOwnerUserId(),
                ascc.getCreationTimestamp(),
                ascc.getLastUpdateTimestamp(),
                ascc.getReleaseId(),
                ascc.getState(),
                revisionNum,
                1,
                Integer.valueOf(RevisionAction.Insert.getValue()).byteValue(),
                ascc.getAsccId()
        ).execute();
    }

    @Transactional
    public void appendBccp(User user, long extensionId, Long releaseId, long bccpId) {
        int nextSeqKey = getNextSeqKey(extensionId);

        bccpId = dslContext.select(BCCP.CURRENT_BCCP_ID)
                .from(BCCP).where(BCCP.BCCP_ID.eq(ULong.valueOf(bccpId)))
                .fetchOneInto(Long.class);

        BccRecord bcc = createBCC(user, extensionId, bccpId, nextSeqKey, releaseId);

        int revisionNum = dslContext.select(ACC.REVISION_NUM)
                .from(ACC).where(ACC.ACC_ID.eq(ULong.valueOf(extensionId)))
                .fetchOneInto(Integer.class);

        createBCCHistory(bcc, revisionNum);
    }

    private BccRecord createBCC(User user, long accId, long bccpId, int seqKey, long releaseId) {
        String accObjectClassTerm = dslContext.select(ACC.OBJECT_CLASS_TERM)
                .from(ACC).where(ACC.ACC_ID.eq(ULong.valueOf(accId)))
                .fetchOneInto(String.class);
        String bccpDen = dslContext.select(BCCP.DEN)
                .from(BCCP).where(BCCP.BCCP_ID.eq(ULong.valueOf(bccpId)))
                .fetchOneInto(String.class);

        ULong userId = ULong.valueOf(sessionService.userId(user));
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        return dslContext.insertInto(Tables.BCC,
                Tables.BCC.GUID,
                Tables.BCC.CARDINALITY_MIN,
                Tables.BCC.CARDINALITY_MAX,
                Tables.BCC.SEQ_KEY,
                Tables.BCC.ENTITY_TYPE,
                Tables.BCC.FROM_ACC_ID,
                Tables.BCC.TO_BCCP_ID,
                Tables.BCC.DEN,
                Tables.BCC.IS_DEPRECATED,
                Tables.BCC.CREATED_BY,
                Tables.BCC.LAST_UPDATED_BY,
                Tables.BCC.OWNER_USER_ID,
                Tables.BCC.CREATION_TIMESTAMP,
                Tables.BCC.LAST_UPDATE_TIMESTAMP,
                Tables.BCC.RELEASE_ID,
                Tables.BCC.STATE,
                Tables.BCC.REVISION_NUM,
                Tables.BCC.REVISION_TRACKING_NUM,
                Tables.BCC.REVISION_ACTION).values(
                SrtGuid.randomGuid(),
                0,
                -1,
                seqKey,
                BCCEntityType.Element.getValue(),
                ULong.valueOf(accId),
                ULong.valueOf(bccpId),
                accObjectClassTerm + ". " + bccpDen,
                Byte.valueOf((byte) 0),
                userId,
                userId,
                userId,
                timestamp,
                timestamp,
                ULong.valueOf(releaseId),
                CcState.Editing.getValue(),
                0,
                0,
                null
        ).returning().fetchOne();
    }

    private void createBCCHistory(BccRecord bcc, int revisionNum) {
        dslContext.insertInto(Tables.BCC,
                Tables.BCC.GUID,
                Tables.BCC.CARDINALITY_MIN,
                Tables.BCC.CARDINALITY_MAX,
                Tables.BCC.SEQ_KEY,
                Tables.BCC.ENTITY_TYPE,
                Tables.BCC.FROM_ACC_ID,
                Tables.BCC.TO_BCCP_ID,
                Tables.BCC.DEN,
                Tables.BCC.IS_DEPRECATED,
                Tables.BCC.CREATED_BY,
                Tables.BCC.LAST_UPDATED_BY,
                Tables.BCC.OWNER_USER_ID,
                Tables.BCC.CREATION_TIMESTAMP,
                Tables.BCC.LAST_UPDATE_TIMESTAMP,
                Tables.BCC.RELEASE_ID,
                Tables.BCC.STATE,
                Tables.BCC.REVISION_NUM,
                Tables.BCC.REVISION_TRACKING_NUM,
                Tables.BCC.REVISION_ACTION,
                Tables.BCC.CURRENT_BCC_ID).values(
                bcc.getGuid(),
                bcc.getCardinalityMin(),
                bcc.getCardinalityMax(),
                bcc.getSeqKey(),
                bcc.getEntityType(),
                bcc.getFromAccId(),
                bcc.getToBccpId(),
                bcc.getDen(),
                bcc.getIsDeprecated(),
                bcc.getCreatedBy(),
                bcc.getLastUpdatedBy(),
                bcc.getOwnerUserId(),
                bcc.getCreationTimestamp(),
                bcc.getLastUpdateTimestamp(),
                bcc.getReleaseId(),
                bcc.getState(),
                revisionNum,
                1,
                Integer.valueOf(RevisionAction.Insert.getValue()).byteValue(),
                bcc.getBccId()
        ).execute();
    }

    private int getNextSeqKey(long accId) {
        Integer asccMaxSeqKey = dslContext.select(max(ASCC.SEQ_KEY))
                .from(ASCC)
                .where(ASCC.FROM_ACC_ID.eq(ULong.valueOf(accId)))
                .fetchOneInto(Integer.class);
        if (asccMaxSeqKey == null) {
            asccMaxSeqKey = 0;
        }

        Integer bccMaxSeqKey = dslContext.select(max(BCC.SEQ_KEY))
                .from(BCC)
                .where(BCC.FROM_ACC_ID.eq(ULong.valueOf(accId)))
                .fetchOneInto(Integer.class);
        if (bccMaxSeqKey == null) {
            bccMaxSeqKey = 0;
        }

        return Math.max(asccMaxSeqKey, bccMaxSeqKey) + 1;
    }

    @Transactional
    public void discardAscc(User user, long extensionId, Long releaseId, long asccId) {
        dslContext.deleteFrom(Tables.ASCC)
                .where(ASCC.CURRENT_ASCC_ID.eq(ULong.valueOf(asccId)))
                .execute();
        int seqKey = dslContext.select(ASCC.SEQ_KEY)
                .from(Tables.ASCC).where(ASCC.ASCC_ID.eq(ULong.valueOf(asccId)))
                .fetchOneInto(Integer.class);
        dslContext.deleteFrom(Tables.ASCC)
                .where(ASCC.ASCC_ID.eq(ULong.valueOf(asccId)))
                .execute();

        decreaseSeqKeyGreaterThan(extensionId, seqKey);
    }

    @Transactional
    public void discardBcc(User user, long extensionId, Long releaseId, long bccId) {
        dslContext.deleteFrom(Tables.BCC)
                .where(BCC.CURRENT_BCC_ID.eq(ULong.valueOf(bccId)))
                .execute();
        int seqKey = dslContext.select(BCC.SEQ_KEY)
                .from(Tables.BCC).where(BCC.BCC_ID.eq(ULong.valueOf(bccId)))
                .fetchOneInto(Integer.class);
        dslContext.deleteFrom(Tables.BCC)
                .where(BCC.BCC_ID.eq(ULong.valueOf(bccId)))
                .execute();

        decreaseSeqKeyGreaterThan(extensionId, seqKey);
    }

    private void decreaseSeqKeyGreaterThan(long extensionId, int seqKey) {
        dslContext.update(Tables.ASCC)
                .set(ASCC.SEQ_KEY, ASCC.SEQ_KEY.subtract(1))
                .where(and(
                        ASCC.FROM_ACC_ID.eq(ULong.valueOf(extensionId)),
                        ASCC.SEQ_KEY.greaterThan(seqKey)
                ))
                .execute();

        dslContext.update(Tables.BCC)
                .set(BCC.SEQ_KEY, BCC.SEQ_KEY.subtract(1))
                .where(and(
                        BCC.FROM_ACC_ID.eq(ULong.valueOf(extensionId)),
                        BCC.SEQ_KEY.greaterThan(seqKey)
                ))
                .execute();
    }

    public List<? extends CcNode> getDescendants(User user, CcAccNode accNode) {
        return repository.getDescendants(user, accNode);
    }

    public List<? extends CcNode> getDescendants(User user, CcAsccpNode asccpNode) {
        return repository.getDescendants(user, asccpNode);
    }

    public List<? extends CcNode> getDescendants(User user, CcBccpNode bccpNode) {
        return repository.getDescendants(user, bccpNode);
    }

    public CcAccNodeDetail getAccNodeDetail(User user, CcAccNode accNode) {
        return repository.getAccNodeDetail(user, accNode);
    }

    public CcAsccpNodeDetail getAsccpNodeDetail(User user, CcAsccpNode asccpNode) {
        return repository.getAsccpNodeDetail(user, asccpNode);
    }

    public CcBccpNodeDetail getBccpNodeDetail(User user, CcBccpNode bccpNode) {
        return repository.getBccpNodeDetail(user, bccpNode);
    }

    public CcBdtScNodeDetail getBdtScNodeDetail(User user, CcBdtScNode bdtScNode) {
        return repository.getBdtScNodeDetail(user, bdtScNode);
    }

    @Transactional
    public long createAcc(User user, CcAccNode ccAccNode) {
        return repository.createAcc(user, ccAccNode);
    }

    @Transactional
    public void updateAcc(User user, CcAccNode ccAccNode, long accId) {
        repository.updateAcc(user, ccAccNode);
    }

}

