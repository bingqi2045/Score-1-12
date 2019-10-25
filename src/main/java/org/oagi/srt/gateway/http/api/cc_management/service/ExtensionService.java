package org.oagi.srt.gateway.http.api.cc_management.service;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.jooq.types.ULong;
import org.oagi.srt.data.ACC;
import org.oagi.srt.data.*;
import org.oagi.srt.entity.jooq.Tables;
import org.oagi.srt.entity.jooq.tables.records.*;
import org.oagi.srt.gateway.http.api.bie_management.data.bie_edit.BieEditAcc;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;
import org.oagi.srt.gateway.http.api.cc_management.data.ExtensionUpdateRequest;
import org.oagi.srt.gateway.http.api.cc_management.data.ExtensionUpdateResponse;
import org.oagi.srt.gateway.http.api.cc_management.data.node.*;
import org.oagi.srt.gateway.http.api.cc_management.repository.CcNodeRepository;
import org.oagi.srt.gateway.http.api.common.data.AccessPrivilege;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.oagi.srt.gateway.http.helper.SrtGuid;
import org.oagi.srt.gateway.http.helper.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static org.jooq.impl.DSL.and;
import static org.jooq.impl.DSL.max;
import static org.oagi.srt.entity.jooq.Tables.*;
import static org.oagi.srt.gateway.http.api.common.data.AccessPrivilege.*;

@Service
@Transactional(readOnly = true)
public class ExtensionService {

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private CcNodeRepository repository;

    @Autowired
    private CcListService ccListService;

    @Autowired
    private ApplicationContext applicationContext;


    public CcAccNode getExtensionNode(User user, long extensionId, long releaseId) {
        CcAccNode ueAcc = repository.getAccNodeByAccId(extensionId, releaseId);
        CcAsccpNode asccpNode = repository.getAsccpNodeByRoleOfAccId(ueAcc.getAccId(), releaseId);
        CcAccNode eAcc = repository.getAccNodeFromAsccByAsccpId(asccpNode.getAsccpId(), releaseId);
        eAcc.setState(CcState.valueOf(ueAcc.getRawState()));

        long userId = sessionService.userId(user);
        long ownerUserId = dslContext.select(ACC.OWNER_USER_ID).from(ACC)
                .where(ACC.ACC_ID.eq(ULong.valueOf(ueAcc.getAccId()))).fetchOneInto(Long.class);

        AccessPrivilege accessPrivilege = Prohibited;
        switch (eAcc.getState()) {
            case Editing:
                if (userId == ownerUserId) {
                    accessPrivilege = CanEdit;
                } else {
                    accessPrivilege = Prohibited;
                }
                break;

            case Candidate:
                if (userId == ownerUserId) {
                    accessPrivilege = CanEdit;
                } else {
                    accessPrivilege = CanView;
                }

                break;

            case Published:
                accessPrivilege = CanView;
                break;
        }

        eAcc.setAccess(accessPrivilege.name());
        return eAcc;
    }

    public ACC getExistsUserExtension(long accId, long releaseId) {
        ACC ueAcc =
                dslContext.select(
                        Tables.ACC.as("ueAcc").ACC_ID,
                        Tables.ACC.as("ueAcc").OAGIS_COMPONENT_TYPE
                ).from(Tables.ACC.as("eAcc"))
                        .join(Tables.ASCC).on(Tables.ACC.as("eAcc").ACC_ID.eq(ASCC.FROM_ACC_ID))
                        .join(Tables.ASCCP).on(ASCC.TO_ASCCP_ID.eq(ASCCP.ASCCP_ID))
                        .join(Tables.ACC.as("ueAcc")).on(ASCCP.ROLE_OF_ACC_ID.eq(Tables.ACC.as("ueAcc").ACC_ID))
                        .where(and(ACC.as("eAcc").ACC_ID.eq(ULong.valueOf(accId)),
                                ASCC.REVISION_NUM.eq(0))
                        ).fetchOneInto(ACC.class);

        if (ueAcc == null) {
            return null;
        }

        if (ueAcc.getOagisComponentType() == OagisComponentType.UserExtensionGroup.getValue()) {
            return dslContext.selectFrom(Tables.ACC)
                    .where(Tables.ACC.ACC_ID.eq(ULong.valueOf(ueAcc.getAccId())))
                    .fetchOneInto(ACC.class);
        }
        return null;
    }


    @Transactional
    public long appendUserExtension(BieEditAcc eAcc, ACC ueAcc,
                                    long releaseId, User user) {
        if (ueAcc != null) {
            if (CcState.Published.getValue() == ueAcc.getState()) {
                return increaseRevisionNum(ueAcc, releaseId, user);
            } else {
                return ueAcc.getAccId();
            }
        } else {
            return createNewUserExtensionGroupACC(ccListService.getAcc(eAcc.getAccId()), releaseId, user);
        }
    }

    private long increaseRevisionNum(ACC ueAcc, long releaseId, User user) {
        ULong userId = ULong.valueOf(sessionService.userId(user));
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        int revisionNum = increaseAccRevisionNum(ueAcc, releaseId, userId, timestamp);
        increaseAsccRevisionNum(ueAcc, revisionNum, releaseId, userId, timestamp);
        increaseBccRevisionNum(ueAcc, revisionNum, releaseId, userId, timestamp);

        return ueAcc.getAccId();
    }

