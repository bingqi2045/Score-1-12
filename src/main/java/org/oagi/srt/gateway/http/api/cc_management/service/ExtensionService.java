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
import org.oagi.srt.gateway.http.api.cc_management.repository.ManifestRepository;
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
    private ManifestRepository manifestRepository;

    @Autowired
    private CcListService ccListService;

    private AccManifestRecord getExtensionAcc(long manifestId) {
        return dslContext.selectFrom(ACC_MANIFEST)
                .where(ACC_MANIFEST.ACC_MANIFEST_ID.eq(ULong.valueOf(manifestId)))
                .fetchOptional().orElse(null);
    }

    public CcAccNode getExtensionNode(User user, long manifestId) {
        AccManifestRecord extensionAcc = getExtensionAcc(manifestId);

        CcAccNode ueAcc = repository.getAccNodeByAccManifestId(user, extensionAcc.getAccManifestId());
        CcAsccpNode asccpNode = repository.getAsccpNodeByRoleOfAccId(ueAcc.getAccId(), extensionAcc.getReleaseId());
        if (asccpNode == null) {
            return null;
        }
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
                        .join(ACC_MANIFEST.as("eACCRM")).on(and(
                                ACC.as("eAcc").ACC_ID.eq(ACC_MANIFEST.as("eACCRM").ACC_ID),
                                ACC_MANIFEST.as("eACCRM").RELEASE_ID.eq(ULong.valueOf(releaseId))
                        ))
                        .join(ASCC_MANIFEST).on(ACC_MANIFEST.as("eACCRM").ACC_MANIFEST_ID.eq(ASCC_MANIFEST.FROM_ACC_MANIFEST_ID))
                        .join(ASCCP_MANIFEST).on(ASCC_MANIFEST.TO_ASCCP_MANIFEST_ID.eq(ASCCP_MANIFEST.ASCCP_MANIFEST_ID))
                        .join(ACC_MANIFEST).on(ACC_MANIFEST.ACC_MANIFEST_ID.eq(ASCCP_MANIFEST.ROLE_OF_ACC_MANIFEST_ID))
                        .join(ACC).on(ACC_MANIFEST.ACC_ID.eq(ACC.ACC_ID))
                        .where(and(
                                ACC.as("eAcc").ACC_ID.eq(ULong.valueOf(accId)),
                                ACC.OAGIS_COMPONENT_TYPE.eq(OagisComponentType.UserExtensionGroup.getValue())
                        )).fetchOneInto(ACC.class);
        return ueAcc;
    }

    @Transactional
    public long appendUserExtension(BieEditAcc eAcc, ACC ueAcc,
                                    long releaseId, User user) {
        if (ueAcc != null) {
            if (CcState.Published.getValue() == ueAcc.getState()) {
                AccManifestRecord accManifest = repository.getAccManifestByAcc(ueAcc.getAccId(), releaseId);
                CcAccNode acc = repository.updateAccState(user, accManifest.getAccManifestId(), CcState.Editing);
                return acc.getManifestId();
            } else {
                AccManifestRecord ueAccManifest = repository.getAccManifestByAcc(ueAcc.getAccId(), releaseId);
                return ueAccManifest.getAccManifestId().longValue();
            }
        } else {
            return createNewUserExtensionGroupACC(ccListService.getAcc(eAcc.getAccId()), releaseId, user);
        }
    }

    private long createNewUserExtensionGroupACC(ACC eAcc, long releaseId, User user) {
        AccRecord ueAcc = createACCForExtension(eAcc, user);
        AccManifestRecord ueAccManifest = createACCManifestForExtension(ueAcc, releaseId);

        AsccpRecord ueAsccp = createASCCPForExtension(eAcc, user, ueAcc);
        AsccpManifestRecord ueAsccpManifest =
                createASCCPManifestForExtension(ueAsccp, ueAccManifest, releaseId);

        AsccRecord ueAscc = createASCCForExtension(eAcc, ueAsccp, user);
        AccManifestRecord eAccManifest = dslContext.selectFrom(ACC_MANIFEST)
                .where(and(
                        ACC_MANIFEST.ACC_ID.eq(ULong.valueOf(eAcc.getAccId())),
                        ACC_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId))
                )).fetchOne();
        createASCCManifestForExtension(ueAscc, eAccManifest, ueAsccpManifest, releaseId);

        return ueAccManifest.getAccManifestId().longValue();
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

    private AccManifestRecord createACCManifestForExtension(AccRecord ueAcc, long releaseId) {
        return dslContext.insertInto(ACC_MANIFEST,
                ACC_MANIFEST.ACC_ID,
                ACC_MANIFEST.RELEASE_ID
        ).values(
                ueAcc.getAccId(),
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

    private AsccpManifestRecord createASCCPManifestForExtension(
            AsccpRecord ueAsccp, AccManifestRecord ueAccManifest, long releaseId) {
        return dslContext.insertInto(ASCCP_MANIFEST,
                ASCCP_MANIFEST.ASCCP_ID,
                ASCCP_MANIFEST.ROLE_OF_ACC_MANIFEST_ID,
                ASCCP_MANIFEST.RELEASE_ID
        ).values(
                ueAsccp.getAsccpId(),
                ueAccManifest.getAccManifestId(),
                ULong.valueOf(releaseId)
        ).returning().fetchOne();
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

    private void createASCCManifestForExtension(
            AsccRecord ueAscc, AccManifestRecord eAccManifest, AsccpManifestRecord ueAsccpManifest, long releaseId) {
        dslContext.insertInto(ASCC_MANIFEST,
                ASCC_MANIFEST.ASCC_ID,
                ASCC_MANIFEST.FROM_ACC_MANIFEST_ID,
                ASCC_MANIFEST.TO_ASCCP_MANIFEST_ID,
                ASCC_MANIFEST.RELEASE_ID
        ).values(
                ueAscc.getAsccId(),
                eAccManifest.getAccManifestId(),
                ueAsccpManifest.getAsccpManifestId(),
                ULong.valueOf(releaseId)
        ).execute();
    }

    @Transactional
    public void appendAsccp(User user, long manifestId, long asccpManifestId) {
        AccManifestRecord extensionAcc = getExtensionAcc(manifestId);
        AsccpManifestRecord asccpManifestRecord =
                dslContext.selectFrom(ASCCP_MANIFEST)
                        .where(ASCCP_MANIFEST.ASCCP_MANIFEST_ID.eq(ULong.valueOf(asccpManifestId)))
                        .fetchOne();

        repository.appendAsccp(user, extensionAcc.getAccManifestId(),
                asccpManifestRecord.getAsccpManifestId());
    }

    @Transactional
    public void appendBccp(User user, long manifestId, long bccpManifestId) {
        AccManifestRecord extensionAcc = getExtensionAcc(manifestId);
        BccpManifestRecord bccpManifestRecord =
                dslContext.selectFrom(BCCP_MANIFEST)
                        .where(BCCP_MANIFEST.BCCP_MANIFEST_ID.eq(ULong.valueOf(bccpManifestId)))
                        .fetchOne();

        repository.appendBccp(user, extensionAcc.getAccManifestId(),
                bccpManifestRecord.getBccpManifestId());
    }

    @Transactional
    public void discardAscc(User user, long manifestId, long asccId) {
        AccManifestRecord extensionAcc = getExtensionAcc(manifestId);

        long asccManifestId = dslContext.select(ASCC_MANIFEST.ASCC_MANIFEST_ID)
                .from(ASCC_MANIFEST)
                .join(ACC_MANIFEST).on(ASCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(ACC_MANIFEST.ACC_MANIFEST_ID))
                .where(and(ACC_MANIFEST.ACC_ID.eq(extensionAcc.getAccId()),
                        ASCC_MANIFEST.ASCC_ID.eq(ULong.valueOf(asccId)),
                        ASCC_MANIFEST.RELEASE_ID.eq(extensionAcc.getReleaseId())
                ))
                .fetchOneInto(long.class);

        repository.discardAsccById(user, asccManifestId);
    }

    @Transactional
    public void discardBcc(User user, long manifestId, long bccId) {
        AccManifestRecord extensionAcc = getExtensionAcc(manifestId);

        long bccManifestId = dslContext.select(BCC_MANIFEST.BCC_MANIFEST_ID)
                .from(BCC_MANIFEST)
                .join(ACC_MANIFEST).on(BCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(ACC_MANIFEST.ACC_MANIFEST_ID))
                .where(and(ACC_MANIFEST.ACC_ID.eq(extensionAcc.getAccId()),
                        BCC_MANIFEST.BCC_ID.eq(ULong.valueOf(bccId)),
                        BCC_MANIFEST.RELEASE_ID.eq(extensionAcc.getReleaseId())))
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

        AccManifestRecord extensionAcc = getExtensionAcc(request.getManifestId());
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

    private boolean updateAscc(AccManifestRecord extensionAcc,
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
        history.setDefinitionSource(ascc.getDefinitionSource());
        history.setRevisionTrackingNum(history.getRevisionTrackingNum() + 1);
        history.setRevisionAction((byte) RevisionAction.Update.getValue());
        history.setCreatedBy(userId);
        history.setLastUpdatedBy(userId);
        history.setCreationTimestamp(timestamp);
        history.setLastUpdateTimestamp(timestamp);

        history = dslContext.insertInto(ASCC).set(history).returning().fetchOne();
        int result = dslContext.update(ASCC_MANIFEST)
                .set(ASCC_MANIFEST.ASCC_ID, history.getAsccId())
                .where(and(
                        ASCC_MANIFEST.ASCC_ID.eq(ULong.valueOf(ascc.getAsccId())),
                        ASCC_MANIFEST.RELEASE_ID.eq(extensionAcc.getReleaseId())
                )).execute();

        return (result == 1);
    }

    private boolean updateBcc(AccManifestRecord extensionAcc,
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
        history.setDefinitionSource(bcc.getDefinitionSource());
        history.setRevisionTrackingNum(history.getRevisionTrackingNum() + 1);
        history.setRevisionAction((byte) RevisionAction.Update.getValue());
        history.setCreatedBy(userId);
        history.setLastUpdatedBy(userId);
        history.setCreationTimestamp(timestamp);
        history.setLastUpdateTimestamp(timestamp);

        history = dslContext.insertInto(BCC).set(history).returning().fetchOne();
        int result = dslContext.update(BCC_MANIFEST)
                .set(BCC_MANIFEST.BCC_ID, history.getBccId())
                .where(and(
                        BCC_MANIFEST.BCC_ID.eq(ULong.valueOf(bcc.getBccId())),
                        BCC_MANIFEST.RELEASE_ID.eq(extensionAcc.getReleaseId())
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

        AccManifestRecord accManifest =
                dslContext.selectFrom(ACC_MANIFEST)
                        .where(ACC_MANIFEST.ACC_MANIFEST_ID.eq(ULong.valueOf(accManifestId)))
                        .fetchOptional().orElse(null);
        if (accManifest == null) {
            throw new IllegalArgumentException("Not found a target ACC.");
        }

        ULong target = ULong.valueOf(targetAppUserId);
        ULong userId = ULong.valueOf(sessionService.userId(user));
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        updateAsccOwnerUserId(accManifest, target, userId, timestamp);
        updateBccOwnerUserId(accManifest, target, userId, timestamp);
        updateAccOwnerUserId(accManifest, target, userId, timestamp);
    }

    private void updateAccOwnerUserId(AccManifestRecord accManifest,
                                      ULong targetAppUserId,
                                      ULong userId, Timestamp timestamp) {

        AccRecord history = dslContext.selectFrom(Tables.ACC)
                .where(ACC.ACC_ID.eq(accManifest.getAccId()))
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
        dslContext.update(ACC_MANIFEST)
                .set(ACC_MANIFEST.ACC_ID, history.getAccId())
                .where(ACC_MANIFEST.ACC_MANIFEST_ID.eq(accManifest.getAccManifestId()))
                .execute();
    }

    private void updateAsccOwnerUserId(AccManifestRecord accManifest,
                                       ULong targetAppUserId,
                                       ULong userId, Timestamp timestamp) {

        Map<ULong, AsccManifestRecord> asccManifestRecordMap =
                dslContext.select(ASCC_MANIFEST.fields()).from(ASCC_MANIFEST)
                        .join(ACC_MANIFEST).on(ASCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(ACC_MANIFEST.ACC_MANIFEST_ID))
                        .where(ACC_MANIFEST.ACC_ID.eq(accManifest.getAccId()))
                        .fetchInto(AsccManifestRecord.class)
                        .stream().collect(Collectors.toMap(AsccManifestRecord::getAsccId, Function.identity()));

        if (asccManifestRecordMap.isEmpty()) {
            return;
        }

        Result<AsccRecord> asccRecordResult = dslContext.selectFrom(ASCC)
                .where(ASCC.ASCC_ID.in(asccManifestRecordMap.keySet()))
                .fetch();

        for (AsccRecord history : asccRecordResult) {
            AsccManifestRecord asccManifestRecord =
                    asccManifestRecordMap.get(history.getAsccId());

            history.setAsccId(null);
            history.setRevisionTrackingNum(history.getRevisionTrackingNum() + 1);
            history.setRevisionAction((byte) RevisionAction.Update.getValue());
            history.setCreatedBy(userId);
            history.setLastUpdatedBy(userId);
            history.setCreationTimestamp(timestamp);
            history.setLastUpdateTimestamp(timestamp);
            history.setOwnerUserId(targetAppUserId);

            history = dslContext.insertInto(ASCC).set(history).returning().fetchOne();
            dslContext.update(ASCC_MANIFEST)
                    .set(ASCC_MANIFEST.ASCC_ID, history.getAsccId())
                    .where(ASCC_MANIFEST.ASCC_MANIFEST_ID.eq(asccManifestRecord.getAsccManifestId()))
                    .execute();
        }
    }

    private void updateBccOwnerUserId(AccManifestRecord accManifest,
                                      ULong targetAppUserId,
                                      ULong userId, Timestamp timestamp) {

        Map<ULong, BccManifestRecord> bccManifestRecordMap =
                dslContext.select(BCC_MANIFEST.fields()).from(BCC_MANIFEST)
                        .join(ACC_MANIFEST).on(BCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(ACC_MANIFEST.ACC_MANIFEST_ID))
                        .where(ACC_MANIFEST.ACC_ID.eq(accManifest.getAccId()))
                        .fetchInto(BccManifestRecord.class)
                        .stream().collect(Collectors.toMap(BccManifestRecord::getBccId, Function.identity()));

        if (bccManifestRecordMap.isEmpty()) {
            return;
        }

        Result<BccRecord> bccRecordResult = dslContext.selectFrom(BCC)
                .where(BCC.BCC_ID.in(bccManifestRecordMap.keySet()))
                .fetch();

        for (BccRecord history : bccRecordResult) {
            BccManifestRecord bccManifestRecord =
                    bccManifestRecordMap.get(history.getBccId());

            history.setBccId(null);
            history.setRevisionTrackingNum(history.getRevisionTrackingNum() + 1);
            history.setRevisionAction((byte) RevisionAction.Update.getValue());
            history.setCreatedBy(userId);
            history.setLastUpdatedBy(userId);
            history.setCreationTimestamp(timestamp);
            history.setLastUpdateTimestamp(timestamp);
            history.setOwnerUserId(targetAppUserId);

            history = dslContext.insertInto(BCC).set(history).returning().fetchOne();
            dslContext.update(BCC_MANIFEST)
                    .set(BCC_MANIFEST.BCC_ID, history.getBccId())
                    .where(BCC_MANIFEST.BCC_MANIFEST_ID.eq(bccManifestRecord.getBccManifestId()))
                    .execute();
        }
    }

    public CcNode getLastRevisionCc(User user, String type, long manifestId) {

        if (type.equals("ascc")) {
            AsccManifestRecord asccManifest = manifestRepository.getAsccManifestById(manifestId);
            if (asccManifest == null) {
                return null;
            }
            String guid = dslContext.select(ASCC.GUID).from(ASCC)
                    .where(ASCC.ASCC_ID.eq(asccManifest.getAsccId()))
                    .fetchOneInto(String.class);
            return dslContext.select(
                    ASCC.ASCC_ID,
                    ASCC.GUID,
                    ASCC.REVISION_NUM,
                    ASCC.REVISION_TRACKING_NUM,
                    ASCC.CARDINALITY_MIN,
                    ASCC.CARDINALITY_MAX).from(ASCC)
                    .where(and(ASCC.GUID.eq(guid), ASCC.STATE.eq(CcState.Published.getValue())))
                    .orderBy(ASCC.ASCC_ID.desc()).limit(1)
                    .fetchOneInto(CcAsccNode.class);
        } else if (type.equals("bcc")) {
            BccManifestRecord bccManifest = manifestRepository.getBccManifestById(manifestId);
            if (bccManifest == null) {
                return null;
            }
            String guid = dslContext.select(BCC.GUID).from(BCC)
                    .where(BCC.BCC_ID.eq(bccManifest.getBccId()))
                    .fetchOneInto(String.class);
            return dslContext.select(BCC.BCC_ID,
                    BCC.GUID,
                    BCC.REVISION_NUM,
                    BCC.REVISION_TRACKING_NUM,
                    BCC.CARDINALITY_MIN,
                    BCC.CARDINALITY_MAX,
                    BCC.IS_NILLABLE.as("nillable")).from(BCC)
                    .where(and(BCC.GUID.eq(guid), BCC.STATE.eq(CcState.Published.getValue())))
                    .orderBy(BCC.BCC_ID.desc()).limit(1)
                    .fetchOneInto(CcBccNode.class);
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
