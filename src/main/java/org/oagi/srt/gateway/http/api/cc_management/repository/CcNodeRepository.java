package org.oagi.srt.gateway.http.api.cc_management.repository;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jooq.*;
import org.jooq.types.ULong;
import org.oagi.srt.data.BCCEntityType;
import org.oagi.srt.data.OagisComponentType;
import org.oagi.srt.data.RevisionAction;
import org.oagi.srt.data.SeqKeySupportable;
import org.oagi.srt.entity.jooq.tables.AccReleaseManifest;
import org.oagi.srt.entity.jooq.tables.AsccReleaseManifest;
import org.oagi.srt.entity.jooq.tables.records.*;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;
import org.oagi.srt.gateway.http.api.cc_management.data.node.*;
import org.oagi.srt.gateway.http.api.common.data.AccessPrivilege;
import org.oagi.srt.gateway.http.api.common.data.TrackableImpl;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.oagi.srt.gateway.http.helper.SrtGuid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.*;
import java.util.Comparator;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.*;
import static org.oagi.srt.data.BCCEntityType.*;
import static org.oagi.srt.entity.jooq.Tables.*;
import static org.oagi.srt.gateway.http.api.common.data.AccessPrivilege.*;

@Repository
public class CcNodeRepository {

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private ReleaseManifestRepository manifestRepository;

    @Autowired
    private SessionService sessionService;

    private SelectOnConditionStep<Record12<
                ULong, String, String, ULong, Integer,
                String, Integer, Integer, Integer, ULong, ULong, ULong>> getSelectJoinStepForAccNode() {
        return dslContext.select(
                ACC.ACC_ID,
                ACC.GUID,
                ACC.DEN.as("name"),
                ACC_RELEASE_MANIFEST.BASED_ACC_ID,
                ACC.OAGIS_COMPONENT_TYPE,
                ACC.OBJECT_CLASS_TERM,
                ACC.STATE.as("raw_state"),
                ACC.REVISION_NUM,
                ACC.REVISION_TRACKING_NUM,
                ACC_RELEASE_MANIFEST.RELEASE_ID,
                ACC.OWNER_USER_ID,
                ACC_RELEASE_MANIFEST.ACC_RELEASE_MANIFEST_ID.as("manifest_id"))
                .from(ACC)
                .join(ACC_RELEASE_MANIFEST)
                .on(ACC.ACC_ID.eq(ACC_RELEASE_MANIFEST.ACC_ID));
    }