    private int increaseAccRevisionNum(ACC ueAcc, long releaseId,
                                       ULong userId, Timestamp timestamp) {
        
        AccRecord history = dslContext.selectFrom(Tables.ACC)
                .where(Tables.ACC.ACC_ID.eq(ULong.valueOf(ueAcc.getAccId())))
                .orderBy(Tables.ACC.ACC_ID.desc()).limit(1)
                .fetchOne();

        int newRevisionNum = history.getRevisionNum() + 1;
        history.setAccId(null);
        history.setState(CcState.Editing.getValue());
        history.setRevisionNum(newRevisionNum);
        history.setRevisionTrackingNum(1);
        history.setRevisionAction(Integer.valueOf(RevisionAction.Update.getValue()).byteValue());
        history.setCreatedBy(userId);
        history.setLastUpdatedBy(userId);
        history.setOwnerUserId(userId);
        history.setCreationTimestamp(timestamp);
        history.setLastUpdateTimestamp(timestamp);

        dslContext.insertInto(Tables.ACC).set(history).execute();

        dslContext.update(Tables.ACC)
                .set(Tables.ACC.STATE, history.getState())
                .set(Tables.ACC.LAST_UPDATED_BY, userId)
                .set(Tables.ACC.LAST_UPDATE_TIMESTAMP, timestamp)
                .where(Tables.ACC.ACC_ID.eq(ULong.valueOf(ueAcc.getAccId())))
                .execute();

        return newRevisionNum;
    }

    private void increaseAsccRevisionNum(ACC ueAcc, int revisionNum, long releaseId,
                                         ULong userId, Timestamp timestamp) {
        List<CcAsccNode> asccNodes = dslContext.select(
                ASCC.ASCC_ID,
                ASCC.GUID,
                ASCC.REVISION_NUM,
                ASCC.REVISION_TRACKING_NUM,
                ASCC_RELEASE_MANIFEST.RELEASE_ID)
                .from(ASCC)
                .join(ASCC_RELEASE_MANIFEST)
                .on(ASCC.ASCC_ID.eq(ASCC_RELEASE_MANIFEST.ASCC_ID))
                .where(and(
                        ASCC.FROM_ACC_ID.eq(ULong.valueOf(ueAcc.getAccId())),
                        ASCC.REVISION_NUM.greaterThan(0)
                ))
                .fetchInto(CcAsccNode.class);

        if (asccNodes.isEmpty()) {
            return;
        }

        // Update a state of the 'current record'.
        dslContext.update(ASCC)
                .set(ASCC.STATE, CcState.Editing.getValue())
                .set(ASCC.LAST_UPDATED_BY, userId)
                .set(ASCC.LAST_UPDATE_TIMESTAMP, timestamp)
                .where(and(
                        ASCC.FROM_ACC_ID.eq(ULong.valueOf(ueAcc.getAccId())),
                        ASCC.REVISION_NUM.eq(0)))
                .execute();

        Result<AsccRecord> asccRecordResult = asccRecordResult(asccNodes, releaseId);

        for (AsccRecord history : asccRecordResult) {
            history.setAsccId(null);
            history.setRevisionNum(revisionNum);
            history.setRevisionTrackingNum(1);
            history.setRevisionAction((byte) RevisionAction.Update.getValue());
            history.setCreatedBy(userId);
            history.setLastUpdatedBy(userId);
            history.setOwnerUserId(userId);
            history.setCreationTimestamp(timestamp);
            history.setLastUpdateTimestamp(timestamp);
            history.setState(CcState.Editing.getValue());

            dslContext.insertInto(ASCC).set(history).execute();
        }
    }

    private Result<AsccRecord> asccRecordResult(List<CcAsccNode> asccNodes, long releaseId) {
        List<ULong> asccIds = asccNodes.stream()
                .map(asccNode -> ULong.valueOf(asccNode.getAsccId()))
                .collect(Collectors.toList());

        return dslContext.selectFrom(ASCC)
                .where(ASCC.ASCC_ID.in(asccIds))
                .fetch();
    }

    private void increaseBccRevisionNum(ACC ueAcc, int revisionNum, long releaseId,
                                        ULong userId, Timestamp timestamp) {
        List<CcBccNode> bccNodes = dslContext.select(
                BCC.BCC_ID,
                BCC.GUID,
                BCC.REVISION_NUM,
                BCC.REVISION_TRACKING_NUM,
                BCC_RELEASE_MANIFEST.RELEASE_ID)
                .from(BCC)
                .join(BCC_RELEASE_MANIFEST)
                .on(BCC.BCC_ID.eq(BCC_RELEASE_MANIFEST.BCC_ID))
                .where(and(
                        BCC.FROM_ACC_ID.eq(ULong.valueOf(ueAcc.getAccId())),
                        BCC.REVISION_NUM.greaterThan(0)))
                .fetchInto(CcBccNode.class);

        if (bccNodes.isEmpty()) {
            return;
        }

        // Update a state of the 'current record'.
        dslContext.update(BCC)
                .set(BCC.STATE, CcState.Editing.getValue())
                .set(BCC.LAST_UPDATED_BY, userId)
                .set(BCC.LAST_UPDATE_TIMESTAMP, timestamp)
                .where(and(
                        BCC.FROM_ACC_ID.eq(ULong.valueOf(ueAcc.getAccId())),
                        BCC.REVISION_NUM.eq(0)))
                .execute();

        Result<BccRecord> bccRecordResult = bccRecordResult(bccNodes, releaseId);

        for (BccRecord history : bccRecordResult) {
            history.setBccId(null);
            history.setRevisionNum(revisionNum);
            history.setRevisionTrackingNum(1);
            history.setRevisionAction((byte) RevisionAction.Update.getValue());
            history.setCreatedBy(userId);
            history.setLastUpdatedBy(userId);
            history.setOwnerUserId(userId);
            history.setCreationTimestamp(timestamp);
            history.setLastUpdateTimestamp(timestamp);
            history.setState(CcState.Editing.getValue());

            dslContext.insertInto(BCC).set(history).execute();
        }
    }

