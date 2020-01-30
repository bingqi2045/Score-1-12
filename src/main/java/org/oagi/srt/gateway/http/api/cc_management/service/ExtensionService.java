package org.oagi.srt.gateway.http.api.cc_management.service;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.jooq.types.ULong;
import org.oagi.srt.data.ACC;
import org.oagi.srt.data.OagisComponentType;
import org.oagi.srt.data.RevisionAction;
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
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.and;
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

    private AccReleaseManifestRecord getExtensionAcc(long manifestId) {
        return dslContext.selectFrom(ACC_RELEASE_MANIFEST)
                .where(ACC_RELEASE_MANIFEST.ACC_RELEASE_MANIFEST_ID.eq(ULong.valueOf(manifestId)))
                .fetchOptional().orElse(null);
    }

    public CcAccNode getExtensionNode(User user, long manifestId) {
        AccReleaseManifestRecord extensionAcc = getExtensionAcc(manifestId);

        CcAccNode ueAcc = repository.getAccNodeByAccId(user, extensionAcc);
        CcAsccpNode asccpNode = repository.getAsccpNodeByRoleOfAccId(ueAcc.getAccId(), extensionAcc.getReleaseId());
        CcAccNode eAcc = repository.getAccNodeFromAsccByAsccpId(user, asccpNode.getAsccpId(), extensionAcc.getReleaseId());
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
                dslContext.select(ACC.fields())
                .from(ACC.as("eAcc"))
                        .join(ACC_RELEASE_MANIFEST.as("eACCRM")).on(
                                and(ACC.as("eAcc").ACC_ID.eq(ACC_RELEASE_MANIFEST.as("eACCRM").ACC_ID),
                                        ACC_RELEASE_MANIFEST.as("eACCRM").RELEASE_ID.eq(ULong.valueOf(releaseId))))
                        .join(ASCC_RELEASE_MANIFEST).on(ACC_RELEASE_MANIFEST.as("eACCRM").ACC_ID.eq(ASCC_RELEASE_MANIFEST.FROM_ACC_ID))
                        .join(ASCCP_RELEASE_MANIFEST).on(ASCC_RELEASE_MANIFEST.TO_ASCCP_ID.eq(ASCCP_RELEASE_MANIFEST.ASCCP_ID))
                        .join(ACC_RELEASE_MANIFEST).on(ACC_RELEASE_MANIFEST.ACC_ID.eq(ASCCP_RELEASE_MANIFEST.ROLE_OF_ACC_ID))
                        .join(ACC).on(ACC_RELEASE_MANIFEST.ACC_ID.eq(ACC.ACC_ID))
                .where(and(ACC.as("eAcc").ACC_ID.eq(ULong.valueOf(accId)),
                        ACC.OAGIS_COMPONENT_TYPE.eq(OagisComponentType.UserExtensionGroup.getValue())
                )).fetchOneInto(ACC.class);
        return ueAcc;
    }

    @Transactional
    public long appendUserExtension(BieEditAcc eAcc, ACC ueAcc,
                                    long releaseId, User user) {
        if (ueAcc != null) {
            if (CcState.Published.getValue() == ueAcc.getState()) {
                return increaseRevisionNum(ueAcc, releaseId, user);
            } else {
                AccReleaseManifestRecord ueAccManifest = repository.getAccReleaseManifestByAcc(ueAcc.getAccId(), releaseId);
                return ueAccManifest.getAccReleaseManifestId().longValue();
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

        AccReleaseManifestRecord accManifest = repository.getAccReleaseManifestByAcc(ueAcc.getAccId(), releaseId);

        return accManifest.getAccReleaseManifestId().longValue();
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

        AccRecord newUeAcc = dslContext.insertInto(Tables.ACC).set(history).returning().fetchOne();

        dslContext.update(ACC_RELEASE_MANIFEST)
                .set(ACC_RELEASE_MANIFEST.ACC_ID, newUeAcc.getAccId())
                .where(ACC_RELEASE_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId))
                        .and(ACC_RELEASE_MANIFEST.ACC_ID.eq(ULong.valueOf(ueAcc.getAccId()))))
                .execute();


        dslContext.update(Tables.ACC)
                .set(Tables.ACC.STATE, history.getState())
                .set(Tables.ACC.OWNER_USER_ID, userId)
                .set(Tables.ACC.LAST_UPDATED_BY, userId)
                .set(Tables.ACC.LAST_UPDATE_TIMESTAMP, timestamp)
                .where(Tables.ACC.ACC_ID.eq(ULong.valueOf(ueAcc.getAccId())))
                .execute();

        dslContext.insertInto(ACC_RELEASE_MANIFEST).set(history).execute();

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
            long historyAsccId = history.getAsccId().longValue();
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

            AsccRecord asccRecord = dslContext.insertInto(ASCC).set(history).returning().fetchOne();

            dslContext.update(ASCC_RELEASE_MANIFEST)
                    .set(ASCC_RELEASE_MANIFEST.ASCC_ID, asccRecord.getAsccId())
                    .where(ASCC_RELEASE_MANIFEST.ASCC_ID.eq(ULong.valueOf(historyAsccId))
                            .and(ASCC_RELEASE_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId))));
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
            long historyBccId = history.getBccId().longValue();
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

            BccRecord bccRecord = dslContext.insertInto(BCC).set(history).returning().fetchOne();
            dslContext.update(BCC_RELEASE_MANIFEST)
                    .set(BCC_RELEASE_MANIFEST.BCC_ID, bccRecord.getBccId())
                    .where(BCC_RELEASE_MANIFEST.BCC_ID.eq(ULong.valueOf(historyBccId))
                            .and(BCC_RELEASE_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId))));
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
        AccReleaseManifestRecord ueAccManifest = createACCReleaseManifestForExtension(ueAcc, releaseId);

        AsccpRecord ueAsccp = createASCCPForExtension(eAcc, user, ueAcc);
        createASCCPReleaseManifestForExtension(ueAsccp, releaseId);

        AsccRecord ueAscc = createASCCForExtension(eAcc, ueAsccp, user);
        createASCCReleaseManifestForExtension(ueAscc, releaseId);

        return ueAccManifest.getAccReleaseManifestId().longValue();
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

    private AccReleaseManifestRecord createACCReleaseManifestForExtension(AccRecord ueAcc, long releaseId) {
        return dslContext.insertInto(ACC_RELEASE_MANIFEST,
                ACC_RELEASE_MANIFEST.ACC_ID,
                ACC_RELEASE_MANIFEST.BASED_ACC_ID,
                ACC_RELEASE_MANIFEST.RELEASE_ID
        ).values(
                ueAcc.getAccId(),
                ueAcc.getBasedAccId(),
                ULong.valueOf(releaseId)
        ).returning().fetchOne();
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
    public void appendAsccp(User user, long manifestId, long asccpManifestId) {
        AccReleaseManifestRecord extensionAcc = getExtensionAcc(manifestId);
        AsccpReleaseManifestRecord asccpReleaseManifestRecord =
                dslContext.selectFrom(ASCCP_RELEASE_MANIFEST)
                        .where(ASCCP_RELEASE_MANIFEST.ASCCP_RELEASE_MANIFEST_ID.eq(ULong.valueOf(asccpManifestId)))
                        .fetchOne();

        repository.appendAsccp(user, extensionAcc.getAccReleaseManifestId(),
                asccpReleaseManifestRecord.getAsccpReleaseManifestId());
    }

    @Transactional
    public void appendBccp(User user, long manifestId, long bccpManifestId) {
        AccReleaseManifestRecord extensionAcc = getExtensionAcc(manifestId);
        BccpReleaseManifestRecord bccpReleaseManifestRecord =
                dslContext.selectFrom(BCCP_RELEASE_MANIFEST)
                        .where(BCCP_RELEASE_MANIFEST.BCCP_RELEASE_MANIFEST_ID.eq(ULong.valueOf(bccpManifestId)))
                        .fetchOne();

        repository.appendBccp(user, extensionAcc.getAccReleaseManifestId(),
                bccpReleaseManifestRecord.getBccpReleaseManifestId());
    }

    @Transactional
    public void discardAscc(User user, long manifestId, long asccId) {
        AccReleaseManifestRecord extensionAcc = getExtensionAcc(manifestId);

        long asccManifestId = dslContext.select(ASCC_RELEASE_MANIFEST.ASCC_RELEASE_MANIFEST_ID)
                .from(ASCC_RELEASE_MANIFEST).where(and(ASCC_RELEASE_MANIFEST.FROM_ACC_ID.eq(extensionAcc.getAccId()),
                        ASCC_RELEASE_MANIFEST.ASCC_ID.eq(ULong.valueOf(asccId)),
                        ASCC_RELEASE_MANIFEST.RELEASE_ID.eq(extensionAcc.getReleaseId())))
                .fetchOneInto(long.class);

        repository.discardAsccById(user, asccManifestId);
    }

    @Transactional
    public void discardBcc(User user, long manifestId, long bccId) {
        AccReleaseManifestRecord extensionAcc = getExtensionAcc(manifestId);

        long bccManifestId = dslContext.select(BCC_RELEASE_MANIFEST.BCC_RELEASE_MANIFEST_ID)
                .from(BCC_RELEASE_MANIFEST).where(and(BCC_RELEASE_MANIFEST.FROM_ACC_ID.eq(extensionAcc.getAccId()),
                        BCC_RELEASE_MANIFEST.BCC_ID.eq(ULong.valueOf(bccId)),
                        BCC_RELEASE_MANIFEST.RELEASE_ID.eq(extensionAcc.getReleaseId())))
                .fetchOneInto(long.class);

        repository.discardBccById(user, bccManifestId);
    }

    @Transactional
    public void updateState(User user, long manifestId, CcState state) {
        repository.updateAccState(user, ULong.valueOf(manifestId), state);
    }

    @Transactional
    public ExtensionUpdateResponse updateDetails(User user, ExtensionUpdateRequest request) {
        ExtensionUpdateResponse response = new ExtensionUpdateResponse();

        AccReleaseManifestRecord extensionAcc = getExtensionAcc(request.getManifestId());
        ULong userId = ULong.valueOf(sessionService.userId(user));
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        List<CcAsccpNodeDetail.Ascc> asccList = request.getAsccpDetails().stream()
                .map(asccpDetail -> asccpDetail.getAscc())
                .collect(Collectors.toList());

        for (CcAsccpNodeDetail.Ascc ascc : asccList) {
            response.getAsccResults().put(ascc.getAsccId(),
                    updateAscc(extensionAcc, ascc, userId, timestamp)
            );
        }

        List<CcBccpNodeDetail.Bcc> bccList = request.getBccpDetails().stream()
                .map(bccpDetail -> bccpDetail.getBcc())
                .collect(Collectors.toList());

        for (CcBccpNodeDetail.Bcc bcc : bccList) {
            response.getBccResults().put(bcc.getBccId(),
                    updateBcc(extensionAcc, bcc, userId, timestamp)
            );
        }

        return response;
    }

    private boolean updateAscc(AccReleaseManifestRecord extensionAcc,
                               CcAsccpNodeDetail.Ascc ascc,
                               ULong userId, Timestamp timestamp) {

        String guid = dslContext.select(ASCC.GUID).from(ASCC)
                .where(ASCC.ASCC_ID.eq(ULong.valueOf(ascc.getAsccId())))
                .fetchOneInto(String.class);

        ULong asccId = dslContext.select(ASCC.ASCC_ID).from(ASCC)
                .where(ASCC.GUID.eq(guid))
                .orderBy(ASCC.ASCC_ID.desc()).limit(1).fetchOneInto(ULong.class);

        AsccRecord history = dslContext.selectFrom(Tables.ASCC)
                .where(ASCC.ASCC_ID.eq(asccId))
                .fetchOne();

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

        history = dslContext.insertInto(ASCC).set(history).returning().fetchOne();
        int result = dslContext.update(ASCC_RELEASE_MANIFEST)
                .set(ASCC_RELEASE_MANIFEST.ASCC_ID, history.getAsccId())
                .where(and(
                        ASCC_RELEASE_MANIFEST.ASCC_ID.eq(ULong.valueOf(ascc.getAsccId())),
                        ASCC_RELEASE_MANIFEST.RELEASE_ID.eq(extensionAcc.getReleaseId())
                )).execute();

        return (result == 1);
    }

    private boolean updateBcc(AccReleaseManifestRecord extensionAcc,
                              CcBccpNodeDetail.Bcc bcc,
                              ULong userId, Timestamp timestamp) {

        String guid = dslContext.select(BCC.GUID).from(BCC)
                .where(BCC.BCC_ID.eq(ULong.valueOf(bcc.getBccId())))
                .fetchOneInto(String.class);

        ULong bccId = dslContext.select(BCC.BCC_ID).from(BCC)
                .where(BCC.GUID.eq(guid))
                .orderBy(BCC.BCC_ID.desc()).limit(1).fetchOneInto(ULong.class);

        BccRecord history = dslContext.selectFrom(Tables.BCC)
                .where(BCC.BCC_ID.eq(bccId))
                .fetchOne();

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

        history = dslContext.insertInto(BCC).set(history).returning().fetchOne();
        int result = dslContext.update(BCC_RELEASE_MANIFEST)
                .set(BCC_RELEASE_MANIFEST.BCC_ID, history.getBccId())
                .where(and(
                        BCC_RELEASE_MANIFEST.BCC_ID.eq(ULong.valueOf(bcc.getBccId())),
                        BCC_RELEASE_MANIFEST.RELEASE_ID.eq(extensionAcc.getReleaseId())
                )).execute();

        return (result == 1);
    }

    @Transactional
    public void transferOwnership(User user, long accManifestId, String targetLoginId) {
        long targetAppUserId = dslContext.select(APP_USER.APP_USER_ID)
                .from(APP_USER)
                .where(APP_USER.LOGIN_ID.equalIgnoreCase(targetLoginId))
                .fetchOptionalInto(Long.class).orElse(0L);
        if (targetAppUserId == 0L) {
            throw new IllegalArgumentException("Not found a target user.");
        }

        AccReleaseManifestRecord accReleaseManifest =
                dslContext.selectFrom(ACC_RELEASE_MANIFEST)
                        .where(ACC_RELEASE_MANIFEST.ACC_RELEASE_MANIFEST_ID.eq(ULong.valueOf(accManifestId)))
                        .fetchOptional().orElse(null);
        if (accReleaseManifest == null) {
            throw new IllegalArgumentException("Not found a target ACC.");
        }

        ULong target = ULong.valueOf(targetAppUserId);
        ULong userId = ULong.valueOf(sessionService.userId(user));
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        updateAsccOwnerUserId(accReleaseManifest, target, userId, timestamp);
        updateBccOwnerUserId(accReleaseManifest, target, userId, timestamp);
        updateAccOwnerUserId(accReleaseManifest, target, userId, timestamp);
    }

    private void updateAccOwnerUserId(AccReleaseManifestRecord accReleaseManifest,
                                      ULong targetAppUserId,
                                      ULong userId, Timestamp timestamp) {

        AccRecord history = dslContext.selectFrom(Tables.ACC)
                .where(ACC.ACC_ID.eq(accReleaseManifest.getAccId()))
                .fetchOne();

        history.setAccId(null);
        history.setRevisionTrackingNum(history.getRevisionTrackingNum() + 1);
        history.setRevisionAction((byte) RevisionAction.Update.getValue());
        history.setCreatedBy(userId);
        history.setLastUpdatedBy(userId);
        history.setCreationTimestamp(timestamp);
        history.setLastUpdateTimestamp(timestamp);
        history.setOwnerUserId(targetAppUserId);

        history = dslContext.insertInto(Tables.ACC).set(history).returning().fetchOne();
        dslContext.update(ACC_RELEASE_MANIFEST)
                .set(ACC_RELEASE_MANIFEST.ACC_ID, history.getAccId())
                .where(ACC_RELEASE_MANIFEST.ACC_RELEASE_MANIFEST_ID.eq(accReleaseManifest.getAccReleaseManifestId()))
                .execute();

        dslContext.update(ASCC_RELEASE_MANIFEST)
                .set(ASCC_RELEASE_MANIFEST.FROM_ACC_ID, history.getAccId())
                .where(and(
                        ASCC_RELEASE_MANIFEST.FROM_ACC_ID.eq(accReleaseManifest.getAccId()),
                        ASCC_RELEASE_MANIFEST.RELEASE_ID.eq(accReleaseManifest.getReleaseId())
                ))
                .execute();

        dslContext.update(BCC_RELEASE_MANIFEST)
                .set(BCC_RELEASE_MANIFEST.FROM_ACC_ID, history.getAccId())
                .where(and(
                        BCC_RELEASE_MANIFEST.FROM_ACC_ID.eq(accReleaseManifest.getAccId()),
                        BCC_RELEASE_MANIFEST.RELEASE_ID.eq(accReleaseManifest.getReleaseId())
                ))
                .execute();
    }

    private void updateAsccOwnerUserId(AccReleaseManifestRecord accReleaseManifest,
                                       ULong targetAppUserId,
                                       ULong userId, Timestamp timestamp) {

        Map<ULong, AsccReleaseManifestRecord> asccReleaseManifestRecordMap =
                dslContext.selectFrom(ASCC_RELEASE_MANIFEST)
                        .where(ASCC_RELEASE_MANIFEST.FROM_ACC_ID.eq(accReleaseManifest.getAccId()))
                        .fetch().stream().collect(Collectors.toMap(AsccReleaseManifestRecord::getAsccId, Function.identity()));

        if (asccReleaseManifestRecordMap.isEmpty()) {
            return;
        }

        Result<AsccRecord> asccRecordResult = dslContext.selectFrom(ASCC)
                .where(ASCC.ASCC_ID.in(asccReleaseManifestRecordMap.keySet()))
                .fetch();

        for (AsccRecord history : asccRecordResult) {
            AsccReleaseManifestRecord asccReleaseManifestRecord =
                    asccReleaseManifestRecordMap.get(history.getAsccId());

            history.setAsccId(null);
            history.setRevisionTrackingNum(history.getRevisionTrackingNum() + 1);
            history.setRevisionAction((byte) RevisionAction.Update.getValue());
            history.setCreatedBy(userId);
            history.setLastUpdatedBy(userId);
            history.setCreationTimestamp(timestamp);
            history.setLastUpdateTimestamp(timestamp);
            history.setOwnerUserId(targetAppUserId);

            history = dslContext.insertInto(ASCC).set(history).returning().fetchOne();
            dslContext.update(ASCC_RELEASE_MANIFEST)
                    .set(ASCC_RELEASE_MANIFEST.ASCC_ID, history.getAsccId())
                    .where(ASCC_RELEASE_MANIFEST.ASCC_RELEASE_MANIFEST_ID.eq(asccReleaseManifestRecord.getAsccReleaseManifestId()))
                    .execute();
        }
    }

    private void updateBccOwnerUserId(AccReleaseManifestRecord accReleaseManifest,
                                      ULong targetAppUserId,
                                      ULong userId, Timestamp timestamp) {

        Map<ULong, BccReleaseManifestRecord> bccReleaseManifestRecordMap =
                dslContext.selectFrom(BCC_RELEASE_MANIFEST)
                        .where(BCC_RELEASE_MANIFEST.FROM_ACC_ID.eq(accReleaseManifest.getAccId()))
                        .fetch().stream().collect(Collectors.toMap(BccReleaseManifestRecord::getBccId, Function.identity()));

        if (bccReleaseManifestRecordMap.isEmpty()) {
            return;
        }

        Result<BccRecord> bccRecordResult = dslContext.selectFrom(BCC)
                .where(BCC.BCC_ID.in(bccReleaseManifestRecordMap.keySet()))
                .fetch();

        for (BccRecord history : bccRecordResult) {
            BccReleaseManifestRecord bccReleaseManifestRecord =
                    bccReleaseManifestRecordMap.get(history.getBccId());

            history.setBccId(null);
            history.setRevisionTrackingNum(history.getRevisionTrackingNum() + 1);
            history.setRevisionAction((byte) RevisionAction.Update.getValue());
            history.setCreatedBy(userId);
            history.setLastUpdatedBy(userId);
            history.setCreationTimestamp(timestamp);
            history.setLastUpdateTimestamp(timestamp);
            history.setOwnerUserId(targetAppUserId);

            history = dslContext.insertInto(BCC).set(history).returning().fetchOne();
            dslContext.update(BCC_RELEASE_MANIFEST)
                    .set(BCC_RELEASE_MANIFEST.BCC_ID, history.getBccId())
                    .where(BCC_RELEASE_MANIFEST.BCC_RELEASE_MANIFEST_ID.eq(bccReleaseManifestRecord.getBccReleaseManifestId()))
                    .execute();
        }
    }

    public CcNode getLastRevisionCc(User user, String type, long manifestId) {
        if (type.equals("ascc")) {
            return null;
        } else if (type.equals("bcc")) {
            return null;
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