    public CcAccNode getAccNodeByAccId(User user, long accId, long releaseId) {
        AccReleaseManifestRecord accReleaseManifestRecord =
                dslContext.selectFrom(ACC_RELEASE_MANIFEST)
                .where(and(
                        ACC_RELEASE_MANIFEST.ACC_ID.eq(ULong.valueOf(accId)),
                        ACC_RELEASE_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId))
                ))
                .fetchOne();

        return getAccNodeByAccId(user, accReleaseManifestRecord);
    }

    public CcAccNode getAccNodeByAccId(User user, AccReleaseManifestRecord accReleaseManifestRecord) {
        CcAccNode accNode = getSelectJoinStepForAccNode()
                .where(ACC_RELEASE_MANIFEST.ACC_RELEASE_MANIFEST_ID.eq(
                        accReleaseManifestRecord.getAccReleaseManifestId()))
                .fetchOneInto(CcAccNode.class);
        return arrangeAccNode(user, accNode, accReleaseManifestRecord.getReleaseId());
    }

    public CcAccNode getAccNodeFromAsccByAsccpId(User user, long toAsccpId, ULong releaseId) {
        CcAsccNode asccNode = dslContext.select(
                ASCC.ASCC_ID,
                ASCC_RELEASE_MANIFEST.FROM_ACC_ID,
                ASCC.SEQ_KEY,
                ASCC.REVISION_NUM,
                ASCC.REVISION_TRACKING_NUM,
                ASCC_RELEASE_MANIFEST.RELEASE_ID)
                .from(ASCC)
                .join(ASCC_RELEASE_MANIFEST)
                .on(ASCC.ASCC_ID.eq(ASCC_RELEASE_MANIFEST.ASCC_ID))
                .where(and(
                        ASCC_RELEASE_MANIFEST.TO_ASCCP_ID.eq(ULong.valueOf(toAsccpId)),
                        ASCC_RELEASE_MANIFEST.RELEASE_ID.eq(releaseId)
                ))
                .fetchOneInto(CcAsccNode.class);

        AccReleaseManifestRecord accReleaseManifestRecord = dslContext.selectFrom(ACC_RELEASE_MANIFEST)
                .where(and(
                        ACC_RELEASE_MANIFEST.ACC_ID.eq(ULong.valueOf(asccNode.getFromAccId())),
                        ACC_RELEASE_MANIFEST.RELEASE_ID.eq(releaseId)
                ))
                .fetchOne();

        return getAccNodeByAccId(user, accReleaseManifestRecord);
    }

    public void createAscc(User user, long accManifestId, long asccManifestId) {
        AccReleaseManifestRecord accReleaseManifestRecord =
                manifestRepository.getAccReleaseManifestById(accManifestId);
        AsccReleaseManifestRecord asccReleaseManifestRecord =
                manifestRepository.getAsccReleaseManifestById(asccManifestId);

        String accObjectClassTerm = dslContext.select(ACC.OBJECT_CLASS_TERM)
                .from(ACC).where(ACC.ACC_ID.eq(accReleaseManifestRecord.getAccId()))
                .fetchOneInto(String.class);

        String asccDen = dslContext.select(ASCC.DEN)
                .from(ASCC).where(ASCC.ASCC_ID.eq(asccReleaseManifestRecord.getAsccId()))
                .fetchOneInto(String.class);

        long toAsccpId = dslContext.select(ASCC.TO_ASCCP_ID)
                .from(ASCC).where(ASCC.ASCC_ID.eq(asccReleaseManifestRecord.getAsccId()))
                .fetchOneInto(long.class);

        // ULong releaseID = ULong.valueOf(releaseId);
        ULong userId = ULong.valueOf(sessionService.userId(user));
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        dslContext.insertInto(ASCC,
                ASCC.GUID,
                ASCC.CARDINALITY_MIN,
                ASCC.CARDINALITY_MAX,
                ASCC.SEQ_KEY,
                ASCC.FROM_ACC_ID,
                ASCC.TO_ASCCP_ID,
                ASCC.DEN,
                ASCC.IS_DEPRECATED,
                ASCC.CREATED_BY,
                ASCC.LAST_UPDATED_BY,
                ASCC.OWNER_USER_ID,
                ASCC.CREATION_TIMESTAMP,
                ASCC.LAST_UPDATE_TIMESTAMP,
                ASCC.STATE,
                ASCC.REVISION_NUM,
                ASCC.REVISION_TRACKING_NUM,
                ASCC.REVISION_ACTION).values(
                SrtGuid.randomGuid(),
                0,
                1,
                1,
                accReleaseManifestRecord.getAccId(),
                asccReleaseManifestRecord.getToAsccpId(),
                accObjectClassTerm + ". " + asccDen,
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

    /**
     * Create a new ACC record.
     * The release ID of this record should link to 'Working' release.
     *
     * @param requesterId a requester user ID
     * @param releaseId a release ID
     *
     * @return ACC release manifest ID
     */
    public long createAcc(long requesterId, long releaseId) {
        ULong userId = ULong.valueOf(requesterId);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        AccRecord accRecord = new AccRecord();
        accRecord.setGuid(SrtGuid.randomGuid());
        accRecord.setObjectClassTerm("A new ACC Object");
        accRecord.setDen(accRecord.getObjectClassTerm() + ". Details");
        accRecord.setOagisComponentType(OagisComponentType.Semantics.getValue());
        accRecord.setState(CcState.Editing.getValue());
        accRecord.setRevisionNum(1);
        accRecord.setRevisionTrackingNum(1);
        accRecord.setRevisionAction((byte) RevisionAction.Insert.getValue());
        accRecord.setCreatedBy(userId);
        accRecord.setLastUpdatedBy(userId);
        accRecord.setOwnerUserId(userId);
        accRecord.setCreationTimestamp(timestamp);
        accRecord.setLastUpdateTimestamp(timestamp);
        accRecord.setAccId(
                dslContext.insertInto(ACC)
                        .set(accRecord)
                        .returning(ACC.ACC_ID).fetchOne().getAccId()
        );

        AccReleaseManifestRecord accReleaseManifestRecord =
                new AccReleaseManifestRecord();
        accReleaseManifestRecord.setAccId(accRecord.getAccId());
        accReleaseManifestRecord.setReleaseId(ULong.valueOf(releaseId));

        return dslContext.insertInto(ACC_RELEASE_MANIFEST)
                .set(accReleaseManifestRecord)
                .returning(ACC_RELEASE_MANIFEST.ACC_RELEASE_MANIFEST_ID)
                .fetchOne().getAccReleaseManifestId().longValue();
    }

    public long createAsccp(long requesterId, long roleOfAccManifestId) {
        AccReleaseManifestRecord accReleaseManifestRecord =
                dslContext.selectFrom(ACC_RELEASE_MANIFEST)
                        .where(ACC_RELEASE_MANIFEST.ACC_RELEASE_MANIFEST_ID.eq(ULong.valueOf(roleOfAccManifestId)))
                        .fetchOneInto(AccReleaseManifestRecord.class);

        String accObjectClassTerm = dslContext.select(ACC.OBJECT_CLASS_TERM)
                .from(ACC)
                .where(ACC.ACC_ID.eq(accReleaseManifestRecord.getAccId()))
                .fetchOneInto(String.class);

        ULong userId = ULong.valueOf(requesterId);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        AsccpRecord asccpRecord = new AsccpRecord();
        asccpRecord.setGuid(SrtGuid.randomGuid());
        asccpRecord.setPropertyTerm("A new ASCCP property");
        asccpRecord.setRoleOfAccId(accReleaseManifestRecord.getAccId());
        asccpRecord.setDen(asccpRecord.getPropertyTerm() + ". " + accObjectClassTerm);
        asccpRecord.setState(CcState.Editing.getValue());
        asccpRecord.setReusableIndicator((byte) 0);
        asccpRecord.setIsDeprecated((byte) 0);
        asccpRecord.setIsNillable((byte) 0);
        asccpRecord.setRevisionNum(1);
        asccpRecord.setRevisionTrackingNum(1);
        asccpRecord.setRevisionAction((byte) RevisionAction.Insert.getValue());
        asccpRecord.setCreatedBy(userId);
        asccpRecord.setLastUpdatedBy(userId);
        asccpRecord.setOwnerUserId(userId);
        asccpRecord.setCreationTimestamp(timestamp);
        asccpRecord.setLastUpdateTimestamp(timestamp);
        asccpRecord.setAsccpId(
                dslContext.insertInto(ASCCP)
                        .set(asccpRecord)
                        .returning(ASCCP.ASCCP_ID).fetchOne().getAsccpId()
        );

        AsccpReleaseManifestRecord asccpReleaseManifestRecord =
                new AsccpReleaseManifestRecord();
        asccpReleaseManifestRecord.setAsccpId(asccpRecord.getAsccpId());
        asccpReleaseManifestRecord.setReleaseId(accReleaseManifestRecord.getReleaseId());
        asccpReleaseManifestRecord.setRoleOfAccId(accReleaseManifestRecord.getAccId());

        return dslContext.insertInto(ASCCP_RELEASE_MANIFEST)
                .set(asccpReleaseManifestRecord)
                .returning(ASCCP_RELEASE_MANIFEST.ASCCP_RELEASE_MANIFEST_ID)
                .fetchOne().getAsccpReleaseManifestId().longValue();
    }

    public long createBccp(long requesterId, long bdtManifestId) {
        DtReleaseManifestRecord dtReleaseManifestRecord =
                dslContext.selectFrom(DT_RELEASE_MANIFEST)
                        .where(DT_RELEASE_MANIFEST.DT_RELEASE_MANIFEST_ID.eq(ULong.valueOf(bdtManifestId)))
                        .fetchOneInto(DtReleaseManifestRecord.class);

        String dtDataTypeTerm = dslContext.select(DT.DATA_TYPE_TERM)
                .from(DT)
                .where(DT.DT_ID.eq(dtReleaseManifestRecord.getDtId()))
                .fetchOneInto(String.class);

        ULong userId = ULong.valueOf(requesterId);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        BccpRecord bccpRecord = new BccpRecord();
        bccpRecord.setGuid(SrtGuid.randomGuid());
        bccpRecord.setPropertyTerm("A new BCCP property");
        bccpRecord.setRepresentationTerm(dtDataTypeTerm);
        bccpRecord.setBdtId(dtReleaseManifestRecord.getDtId());
        bccpRecord.setDen(bccpRecord.getPropertyTerm() + ". " + dtDataTypeTerm);
        bccpRecord.setState(CcState.Editing.getValue());
        bccpRecord.setIsDeprecated((byte) 0);
        bccpRecord.setIsNillable((byte) 0);
        bccpRecord.setRevisionNum(1);
        bccpRecord.setRevisionTrackingNum(1);
        bccpRecord.setRevisionAction(RevisionAction.Insert.getValue());
        bccpRecord.setCreatedBy(userId);
        bccpRecord.setLastUpdatedBy(userId);
        bccpRecord.setOwnerUserId(userId);
        bccpRecord.setCreationTimestamp(timestamp);
        bccpRecord.setLastUpdateTimestamp(timestamp);
        bccpRecord.setBccpId(
                dslContext.insertInto(BCCP)
                        .set(bccpRecord)
                        .returning(BCCP.BCCP_ID).fetchOne().getBccpId()
        );

        BccpReleaseManifestRecord bccpReleaseManifestRecord =
                new BccpReleaseManifestRecord();
        bccpReleaseManifestRecord.setBccpId(bccpRecord.getBccpId());
        bccpReleaseManifestRecord.setReleaseId(dtReleaseManifestRecord.getReleaseId());
        bccpReleaseManifestRecord.setBdtId(dtReleaseManifestRecord.getDtId());

        return dslContext.insertInto(BCCP_RELEASE_MANIFEST)
                .set(bccpReleaseManifestRecord)
                .returning(BCCP_RELEASE_MANIFEST.BCCP_RELEASE_MANIFEST_ID)
                .fetchOne().getBccpReleaseManifestId().longValue();
    }

    public CcAccNodeDetail updateAcc(User user, CcAccNodeDetail accNodeDetail) {
        long userId = sessionService.userId(user);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        AccReleaseManifestRecord accReleaseManifestRecord = dslContext.selectFrom(ACC_RELEASE_MANIFEST)
                .where(ACC_RELEASE_MANIFEST.ACC_RELEASE_MANIFEST_ID.eq(ULong.valueOf(accNodeDetail.getManifestId()))).fetchOne();

        AccRecord baseAccRecord = dslContext.selectFrom(ACC)
                .where(ACC.ACC_ID.eq(accReleaseManifestRecord.getAccId())).fetchOne();

        long originAccid = accReleaseManifestRecord.getAccId().longValue();

        baseAccRecord.set(ACC.ACC_ID, null);
        baseAccRecord.set(ACC.OBJECT_CLASS_TERM, accNodeDetail.getObjectClassTerm());
        baseAccRecord.set(ACC.OAGIS_COMPONENT_TYPE, (int) accNodeDetail.getOagisComponentType());
        baseAccRecord.set(ACC.DEFINITION, accNodeDetail.getDefinition());
        baseAccRecord.set(ACC.DEN, accNodeDetail.getObjectClassTerm() + ". Details");
        baseAccRecord.set(ACC.IS_DEPRECATED,(byte) (accNodeDetail.isDeprecated() ? 1 : 0));
        baseAccRecord.set(ACC.IS_ABSTRACT,(byte) (accNodeDetail.isAbstracted() ? 1 : 0));
        baseAccRecord.set(ACC.LAST_UPDATED_BY, ULong.valueOf(userId));
        baseAccRecord.set(ACC.LAST_UPDATE_TIMESTAMP, timestamp);
        baseAccRecord.set(ACC.REVISION_ACTION, (byte) RevisionAction.Update.getValue());
        baseAccRecord.set(ACC.REVISION_TRACKING_NUM, baseAccRecord.getRevisionTrackingNum() + 1);
        baseAccRecord.insert();

        accReleaseManifestRecord.setAccId(baseAccRecord.getAccId());
        accReleaseManifestRecord.update();

        dslContext.update(ACC_RELEASE_MANIFEST)
                .set(accReleaseManifestRecord)
                .where(ACC_RELEASE_MANIFEST.ACC_RELEASE_MANIFEST_ID.eq(accReleaseManifestRecord.getAccReleaseManifestId()))
                .execute();

        updateAccChain(userId, originAccid, baseAccRecord.getAccId().longValue(),
                accReleaseManifestRecord.getReleaseId().longValue(), baseAccRecord.getObjectClassTerm(), timestamp);

        CcAccNode updateAccNode = getAccNodeByAccId(user, accReleaseManifestRecord);
        return getAccNodeDetail(user, updateAccNode);
    }

    private void updateAccChain(long userId, long originAccid, long newAccId, long releaseId, String objectClassTerm,
                                Timestamp timestamp) {
        updateAsccByFromAcc(userId, originAccid, newAccId, releaseId, objectClassTerm, timestamp);
        updateBccByFromAcc(userId, originAccid, newAccId, releaseId, objectClassTerm, timestamp);
        updateAsccpByRoleOfAcc(userId, originAccid, newAccId, releaseId, objectClassTerm, timestamp);
        updateAccByBasedAcc(userId, originAccid, newAccId, releaseId, timestamp);
    }

    private void updateAsccpByRoleOfAcc(long userId, long originRoleOfAccId, long newRoleOfAccId, long releaseId,
                                            String accObjectClassTerm, Timestamp timestamp) {
        List<AsccpReleaseManifestRecord> asccpReleaseManifestRecords =
                manifestRepository.getAsccpReleaseManifestByRoleOfAccId(originRoleOfAccId, releaseId);

        for(AsccpReleaseManifestRecord asccpReleaseManifestRecord : asccpReleaseManifestRecords) {
            long originAsccpId = asccpReleaseManifestRecord.getAsccpId().longValue();
            AsccpRecord asccpRecord = dslContext.selectFrom(ASCCP)
                    .where(ASCCP.ASCCP_ID.eq(asccpReleaseManifestRecord.getAsccpId())).fetchOne();
            asccpRecord.setAsccpId(null);
            asccpRecord.setRoleOfAccId(ULong.valueOf(newRoleOfAccId));
            asccpRecord.setDen(asccpRecord.getPropertyTerm() + ". " + accObjectClassTerm);
            asccpRecord.setLastUpdatedBy(ULong.valueOf(userId));
            asccpRecord.setLastUpdateTimestamp(timestamp);
            asccpRecord.setRevisionAction((byte) RevisionAction.Update.getValue());
            asccpRecord.setRevisionTrackingNum(asccpRecord.getRevisionTrackingNum() + 1);
            asccpRecord.insert();

            asccpReleaseManifestRecord.setRoleOfAccId(ULong.valueOf(newRoleOfAccId));
            asccpReleaseManifestRecord.setAsccpId(asccpRecord.getAsccpId());
            asccpReleaseManifestRecord.update();

            List<AsccReleaseManifestRecord> asccReleaseManifestRecordList = manifestRepository.getAsccReleaseManifestByToAsccpId(originAsccpId, releaseId);
            for(AsccReleaseManifestRecord asccReleaseManifestRecord : asccReleaseManifestRecordList) {
                AsccRecord asccRecord = dslContext.selectFrom(ASCC)
                        .where(ASCC.ASCC_ID.eq(asccReleaseManifestRecord.getAsccId())).fetchOne();

                asccRecord.setAsccId(null);
                asccRecord.setToAsccpId(asccpRecord.getAsccpId());
                asccRecord.setLastUpdatedBy(ULong.valueOf(userId));
                asccRecord.setLastUpdateTimestamp(timestamp);
                asccRecord.setRevisionAction((byte) RevisionAction.Update.getValue());
                asccRecord.setRevisionTrackingNum(asccRecord.getRevisionTrackingNum() + 1);
                asccRecord.insert();

                asccReleaseManifestRecord.setToAsccpId(asccpRecord.getAsccpId());
                asccReleaseManifestRecord.setAsccId(asccRecord.getAsccId());
            }

        }
    }

    private void updateAccByBasedAcc(long userId, long originBasedAccId, long newBasedAccId, long releaseId, 
                                     Timestamp timestamp) {
        List<AccReleaseManifestRecord> accReleaseManifestRecords =
                manifestRepository.getAccReleaseManifestByBasedAccId(originBasedAccId, releaseId);

        for(AccReleaseManifestRecord accReleaseManifestRecord : accReleaseManifestRecords) {
            long originAccId = accReleaseManifestRecord.getAccId().longValue();
            AccRecord accRecord = dslContext.selectFrom(ACC)
                    .where(ACC.ACC_ID.eq(accReleaseManifestRecord.getAccId())).fetchOne();
            accRecord.setAccId(null);
            accRecord.setBasedAccId(ULong.valueOf(newBasedAccId));
            accRecord.setLastUpdatedBy(ULong.valueOf(userId));
            accRecord.setLastUpdateTimestamp(timestamp);
            accRecord.setRevisionAction((byte) RevisionAction.Update.getValue());
            accRecord.setRevisionTrackingNum(accRecord.getRevisionTrackingNum() + 1);
            accRecord.insert();

            accReleaseManifestRecord.setBasedAccId(ULong.valueOf(newBasedAccId));
            accReleaseManifestRecord.setAccId(accRecord.getAccId());
            accReleaseManifestRecord.update();

            updateAccChain(userId, originAccId, accRecord.getAccId().longValue(),
                    accReleaseManifestRecord.getReleaseId().longValue(), accRecord.getObjectClassTerm(), timestamp);
        }
    }

    public long updateAscc(User user, CcAsccpNodeDetail.Ascc asccNodeDetail, long manifestId) {
        long userId = sessionService.userId(user);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        boolean isChanged = false;

        AsccReleaseManifestRecord asccReleaseManifestRecord = dslContext.selectFrom(ASCC_RELEASE_MANIFEST)
                .where(ASCC_RELEASE_MANIFEST.ASCC_RELEASE_MANIFEST_ID.eq(ULong.valueOf(manifestId))).fetchOne();
        AsccRecord baseAsccRecord = dslContext.selectFrom(ASCC)
                .where(ASCC.ASCC_ID.eq(asccReleaseManifestRecord.getAsccId())).fetchOne();

        if (!baseAsccRecord.getCardinalityMin().equals(asccNodeDetail.getCardinalityMin())) {
            baseAsccRecord.setCardinalityMin(asccNodeDetail.getCardinalityMin());
            isChanged = true;
        }

        if (!baseAsccRecord.getCardinalityMax().equals(asccNodeDetail.getCardinalityMax())) {
            baseAsccRecord.setCardinalityMax(asccNodeDetail.getCardinalityMax());
            isChanged = true;
        }

        byte deprecated = (byte) (asccNodeDetail.isDeprecated() ? 1 : 0);
        if (!baseAsccRecord.getIsDeprecated().equals(deprecated)) {
            baseAsccRecord.setIsDeprecated(deprecated);
            isChanged = true;
        }

        if (!Objects.equals(baseAsccRecord.getDefinition(), asccNodeDetail.getDefinition())) {
            baseAsccRecord.setDefinition(asccNodeDetail.getDefinition());
            isChanged = true;
        }

        if (isChanged) {
            baseAsccRecord.setAsccId(null);
            baseAsccRecord.setLastUpdatedBy(ULong.valueOf(userId));
            baseAsccRecord.setLastUpdateTimestamp(timestamp);
            baseAsccRecord.setRevisionAction((byte) RevisionAction.Update.getValue());
            baseAsccRecord.setRevisionTrackingNum(baseAsccRecord.getRevisionTrackingNum() + 1);
            baseAsccRecord.insert();

            asccReleaseManifestRecord.setAsccId(baseAsccRecord.getAsccId());
            asccReleaseManifestRecord.update();
        }

        return baseAsccRecord.getAsccId().longValue();
    }

    public CcAsccpNode updateAsccp(User user, CcAsccpNodeDetail.Asccp asccpNodeDetail, long manifestId) {
        long userId = sessionService.userId(user);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        boolean isChanged = false;

        AsccpReleaseManifestRecord asccpReleaseManifestRecord = dslContext.selectFrom(ASCCP_RELEASE_MANIFEST)
                .where(ASCCP_RELEASE_MANIFEST.ASCCP_RELEASE_MANIFEST_ID.eq(ULong.valueOf(manifestId))).fetchOne();
        AsccpRecord baseAsccpRecord = dslContext.selectFrom(ASCCP)
                .where(ASCCP.ASCCP_ID.eq(asccpReleaseManifestRecord.getAsccpId())).fetchOne();

        long originAsccpId = asccpReleaseManifestRecord.getAsccpId().longValue();

        if (!baseAsccpRecord.getPropertyTerm().equals(asccpNodeDetail.getPropertyTerm())) {
            AccRecord accRecord = dslContext.selectFrom(ACC)
                    .where(ACC.ACC_ID.eq(asccpReleaseManifestRecord.getRoleOfAccId())).fetchOne();
            baseAsccpRecord.setPropertyTerm(asccpNodeDetail.getPropertyTerm());
            baseAsccpRecord.setDen(asccpNodeDetail.getPropertyTerm() + ". " + accRecord.getObjectClassTerm());
            isChanged = true;
        }
        byte reusable = (byte)(asccpNodeDetail.isReusable() ? 1 : 0);
        if (!baseAsccpRecord.getReusableIndicator().equals(reusable)) {
            baseAsccpRecord.setReusableIndicator(reusable);
            isChanged = true;
        }

        byte deprecated = (byte)(asccpNodeDetail.isDeprecated() ? 1 : 0);
        if (!baseAsccpRecord.getIsDeprecated().equals(deprecated)) {
            baseAsccpRecord.setIsDeprecated(deprecated);
            isChanged = true;
        }

        if (!Objects.equals(baseAsccpRecord.getDefinition(), asccpNodeDetail.getDefinition())) {
            baseAsccpRecord.setDefinition(asccpNodeDetail.getDefinition());
            isChanged = true;
        }

        if (isChanged) {
            baseAsccpRecord.setAsccpId(null);
            baseAsccpRecord.setLastUpdatedBy(ULong.valueOf(userId));
            baseAsccpRecord.setLastUpdateTimestamp(timestamp);
            baseAsccpRecord.setRevisionAction((byte) RevisionAction.Update.getValue());
            baseAsccpRecord.setRevisionTrackingNum(baseAsccpRecord.getRevisionTrackingNum() + 1);
            baseAsccpRecord.insert();

            asccpReleaseManifestRecord.setAsccpId(baseAsccpRecord.getAsccpId());
            asccpReleaseManifestRecord.update();

            updateAsccByToAsccp(userId, originAsccpId, baseAsccpRecord.getAsccpId().longValue(),
                    asccpReleaseManifestRecord.getReleaseId().longValue(), baseAsccpRecord.getPropertyTerm(), timestamp);
        }

        return getAsccpNodeByAsccpManifestId(user, asccpReleaseManifestRecord.getAsccpReleaseManifestId().longValue());
    }

    public long updateBcc(User user, CcBccpNodeDetail.Bcc bccNodeDetail, long manifestId) {
        long userId = sessionService.userId(user);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        boolean isChanged = false;

        BccReleaseManifestRecord bccReleaseManifestRecord = dslContext.selectFrom(BCC_RELEASE_MANIFEST)
                .where(BCC_RELEASE_MANIFEST.BCC_RELEASE_MANIFEST_ID.eq(ULong.valueOf(manifestId))).fetchOne();
        BccRecord baseBccRecord = dslContext.selectFrom(BCC)
                .where(BCC.BCC_ID.eq(bccReleaseManifestRecord.getBccId())).fetchOne();

        if (!baseBccRecord.getCardinalityMin().equals(bccNodeDetail.getCardinalityMin())) {
            baseBccRecord.setCardinalityMin(bccNodeDetail.getCardinalityMin());
            isChanged = true;
        }

        if (!baseBccRecord.getCardinalityMax().equals(bccNodeDetail.getCardinalityMax())) {
            baseBccRecord.setCardinalityMax(bccNodeDetail.getCardinalityMax());
            isChanged = true;
        }

        byte nillable = (byte) (bccNodeDetail.isNillable() ? 1 : 0);
        if (!baseBccRecord.getIsNillable().equals(nillable)) {
            baseBccRecord.setIsNillable(nillable);
            isChanged = true;
        }

        byte deprecated = (byte) (bccNodeDetail.isDeprecated() ? 1 : 0);
        if (!baseBccRecord.getIsDeprecated().equals(deprecated)) {
            baseBccRecord.setIsDeprecated(deprecated);
            isChanged = true;
        }

        if (!Objects.equals(baseBccRecord.getDefaultValue(), bccNodeDetail.getDefaultValue())) {
            baseBccRecord.setDefaultValue(bccNodeDetail.getDefaultValue());
            baseBccRecord.setFixedValue(null);
            isChanged = true;
        }

        if (!Objects.equals(baseBccRecord.getFixedValue(), bccNodeDetail.getFixedValue())) {
            baseBccRecord.setFixedValue(bccNodeDetail.getFixedValue());
            baseBccRecord.setDefaultValue(null);
            isChanged = true;
        }

        if (!Objects.equals(baseBccRecord.getDefinition(), bccNodeDetail.getDefinition())) {
            baseBccRecord.setDefinition(bccNodeDetail.getDefinition());
            isChanged = true;
        }

        if (!baseBccRecord.getEntityType().equals(bccNodeDetail.getEntityType())) {
            baseBccRecord.setEntityType(bccNodeDetail.getEntityType());
            isChanged = true;
        }

        if (isChanged) {
            baseBccRecord.setBccId(null);
            baseBccRecord.setDefinition(bccNodeDetail.getDefinition());
            baseBccRecord.setLastUpdatedBy(ULong.valueOf(userId));
            baseBccRecord.setLastUpdateTimestamp(timestamp);
            baseBccRecord.setRevisionAction((byte) RevisionAction.Update.getValue());
            baseBccRecord.setRevisionTrackingNum(baseBccRecord.getRevisionTrackingNum() + 1);
            baseBccRecord.insert();

            bccReleaseManifestRecord.setBccId(baseBccRecord.getBccId());
            bccReleaseManifestRecord.update();
        }

        return baseBccRecord.getBccId().longValue();
    }

    public CcBccpNode updateBccp(User user, CcBccpNodeDetail.Bccp bccpNodeDetail, long manifestId) {
        long userId = sessionService.userId(user);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        boolean isChanged = false;
        
        BccpReleaseManifestRecord bccpReleaseManifestRecord = dslContext.selectFrom(BCCP_RELEASE_MANIFEST)
                .where(BCCP_RELEASE_MANIFEST.BCCP_RELEASE_MANIFEST_ID.eq(ULong.valueOf(manifestId))).fetchOne();

        long originBccpId = bccpReleaseManifestRecord.getBccpId().longValue();

        BccpRecord baseBccpRecord = dslContext.selectFrom(BCCP)
                .where(BCCP.BCCP_ID.eq(bccpReleaseManifestRecord.getBccpId())).fetchOne();

        if (!baseBccpRecord.getPropertyTerm().equals(bccpNodeDetail.getPropertyTerm())) {
            DtRecord dtRecord = dslContext.selectFrom(DT)
                    .where(DT.DT_ID.eq(bccpReleaseManifestRecord.getBdtId())).fetchOne();
            baseBccpRecord.setPropertyTerm(bccpNodeDetail.getPropertyTerm());
            baseBccpRecord.setDen(bccpNodeDetail.getPropertyTerm() + ". " + dtRecord.getDataTypeTerm());
            isChanged = true;
        }

        byte nillable = (byte) (bccpNodeDetail.isNillable() ? 1 : 0);
        if (!baseBccpRecord.getIsNillable().equals(nillable)) {
            baseBccpRecord.setIsNillable(nillable);
            isChanged = true;
        }

        byte deprecated = (byte) (bccpNodeDetail.isDeprecated() ? 1 : 0);
        if (!baseBccpRecord.getIsDeprecated().equals(deprecated)) {
            baseBccpRecord.setIsDeprecated(deprecated);
            isChanged = true;
        }
        if (!Objects.equals(baseBccpRecord.getDefaultValue(), bccpNodeDetail.getDefaultValue())) {
            baseBccpRecord.setDefaultValue(bccpNodeDetail.getDefaultValue());
            baseBccpRecord.setFixedValue(null);
            isChanged = true;
        }

        if (!Objects.equals(baseBccpRecord.getFixedValue(), bccpNodeDetail.getFixedValue())) {
            baseBccpRecord.setFixedValue(bccpNodeDetail.getFixedValue());
            baseBccpRecord.setDefaultValue(null);
            isChanged = true;
        }

        if (!Objects.equals(baseBccpRecord.getDefinition(), bccpNodeDetail.getDefinition())) {
            baseBccpRecord.setDefinition(bccpNodeDetail.getDefinition());
            isChanged = true;
        }

        if (isChanged) {
            baseBccpRecord.set(BCCP.BCCP_ID, null);
            baseBccpRecord.set(BCCP.LAST_UPDATED_BY, ULong.valueOf(userId));
            baseBccpRecord.set(BCCP.LAST_UPDATE_TIMESTAMP, timestamp);
            baseBccpRecord.set(BCCP.REVISION_ACTION, RevisionAction.Update.getValue());
            baseBccpRecord.set(BCCP.REVISION_TRACKING_NUM, baseBccpRecord.getRevisionTrackingNum() + 1);
            baseBccpRecord.insert();

            bccpReleaseManifestRecord.setBccpId(baseBccpRecord.getBccpId());
            bccpReleaseManifestRecord.update();

            updateBccByToBccp(userId, originBccpId, baseBccpRecord.getBccpId().longValue(),
                    bccpReleaseManifestRecord.getReleaseId().longValue(), baseBccpRecord.getPropertyTerm(), timestamp);
        }

        return getBccpNodeByBccpManifestId(user, bccpReleaseManifestRecord.getBccpReleaseManifestId().longValue());
    }

    private CcAccNode arrangeAccNode(User user, CcAccNode accNode, ULong releaseId) {
        OagisComponentType oagisComponentType =
                OagisComponentType.valueOf(accNode.getOagisComponentType());
        accNode.setGroup(oagisComponentType.isGroup());
        accNode.setState(CcState.valueOf(accNode.getRawState()));
        accNode.setAccess(getAccess(accNode, user));
        accNode.setHasChild(hasChild(accNode, releaseId));

        return accNode;
    }

    private boolean hasChild(CcAccNode accNode, ULong releaseId) {
        if (accNode.getBasedAccId() != null) {
            return true;
        } else {
            Long fromAccId = accNode.getAccId();
            if (fromAccId == null) {
                return false;
            }
            List<AsccForAccHasChild> asccList = dslContext.select(
                    ASCC.ASCC_ID,
                    ASCC.GUID,
                    ASCC.REVISION_NUM,
                    ASCC.REVISION_TRACKING_NUM,
                    ASCC_RELEASE_MANIFEST.RELEASE_ID)
                    .from(ASCC)
                    .join(ASCC_RELEASE_MANIFEST)
                    .on(ASCC.ASCC_ID.eq(ASCC_RELEASE_MANIFEST.ASCC_ID))
                    .where(ASCC.FROM_ACC_ID.eq(ULong.valueOf(fromAccId)))
                    .fetchInto(AsccForAccHasChild.class);

            long asccCount = asccList.size();
            if (asccCount > 0L) {
                return true;
            }

            List<BccForAccHasChild> bccList = dslContext.select(
                    BCC.BCC_ID,
                    BCC.BCC_ID,
                    BCC.GUID,
                    BCC.REVISION_NUM,
                    BCC.REVISION_TRACKING_NUM,
                    BCC_RELEASE_MANIFEST.RELEASE_ID)
                    .from(BCC)
                    .join(BCC_RELEASE_MANIFEST)
                    .on(BCC.BCC_ID.eq(BCC_RELEASE_MANIFEST.BCC_ID))
                    .where(BCC.FROM_ACC_ID.eq(ULong.valueOf(fromAccId)))
                    .fetchInto(BccForAccHasChild.class);

            long bccCount = bccList.size();
            return (bccCount > 0L);
        }
    }

    public CcAsccpNode getAsccpNodeByAsccpManifestId(User user, long manifestId) {
        CcAsccpNode asccpNode = dslContext.select(
                ASCCP.ASCCP_ID,
                ASCCP.GUID,
                ASCCP.PROPERTY_TERM.as("name"),
                ASCCP_RELEASE_MANIFEST.ROLE_OF_ACC_ID,
                ASCCP.STATE.as("raw_state"),
                ASCCP.REVISION_NUM,
                ASCCP.REVISION_TRACKING_NUM,
                ASCCP_RELEASE_MANIFEST.RELEASE_ID,
                ASCCP_RELEASE_MANIFEST.ASCCP_RELEASE_MANIFEST_ID.as("manifest_id"),
                ASCCP.OWNER_USER_ID)
                .from(ASCCP)
                .join(ASCCP_RELEASE_MANIFEST)
                .on(ASCCP.ASCCP_ID.eq(ASCCP_RELEASE_MANIFEST.ASCCP_ID))
                .where(ASCCP_RELEASE_MANIFEST.ASCCP_RELEASE_MANIFEST_ID.eq(ULong.valueOf(manifestId)))
                .fetchOneInto(CcAsccpNode.class);

        asccpNode.setState(CcState.valueOf(asccpNode.getRawState()));
        asccpNode.setAccess(getAccess(asccpNode, user));
        asccpNode.setHasChild(true); // role_of_acc_id must not be null.

        return asccpNode;
    }

    private String getAccess(CcNode ccNode, User user) {
        AccessPrivilege accessPrivilege = Prohibited;
        long userId = sessionService.userId(user);
        long ownerUserId = ccNode.getOwnerUserId();
        switch (ccNode.getState()) {
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
        return accessPrivilege.name();
    }

    public CcAsccpNode getAsccpNodeByRoleOfAccId(long roleOfAccId, ULong releaseId) {
        CcAsccpNode asccpNode = dslContext.select(
                ASCCP.ASCCP_ID,
                ASCCP.GUID,
                ASCCP.PROPERTY_TERM.as("name"),
                ASCCP.STATE.as("raw_state"),
                ASCCP.REVISION_NUM,
                ASCCP.REVISION_TRACKING_NUM,
                ASCCP_RELEASE_MANIFEST.RELEASE_ID)
                .from(ASCCP)
                .join(ASCCP_RELEASE_MANIFEST)
                .on(ASCCP.ASCCP_ID.eq(ASCCP_RELEASE_MANIFEST.ASCCP_ID))
                .where(and(
                        ASCCP_RELEASE_MANIFEST.ROLE_OF_ACC_ID.eq(ULong.valueOf(roleOfAccId)),
                        ASCCP_RELEASE_MANIFEST.RELEASE_ID.eq(releaseId)
                ))
                .fetchOneInto(CcAsccpNode.class);
        asccpNode.setState(CcState.valueOf(asccpNode.getRawState()));
        asccpNode.setHasChild(true); // role_of_acc_id must not be null.

        return asccpNode;
    }

    public CcBccpNode getBccpNodeByBccpManifestId(User user, long manifestId) {
        CcBccpNode bccpNode = dslContext.select(
                BCCP.BCCP_ID,
                BCCP.GUID,
                BCCP.PROPERTY_TERM.as("name"),
                BCCP_RELEASE_MANIFEST.BDT_ID,
                BCCP.STATE.as("raw_state"),
                BCCP.REVISION_NUM,
                BCCP.REVISION_TRACKING_NUM,
                BCCP_RELEASE_MANIFEST.RELEASE_ID,
                BCCP_RELEASE_MANIFEST.BCCP_RELEASE_MANIFEST_ID.as("manifest_id"),
                BCCP.OWNER_USER_ID)
                .from(BCCP)
                .join(BCCP_RELEASE_MANIFEST)
                .on(BCCP.BCCP_ID.eq(BCCP_RELEASE_MANIFEST.BCCP_ID))
                .where(BCCP_RELEASE_MANIFEST.BCCP_RELEASE_MANIFEST_ID.eq(ULong.valueOf(manifestId)))
                .fetchOneInto(CcBccpNode.class);
        bccpNode.setState(CcState.valueOf(bccpNode.getRawState()));
        bccpNode.setAccess(getAccess(bccpNode, user));
        bccpNode.setHasChild(hasChild(bccpNode));

        return bccpNode;
    }

    private boolean hasChild(CcBccpNode bccpNode) {
        long bdtId = bccpNode.getBdtId();
        int dtScCount = dslContext.selectCount().from(DT_SC)
                .where(and(
                        DT_SC.OWNER_DT_ID.eq(ULong.valueOf(bdtId)),
                        or(
                                DT_SC.CARDINALITY_MIN.ne(0),
                                DT_SC.CARDINALITY_MAX.ne(0)
                        ))).fetchOneInto(Integer.class);
        return (dtScCount > 0);
    }

    public List<? extends CcNode> getDescendants(User user, CcAccNode accNode) {
        if (accNode == null) {
            return Collections.emptyList();
        }

        List<CcNode> descendants = new ArrayList();

        AccReleaseManifestRecord accReleaseManifestRecord = manifestRepository.getAccReleaseManifestById(accNode.getManifestId());

        if (accReleaseManifestRecord.getBasedAccId() != null) {
            Long basedAccId = accReleaseManifestRecord.getBasedAccId().longValue();
            Long releaseId = accNode.getReleaseId();
            CcAccNode basedAccNode;

            basedAccNode = getAccNodeByAccId(user, basedAccId, releaseId);

            descendants.add(basedAccNode);
        }

        Long releaseId = accNode.getReleaseId();
        long fromAccId;
        fromAccId = accReleaseManifestRecord.getAccId().longValue();

        List<SeqKeySupportable> seqKeySupportableList = new ArrayList();
        seqKeySupportableList.addAll(
                getAsccpNodes(user, fromAccId, releaseId)
        );
        seqKeySupportableList.addAll(
                getBccpNodes(user, fromAccId, releaseId)
        );
        seqKeySupportableList.sort(Comparator.comparingInt(SeqKeySupportable::getSeqKey));

        int seqKey = 1;
        for (SeqKeySupportable e : seqKeySupportableList) {
            if (e instanceof CcAsccpNode) {
                CcAsccpNode asccpNode = (CcAsccpNode) e;
                OagisComponentType oagisComponentType = getOagisComponentTypeByAccId(asccpNode.getRoleOfAccId());
                if (oagisComponentType.equals(OagisComponentType.UserExtensionGroup)) {
                    CcAccNode uegAccNode = getAccNodeByAccId(user, asccpNode.getRoleOfAccId(), releaseId);
                    List<? extends CcNode> uegChildren = getDescendants(user, uegAccNode);
                    for (CcNode uegChild : uegChildren) {
                        ((SeqKeySupportable) uegChild).setSeqKey(seqKey++);
                    }
                    descendants.addAll(uegChildren);
                } else {
                    asccpNode.setSeqKey(seqKey++);
                    descendants.add(asccpNode);
                }
            } else {
                CcBccpNode bccpNode = (CcBccpNode) e;
                bccpNode.setSeqKey(seqKey++);
                descendants.add(bccpNode);
            }
        }

        return descendants;
    }

    public OagisComponentType getOagisComponentTypeByAccId(long accId) {
        int oagisComponentType = dslContext.select(ACC.OAGIS_COMPONENT_TYPE)
                .from(ACC).where(ACC.ACC_ID.eq(ULong.valueOf(accId)))
                .fetchOneInto(Integer.class);
        return OagisComponentType.valueOf(oagisComponentType);
    }

    private List<CcAsccpNode> getAsccpNodes(User user, long fromAccId, Long releaseId) {
        List<CcAsccNode> asccNodes = dslContext.select(
                ASCC.ASCC_ID,
                ASCC_RELEASE_MANIFEST.ASCC_RELEASE_MANIFEST_ID.as("manifest_id"),
                ASCC.GUID,
                ASCC_RELEASE_MANIFEST.TO_ASCCP_ID,
                ASCC_RELEASE_MANIFEST.FROM_ACC_ID,
                ASCC.SEQ_KEY,
                ASCC.STATE.as("raw_state"),
                ASCC.REVISION_NUM,
                ASCC.REVISION_TRACKING_NUM,
                ASCC_RELEASE_MANIFEST.RELEASE_ID)
                .from(ASCC)
                .join(ASCC_RELEASE_MANIFEST)
                .on(ASCC.ASCC_ID.eq(ASCC_RELEASE_MANIFEST.ASCC_ID))
                .where(and(
                        ASCC_RELEASE_MANIFEST.FROM_ACC_ID.eq(ULong.valueOf(fromAccId)),
                        ASCC_RELEASE_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId))
                ))
                .fetchInto(CcAsccNode.class);

        if (asccNodes.isEmpty()) {
            return Collections.emptyList();
        }

        return asccNodes.stream().map(asccNode -> {
            long manifestId =
                    dslContext.select(ASCCP_RELEASE_MANIFEST.ASCCP_RELEASE_MANIFEST_ID)
                    .from(ASCCP_RELEASE_MANIFEST)
                    .where(and(
                            ASCCP_RELEASE_MANIFEST.ASCCP_ID.eq(ULong.valueOf(asccNode.getToAsccpId())),
                            ASCCP_RELEASE_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId))
                    ))
                    .fetchOneInto(ULong.class).longValue();

            CcAsccpNode asccpNode =
                    getAsccpNodeByAsccpManifestId(user, manifestId);
            asccpNode.setSeqKey(asccNode.getSeqKey());
            asccpNode.setAsccId(asccNode.getAsccId());
            asccpNode.setAsccManifestId(asccNode.getManifestId());
            return asccpNode;
        }).collect(Collectors.toList());
    }

    private List<CcBccpNode> getBccpNodes(User user, long fromAccId, Long releaseId) {
        List<CcBccNode> bccNodes = dslContext.select(
                BCC.BCC_ID,
                BCC_RELEASE_MANIFEST.BCC_RELEASE_MANIFEST_ID.as("manifest_id"),
                BCC.GUID,
                BCC_RELEASE_MANIFEST.TO_BCCP_ID,
                BCC.SEQ_KEY,
                BCC.ENTITY_TYPE,
                BCC.STATE.as("raw_state"),
                BCC.REVISION_NUM,
                BCC.REVISION_TRACKING_NUM,
                BCC_RELEASE_MANIFEST.RELEASE_ID)
                .from(BCC)
                .join(BCC_RELEASE_MANIFEST)
                .on(BCC.BCC_ID.eq(BCC_RELEASE_MANIFEST.BCC_ID))
                .where(BCC.FROM_ACC_ID.eq(ULong.valueOf(fromAccId)).and(BCC_RELEASE_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId))))
                .fetchInto(CcBccNode.class);

        if (bccNodes.isEmpty()) {
            return Collections.emptyList();
        }

        return bccNodes.stream().map(bccNode -> {
            long manifestId =
                    dslContext.select(BCCP_RELEASE_MANIFEST.BCCP_RELEASE_MANIFEST_ID)
                            .from(BCCP_RELEASE_MANIFEST)
                            .where(and(
                                    BCCP_RELEASE_MANIFEST.BCCP_ID.eq(ULong.valueOf(bccNode.getToBccpId())),
                                    BCCP_RELEASE_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId))
                            ))
                            .fetchOneInto(ULong.class).longValue();

            CcBccpNode bccpNode = getBccpNodeByBccpManifestId(user, manifestId);
            bccpNode.setSeqKey(bccNode.getSeqKey());
            bccpNode.setAttribute(BCCEntityType.valueOf(bccNode.getEntityType()) == Attribute);
            bccpNode.setBccId(bccNode.getBccId());
            bccpNode.setBccManifestId(bccNode.getManifestId());
            return bccpNode;
        }).collect(Collectors.toList());
    }

    public List<? extends CcNode> getDescendants(User user, CcAsccpNode asccpNode) {
        AsccpReleaseManifestRecord asccpReleaseManifestRecord = manifestRepository.getAsccpReleaseManifestById(asccpNode.getManifestId());
        long asccpId = asccpReleaseManifestRecord.getAsccpId().longValue();

        long roleOfAccId = dslContext.select(ASCCP.ROLE_OF_ACC_ID).from(ASCCP)
                .where(ASCCP.ASCCP_ID.eq(ULong.valueOf(asccpId)))
                .fetchOneInto(Long.class);

        Long releaseId = asccpNode.getReleaseId();

        return Arrays.asList(getAccNodeByAccId(user, roleOfAccId, releaseId));
    }

    public List<? extends CcNode> getDescendants(User user, CcBccpNode bccpNode) {
        BccpReleaseManifestRecord BccpReleaseManifestRecord = manifestRepository.getBccpReleaseManifestById(bccpNode.getManifestId());
        long bccpId = BccpReleaseManifestRecord.getBccpId().longValue();

        return dslContext.select(
                DT_SC.DT_SC_ID.as("bdt_sc_id"),
                DT_SC.GUID,
                concat(DT_SC.PROPERTY_TERM, val(". "), DT_SC.REPRESENTATION_TERM).as("name")
        ).from(DT_SC).join(BCCP).on(DT_SC.OWNER_DT_ID.eq(BCCP.BDT_ID))
                .where(and(
                        BCCP.BCCP_ID.eq(ULong.valueOf(bccpId)),
                        or(
                                DT_SC.CARDINALITY_MIN.ne(0),
                                DT_SC.CARDINALITY_MAX.ne(0)
                        ))).fetchInto(CcBdtScNode.class);
    }

    public CcAccNodeDetail getAccNodeDetail(User user, CcAccNode accNode) {
        return dslContext.select(
                ACC.ACC_ID,
                ACC.GUID,
                ACC.OBJECT_CLASS_TERM,
                ACC.DEN,
                ACC.OAGIS_COMPONENT_TYPE.as("oagisComponentType"),
                ACC.IS_ABSTRACT.as("abstracted"),
                ACC.IS_DEPRECATED.as("deprecated"),
                ACC.DEFINITION,
                ACC_RELEASE_MANIFEST.ACC_RELEASE_MANIFEST_ID.as("manifest_id"))
                .from(ACC_RELEASE_MANIFEST)
                .join(ACC)
                .on(ACC.ACC_ID.eq(ACC_RELEASE_MANIFEST.ACC_ID))
                .where(ACC_RELEASE_MANIFEST.ACC_RELEASE_MANIFEST_ID.eq(ULong.valueOf(accNode.getManifestId())))
                .fetchOneInto(CcAccNodeDetail.class);
    }

    public CcAsccpNodeDetail getAsccpNodeDetail(User user, CcAsccpNode asccpNode) {
        CcAsccpNodeDetail asccpNodeDetail = new CcAsccpNodeDetail();

        long asccManifestId = asccpNode.getAsccManifestId();
        if (asccManifestId > 0L) {
            CcAsccpNodeDetail.Ascc ascc = dslContext.select(
                    ASCC_RELEASE_MANIFEST.ASCC_RELEASE_MANIFEST_ID.as("manifest_id"),
                    ASCC.ASCC_ID,
                    ASCC.GUID,
                    ASCC.DEN,
                    ASCC.CARDINALITY_MIN,
                    ASCC.CARDINALITY_MAX,
                    ASCC.IS_DEPRECATED.as("deprecated"),
                    ASCC.DEFINITION)
                    .from(ASCC_RELEASE_MANIFEST)
                    .join(ASCC)
                    .on(ASCC.ASCC_ID.eq(ASCC_RELEASE_MANIFEST.ASCC_ID))
                    .where(ASCC_RELEASE_MANIFEST.ASCC_RELEASE_MANIFEST_ID.eq(ULong.valueOf(asccManifestId)))
                    .fetchOneInto(CcAsccpNodeDetail.Ascc.class);

            asccpNodeDetail.setAscc(ascc);
        }

        long asccpManifestIdId = asccpNode.getManifestId();
        CcAsccpNodeDetail.Asccp asccp = dslContext.select(
                ASCCP_RELEASE_MANIFEST.ASCCP_RELEASE_MANIFEST_ID.as("manifest_id"),
                ASCCP.ASCCP_ID,
                ASCCP.GUID,
                ASCCP.PROPERTY_TERM,
                ASCCP.DEN,
                ASCCP.REUSABLE_INDICATOR.as("reusable"),
                ASCCP.IS_DEPRECATED.as("deprecated"),
                ASCCP.DEFINITION)
                .from(ASCCP_RELEASE_MANIFEST)
                .join(ASCCP)
                .on(ASCCP.ASCCP_ID.eq(ASCCP_RELEASE_MANIFEST.ASCCP_ID))
                .where(ASCCP_RELEASE_MANIFEST.ASCCP_RELEASE_MANIFEST_ID.eq(ULong.valueOf(asccpManifestIdId)))
                .fetchOneInto(CcAsccpNodeDetail.Asccp.class);
        asccpNodeDetail.setAsccp(asccp);

        return asccpNodeDetail;
    }

    public CcAsccpNodeDetail.Asccp getAsccp(long asccpId) {
        CcAsccpNodeDetail.Asccp asccp = dslContext.select(
                ASCCP.ASCCP_ID,
                ASCCP.DEN,
                ASCCP.PROPERTY_TERM,
                ASCCP.DEFINITION,
                ASCCP.GUID,
                ASCCP.ROLE_OF_ACC_ID)
                .from(ASCCP)
                .where(ASCCP.ASCCP_ID.eq(ULong.valueOf(asccpId)))
                .fetchOneInto(CcAsccpNodeDetail.Asccp.class);
        return asccp;
    }

    public AsccpReleaseManifestRecord getAsccpReleaseManifestById(long manifestId) {
        return dslContext.selectFrom(ASCCP_RELEASE_MANIFEST)
                .where(ASCCP_RELEASE_MANIFEST.ASCCP_RELEASE_MANIFEST_ID.eq(ULong.valueOf(manifestId)))
                .fetchOne();
    }

    public CcBccpNodeDetail getBccpNodeDetail(User user, CcBccpNode bccpNode) {
        CcBccpNodeDetail bccpNodeDetail = new CcBccpNodeDetail();

        long bccManifestId = bccpNode.getBccManifestId();
        if (bccManifestId > 0L) {
            CcBccpNodeDetail.Bcc bcc = dslContext.select(
                    BCC.BCC_ID,
                    BCC.GUID,
                    BCC.DEN,
                    BCC.ENTITY_TYPE,
                    BCC.CARDINALITY_MIN,
                    BCC.CARDINALITY_MAX,
                    BCC.IS_DEPRECATED.as("deprecated"),
                    BCC.DEFAULT_VALUE,
                    BCC.FIXED_VALUE,
                    BCC.DEFINITION,
                    BCC_RELEASE_MANIFEST.BCC_RELEASE_MANIFEST_ID.as("manifest_id"))
                    .from(BCC_RELEASE_MANIFEST)
                    .join(BCC)
                    .on(BCC_RELEASE_MANIFEST.BCC_ID.eq(BCC.BCC_ID))
                    .where(BCC_RELEASE_MANIFEST.BCC_RELEASE_MANIFEST_ID.eq(ULong.valueOf(bccManifestId)))
                    .fetchOneInto(CcBccpNodeDetail.Bcc.class);

            bccpNodeDetail.setBcc(bcc);
        }

        long bccpManifestId = bccpNode.getManifestId();
        CcBccpNodeDetail.Bccp bccp = dslContext.select(
                BCCP.BCCP_ID,
                BCCP.GUID,
                BCCP.PROPERTY_TERM,
                BCCP.DEN,
                BCCP.IS_NILLABLE.as("nillable"),
                BCCP.IS_DEPRECATED.as("deprecated"),
                BCCP.DEFAULT_VALUE,
                BCCP.FIXED_VALUE,
                BCCP.DEFINITION,
                BCCP_RELEASE_MANIFEST.BCCP_RELEASE_MANIFEST_ID.as("manifest_id"))
                .from(BCCP_RELEASE_MANIFEST)
                .join(BCCP)
                .on(BCCP.BCCP_ID.eq(BCCP_RELEASE_MANIFEST.BCCP_ID))
                .where(BCCP_RELEASE_MANIFEST.BCCP_RELEASE_MANIFEST_ID.eq(ULong.valueOf(bccpManifestId)))
                .fetchOneInto(CcBccpNodeDetail.Bccp.class);
        bccpNodeDetail.setBccp(bccp);

        long bdtId = dslContext.select(BCCP_RELEASE_MANIFEST.BDT_ID).from(BCCP_RELEASE_MANIFEST)
                .where(BCCP_RELEASE_MANIFEST.BCCP_RELEASE_MANIFEST_ID.eq(ULong.valueOf(bccpManifestId))).fetchOneInto(Long.class);

        CcBccpNodeDetail.Bdt bdt = dslContext.select(
                DT.DT_ID.as("bdt_id"),
                DT.GUID,
                DT.DATA_TYPE_TERM,
                DT.QUALIFIER,
                DT.DEN,
                DT.DEFINITION).from(DT)
                .where(DT.DT_ID.eq(ULong.valueOf(bdtId)))
                .fetchOneInto(CcBccpNodeDetail.Bdt.class);
        bccpNodeDetail.setBdt(bdt);

        return bccpNodeDetail;
    }

    public CcBdtScNodeDetail getBdtScNodeDetail(User user, CcBdtScNode bdtScNode) {
        long bdtScId = bdtScNode.getBdtScId();
        return dslContext.select(
                DT_SC.DT_SC_ID.as("bdt_sc_id"),
                DT_SC.GUID,
                concat(DT_SC.PROPERTY_TERM, val(". "), DT_SC.PROPERTY_TERM).as("den"),
                DT_SC.CARDINALITY_MIN,
                DT_SC.CARDINALITY_MAX,
                DT_SC.DEFINITION,
                DT_SC.DEFAULT_VALUE,
                DT_SC.FIXED_VALUE).from(DT_SC)
                .where(DT_SC.DT_SC_ID.eq(ULong.valueOf(bdtScId)))
                .fetchOneInto(CcBdtScNodeDetail.class);
    }

    public AccReleaseManifestRecord getAccReleaseManifestByAcc(long accId, long releaseId) {
        return dslContext.selectFrom(ACC_RELEASE_MANIFEST)
                .where(ACC_RELEASE_MANIFEST.ACC_ID.eq(ULong.valueOf(accId))
                        .and(ACC_RELEASE_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId))))
                .fetchOne();

    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class AsccForAccHasChild extends TrackableImpl {
        private long asccId;
        private String guid;

        @Override
        public long getId() {
            return asccId;
        }
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class BccForAccHasChild extends TrackableImpl {
        private long bccId;
        private String guid;

        @Override
        public long getId() {
            return bccId;
        }
    }

    public boolean isAccUsed(long accId) {
        int cnt = dslContext.selectCount()
                .from(ASCCP)
                .where(ASCCP.ROLE_OF_ACC_ID.eq(ULong.valueOf(accId)))
                .fetchOptionalInto(Integer.class).orElse(0);
        if (cnt > 0) {
            return true;
        }

        cnt = dslContext.selectCount()
                .from(ACC)
                .where(ACC.BASED_ACC_ID.eq(ULong.valueOf(accId)))
                .fetchOptionalInto(Integer.class).orElse(0);
        if (cnt > 0) {
            return true;
        }

        cnt = dslContext.selectCount()
                .from(ASCC)
                .where(ASCC.FROM_ACC_ID.eq(ULong.valueOf(accId)))
                .fetchOptionalInto(Integer.class).orElse(0);
        if (cnt > 0) {
            return true;
        }

        cnt = dslContext.selectCount()
                .from(BCC)
                .where(BCC.FROM_ACC_ID.eq(ULong.valueOf(accId)))
                .fetchOptionalInto(Integer.class).orElse(0);
        if (cnt > 0) {
            return true;
        }

        cnt = dslContext.selectCount()
                .from(ABIE)
                .where(ABIE.BASED_ACC_ID.eq(ULong.valueOf(accId)))
                .fetchOptionalInto(Integer.class).orElse(0);
        if (cnt > 0) {
            return true;
        }

        return false;
    }

    public boolean isAsccpUsed(long asccpId) {
        int cnt = dslContext.selectCount()
                .from(ASCC)
                .where(ASCC.TO_ASCCP_ID.eq(ULong.valueOf(asccpId)))
                .fetchOptionalInto(Integer.class).orElse(0);
        if (cnt > 0) {
            return true;
        }

        cnt = dslContext.selectCount()
                .from(ASBIEP)
                .where(ASBIEP.BASED_ASCCP_ID.eq(ULong.valueOf(asccpId)))
                .fetchOptionalInto(Integer.class).orElse(0);
        if (cnt > 0) {
            return true;
        }

        return false;
    }

    public boolean isBccpUsed(long bccpId) {
        int cnt = dslContext.selectCount()
                .from(BCC)
                .where(BCC.TO_BCCP_ID.eq(ULong.valueOf(bccpId)))
                .fetchOptionalInto(Integer.class).orElse(0);
        if (cnt > 0) {
            return true;
        }

        cnt = dslContext.selectCount()
                .from(BBIEP)
                .where(BBIEP.BASED_BCCP_ID.eq(ULong.valueOf(bccpId)))
                .fetchOptionalInto(Integer.class).orElse(0);
        if (cnt > 0) {
            return true;
        }

        return false;
    }

    public void deleteAccRecords(long accId) {
        String guid = dslContext.select(ACC.GUID)
                .from(ACC)
                .where(ACC.ACC_ID.eq(ULong.valueOf(accId)))
                .fetchOneInto(String.class);

        List<ULong> accIds = dslContext.select(ACC.ACC_ID)
                .from(ACC)
                .where(ACC.GUID.eq(guid))
                .fetchInto(ULong.class);

        dslContext.deleteFrom(ASCC_RELEASE_MANIFEST)
                .where(ASCC_RELEASE_MANIFEST.FROM_ACC_ID.in(accIds))
                .execute();
        dslContext.deleteFrom(ASCC)
                .where(ASCC.FROM_ACC_ID.in(accIds))
                .execute();
        dslContext.deleteFrom(BCC_RELEASE_MANIFEST)
                .where(BCC_RELEASE_MANIFEST.FROM_ACC_ID.in(accIds))
                .execute();
        dslContext.deleteFrom(BCC)
                .where(BCC.FROM_ACC_ID.in(accIds))
                .execute();
        dslContext.deleteFrom(ACC_RELEASE_MANIFEST)
                .where(ACC_RELEASE_MANIFEST.ACC_ID.in(accIds))
                .execute();
        dslContext.deleteFrom(ACC)
                .where(ACC.ACC_ID.in(accIds))
                .execute();
    }

    public void deleteAsccpRecords(long asccpId) {
        String guid = dslContext.select(ASCCP.GUID)
                .from(ASCCP)
                .where(ASCCP.ASCCP_ID.eq(ULong.valueOf(asccpId)))
                .fetchOneInto(String.class);

        List<ULong> asccpIds = dslContext.select(ASCCP.ASCCP_ID)
                .from(ASCCP)
                .where(ASCCP.GUID.eq(guid))
                .fetchInto(ULong.class);

        dslContext.deleteFrom(ASCCP)
                .where(ASCCP.ASCCP_ID.in(asccpIds))
                .execute();
    }

    public void deleteBccpRecords(long bccpId) {
        String guid = dslContext.select(BCCP.GUID)
                .from(BCCP)
                .where(BCCP.BCCP_ID.eq(ULong.valueOf(bccpId)))
                .fetchOneInto(String.class);

        List<ULong> asccpIds = dslContext.select(BCCP.BCCP_ID)
                .from(BCCP)
                .where(BCCP.GUID.eq(guid))
                .fetchInto(ULong.class);

        dslContext.deleteFrom(BCCP)
                .where(BCCP.BCCP_ID.in(asccpIds))
                .execute();
    }

    public CcNode updateAsccpRoleOfAcc(User user, long asccpManifestId, long accManifestId) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        long userId = sessionService.userId(user);

        AsccpReleaseManifestRecord asccpReleaseManifestRecord = manifestRepository.getAsccpReleaseManifestById(asccpManifestId);
        AsccpRecord asccpRecord = getAsccpRecordById(asccpReleaseManifestRecord.getAsccpId().longValue());
        AccReleaseManifestRecord accReleaseManifestRecord = manifestRepository.getAccReleaseManifestById(accManifestId);
        AccRecord accRecord = getAccRecordById(accReleaseManifestRecord.getAccId().longValue());

        long originAsccpId = asccpReleaseManifestRecord.getAsccpId().longValue();
        asccpRecord.setAsccpId(null);
        asccpRecord.setRoleOfAccId(accRecord.getAccId());
        asccpRecord.setDen(asccpRecord.getPropertyTerm() + ". " + accRecord.getObjectClassTerm());
        asccpRecord.setLastUpdatedBy(ULong.valueOf(userId));
        asccpRecord.setLastUpdateTimestamp(timestamp);
        asccpRecord.setRevisionAction((byte) RevisionAction.Update.getValue());
        asccpRecord.setRevisionTrackingNum(asccpRecord.getRevisionTrackingNum() + 1);
        asccpRecord.insert();

        asccpReleaseManifestRecord.setRoleOfAccId(accRecord.getAccId());
        asccpReleaseManifestRecord.setAsccpId(asccpRecord.getAsccpId());
        asccpReleaseManifestRecord.update();

        updateAsccByToAsccp(userId, originAsccpId, asccpRecord.getAsccpId().longValue(),
                asccpReleaseManifestRecord.getReleaseId().longValue(), asccpRecord.getPropertyTerm(), timestamp);

        return getAsccpNodeByAsccpManifestId(user, asccpReleaseManifestRecord.getAsccpReleaseManifestId().longValue());
    }

    public CcNode updateBccpBdt(User user, long bccpManifestId, long bdtManifestId) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        ULong userId = ULong.valueOf(sessionService.userId(user));

        long bdtId = dslContext.select(DT_RELEASE_MANIFEST.DT_ID).from(DT_RELEASE_MANIFEST)
                .where(DT_RELEASE_MANIFEST.DT_RELEASE_MANIFEST_ID.eq(ULong.valueOf(bdtManifestId)))
                .fetchOneInto(long.class);

        DtRecord dtRecord = dslContext.selectFrom(DT)
                .where(DT.DT_ID.eq(ULong.valueOf(bdtId))).fetchOne();

        BccpReleaseManifestRecord bccpReleaseManifestRecord = dslContext.selectFrom(BCCP_RELEASE_MANIFEST)
                .where(BCCP_RELEASE_MANIFEST.BCCP_RELEASE_MANIFEST_ID.eq(ULong.valueOf(bccpManifestId))).fetchOne();

        long originBccpId = bccpReleaseManifestRecord.getBccpId().longValue();

        BccpRecord baseBccpRecord = dslContext.selectFrom(BCCP)
                .where(BCCP.BCCP_ID.eq(bccpReleaseManifestRecord.getBccpId())).fetchOne();

        baseBccpRecord.set(BCCP.BCCP_ID, null);
        baseBccpRecord.set(BCCP.BDT_ID, ULong.valueOf(bdtId));
        baseBccpRecord.set(BCCP.DEN, baseBccpRecord.getPropertyTerm() + ". " + dtRecord.getDataTypeTerm());
        baseBccpRecord.set(BCCP.LAST_UPDATED_BY, userId);
        baseBccpRecord.set(BCCP.LAST_UPDATE_TIMESTAMP, timestamp);
        baseBccpRecord.set(BCCP.REVISION_ACTION, RevisionAction.Update.getValue());
        baseBccpRecord.set(BCCP.REVISION_TRACKING_NUM, baseBccpRecord.getRevisionTrackingNum() + 1);
        baseBccpRecord.insert();

        bccpReleaseManifestRecord.setBccpId(baseBccpRecord.getBccpId());
        bccpReleaseManifestRecord.setBdtId(baseBccpRecord.getBdtId());
        bccpReleaseManifestRecord.update();

        updateBccByToBccp(userId.longValue(), originBccpId, baseBccpRecord.getBccpId().longValue(),
                bccpReleaseManifestRecord.getReleaseId().longValue(), baseBccpRecord.getPropertyTerm(), timestamp);

        return getBccpNodeByBccpManifestId(user, bccpReleaseManifestRecord.getBccpReleaseManifestId().longValue());
    }
    public CcAccNodeDetail updateAccState(User user, long accManifestId, CcState ccState) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        ULong userId = ULong.valueOf(sessionService.userId(user));

        AccReleaseManifestRecord accReleaseManifestRecord = manifestRepository.getAccReleaseManifestById(accManifestId);
        AccRecord accRecord = getAccRecordById(accReleaseManifestRecord.getAccId().longValue());
        
        int minChildState = CcState.Published.getValue();
        int childState;

        List<AsccReleaseManifestRecord> asccReleaseManifestRecords =
                manifestRepository.getAsccReleaseManifestByFromAccId(accReleaseManifestRecord.getAccId().longValue(),
                        accReleaseManifestRecord.getReleaseId().longValue());
        
        for (AsccReleaseManifestRecord asccReleaseManifestRecord : asccReleaseManifestRecords) {
            childState = getAsccpRecordById(asccReleaseManifestRecord.getToAsccpId().longValue()).getState();
            if (childState < minChildState) {
                minChildState = childState;
            }
        }

        List<BccReleaseManifestRecord> bccReleaseManifestRecords =
                manifestRepository.getBccReleaseManifestByFromAccId(accReleaseManifestRecord.getAccId().longValue(),
                        accReleaseManifestRecord.getReleaseId().longValue());

        for (BccReleaseManifestRecord bccReleaseManifestRecord : bccReleaseManifestRecords) {
            childState = getBccpRecordById(bccReleaseManifestRecord.getToBccpId().longValue()).getState();
            if (childState < minChildState) {
                minChildState = childState;
            }
        }

        if (minChildState < ccState.getValue()) {
            throw new IllegalArgumentException("ACC must precede the state of child CCs.");
        }

        if (accReleaseManifestRecord.getBasedAccId() != null) {
            AccRecord basedAccRecord = getAccRecordById(accReleaseManifestRecord.getBasedAccId().longValue());
            if (basedAccRecord.getState() < ccState.getValue()) {
                throw new IllegalArgumentException("ACC cannot precede the state of basedAcc.");
            }
        }

        int minBasedState = CcState.Editing.getValue();
        int basedAccState;

        List<AccReleaseManifestRecord> accReleaseManifestRecordList = manifestRepository
                .getAccReleaseManifestByBasedAccId(accReleaseManifestRecord.getAccId().longValue(),
                        accReleaseManifestRecord.getReleaseId().longValue());

        for (AccReleaseManifestRecord basedAcc: accReleaseManifestRecordList) {
            basedAccState = getAccRecordById(basedAcc.getAccId().longValue()).getState();
            if (minBasedState < basedAccState) {
                minBasedState = basedAccState;
            }
        }

        if (minBasedState > ccState.getValue()) {
            throw new IllegalArgumentException("ACC cannot be behind state of Referencing ACC.");
        }

        List<AsccpReleaseManifestRecord> asccpReleaseManifestList = manifestRepository
                .getAsccpReleaseManifestByRoleOfAccId(accReleaseManifestRecord.getAccId().longValue(),
                        accReleaseManifestRecord.getReleaseId().longValue());

        int minRoleOfState = CcState.Editing.getValue();
        int asccpState;

        for (AsccpReleaseManifestRecord asccpManifest: asccpReleaseManifestList) {
            asccpState = getAsccpRecordById(asccpManifest.getAsccpId().longValue()).getState();
            if (asccpState < minRoleOfState) {
                minRoleOfState = asccpState;
            }
        }
        if (minRoleOfState > ccState.getValue()) {
            throw new IllegalArgumentException("ACC cannot be behind state of Referencing ASCCP.");
        }

        long originAccId = accRecord.getAccId().longValue();
        
        

        accRecord.set(ACC.ACC_ID, null);
        accRecord.set(ACC.STATE, ccState.getValue());
        accRecord.set(ACC.LAST_UPDATED_BY, userId);
        accRecord.set(ACC.LAST_UPDATE_TIMESTAMP, timestamp);
        accRecord.set(ACC.REVISION_ACTION, (byte) RevisionAction.Update.getValue());
        if(ccState == CcState.Published) {
            accRecord.set(ACC.REVISION_NUM, accRecord.getRevisionNum() + 1);
            accRecord.set(ACC.REVISION_TRACKING_NUM, 0);
        } else {
            accRecord.set(ACC.REVISION_TRACKING_NUM, accRecord.getRevisionTrackingNum() + 1);
        }
        
        accRecord.insert();

        accReleaseManifestRecord.setAccId(accRecord.getAccId());
        accReleaseManifestRecord.update();

        updateAsccState(userId.longValue(), originAccId, accRecord.getAccId().longValue(),
                accReleaseManifestRecord.getReleaseId().longValue(), ccState, timestamp);
        updateBccState(userId.longValue(), originAccId, accRecord.getAccId().longValue(),
                accReleaseManifestRecord.getReleaseId().longValue(), ccState, timestamp);

        updateAsccpByRoleOfAcc(userId.longValue(), originAccId, accRecord.getAccId().longValue(),
                accReleaseManifestRecord.getReleaseId().longValue(), accRecord.getObjectClassTerm(), timestamp);
        updateAccByBasedAcc(userId.longValue(), originAccId, accRecord.getAccId().longValue(),
                accReleaseManifestRecord.getReleaseId().longValue(), timestamp);
                
        CcAccNode ccAccNode = getAccNodeByAccId(user, accReleaseManifestRecord);
        return getAccNodeDetail(user, ccAccNode);
    }

    public CcAsccpNodeDetail updateAsccpState(User user, long asccpManifestId, CcState ccState) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        ULong userId = ULong.valueOf(sessionService.userId(user));

        AsccpReleaseManifestRecord asccpReleaseManifestRecord = dslContext.selectFrom(ASCCP_RELEASE_MANIFEST)
                .where(ASCCP_RELEASE_MANIFEST.ASCCP_RELEASE_MANIFEST_ID.eq(ULong.valueOf(asccpManifestId))).fetchOne();

        AccRecord roleOfAccRecord = dslContext.selectFrom(ACC)
                .where(ACC.ACC_ID.eq(asccpReleaseManifestRecord.getRoleOfAccId())).fetchOne();

        if (roleOfAccRecord.getState() < ccState.getValue()) {
            throw new IllegalArgumentException("ASCCP state can not precede ACC.");
        }

        List<Integer> parentAccStates = dslContext.select().from(ASCC_RELEASE_MANIFEST)
                .join(ACC_RELEASE_MANIFEST).on(and(
                        ACC_RELEASE_MANIFEST.ACC_ID.eq(ASCC_RELEASE_MANIFEST.FROM_ACC_ID),
                        ACC_RELEASE_MANIFEST.RELEASE_ID.eq(ASCC_RELEASE_MANIFEST.RELEASE_ID)))
                .join(ACC).on(ACC.ACC_ID.eq(ACC_RELEASE_MANIFEST.ACC_ID))
                .where(and(ASCC_RELEASE_MANIFEST.TO_ASCCP_ID.eq(asccpReleaseManifestRecord.getAsccpId()),
                        ASCC_RELEASE_MANIFEST.RELEASE_ID.eq(asccpReleaseManifestRecord.getReleaseId()))).fetch().getValues(ACC.STATE);

        for (Integer state : parentAccStates) {
            if (state > ccState.getValue()) {
                throw new IllegalArgumentException("ASCCP state cannot be behind of parent ACC.");
            }
        }

        long originAsccpId = asccpReleaseManifestRecord.getAsccpId().longValue();

        AsccpRecord baseAsccpRecord = dslContext.selectFrom(ASCCP)
                .where(ASCCP.ASCCP_ID.eq(asccpReleaseManifestRecord.getAsccpId())).fetchOne();

        baseAsccpRecord.set(ASCCP.ASCCP_ID, null);
        baseAsccpRecord.set(ASCCP.STATE, ccState.getValue());
        baseAsccpRecord.set(ASCCP.LAST_UPDATED_BY, userId);
        baseAsccpRecord.set(ASCCP.LAST_UPDATE_TIMESTAMP, timestamp);
        baseAsccpRecord.set(ASCCP.REVISION_ACTION, (byte) RevisionAction.Update.getValue());
        if(ccState == CcState.Published) {
            baseAsccpRecord.set(ASCCP.REVISION_NUM, baseAsccpRecord.getRevisionNum() + 1);
            baseAsccpRecord.set(ASCCP.REVISION_TRACKING_NUM, 0);
        } else {
            baseAsccpRecord.set(ASCCP.REVISION_TRACKING_NUM, baseAsccpRecord.getRevisionTrackingNum() + 1);
        }
        baseAsccpRecord.insert();

        asccpReleaseManifestRecord.setAsccpId(baseAsccpRecord.getAsccpId());
        asccpReleaseManifestRecord.update();

        updateAsccByToAsccp(userId.longValue(), originAsccpId, baseAsccpRecord.getAsccpId().longValue(),
                asccpReleaseManifestRecord.getReleaseId().longValue(), baseAsccpRecord.getPropertyTerm(), timestamp);

        CcAsccpNode ccAsccpNode = getAsccpNodeByAsccpManifestId(user, asccpReleaseManifestRecord.getAsccpReleaseManifestId().longValue());
        return getAsccpNodeDetail(user, ccAsccpNode);
    }

    public CcBccpNodeDetail updateBccpState(User user, long bccpManifestId, CcState ccState) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        ULong userId = ULong.valueOf(sessionService.userId(user));

        BccpReleaseManifestRecord bccpReleaseManifestRecord = dslContext.selectFrom(BCCP_RELEASE_MANIFEST)
                .where(BCCP_RELEASE_MANIFEST.BCCP_RELEASE_MANIFEST_ID.eq(ULong.valueOf(bccpManifestId))).fetchOne();

        long originBccpId = bccpReleaseManifestRecord.getBccpId().longValue();

        BccpRecord baseBccpRecord = dslContext.selectFrom(BCCP)
                .where(BCCP.BCCP_ID.eq(bccpReleaseManifestRecord.getBccpId())).fetchOne();

        List<Integer> parentAccStates = dslContext.select().from(BCC_RELEASE_MANIFEST)
                .join(ACC_RELEASE_MANIFEST).on(and(
                        ACC_RELEASE_MANIFEST.ACC_ID.eq(BCC_RELEASE_MANIFEST.FROM_ACC_ID),
                        ACC_RELEASE_MANIFEST.RELEASE_ID.eq(BCC_RELEASE_MANIFEST.RELEASE_ID)))
                .join(ACC).on(ACC.ACC_ID.eq(ACC_RELEASE_MANIFEST.ACC_ID))
                .where(and(BCC_RELEASE_MANIFEST.TO_BCCP_ID.eq(bccpReleaseManifestRecord.getBccpId()),
                        BCC_RELEASE_MANIFEST.RELEASE_ID.eq(bccpReleaseManifestRecord.getReleaseId()))).fetch().getValues(ACC.STATE);

        for (Integer state : parentAccStates) {
            if (state > ccState.getValue()) {
                throw new IllegalArgumentException("BCCP state cannot be behind of parent ACC.");
            }
        }

        baseBccpRecord.set(BCCP.BCCP_ID, null);
        baseBccpRecord.set(BCCP.STATE, ccState.getValue());
        baseBccpRecord.set(BCCP.LAST_UPDATED_BY, userId);
        baseBccpRecord.set(BCCP.LAST_UPDATE_TIMESTAMP, timestamp);
        baseBccpRecord.set(BCCP.REVISION_ACTION, RevisionAction.Update.getValue());
        if(ccState == CcState.Published) {
            baseBccpRecord.set(BCCP.REVISION_NUM, baseBccpRecord.getRevisionNum() + 1);
            baseBccpRecord.set(BCCP.REVISION_TRACKING_NUM, 0);
        } else {
            baseBccpRecord.set(BCCP.REVISION_TRACKING_NUM, baseBccpRecord.getRevisionTrackingNum() + 1);
        }
        baseBccpRecord.insert();

        bccpReleaseManifestRecord.setBccpId(baseBccpRecord.getBccpId());
        bccpReleaseManifestRecord.update();

        updateBccByToBccp(userId.longValue(), originBccpId, baseBccpRecord.getBccpId().longValue(),
                bccpReleaseManifestRecord.getReleaseId().longValue(), baseBccpRecord.getPropertyTerm(), timestamp);

        CcBccpNode ccBccpNode = getBccpNodeByBccpManifestId(user,
                bccpReleaseManifestRecord.getBccpReleaseManifestId().longValue());
        return getBccpNodeDetail(user, ccBccpNode);
    }

    public void appendAsccp(User user, long accManifestId, long asccpManifestId) {
        long userId = sessionService.userId(user);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        AccReleaseManifestRecord accReleaseManifest = manifestRepository.getAccReleaseManifestById(accManifestId);
        AsccpReleaseManifestRecord asccpReleaseManifestRecord =
                manifestRepository.getAsccpReleaseManifestById(asccpManifestId);

        AsccReleaseManifestRecord exist = dslContext.selectFrom(ASCC_RELEASE_MANIFEST).where(
                and(ASCC_RELEASE_MANIFEST.FROM_ACC_ID.eq(accReleaseManifest.getAccId()),
                        ASCC_RELEASE_MANIFEST.TO_ASCCP_ID.eq(asccpReleaseManifestRecord.getAsccpId()))).fetchOne();
        if (exist != null) {
            throw new IllegalArgumentException("You cannot associate the same component.");
        }

        AsccpRecord asccpRecord = getAsccpRecordById(asccpReleaseManifestRecord.getAsccpId().longValue());
        AccRecord accRecord = getAccRecordById(accReleaseManifest.getAccId().longValue());

        int seqKey = getNextSeqKey(accReleaseManifest.getAccId().longValue(), accReleaseManifest.getReleaseId().longValue());

        AsccRecord asccRecord = new AsccRecord();
        asccRecord.setAsccId(null);
        asccRecord.setGuid(SrtGuid.randomGuid());
        asccRecord.setCardinalityMin(0);
        asccRecord.setCardinalityMax(-1);
        asccRecord.setSeqKey(seqKey);
        asccRecord.setFromAccId(accRecord.getAccId());
        asccRecord.setToAsccpId(asccpReleaseManifestRecord.getAsccpId());
        asccRecord.setDen(accRecord.getObjectClassTerm() + ". " + asccpRecord.getPropertyTerm());
        asccRecord.setCreatedBy(ULong.valueOf(userId));
        asccRecord.setCreationTimestamp(timestamp);
        asccRecord.setLastUpdatedBy(ULong.valueOf(userId));
        asccRecord.setLastUpdateTimestamp(timestamp);
        asccRecord.setOwnerUserId(ULong.valueOf(userId));
        asccRecord.setState(CcState.Editing.getValue());
        asccRecord.setRevisionNum(1);
        asccRecord.setRevisionTrackingNum(1);
        asccRecord.setRevisionAction((byte) RevisionAction.Insert.getValue());
        
        AsccRecord insertedAscc = dslContext.insertInto(ASCC).set(asccRecord).returning().fetchOne();

        AsccReleaseManifestRecord asccReleaseManifestRecord = new AsccReleaseManifestRecord();
        asccReleaseManifestRecord.setAsccReleaseManifestId(null);
        asccReleaseManifestRecord.setReleaseId(accReleaseManifest.getReleaseId());
        asccReleaseManifestRecord.setAsccId(insertedAscc.getAsccId());
        asccReleaseManifestRecord.setFromAccId(accRecord.getAccId());
        asccReleaseManifestRecord.setToAsccpId(asccpRecord.getAsccpId());

        dslContext.insertInto(ASCC_RELEASE_MANIFEST).set(asccReleaseManifestRecord).execute();
    }

    public void appendBccp(User user, long accManifestId, long bccpManifestId) {
        long userId = sessionService.userId(user);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        
        AccReleaseManifestRecord accReleaseManifest = manifestRepository.getAccReleaseManifestById(accManifestId);
        BccpReleaseManifestRecord bccpReleaseManifestRecord =
                manifestRepository.getBccpReleaseManifestById(bccpManifestId);

        BccReleaseManifestRecord exist = dslContext.selectFrom(BCC_RELEASE_MANIFEST).where(
                and(BCC_RELEASE_MANIFEST.FROM_ACC_ID.eq(accReleaseManifest.getAccId()),
                        BCC_RELEASE_MANIFEST.TO_BCCP_ID.eq(bccpReleaseManifestRecord.getBccpId()))).fetchOne();
        if (exist != null) {
            throw new IllegalArgumentException("You cannot associate the same component.");
        }

        BccpRecord bccpRecord = getBccpRecordById(bccpReleaseManifestRecord.getBccpId().longValue());
        AccRecord accRecord = getAccRecordById(accReleaseManifest.getAccId().longValue());

        int seqKey = getNextSeqKey(accReleaseManifest.getAccId().longValue(), accReleaseManifest.getReleaseId().longValue());
        BccRecord bccRecord = new BccRecord();
        bccRecord.setBccId(null);
        bccRecord.setGuid(SrtGuid.randomGuid());
        bccRecord.setCardinalityMin(0);
        bccRecord.setCardinalityMax(-1);
        bccRecord.setFromAccId(accRecord.getAccId());
        bccRecord.setToBccpId(bccpRecord.getBccpId());
        bccRecord.setDen(accRecord.getObjectClassTerm() + ". " + bccpRecord.getPropertyTerm());
        bccRecord.setCreatedBy(ULong.valueOf(userId));
        bccRecord.setSeqKey(seqKey);
        bccRecord.setIsDeprecated((byte) 0);
        bccRecord.setCreationTimestamp(timestamp);
        bccRecord.setLastUpdatedBy(ULong.valueOf(userId));
        bccRecord.setLastUpdateTimestamp(timestamp);
        bccRecord.setOwnerUserId(ULong.valueOf(userId));
        bccRecord.setState(CcState.Editing.getValue());
        bccRecord.setState(accRecord.getState());
        bccRecord.setEntityType(Element.getValue());
        bccRecord.setRevisionNum(1);
        bccRecord.setRevisionTrackingNum(1);
        bccRecord.setRevisionAction((byte) RevisionAction.Insert.getValue());

        BccRecord insertedBcc = dslContext.insertInto(BCC).set(bccRecord).returning().fetchOne();

        BccReleaseManifestRecord bccReleaseManifestRecord = new BccReleaseManifestRecord();
        bccReleaseManifestRecord.setBccReleaseManifestId(null);
        bccReleaseManifestRecord.setReleaseId(accReleaseManifest.getReleaseId());
        bccReleaseManifestRecord.setBccId(insertedBcc.getBccId());
        bccReleaseManifestRecord.setFromAccId(accRecord.getAccId());
        bccReleaseManifestRecord.setToBccpId(bccpRecord.getBccpId());

        dslContext.insertInto(BCC_RELEASE_MANIFEST).set(bccReleaseManifestRecord).execute();
    }

    public CcAccNode discardAccBasedId(User user, long accManifestId) {
        long userId = sessionService.userId(user);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        AccReleaseManifestRecord accReleaseManifest = manifestRepository.getAccReleaseManifestById(accManifestId);
        AccRecord accRecord = getAccRecordById(accReleaseManifest.getAccId().longValue());

        long originAccId = accRecord.getAccId().longValue();

        accRecord.setAccId(null);
        accRecord.setBasedAccId(null);
        accRecord.setLastUpdatedBy(ULong.valueOf(userId));
        accRecord.setLastUpdateTimestamp(timestamp);
        accRecord.setRevisionTrackingNum(accRecord.getRevisionTrackingNum() + 1);
        accRecord.setRevisionAction((byte) RevisionAction.Update.getValue());

        AccRecord insertedAccRecord = dslContext.insertInto(ACC).set(accRecord).returning().fetchOne();
        accReleaseManifest.setBasedAccId(null);
        accReleaseManifest.setAccId(insertedAccRecord.getAccId());
        accReleaseManifest.update();

        long newAccId = insertedAccRecord.getAccId().longValue();

        updateAccChain(userId, originAccId, newAccId, accReleaseManifest.getReleaseId().longValue(),
                accRecord.getObjectClassTerm(), timestamp);

        return getAccNodeByAccId(user, accReleaseManifest);
    }

    public CcAccNode updateAccBasedId(User user, long accManifestId, Long basedAccManifestId) {
        long userId = sessionService.userId(user);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        AccReleaseManifestRecord basedAccReleaseManifestRecord = manifestRepository.getAccReleaseManifestById(basedAccManifestId);
        AccReleaseManifestRecord accReleaseManifestRecord = manifestRepository.getAccReleaseManifestById(accManifestId);
        AccRecord accRecord = getAccRecordById(accReleaseManifestRecord.getAccId().longValue());

        long originAccId = accReleaseManifestRecord.getAccId().longValue();

        accRecord.setAccId(null);
        accRecord.setBasedAccId(basedAccReleaseManifestRecord.getAccId());
        accRecord.setLastUpdatedBy(ULong.valueOf(userId));
        accRecord.setLastUpdateTimestamp(timestamp);
        accRecord.setRevisionTrackingNum(accRecord.getRevisionTrackingNum() + 1);
        accRecord.setRevisionAction((byte) RevisionAction.Update.getValue());
        accRecord.insert();

        accReleaseManifestRecord.setBasedAccId(basedAccReleaseManifestRecord.getAccId());
        accReleaseManifestRecord.setAccId(accRecord.getAccId());
        accReleaseManifestRecord.update();

        updateAccChain(userId, originAccId, accRecord.getAccId().longValue(),
                accReleaseManifestRecord.getReleaseId().longValue(), accRecord.getObjectClassTerm(), timestamp);

        return getAccNodeByAccId(user, accReleaseManifestRecord);
    }

    private AccRecord getAccRecordById(long accId) {
        return dslContext.selectFrom(ACC).where(ACC.ACC_ID.eq(ULong.valueOf(accId))).fetchOne();
    }

    private AsccRecord getAsccRecordById(long asccId) {
        return dslContext.selectFrom(ASCC).where(ASCC.ASCC_ID.eq(ULong.valueOf(asccId))).fetchOne();
    }

    private BccRecord getBccRecordById(long bccId) {
        return dslContext.selectFrom(BCC).where(BCC.BCC_ID.eq(ULong.valueOf(bccId))).fetchOne();
    }

    private AsccpRecord getAsccpRecordById(long asccpId) {
        return dslContext.selectFrom(ASCCP).where(ASCCP.ASCCP_ID.eq(ULong.valueOf(asccpId))).fetchOne();
    }

    private BccpRecord getBccpRecordById(long bccpId) {
        return dslContext.selectFrom(BCCP).where(BCCP.BCCP_ID.eq(ULong.valueOf(bccpId))).fetchOne();
    }

    private void updateAsccByFromAcc(long userId, long originAccId, long newAccId, long releaseId,
                                     String accObjectClassTerm, Timestamp timestamp) {
        Result<AsccReleaseManifestRecord> asccReleaseManifestRecords = dslContext.selectFrom(ASCC_RELEASE_MANIFEST)
                .where(and(ASCC_RELEASE_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId)),
                        ASCC_RELEASE_MANIFEST.FROM_ACC_ID.eq(ULong.valueOf(originAccId))))
                .fetch();
        
        for(AsccReleaseManifestRecord asccReleaseManifestRecord : asccReleaseManifestRecords) {
            AsccRecord asccRecord = getAsccRecordById(asccReleaseManifestRecord.getAsccId().longValue());
            String propertyTerm = dslContext.select(ASCCP.PROPERTY_TERM).from(ASCCP).where(ASCCP.ASCCP_ID.eq(asccRecord.getToAsccpId())).fetchOneInto(String.class);
            asccRecord.set(ASCC.ASCC_ID, null);
            asccRecord.set(ASCC.DEN, accObjectClassTerm + ". " + propertyTerm);
            asccRecord.set(ASCC.FROM_ACC_ID, ULong.valueOf(newAccId));
            asccRecord.set(ASCC.LAST_UPDATED_BY, ULong.valueOf(userId));
            asccRecord.set(ASCC.LAST_UPDATE_TIMESTAMP, timestamp);
            asccRecord.set(ASCC.REVISION_ACTION, (byte) RevisionAction.Update.getValue());
            asccRecord.set(ASCC.REVISION_TRACKING_NUM, asccRecord.getRevisionTrackingNum() + 1);
            asccRecord.insert();

            asccReleaseManifestRecord.setAsccId(asccRecord.getAsccId());
            asccReleaseManifestRecord.setFromAccId(ULong.valueOf(newAccId));
            asccReleaseManifestRecord.update();
        }
    }

    private void updateBccByFromAcc(long userId, long originAccId, long newAccId, long releaseId,
                                    String accObjectClassTerm, Timestamp timestamp) {
        Result<BccReleaseManifestRecord> bccReleaseManifestRecords = dslContext.selectFrom(BCC_RELEASE_MANIFEST)
                .where(and(BCC_RELEASE_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId)),
                        BCC_RELEASE_MANIFEST.FROM_ACC_ID.eq(ULong.valueOf(originAccId))))
                .fetch();

        for(BccReleaseManifestRecord bccReleaseManifestRecord : bccReleaseManifestRecords) {
            BccRecord bccRecord = getBccRecordById(bccReleaseManifestRecord.getBccId().longValue());
            String propertyTerm = dslContext.select(BCCP.PROPERTY_TERM).from(BCCP).where(BCCP.BCCP_ID.eq(bccRecord.getToBccpId())).fetchOneInto(String.class);
            bccRecord.set(BCC.BCC_ID, null);
            bccRecord.set(BCC.DEN, accObjectClassTerm + ". " + propertyTerm);
            bccRecord.set(BCC.FROM_ACC_ID, ULong.valueOf(newAccId));
            bccRecord.set(BCC.LAST_UPDATED_BY, ULong.valueOf(userId));
            bccRecord.set(BCC.LAST_UPDATE_TIMESTAMP, timestamp);
            bccRecord.set(BCC.REVISION_ACTION, (byte) RevisionAction.Update.getValue());
            bccRecord.set(BCC.REVISION_TRACKING_NUM, bccRecord.getRevisionTrackingNum() + 1);
            bccRecord.insert();

            bccReleaseManifestRecord.setBccId(bccRecord.getBccId());
            bccReleaseManifestRecord.setFromAccId(ULong.valueOf(newAccId));
            bccReleaseManifestRecord.update();
        }
    }

    private void updateAsccByToAsccp(long userId, long originAsccpId, long newAsccpId, long releaseId,
                                     String propertyTerm, Timestamp timestamp) {
        Result<AsccReleaseManifestRecord> asccReleaseManifestRecords = dslContext.selectFrom(ASCC_RELEASE_MANIFEST)
                .where(and(ASCC_RELEASE_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId)),
                        ASCC_RELEASE_MANIFEST.TO_ASCCP_ID.eq(ULong.valueOf(originAsccpId))))
                .fetch();

        for(AsccReleaseManifestRecord asccReleaseManifestRecord : asccReleaseManifestRecords) {
            AsccRecord asccRecord = getAsccRecordById(asccReleaseManifestRecord.getAsccId().longValue());
            String objectClassTerm = dslContext.select(ACC.OBJECT_CLASS_TERM).from(ACC).where(ACC.ACC_ID.eq(asccRecord.getFromAccId())).fetchOneInto(String.class);
            asccRecord.set(ASCC.ASCC_ID, null);
            asccRecord.set(ASCC.DEN, objectClassTerm + ". " + propertyTerm);
            asccRecord.set(ASCC.TO_ASCCP_ID, ULong.valueOf(newAsccpId));
            asccRecord.set(ASCC.LAST_UPDATED_BY, ULong.valueOf(userId));
            asccRecord.set(ASCC.LAST_UPDATE_TIMESTAMP, timestamp);
            asccRecord.set(ASCC.REVISION_ACTION, (byte) RevisionAction.Update.getValue());
            asccRecord.set(ASCC.REVISION_TRACKING_NUM, asccRecord.getRevisionTrackingNum() + 1);
            asccRecord.insert();

            asccReleaseManifestRecord.setAsccId(asccRecord.getAsccId());
            asccReleaseManifestRecord.setToAsccpId(ULong.valueOf(newAsccpId));
            asccReleaseManifestRecord.update();
        }
    }

    private void updateBccByToBccp(long userId, long originBccpId, long newBccpId, long releaseId,
                                     String propertyTerm, Timestamp timestamp) {
        Result<BccReleaseManifestRecord> bccReleaseManifestRecords = dslContext.selectFrom(BCC_RELEASE_MANIFEST)
                .where(and(BCC_RELEASE_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId)),
                        BCC_RELEASE_MANIFEST.TO_BCCP_ID.eq(ULong.valueOf(originBccpId))))
                .fetch();

        for(BccReleaseManifestRecord bccReleaseManifestRecord : bccReleaseManifestRecords) {
            BccRecord bccRecord = getBccRecordById(bccReleaseManifestRecord.getBccId().longValue());
            String objectClassTerm = dslContext.select(ACC.OBJECT_CLASS_TERM).from(ACC).where(ACC.ACC_ID.eq(bccRecord.getFromAccId())).fetchOneInto(String.class);
            bccRecord.set(BCC.BCC_ID, null);
            bccRecord.set(BCC.DEN, objectClassTerm + ". " + propertyTerm);
            bccRecord.set(BCC.TO_BCCP_ID, ULong.valueOf(newBccpId));
            bccRecord.set(BCC.LAST_UPDATED_BY, ULong.valueOf(userId));
            bccRecord.set(BCC.LAST_UPDATE_TIMESTAMP, timestamp);
            bccRecord.set(BCC.REVISION_ACTION, (byte) RevisionAction.Update.getValue());
            bccRecord.set(BCC.REVISION_TRACKING_NUM, bccRecord.getRevisionTrackingNum() + 1);
            bccRecord.insert();

            bccReleaseManifestRecord.setBccId(bccRecord.getBccId());
            bccReleaseManifestRecord.setToBccpId(ULong.valueOf(newBccpId));
            bccReleaseManifestRecord.update();
        }
    }

    private void updateAsccState(long userId, long originAccId, long newAccId, long releaseId,
                                     CcState state, Timestamp timestamp) {
        Result<AsccReleaseManifestRecord> asccReleaseManifestRecords = dslContext.selectFrom(ASCC_RELEASE_MANIFEST)
                .where(and(ASCC_RELEASE_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId)),
                        ASCC_RELEASE_MANIFEST.FROM_ACC_ID.eq(ULong.valueOf(originAccId))))
                .fetch();

        for(AsccReleaseManifestRecord asccReleaseManifestRecord : asccReleaseManifestRecords) {
            AsccRecord asccRecord = getAsccRecordById(asccReleaseManifestRecord.getAsccId().longValue());
            asccRecord.set(ASCC.ASCC_ID, null);
            asccRecord.set(ASCC.FROM_ACC_ID, ULong.valueOf(newAccId));
            asccRecord.set(ASCC.LAST_UPDATED_BY, ULong.valueOf(userId));
            asccRecord.set(ASCC.LAST_UPDATE_TIMESTAMP, timestamp);
            asccRecord.set(ASCC.REVISION_ACTION, (byte) RevisionAction.Update.getValue());
            asccRecord.set(ASCC.STATE, state.getValue());
            if(state == CcState.Published) {
                asccRecord.set(ASCC.REVISION_NUM, asccRecord.getRevisionNum() + 1);
                asccRecord.set(ASCC.REVISION_TRACKING_NUM, 0);
            } else {
                asccRecord.set(ASCC.REVISION_TRACKING_NUM, asccRecord.getRevisionTrackingNum() + 1);
            }
            asccRecord.insert();

            asccReleaseManifestRecord.setAsccId(asccRecord.getAsccId());
            asccReleaseManifestRecord.setFromAccId(ULong.valueOf(newAccId));
            asccReleaseManifestRecord.update();
        }
    }

    private void updateBccState(long userId, long originAccId, long newAccId, long releaseId,
                                 CcState state, Timestamp timestamp) {
        Result<BccReleaseManifestRecord> bccReleaseManifestRecords = dslContext.selectFrom(BCC_RELEASE_MANIFEST)
                .where(and(BCC_RELEASE_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId)),
                        BCC_RELEASE_MANIFEST.FROM_ACC_ID.eq(ULong.valueOf(originAccId))))
                .fetch();

        for(BccReleaseManifestRecord bccReleaseManifestRecord : bccReleaseManifestRecords) {
            BccRecord bccRecord = getBccRecordById(bccReleaseManifestRecord.getBccId().longValue());
            bccRecord.set(BCC.BCC_ID, null);
            bccRecord.set(BCC.FROM_ACC_ID, ULong.valueOf(newAccId));
            bccRecord.set(BCC.LAST_UPDATED_BY, ULong.valueOf(userId));
            bccRecord.set(BCC.LAST_UPDATE_TIMESTAMP, timestamp);
            bccRecord.set(BCC.REVISION_ACTION, (byte) RevisionAction.Update.getValue());
            bccRecord.set(BCC.STATE, state.getValue());
            if(state == CcState.Published) {
                bccRecord.set(BCC.REVISION_NUM, bccRecord.getRevisionNum() + 1);
                bccRecord.set(BCC.REVISION_TRACKING_NUM, 0);
            } else {
                bccRecord.set(BCC.REVISION_TRACKING_NUM, bccRecord.getRevisionTrackingNum() + 1);
            }
            bccRecord.insert();

            bccReleaseManifestRecord.setBccId(bccRecord.getBccId());
            bccReleaseManifestRecord.setFromAccId(ULong.valueOf(newAccId));
            bccReleaseManifestRecord.update();
        }
    }

    public void discardAsccById(User user, long asccManifestId){
        long userId = sessionService.userId(user);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        AsccReleaseManifestRecord asccReleaseManifestRecord = dslContext.selectFrom(ASCC_RELEASE_MANIFEST).where(
                ASCC_RELEASE_MANIFEST.ASCC_RELEASE_MANIFEST_ID.eq(ULong.valueOf(asccManifestId))).fetchOne();

        AsccRecord ascc = getAsccRecordById(asccReleaseManifestRecord.getAsccId().longValue());
        ascc.setAsccId(null);
        ascc.set(ASCC.LAST_UPDATE_TIMESTAMP, timestamp);
        ascc.set(ASCC.LAST_UPDATED_BY, ULong.valueOf(userId));
        ascc.set(ASCC.REVISION_ACTION, (byte) RevisionAction.Delete.getValue());
        ascc.set(ASCC.REVISION_TRACKING_NUM, ascc.getRevisionTrackingNum() + 1);
        dslContext.insertInto(ASCC).set(ascc).execute();

        decreaseSeqKeyGreaterThan(userId, ascc.getFromAccId().longValue(), asccReleaseManifestRecord.getReleaseId().longValue(), ascc.getSeqKey(), timestamp);

        dslContext.deleteFrom(ASCC_RELEASE_MANIFEST)
                .where(ASCC_RELEASE_MANIFEST.ASCC_RELEASE_MANIFEST_ID.eq(asccReleaseManifestRecord.getAsccReleaseManifestId())).execute();
    }

    public void discardBccById(User user, long bccManifestId){
        long userId = sessionService.userId(user);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        BccReleaseManifestRecord bccReleaseManifestRecord = dslContext.selectFrom(BCC_RELEASE_MANIFEST).where(
                BCC_RELEASE_MANIFEST.BCC_RELEASE_MANIFEST_ID.eq(ULong.valueOf(bccManifestId))).fetchOne();

        BccRecord bcc = getBccRecordById(bccReleaseManifestRecord.getBccId().longValue());
        bcc.setBccId(null);
        bcc.set(BCC.LAST_UPDATE_TIMESTAMP, timestamp);
        bcc.set(BCC.LAST_UPDATED_BY, ULong.valueOf(userId));
        bcc.set(BCC.REVISION_ACTION, (byte) RevisionAction.Delete.getValue());
        bcc.set(BCC.REVISION_TRACKING_NUM, bcc.getRevisionTrackingNum() + 1);
        dslContext.insertInto(BCC).set(bcc).execute();

        decreaseSeqKeyGreaterThan(userId, bcc.getFromAccId().longValue(), bccReleaseManifestRecord.getReleaseId().longValue(), bcc.getSeqKey(), timestamp);

        dslContext.deleteFrom(BCC_RELEASE_MANIFEST)
                .where(BCC_RELEASE_MANIFEST.BCC_RELEASE_MANIFEST_ID.eq(bccReleaseManifestRecord.getBccReleaseManifestId())).execute();
    }

    private int getNextSeqKey(long accId, long releaseId) {
        Integer asccMaxSeqKey = dslContext.select(max(ASCC.SEQ_KEY))
                .from(ASCC)
                .join(ASCC_RELEASE_MANIFEST)
                .on(and(ASCC.ASCC_ID.eq(ASCC_RELEASE_MANIFEST.ASCC_ID),
                        ASCC_RELEASE_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId))))
                .where(ASCC.FROM_ACC_ID.eq(ULong.valueOf(accId)))
                .fetchOneInto(Integer.class);
        if (asccMaxSeqKey == null) {
            asccMaxSeqKey = 0;
        }

        Integer bccMaxSeqKey = dslContext.select(max(BCC.SEQ_KEY))
                .from(BCC)
                .join(BCC_RELEASE_MANIFEST)
                .on(and(BCC.BCC_ID.eq(BCC_RELEASE_MANIFEST.BCC_ID),
                        BCC_RELEASE_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId))))
                .where(BCC.FROM_ACC_ID.eq(ULong.valueOf(accId)))
                .fetchOneInto(Integer.class);
        if (bccMaxSeqKey == null) {
            bccMaxSeqKey = 0;
        }

        return Math.max(asccMaxSeqKey, bccMaxSeqKey) + 1;
    }

    private void decreaseSeqKeyGreaterThan(long userId, long accId, long releaseId, int seqKey, Timestamp timestamp) {
        List<AsccReleaseManifestRecord> asccReleaseManifestRecords = 
                manifestRepository.getAsccReleaseManifestByFromAccId(accId, releaseId);
        
        for (AsccReleaseManifestRecord asccReleaseManifestRecord : asccReleaseManifestRecords) {
            AsccRecord asccRecord = getAsccRecordById(asccReleaseManifestRecord.getAsccId().longValue());
            if (asccRecord.getSeqKey() <= seqKey) {
                continue;
            }
            asccRecord.setAsccId(null);
            asccRecord.setLastUpdatedBy(ULong.valueOf(userId));
            asccRecord.setLastUpdateTimestamp(timestamp);
            asccRecord.setRevisionAction((byte) RevisionAction.Update.getValue());
            asccRecord.setRevisionTrackingNum(asccRecord.getRevisionTrackingNum() + 1);
            asccRecord.setSeqKey(asccRecord.getSeqKey() -1);
            asccRecord.insert();

            asccReleaseManifestRecord.setAsccId(asccRecord.getAsccId());
            asccReleaseManifestRecord.update();
        }

        List<BccReleaseManifestRecord> bccReleaseManifestRecords =
                manifestRepository.getBccReleaseManifestByFromAccId(accId, releaseId);

        for (BccReleaseManifestRecord bccReleaseManifestRecord : bccReleaseManifestRecords) {
            BccRecord bccRecord = getBccRecordById(bccReleaseManifestRecord.getBccId().longValue());
            if (bccRecord.getSeqKey() <= seqKey) {
                continue;
            }
            bccRecord.setBccId(null);
            bccRecord.setLastUpdatedBy(ULong.valueOf(userId));
            bccRecord.setLastUpdateTimestamp(timestamp);
            bccRecord.setRevisionAction((byte) RevisionAction.Update.getValue());
            bccRecord.setRevisionTrackingNum(bccRecord.getRevisionTrackingNum() + 1);
            bccRecord.setSeqKey(bccRecord.getSeqKey() -1);
            bccRecord.insert();

            bccReleaseManifestRecord.setBccId(bccRecord.getBccId());
            bccReleaseManifestRecord.update();
        }
    }
}