    private Result<BccRecord> bccRecordResult(List<CcBccNode> bccNodes, long releaseId) {
        List<ULong> bccIds = bccNodes.stream()
                .map(bccNode -> ULong.valueOf(bccNode.getBccId()))
                .collect(Collectors.toList());

        return dslContext.selectFrom(BCC)
                .where(BCC.BCC_ID.in(bccIds))
                .fetch();
    }

    private long createNewUserExtensionGroupACC(ACC eAcc, long releaseId, User user) {
        AccRecord ueAcc = createACCForExtension(eAcc, user);
        createACCReleaseManifestForExtension(ueAcc, releaseId);

        AsccpRecord ueAsccp = createASCCPForExtension(eAcc, user, ueAcc);
        createASCCPReleaseManifestForExtension(ueAsccp, releaseId);

        AsccRecord ueAscc = createASCCForExtension(eAcc, ueAsccp, user);
        createASCCReleaseManifestForExtension(ueAscc, releaseId);

        return ueAcc.getAccId().longValue();
    }

    private AccRecord createACCForExtension(ACC eAcc, User user) {
        String objectClassTerm = Utility.getUserExtensionGroupObjectClassTerm(eAcc.getObjectClassTerm());
        ULong userId = ULong.valueOf(sessionService.userId(user));
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        return dslContext.insertInto(Tables.ACC,
                Tables.ACC.GUID,
                Tables.ACC.OBJECT_CLASS_TERM,
                Tables.ACC.DEN,
                Tables.ACC.DEFINITION,
                Tables.ACC.OAGIS_COMPONENT_TYPE,
                Tables.ACC.CREATED_BY,
                Tables.ACC.LAST_UPDATED_BY,
                Tables.ACC.OWNER_USER_ID,
                Tables.ACC.CREATION_TIMESTAMP,
                Tables.ACC.LAST_UPDATE_TIMESTAMP,
                Tables.ACC.STATE,
                Tables.ACC.REVISION_NUM,
                Tables.ACC.REVISION_TRACKING_NUM,
                Tables.ACC.REVISION_ACTION).values(
                SrtGuid.randomGuid(),
                objectClassTerm,
                objectClassTerm + ". Details",
                "A system created component containing user extension to the " + eAcc.getObjectClassTerm() + ".",
                OagisComponentType.UserExtensionGroup.getValue(),
                userId,
                userId,
                userId,
                timestamp,
                timestamp,
                CcState.Editing.getValue(),
                1,
                1,
                (byte) 1
        ).returning().fetchOne();
    }

    private void createACCReleaseManifestForExtension(AccRecord ueAcc, long releaseId) {
        dslContext.insertInto(ACC_RELEASE_MANIFEST,
                ACC_RELEASE_MANIFEST.ACC_ID,
                ACC_RELEASE_MANIFEST.BASED_ACC_ID,
                ACC_RELEASE_MANIFEST.RELEASE_ID
                ).values(
                ueAcc.getAccId(),
                ueAcc.getBasedAccId(),
                ULong.valueOf(releaseId)
        ).execute();
    }

    private AsccpRecord createASCCPForExtension(ACC eAcc, User user, AccRecord ueAcc) {
        ULong userId = ULong.valueOf(sessionService.userId(user));
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        return dslContext.insertInto(Tables.ASCCP,
                Tables.ASCCP.GUID,
                Tables.ASCCP.PROPERTY_TERM,
                Tables.ASCCP.ROLE_OF_ACC_ID,
                Tables.ASCCP.DEN,
                Tables.ASCCP.DEFINITION,
                Tables.ASCCP.REUSABLE_INDICATOR,
                Tables.ASCCP.IS_DEPRECATED,
                Tables.ASCCP.IS_NILLABLE,
                Tables.ASCCP.CREATED_BY,
                Tables.ASCCP.LAST_UPDATED_BY,
                Tables.ASCCP.OWNER_USER_ID,
                Tables.ASCCP.CREATION_TIMESTAMP,
                Tables.ASCCP.LAST_UPDATE_TIMESTAMP,
                Tables.ASCCP.STATE,
                Tables.ASCCP.REVISION_NUM,
                Tables.ASCCP.REVISION_TRACKING_NUM,
                Tables.ASCCP.REVISION_ACTION).values(
                SrtGuid.randomGuid(),
                ueAcc.getObjectClassTerm(),
                ueAcc.getAccId(),
                ueAcc.getObjectClassTerm() + ". " + ueAcc.getObjectClassTerm(),
                "A system created component containing user extension to the " + eAcc.getObjectClassTerm() + ".",
                (byte) 0,
                (byte) 0,
                (byte) 0,
                userId,
                userId,
                userId,
                timestamp,
                timestamp,
                CcState.Published.getValue(),
                1,
                1,
                (byte) 1
        ).returning().fetchOne();
    }

    private void createASCCPReleaseManifestForExtension(AsccpRecord ueAsccp, long releaseId) {
        dslContext.insertInto(ASCCP_RELEASE_MANIFEST,
                ASCCP_RELEASE_MANIFEST.ASCCP_ID,
                ASCCP_RELEASE_MANIFEST.ROLE_OF_ACC_ID,
                ASCCP_RELEASE_MANIFEST.RELEASE_ID
        ).values(
                ueAsccp.getAsccpId(),
                ueAsccp.getRoleOfAccId(),
                ULong.valueOf(releaseId)
        ).execute();
    }

    private AsccRecord createASCCForExtension(ACC eAcc, AsccpRecord ueAsccp, User user) {
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
                Tables.ASCC.STATE,
                Tables.ASCC.REVISION_NUM,
                Tables.ASCC.REVISION_TRACKING_NUM,
                Tables.ASCC.REVISION_ACTION).values(
                SrtGuid.randomGuid(),
                0,
                1,
                1,
                ULong.valueOf(eAcc.getAccId()),
                ueAsccp.getAsccpId(),
                eAcc.getObjectClassTerm() + ". " + ueAsccp.getDen(),
                (byte) 0,
                userId,
                userId,
                userId,
                timestamp,
                timestamp,
                CcState.Published.getValue(),
                1,
                1,
                (byte) 1
        ).returning().fetchOne();
    }

    private void createASCCReleaseManifestForExtension(AsccRecord ueAscc, long releaseId) {
        dslContext.insertInto(ASCC_RELEASE_MANIFEST,
                ASCC_RELEASE_MANIFEST.ASCC_ID,
                ASCC_RELEASE_MANIFEST.FROM_ACC_ID,
                ASCC_RELEASE_MANIFEST.TO_ASCCP_ID,
                ASCC_RELEASE_MANIFEST.RELEASE_ID
        ).values(
                ueAscc.getAsccId(),
                ueAscc.getFromAccId(),
                ueAscc.getToAsccpId(),
                ULong.valueOf(releaseId)
        ).execute();
    }

    @Transactional
    public void appendAsccp(User user, long extensionId, Long releaseId, long asccpId) {
        int nextSeqKey = getNextSeqKey(extensionId);

        asccpId = dslContext.select(ASCCP.ASCCP_ID)
                .from(ASCCP).where(ASCCP.ASCCP_ID.eq(ULong.valueOf(asccpId)))
                .fetchOneInto(Long.class);

        /*
         * Issue #710
         * Duplicated associations cannot be existed.
         */
        boolean exists = dslContext.selectCount()
                .from(ASCC).where(and(
                        ASCC.FROM_ACC_ID.eq(ULong.valueOf(extensionId)),
                        ASCC.TO_ASCCP_ID.eq(ULong.valueOf(asccpId))
                ))
                .fetchOptionalInto(Integer.class).orElse(0) > 0;
        if (exists) {
            throw new IllegalArgumentException("You cannot associate the same component.");
        }

        AsccRecord ascc = createASCC(user, extensionId, asccpId, nextSeqKey);

        int revisionNum = dslContext.select(ACC.REVISION_NUM)
                .from(ACC).where(ACC.ACC_ID.eq(ULong.valueOf(extensionId)))
                .orderBy(ACC.ACC_ID.desc()).limit(1)
                .fetchOneInto(Integer.class);

        createASCCHistory(ascc, revisionNum, releaseId);
    }

    private AsccRecord createASCC(User user, long accId, long asccpId, int seqKey) {
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
                CcState.Editing.getValue(),
                0,
                0,
                null
        ).returning().fetchOne();
    }

    private void createASCCHistory(AsccRecord ascc, int revisionNum, long releaseId) {
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
                Tables.ASCC.STATE,
                Tables.ASCC.REVISION_NUM,
                Tables.ASCC.REVISION_TRACKING_NUM,
                Tables.ASCC.REVISION_ACTION).values(
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
                ascc.getState(),
                revisionNum,
                1,
                Integer.valueOf(RevisionAction.Insert.getValue()).byteValue()
        ).execute();
    }

    @Transactional
    public void appendBccp(User user, long extensionId, Long releaseId, long bccpId) {
        int nextSeqKey = getNextSeqKey(extensionId);

        bccpId = dslContext.select(BCCP.BCCP_ID)
                .from(BCCP)
                .join(BCCP_RELEASE_MANIFEST)
                .on(BCCP_RELEASE_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId)).and(BCCP.BCCP_ID.eq(BCCP_RELEASE_MANIFEST.BCCP_ID)))
                .where(BCCP.BCCP_ID.eq(ULong.valueOf(bccpId)))
                .fetchOneInto(Long.class);

        /*
         * Issue #710
         * Duplicated associations cannot be existed.
         */
        boolean exists = dslContext.selectCount()
                .from(BCC)
                .join(BCC_RELEASE_MANIFEST)
                .on(BCC_RELEASE_MANIFEST.BCC_ID.eq(BCC.BCC_ID).and(BCC_RELEASE_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId))))
                .where(and(
                        BCC_RELEASE_MANIFEST.FROM_ACC_ID.eq(ULong.valueOf(extensionId)),
                        BCC_RELEASE_MANIFEST.TO_BCCP_ID.eq(ULong.valueOf(bccpId))
                ))
                .fetchOptionalInto(Integer.class).orElse(0) > 0;
        if (exists) {
            throw new IllegalArgumentException("You cannot associate the same component.");
        }

        BccRecord bcc = createBCC(user, extensionId, bccpId, nextSeqKey);

        int revisionNum = dslContext.select(ACC.REVISION_NUM)
                .from(ACC).where(ACC.ACC_ID.eq(ULong.valueOf(extensionId)))
                .orderBy(ACC.ACC_ID.desc()).limit(1)
                .fetchOneInto(Integer.class);

        createBCCHistory(bcc, revisionNum, releaseId);
    }

    private BccRecord createBCC(User user, long accId, long bccpId, int seqKey) {
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
                CcState.Editing.getValue(),
                0,
                0,
                null
        ).returning().fetchOne();
    }

    private void createBCCHistory(BccRecord bcc, int revisionNum, long releaseId) {
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
                Tables.BCC.STATE,
                Tables.BCC.REVISION_NUM,
                Tables.BCC.REVISION_TRACKING_NUM,
                Tables.BCC.REVISION_ACTION).values(
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
                bcc.getState(),
                revisionNum,
                1,
                Integer.valueOf(RevisionAction.Insert.getValue()).byteValue()
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
                .where(ASCC.ASCC_ID.eq(ULong.valueOf(asccId)))
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
                .where(BCC.BCC_ID.eq(ULong.valueOf(bccId)))
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

    @Transactional
    public void updateState(User user, long extensionId, Long releaseId, CcState state) {
        ULong userId = ULong.valueOf(sessionService.userId(user));
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        updateAccState(extensionId, releaseId, state, userId, timestamp);
        updateAsccState(extensionId, releaseId, state, userId, timestamp);
        updateBccState(extensionId, releaseId, state, userId, timestamp);

        if (state == CcState.Published) {
            storeBieUserExtRevisions(extensionId, releaseId);
        }
    }

    private void updateAccState(long extensionId, Long releaseId, CcState state,
                                ULong userId, Timestamp timestamp) {
        dslContext.update(Tables.ACC)
                .set(ACC.STATE, state.getValue())
                .set(ACC.LAST_UPDATED_BY, userId)
                .set(ACC.LAST_UPDATE_TIMESTAMP, timestamp)
                .where(ACC.ACC_ID.eq(ULong.valueOf(extensionId)))
                .execute();

        AccRecord history = dslContext.selectFrom(Tables.ACC)
                .where(ACC.ACC_ID.eq(ULong.valueOf(extensionId)))
                .orderBy(ACC.ACC_ID.desc()).limit(1)
                .fetchOne();

        history.setAccId(null);
        history.setRevisionTrackingNum(history.getRevisionTrackingNum() + 1);
        history.setRevisionAction((byte) RevisionAction.Update.getValue());
        history.setCreatedBy(userId);
        history.setLastUpdatedBy(userId);
        history.setCreationTimestamp(timestamp);
        history.setLastUpdateTimestamp(timestamp);
        history.setState(state.getValue());
        dslContext.insertInto(Tables.ACC).set(history).execute();
    }

    private void updateAsccState(long extensionId, Long releaseId, CcState state,
                                 ULong userId, Timestamp timestamp) {
        List<CcAsccNode> asccNodes = dslContext.select(
                ASCC.ASCC_ID,
                ASCC.GUID,
                ASCC.REVISION_NUM,
                ASCC.REVISION_TRACKING_NUM,
                ASCC_RELEASE_MANIFEST.RELEASE_ID)
                .from(ASCC)
                .join(ASCC_RELEASE_MANIFEST)
                .on(ASCC.ASCC_ID.eq(ASCC_RELEASE_MANIFEST.ASCC_ID))
                .where(and(
                        ASCC.FROM_ACC_ID.eq(ULong.valueOf(extensionId)),
                        ASCC.REVISION_NUM.greaterThan(0)
                ))
                .fetchInto(CcAsccNode.class);

        if (asccNodes.isEmpty()) {
            return;
        }

        // Update a state of the 'current record'.
        dslContext.update(ASCC)
                .set(ASCC.STATE, state.getValue())
                .set(ASCC.LAST_UPDATED_BY, userId)
                .set(ASCC.LAST_UPDATE_TIMESTAMP, timestamp)
                .where(and(
                        ASCC.FROM_ACC_ID.eq(ULong.valueOf(extensionId)),
                        ASCC.REVISION_NUM.eq(0)))
                .execute();

        Result<AsccRecord> asccRecordResult = asccRecordResult(asccNodes, releaseId);

        for (AsccRecord history : asccRecordResult) {
            history.setAsccId(null);
            history.setRevisionTrackingNum(history.getRevisionTrackingNum() + 1);
            history.setRevisionAction((byte) RevisionAction.Update.getValue());
            history.setCreatedBy(userId);
            history.setLastUpdatedBy(userId);
            history.setCreationTimestamp(timestamp);
            history.setLastUpdateTimestamp(timestamp);
            history.setState(state.getValue());

            dslContext.insertInto(ASCC).set(history).execute();
        }
    }

    private void updateBccState(long extensionId, Long releaseId, CcState state,
                                ULong userId, Timestamp timestamp) {
        List<CcBccNode> bccNodes = dslContext.select(
                BCC.BCC_ID,
                BCC.GUID,
                BCC.REVISION_NUM,
                BCC.REVISION_TRACKING_NUM,
                BCC_RELEASE_MANIFEST.RELEASE_ID)
                .from(BCC)
                .join(BCC_RELEASE_MANIFEST)
                .on(BCC.BCC_ID.eq(BCC_RELEASE_MANIFEST.BCC_ID))
                .where(and(
                        BCC.FROM_ACC_ID.eq(ULong.valueOf(extensionId)),
                        BCC.REVISION_NUM.greaterThan(0)
                )).fetchInto(CcBccNode.class);

        if (bccNodes.isEmpty()) {
            return;
        }

        // Update a state of the 'current record'.
        dslContext.update(BCC)
                .set(BCC.STATE, state.getValue())
                .set(BCC.LAST_UPDATED_BY, userId)
                .set(BCC.LAST_UPDATE_TIMESTAMP, timestamp)
                .where(and(
                        BCC.FROM_ACC_ID.eq(ULong.valueOf(extensionId)),
                        BCC.REVISION_NUM.eq(0)))
                .execute();

        Result<BccRecord> bccRecordResult = bccRecordResult(bccNodes, releaseId);

        for (BccRecord history : bccRecordResult) {
            history.setBccId(null);
            history.setRevisionTrackingNum(history.getRevisionTrackingNum() + 1);
            history.setRevisionAction((byte) RevisionAction.Update.getValue());
            history.setCreatedBy(userId);
            history.setLastUpdatedBy(userId);
            history.setCreationTimestamp(timestamp);
            history.setLastUpdateTimestamp(timestamp);
            history.setState(state.getValue());

            dslContext.insertInto(BCC).set(history).execute();
        }
    }

    private void storeBieUserExtRevisions(long extensionId, Long releaseId) {
        List<TopLevelAbie> topLevelAbies = dslContext.selectFrom(TOP_LEVEL_ABIE)
                .where(TOP_LEVEL_ABIE.STATE.ne(BieState.Published.getValue()))
                .fetchInto(TopLevelAbie.class);

        ExtensionPathHandler extensionPathHandler =
                applicationContext.getBean(ExtensionPathHandler.class, releaseId);

        for (TopLevelAbie topLevelAbie : topLevelAbies) {
            Long abieId = topLevelAbie.getAbieId();
            if (abieId == null) {
                continue;
            }
            long basedAccId = dslContext.select(Tables.ACC.ACC_ID)
                    .from(Tables.ABIE)
                    .join(Tables.ACC)
                    .on(Tables.ABIE.BASED_ACC_ID.eq(Tables.ACC.ACC_ID))
                    .where(Tables.ABIE.ABIE_ID.eq(ULong.valueOf(topLevelAbie.getAbieId())))
                    .fetchOneInto(Long.class);

            ULong eAccId =
                    dslContext.select(Tables.ACC.as("eAcc").ACC_ID)
                            .from(Tables.ACC.as("eAcc"))
                            .join(Tables.ASCC).on(Tables.ACC.as("eAcc").ACC_ID.eq(ASCC.FROM_ACC_ID))
                            .join(Tables.ASCCP).on(ASCC.TO_ASCCP_ID.eq(ASCCP.ASCCP_ID))
                            .join(Tables.ACC.as("ueAcc")).on(ASCCP.ROLE_OF_ACC_ID.eq(Tables.ACC.as("ueAcc").ACC_ID))
                            .where(and(
                                    ACC.as("ueAcc").ACC_ID.eq(ULong.valueOf(extensionId)),
                                    ASCC.REVISION_NUM.eq(0)
                            )).fetchOneInto(ULong.class);
        }
    }

    @Transactional
    public ExtensionUpdateResponse updateDetails(User user, ExtensionUpdateRequest request) {
        ExtensionUpdateResponse response = new ExtensionUpdateResponse();

        ULong extensionId = ULong.valueOf(request.getExtensionId());
        ULong releaseId = ULong.valueOf(request.getReleaseId());
        ULong userId = ULong.valueOf(sessionService.userId(user));
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        List<CcAsccpNodeDetail.Ascc> asccList = request.getAsccpDetails().stream().map(asccpDetail -> asccpDetail.getAscc())
                .collect(Collectors.toList());

        for (CcAsccpNodeDetail.Ascc ascc : asccList) {
            response.getAsccResults().put(ascc.getAsccId(),
                    updateAscc(extensionId, releaseId, ascc, userId, timestamp)
            );
        }

        List<CcBccpNodeDetail.Bcc> bccList = request.getBccpDetails().stream().map(bccpDetail -> bccpDetail.getBcc())
                .collect(Collectors.toList());
        for (CcBccpNodeDetail.Bcc bcc : bccList) {
            response.getBccResults().put(bcc.getBccId(),
                    updateBcc(extensionId, releaseId, bcc, userId, timestamp)
            );
        }

        return response;
    }

    private boolean updateAscc(ULong extensionId, ULong releaseId,
                               CcAsccpNodeDetail.Ascc ascc,
                               ULong userId, Timestamp timestamp) {

        int result = dslContext.update(Tables.ASCC)
                .set(ASCC.CARDINALITY_MIN, ascc.getCardinalityMin())
                .set(ASCC.CARDINALITY_MAX, ascc.getCardinalityMax())
                .set(ASCC.IS_DEPRECATED, (byte) ((ascc.isDeprecated()) ? 1 : 0))
                .set(ASCC.DEFINITION, ascc.getDefinition())
                .set(ASCC.LAST_UPDATED_BY, userId)
                .set(ASCC.LAST_UPDATE_TIMESTAMP, timestamp)
                .where(ASCC.ASCC_ID.eq(ULong.valueOf(ascc.getAsccId())))
                .execute();

        AsccRecord history = dslContext.selectFrom(Tables.ASCC)
                .where(ASCC.ASCC_ID.eq(ULong.valueOf(ascc.getAsccId())))
                .orderBy(ASCC.ASCC_ID.desc()).limit(1).fetchOne();

        history.setAsccId(null);
        history.setCardinalityMin(ascc.getCardinalityMin());
        history.setCardinalityMax(ascc.getCardinalityMax());
        history.setIsDeprecated((byte) ((ascc.isDeprecated()) ? 1 : 0));
        history.setDefinition(ascc.getDefinition());
        history.setRevisionTrackingNum(history.getRevisionTrackingNum() + 1);
        history.setRevisionAction((byte) RevisionAction.Update.getValue());
        history.setCreatedBy(userId);
        history.setLastUpdatedBy(userId);
        history.setCreationTimestamp(timestamp);
        history.setLastUpdateTimestamp(timestamp);

        dslContext.insertInto(ASCC).set(history).execute();

        return (result == 1);
    }

    private boolean updateBcc(ULong extensionId, ULong releaseId,
                              CcBccpNodeDetail.Bcc bcc,
                              ULong userId, Timestamp timestamp) {

        int result = dslContext.update(Tables.BCC)
                .set(BCC.ENTITY_TYPE, bcc.getEntityType())
                .set(BCC.CARDINALITY_MIN, bcc.getCardinalityMin())
                .set(BCC.CARDINALITY_MAX, bcc.getCardinalityMax())
                .set(BCC.IS_NILLABLE, (byte) ((bcc.isNillable()) ? 1 : 0))
                .set(BCC.IS_DEPRECATED, (byte) ((bcc.isDeprecated()) ? 1 : 0))
                .set(BCC.DEFAULT_VALUE, bcc.getDefaultValue())
                .set(BCC.DEFINITION, bcc.getDefinition())
                .set(BCC.LAST_UPDATED_BY, userId)
                .set(BCC.LAST_UPDATE_TIMESTAMP, timestamp)
                .where(BCC.BCC_ID.eq(ULong.valueOf(bcc.getBccId())))
                .execute();

        BccRecord history = dslContext.selectFrom(Tables.BCC)
                .where(BCC.BCC_ID.eq(ULong.valueOf(bcc.getBccId())))
                .orderBy(BCC.BCC_ID.desc()).limit(1).fetchOne();

        history.setBccId(null);
        history.setEntityType(bcc.getEntityType());
        history.setCardinalityMin(bcc.getCardinalityMin());
        history.setCardinalityMax(bcc.getCardinalityMax());
        history.setIsNillable((byte) ((bcc.isNillable()) ? 1 : 0));
        history.setIsDeprecated((byte) ((bcc.isDeprecated()) ? 1 : 0));
        history.setDefaultValue(bcc.getDefaultValue());
        history.setDefinition(bcc.getDefinition());
        history.setRevisionTrackingNum(history.getRevisionTrackingNum() + 1);
        history.setRevisionAction((byte) RevisionAction.Update.getValue());
        history.setCreatedBy(userId);
        history.setLastUpdatedBy(userId);
        history.setCreationTimestamp(timestamp);
        history.setLastUpdateTimestamp(timestamp);

        dslContext.insertInto(BCC).set(history).execute();

        return (result == 1);
    }

    @Transactional
    public void transferOwnership(User user, long releaseId, long extensionId, String targetLoginId) {
        long targetAppUserId = dslContext.select(APP_USER.APP_USER_ID)
                .from(APP_USER)
                .where(APP_USER.LOGIN_ID.eq(targetLoginId))
                .fetchOptionalInto(Long.class).orElse(0L);
        if (targetAppUserId == 0L) {
            throw new IllegalArgumentException("Not found a target user.");
        }

        ULong target = ULong.valueOf(targetAppUserId);
        ULong userId = ULong.valueOf(sessionService.userId(user));
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        updateAccOwnerUserId(extensionId, releaseId, target, userId, timestamp);
        updateAsccOwnerUserId(extensionId, releaseId, target, userId, timestamp);
        updateBccOwnerUserId(extensionId, releaseId, target, userId, timestamp);
    }

    private void updateAccOwnerUserId(long extensionId, Long releaseId, ULong targetAppUserId,
                                      ULong userId, Timestamp timestamp) {
        dslContext.update(Tables.ACC)
                .set(ACC.OWNER_USER_ID, targetAppUserId)
                .set(ACC.LAST_UPDATED_BY, userId)
                .set(ACC.LAST_UPDATE_TIMESTAMP, timestamp)
                .where(ACC.ACC_ID.eq(ULong.valueOf(extensionId)))
                .execute();

        AccRecord history = dslContext.selectFrom(Tables.ACC)
                .where(ACC.ACC_ID.eq(ULong.valueOf(extensionId)))
                .orderBy(ACC.ACC_ID.desc()).limit(1)
                .fetchOne();

        history.setAccId(null);
        history.setRevisionTrackingNum(history.getRevisionTrackingNum() + 1);
        history.setRevisionAction((byte) RevisionAction.Update.getValue());
        history.setCreatedBy(userId);
        history.setLastUpdatedBy(userId);
        history.setCreationTimestamp(timestamp);
        history.setLastUpdateTimestamp(timestamp);
        history.setOwnerUserId(targetAppUserId);
        dslContext.insertInto(Tables.ACC).set(history).execute();
    }

    private void updateAsccOwnerUserId(long extensionId, Long releaseId, ULong targetAppUserId,
                                       ULong userId, Timestamp timestamp) {
        List<CcAsccNode> asccNodes = dslContext.select(
                ASCC.ASCC_ID,
                ASCC.GUID,
                ASCC.REVISION_NUM,
                ASCC.REVISION_TRACKING_NUM,
                ASCC_RELEASE_MANIFEST.RELEASE_ID)
                .from(ASCC)
                .join(ASCC_RELEASE_MANIFEST)
                .on(ASCC.ASCC_ID.eq(ASCC_RELEASE_MANIFEST.ASCC_ID))
                .where(and(
                        ASCC.FROM_ACC_ID.eq(ULong.valueOf(extensionId)),
                        ASCC.REVISION_NUM.greaterThan(0)))
                .fetchInto(CcAsccNode.class);

        if (asccNodes.isEmpty()) {
            return;
        }

        // Update a state of the 'current record'.
        dslContext.update(ASCC)
                .set(ASCC.OWNER_USER_ID, targetAppUserId)
                .set(ASCC.LAST_UPDATED_BY, userId)
                .set(ASCC.LAST_UPDATE_TIMESTAMP, timestamp)
                .where(and(
                        ASCC.FROM_ACC_ID.eq(ULong.valueOf(extensionId)),
                        ASCC.REVISION_NUM.eq(0)))
                .execute();

        List<ULong> asccIds = asccNodes.stream()
                .map(asccNode -> ULong.valueOf(asccNode.getAsccId()))
                .collect(Collectors.toList());

        Result<AsccRecord> asccRecordResult = dslContext.selectFrom(ASCC)
                .where(ASCC.ASCC_ID.in(asccIds))
                .fetch();

        for (AsccRecord history : asccRecordResult) {
            history.setAsccId(null);
            history.setRevisionTrackingNum(history.getRevisionTrackingNum() + 1);
            history.setRevisionAction((byte) RevisionAction.Update.getValue());
            history.setCreatedBy(userId);
            history.setLastUpdatedBy(userId);
            history.setCreationTimestamp(timestamp);
            history.setLastUpdateTimestamp(timestamp);
            history.setOwnerUserId(targetAppUserId);

            dslContext.insertInto(ASCC).set(history).execute();
        }
    }

    private void updateBccOwnerUserId(long extensionId, Long releaseId, ULong targetAppUserId,
                                      ULong userId, Timestamp timestamp) {
        List<CcBccNode> bccNodes = dslContext.select(
                BCC.BCC_ID,
                BCC.GUID,
                BCC.REVISION_NUM,
                BCC.REVISION_TRACKING_NUM,
                BCC_RELEASE_MANIFEST.RELEASE_ID)
                .from(BCC)
                .join(BCC_RELEASE_MANIFEST)
                .on(BCC.BCC_ID.eq(BCC_RELEASE_MANIFEST.BCC_ID))
                .where(and(
                        BCC.FROM_ACC_ID.eq(ULong.valueOf(extensionId)),
                        BCC.REVISION_NUM.greaterThan(0)))
                .fetchInto(CcBccNode.class);

        if (bccNodes.isEmpty()) {
            return;
        }

        // Update a state of the 'current record'.
        dslContext.update(BCC)
                .set(BCC.OWNER_USER_ID, targetAppUserId)
                .set(BCC.LAST_UPDATED_BY, userId)
                .set(BCC.LAST_UPDATE_TIMESTAMP, timestamp)
                .where(and(
                        BCC.FROM_ACC_ID.eq(ULong.valueOf(extensionId)),
                        BCC.REVISION_NUM.eq(0)))
                .execute();

        List<ULong> bccIds = bccNodes.stream()
                .map(bccNode -> ULong.valueOf(bccNode.getBccId()))
                .collect(Collectors.toList());

        Result<BccRecord> bccRecordResult = dslContext.selectFrom(BCC)
                .where(BCC.BCC_ID.in(bccIds))
                .fetch();

        for (BccRecord history : bccRecordResult) {
            history.setBccId(null);
            history.setRevisionTrackingNum(history.getRevisionTrackingNum() + 1);
            history.setRevisionAction((byte) RevisionAction.Update.getValue());
            history.setCreatedBy(userId);
            history.setLastUpdatedBy(userId);
            history.setCreationTimestamp(timestamp);
            history.setLastUpdateTimestamp(timestamp);
            history.setOwnerUserId(targetAppUserId);

            dslContext.insertInto(BCC).set(history).execute();
        }
    }
}
