package org.oagi.srt.gateway.http.api.cc_management.repository;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jooq.DSLContext;
import org.jooq.Record12;
import org.jooq.Record4;
import org.jooq.SelectOnConditionStep;
import org.jooq.types.ULong;
import org.oagi.srt.data.*;
import org.oagi.srt.entity.jooq.tables.records.*;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;
import org.oagi.srt.gateway.http.api.cc_management.data.node.*;
import org.oagi.srt.gateway.http.api.common.data.AccessPrivilege;
import org.oagi.srt.gateway.http.api.common.data.TrackableImpl;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.oagi.srt.gateway.http.helper.SrtGuid;
import org.oagi.srt.repository.ReleaseRepository;
import org.oagi.srt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.*;
import static org.oagi.srt.data.BCCEntityType.Attribute;
import static org.oagi.srt.data.BCCEntityType.Element;
import static org.oagi.srt.entity.jooq.Tables.*;
import static org.oagi.srt.gateway.http.api.common.data.AccessPrivilege.*;

@Repository
public class CcNodeRepository {

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private ManifestRepository manifestRepository;

    @Autowired
    private ReleaseRepository releaseRepository;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private UserRepository userRepository;

    private SelectOnConditionStep<Record12<
            ULong, String, String, ULong, Integer,
            String, Integer, Integer, Integer, ULong, ULong, ULong>> getSelectJoinStepForAccNode() {
        return dslContext.select(
                ACC.ACC_ID,
                ACC.GUID,
                ACC.DEN.as("name"),
                ACC_MANIFEST.BASED_ACC_MANIFEST_ID,
                ACC.OAGIS_COMPONENT_TYPE,
                ACC.OBJECT_CLASS_TERM,
                ACC.STATE.as("raw_state"),
                ACC.REVISION_NUM,
                ACC.REVISION_TRACKING_NUM,
                ACC_MANIFEST.RELEASE_ID,
                ACC.OWNER_USER_ID,
                ACC_MANIFEST.ACC_MANIFEST_ID.as("manifest_id"))
                .from(ACC)
                .join(ACC_MANIFEST)
                .on(ACC.ACC_ID.eq(ACC_MANIFEST.ACC_ID));
    }

    public CcAccNode getAccNodeByAccManifest(User user, long accId, long releaseId) {
        AccManifestRecord accManifestRecord =
                dslContext.selectFrom(ACC_MANIFEST)
                        .where(and(
                                ACC_MANIFEST.ACC_ID.eq(ULong.valueOf(accId)),
                                ACC_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId))
                        ))
                        .fetchOne();

        return getAccNodeByAccManifest(user, accManifestRecord);
    }

    public CcAccNode getAccNodeByAccManifest(User user, AccManifestRecord accManifestRecord) {
        CcAccNode accNode = getSelectJoinStepForAccNode()
                .where(ACC_MANIFEST.ACC_MANIFEST_ID.eq(accManifestRecord.getAccManifestId()))
                .fetchOneInto(CcAccNode.class);
        return arrangeAccNode(user, accNode, accManifestRecord.getReleaseId());
    }

    public CcAccNode getAccNodeFromAsccByAsccpId(User user, long toAsccpId, ULong releaseId) {
        CcAsccNode asccNode = dslContext.select(
                ASCC.ASCC_ID,
                ASCC_MANIFEST.FROM_ACC_MANIFEST_ID,
                ASCC.SEQ_KEY,
                ASCC.REVISION_NUM,
                ASCC.REVISION_TRACKING_NUM,
                ASCC_MANIFEST.RELEASE_ID)
                .from(ASCC)
                .join(ASCC_MANIFEST)
                .on(ASCC.ASCC_ID.eq(ASCC_MANIFEST.ASCC_ID))
                .join(ASCCP_MANIFEST)
                .on(ASCC_MANIFEST.TO_ASCCP_MANIFEST_ID.eq(ASCCP_MANIFEST.ASCCP_MANIFEST_ID))
                .where(and(
                        ASCCP_MANIFEST.ASCCP_ID.eq(ULong.valueOf(toAsccpId)),
                        ASCC_MANIFEST.RELEASE_ID.eq(releaseId)
                ))
                .fetchOneInto(CcAsccNode.class);

        AccManifestRecord accManifestRecord = dslContext.select(ACC_MANIFEST.fields())
                .from(ACC_MANIFEST)
                .join(ASCC_MANIFEST).on(ACC_MANIFEST.ACC_MANIFEST_ID.eq(ASCC_MANIFEST.FROM_ACC_MANIFEST_ID))
                .where(and(
                        ASCC_MANIFEST.ASCC_MANIFEST_ID.eq(ULong.valueOf(asccNode.getFromAccManifestId())),
                        ACC_MANIFEST.RELEASE_ID.eq(releaseId)
                ))
                .fetchOneInto(AccManifestRecord.class);

        return getAccNodeByAccManifest(user, accManifestRecord);
    }

    public void createAscc(User user, ULong accManifestId, long asccManifestId) {
        AccManifestRecord accManifestRecord =
                manifestRepository.getAccManifestById(accManifestId);
        AsccManifestRecord asccManifestRecord =
                manifestRepository.getAsccManifestById(asccManifestId);

        String accObjectClassTerm = dslContext.select(ACC.OBJECT_CLASS_TERM)
                .from(ACC).where(ACC.ACC_ID.eq(accManifestRecord.getAccId()))
                .fetchOneInto(String.class);

        String asccDen = dslContext.select(ASCC.DEN)
                .from(ASCC).where(ASCC.ASCC_ID.eq(asccManifestRecord.getAsccId()))
                .fetchOneInto(String.class);

        ULong toAsccpId = dslContext.select(ASCC.TO_ASCCP_ID)
                .from(ASCC).where(ASCC.ASCC_ID.eq(asccManifestRecord.getAsccId()))
                .fetchOneInto(ULong.class);

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
                accManifestRecord.getAccId(),
                toAsccpId,
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
     * @param releaseId   a release ID
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

        AccManifestRecord accManifestRecord =
                new AccManifestRecord();
        accManifestRecord.setAccId(accRecord.getAccId());
        accManifestRecord.setReleaseId(ULong.valueOf(releaseId));

        return dslContext.insertInto(ACC_MANIFEST)
                .set(accManifestRecord)
                .returning(ACC_MANIFEST.ACC_MANIFEST_ID)
                .fetchOne().getAccManifestId().longValue();
    }

    public long createAsccp(long requesterId, long roleOfAccManifestId) {
        AccManifestRecord accManifestRecord =
                dslContext.selectFrom(ACC_MANIFEST)
                        .where(ACC_MANIFEST.ACC_MANIFEST_ID.eq(ULong.valueOf(roleOfAccManifestId)))
                        .fetchOneInto(AccManifestRecord.class);

        String accObjectClassTerm = dslContext.select(ACC.OBJECT_CLASS_TERM)
                .from(ACC)
                .where(ACC.ACC_ID.eq(accManifestRecord.getAccId()))
                .fetchOneInto(String.class);

        ULong userId = ULong.valueOf(requesterId);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        AsccpRecord asccpRecord = new AsccpRecord();
        asccpRecord.setGuid(SrtGuid.randomGuid());
        asccpRecord.setPropertyTerm("A new ASCCP property");
        asccpRecord.setRoleOfAccId(accManifestRecord.getAccId());
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

        AsccpManifestRecord asccpManifestRecord =
                new AsccpManifestRecord();
        asccpManifestRecord.setAsccpId(asccpRecord.getAsccpId());
        asccpManifestRecord.setReleaseId(accManifestRecord.getReleaseId());
        asccpManifestRecord.setRoleOfAccManifestId(accManifestRecord.getAccManifestId());

        return dslContext.insertInto(ASCCP_MANIFEST)
                .set(asccpManifestRecord)
                .returning(ASCCP_MANIFEST.ASCCP_MANIFEST_ID)
                .fetchOne().getAsccpManifestId().longValue();
    }

    public long createBccp(long requesterId, long bdtManifestId) {
        DtManifestRecord dtManifestRecord =
                dslContext.selectFrom(DT_MANIFEST)
                        .where(DT_MANIFEST.DT_MANIFEST_ID.eq(ULong.valueOf(bdtManifestId)))
                        .fetchOneInto(DtManifestRecord.class);

        String dtDataTypeTerm = dslContext.select(DT.DATA_TYPE_TERM)
                .from(DT)
                .where(DT.DT_ID.eq(dtManifestRecord.getDtId()))
                .fetchOneInto(String.class);

        ULong userId = ULong.valueOf(requesterId);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        BccpRecord bccpRecord = new BccpRecord();
        bccpRecord.setGuid(SrtGuid.randomGuid());
        bccpRecord.setPropertyTerm("A new BCCP property");
        bccpRecord.setRepresentationTerm(dtDataTypeTerm);
        bccpRecord.setBdtId(dtManifestRecord.getDtId());
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

        BccpManifestRecord bccpManifestRecord =
                new BccpManifestRecord();
        bccpManifestRecord.setBccpId(bccpRecord.getBccpId());
        bccpManifestRecord.setReleaseId(dtManifestRecord.getReleaseId());
        bccpManifestRecord.setBdtManifestId(dtManifestRecord.getDtManifestId());

        return dslContext.insertInto(BCCP_MANIFEST)
                .set(bccpManifestRecord)
                .returning(BCCP_MANIFEST.BCCP_MANIFEST_ID)
                .fetchOne().getBccpManifestId().longValue();
    }

    public CcAccNodeDetail updateAcc(User user, CcAccNodeDetail accNodeDetail) {
        long userId = sessionService.userId(user);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        AccManifestRecord accManifestRecord = dslContext.selectFrom(ACC_MANIFEST)
                .where(ACC_MANIFEST.ACC_MANIFEST_ID.eq(ULong.valueOf(accNodeDetail.getManifestId()))).fetchOne();

        AccRecord baseAccRecord = dslContext.selectFrom(ACC)
                .where(ACC.ACC_ID.eq(accManifestRecord.getAccId())).fetchOne();

        long originAccid = accManifestRecord.getAccId().longValue();

        baseAccRecord.set(ACC.ACC_ID, null);
        baseAccRecord.set(ACC.OBJECT_CLASS_TERM, accNodeDetail.getObjectClassTerm());
        baseAccRecord.set(ACC.OAGIS_COMPONENT_TYPE, (int) accNodeDetail.getOagisComponentType());
        baseAccRecord.set(ACC.DEFINITION, accNodeDetail.getDefinition());
        baseAccRecord.set(ACC.DEN, accNodeDetail.getObjectClassTerm() + ". Details");
        baseAccRecord.set(ACC.IS_DEPRECATED, (byte) (accNodeDetail.isDeprecated() ? 1 : 0));
        baseAccRecord.set(ACC.IS_ABSTRACT, (byte) (accNodeDetail.isAbstracted() ? 1 : 0));
        baseAccRecord.set(ACC.LAST_UPDATED_BY, ULong.valueOf(userId));
        baseAccRecord.set(ACC.LAST_UPDATE_TIMESTAMP, timestamp);
        baseAccRecord.set(ACC.REVISION_ACTION, (byte) RevisionAction.Update.getValue());
        baseAccRecord.set(ACC.REVISION_TRACKING_NUM, baseAccRecord.getRevisionTrackingNum() + 1);
        baseAccRecord.insert();

        accManifestRecord.setAccId(baseAccRecord.getAccId());
        accManifestRecord.update();

        dslContext.update(ACC_MANIFEST)
                .set(accManifestRecord)
                .where(ACC_MANIFEST.ACC_MANIFEST_ID.eq(accManifestRecord.getAccManifestId()))
                .execute();

        updateAccChain(userId, originAccid,
                baseAccRecord.getAccId(),
                accManifestRecord.getReleaseId(),
                baseAccRecord.getObjectClassTerm(), timestamp);

        CcAccNode updateAccNode = getAccNodeByAccManifest(user, accManifestRecord);
        return getAccNodeDetail(user, updateAccNode);
    }

    private void updateAccChain(long userId, long originAccid,
                                ULong newAccId, ULong releaseId,
                                String objectClassTerm, Timestamp timestamp) {
        updateAsccByFromAcc(userId, originAccid, newAccId, releaseId, objectClassTerm, timestamp);
        updateBccByFromAcc(userId, originAccid, newAccId, releaseId, objectClassTerm, timestamp);
        updateAsccpByRoleOfAcc(userId, originAccid, newAccId, releaseId, objectClassTerm, timestamp);
        updateAccByBasedAcc(userId, originAccid, newAccId, releaseId, timestamp);
    }

    public void updateAsccpByRoleOfAcc(long userId, long originRoleOfAccId,
                                       ULong newRoleOfAccId, ULong releaseId,
                                       String accObjectClassTerm, Timestamp timestamp) {
        List<AsccpManifestRecord> asccpManifestRecords =
                manifestRepository.getAsccpManifestByRoleOfAccId(originRoleOfAccId, releaseId);

        for (AsccpManifestRecord asccpManifestRecord : asccpManifestRecords) {
            AsccpRecord asccpRecord = dslContext.selectFrom(ASCCP)
                    .where(ASCCP.ASCCP_ID.eq(asccpManifestRecord.getAsccpId())).fetchOne();

            asccpRecord.setAsccpId(null);
            asccpRecord.setRoleOfAccId(newRoleOfAccId);
            if (accObjectClassTerm != null) {
                asccpRecord.setDen(asccpRecord.getPropertyTerm() + ". " + accObjectClassTerm);
            }
            asccpRecord.setLastUpdatedBy(ULong.valueOf(userId));
            asccpRecord.setLastUpdateTimestamp(timestamp);
            asccpRecord.setRevisionAction((byte) RevisionAction.Update.getValue());
            asccpRecord.setRevisionTrackingNum(asccpRecord.getRevisionTrackingNum() + 1);
            asccpRecord.insert();

            ULong originAsccpId = asccpManifestRecord.getAsccpId();
            asccpManifestRecord.setRoleOfAccManifestId(newRoleOfAccId);
            asccpManifestRecord.setAsccpId(asccpRecord.getAsccpId());
            asccpManifestRecord.update();

            List<AsccManifestRecord> asccManifestRecordList =
                    manifestRepository.getAsccManifestByToAsccpId(originAsccpId, releaseId);

            for (AsccManifestRecord asccManifestRecord : asccManifestRecordList) {
                AsccRecord asccRecord = dslContext.selectFrom(ASCC)
                        .where(ASCC.ASCC_ID.eq(asccManifestRecord.getAsccId())).fetchOne();

                asccRecord.setAsccId(null);
                asccRecord.setToAsccpId(asccpRecord.getAsccpId());
                asccRecord.setLastUpdatedBy(ULong.valueOf(userId));
                asccRecord.setLastUpdateTimestamp(timestamp);
                asccRecord.setRevisionAction((byte) RevisionAction.Update.getValue());
                asccRecord.setRevisionTrackingNum(asccRecord.getRevisionTrackingNum() + 1);
                asccRecord.insert();

                asccManifestRecord.setToAsccpManifestId(asccpManifestRecord.getAsccpManifestId());
                asccManifestRecord.setAsccId(asccRecord.getAsccId());
                asccManifestRecord.update();
            }
        }
    }

    public void updateAccByBasedAcc(long userId, long originBasedAccId,
                                    ULong newBasedAccId, ULong releaseId,
                                    Timestamp timestamp) {
        List<AccManifestRecord> accManifestRecords =
                manifestRepository.getAccManifestByBasedAccId(originBasedAccId, releaseId);

        for (AccManifestRecord accManifestRecord : accManifestRecords) {
            AccRecord accRecord = dslContext.selectFrom(ACC)
                    .where(ACC.ACC_ID.eq(accManifestRecord.getAccId())).fetchOne();
            accRecord.setAccId(null);
            accRecord.setBasedAccId(newBasedAccId);
            accRecord.setLastUpdatedBy(ULong.valueOf(userId));
            accRecord.setLastUpdateTimestamp(timestamp);
            accRecord.setRevisionAction((byte) RevisionAction.Update.getValue());
            accRecord.setRevisionTrackingNum(accRecord.getRevisionTrackingNum() + 1);
            accRecord.insert();

            long originAccId = accManifestRecord.getAccId().longValue();
            accManifestRecord.setAccId(accRecord.getAccId());
            accManifestRecord.update();

            updateAccChain(userId, originAccId,
                    accRecord.getAccId(), accManifestRecord.getReleaseId(),
                    accRecord.getObjectClassTerm(), timestamp);
        }
    }

    public long updateAscc(User user, CcAsccpNodeDetail.Ascc asccNodeDetail, long manifestId) {
        long userId = sessionService.userId(user);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        boolean isChanged = false;

        AsccManifestRecord asccManifestRecord = dslContext.selectFrom(ASCC_MANIFEST)
                .where(ASCC_MANIFEST.ASCC_MANIFEST_ID.eq(ULong.valueOf(manifestId))).fetchOne();
        AsccRecord baseAsccRecord = dslContext.selectFrom(ASCC)
                .where(ASCC.ASCC_ID.eq(asccManifestRecord.getAsccId())).fetchOne();

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

            asccManifestRecord.setAsccId(baseAsccRecord.getAsccId());
            asccManifestRecord.update();
        }

        return baseAsccRecord.getAsccId().longValue();
    }

    public CcAsccpNode updateAsccp(User user, CcAsccpNodeDetail.Asccp asccpNodeDetail, long manifestId) {
        long userId = sessionService.userId(user);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        boolean isChanged = false;

        AsccpManifestRecord asccpManifestRecord = dslContext.selectFrom(ASCCP_MANIFEST)
                .where(ASCCP_MANIFEST.ASCCP_MANIFEST_ID.eq(ULong.valueOf(manifestId))).fetchOne();
        AsccpRecord baseAsccpRecord = dslContext.selectFrom(ASCCP)
                .where(ASCCP.ASCCP_ID.eq(asccpManifestRecord.getAsccpId())).fetchOne();

        long originAsccpId = asccpManifestRecord.getAsccpId().longValue();

        if (!baseAsccpRecord.getPropertyTerm().equals(asccpNodeDetail.getPropertyTerm())) {
            String objectClassTerm = dslContext.select(ACC.OBJECT_CLASS_TERM)
                    .from(ACC)
                    .join(ACC_MANIFEST).on(ACC_MANIFEST.ACC_ID.eq(ACC.ACC_ID))
                    .where(ACC_MANIFEST.ACC_MANIFEST_ID.eq(asccpManifestRecord.getRoleOfAccManifestId()))
                    .fetchOneInto(String.class);
            baseAsccpRecord.setPropertyTerm(asccpNodeDetail.getPropertyTerm());
            baseAsccpRecord.setDen(asccpNodeDetail.getPropertyTerm() + ". " + objectClassTerm);
            isChanged = true;
        }
        byte reusable = (byte) (asccpNodeDetail.isReusable() ? 1 : 0);
        if (!baseAsccpRecord.getReusableIndicator().equals(reusable)) {
            baseAsccpRecord.setReusableIndicator(reusable);
            isChanged = true;
        }

        byte deprecated = (byte) (asccpNodeDetail.isDeprecated() ? 1 : 0);
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

            asccpManifestRecord.setAsccpId(baseAsccpRecord.getAsccpId());
            asccpManifestRecord.update();

            updateAsccByToAsccp(userId, originAsccpId,
                    baseAsccpRecord.getAsccpId().longValue(),
                    asccpManifestRecord.getReleaseId(),
                    baseAsccpRecord.getPropertyTerm(), timestamp);
        }

        return getAsccpNodeByAsccpManifestId(user, asccpManifestRecord.getAsccpManifestId().longValue());
    }

    public long updateBcc(User user, CcBccpNodeDetail.Bcc bccNodeDetail, long manifestId) {
        long userId = sessionService.userId(user);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        boolean isChanged = false;

        BccManifestRecord bccManifestRecord = dslContext.selectFrom(BCC_MANIFEST)
                .where(BCC_MANIFEST.BCC_MANIFEST_ID.eq(ULong.valueOf(manifestId))).fetchOne();
        BccRecord baseBccRecord = dslContext.selectFrom(BCC)
                .where(BCC.BCC_ID.eq(bccManifestRecord.getBccId())).fetchOne();

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

            bccManifestRecord.setBccId(baseBccRecord.getBccId());
            bccManifestRecord.update();
        }

        return baseBccRecord.getBccId().longValue();
    }

    public CcBccpNode updateBccp(User user, CcBccpNodeDetail.Bccp bccpNodeDetail, long manifestId) {
        long userId = sessionService.userId(user);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        boolean isChanged = false;

        BccpManifestRecord bccpManifestRecord = dslContext.selectFrom(BCCP_MANIFEST)
                .where(BCCP_MANIFEST.BCCP_MANIFEST_ID.eq(ULong.valueOf(manifestId))).fetchOne();

        long originBccpId = bccpManifestRecord.getBccpId().longValue();

        BccpRecord baseBccpRecord = dslContext.selectFrom(BCCP)
                .where(BCCP.BCCP_ID.eq(bccpManifestRecord.getBccpId())).fetchOne();

        if (!baseBccpRecord.getPropertyTerm().equals(bccpNodeDetail.getPropertyTerm())) {
            String dataTypeTerm = dslContext.select(DT.DATA_TYPE_TERM)
                    .from(DT)
                    .join(DT_MANIFEST).on(DT.DT_ID.eq(DT_MANIFEST.DT_ID))
                    .where(DT_MANIFEST.DT_MANIFEST_ID.eq(bccpManifestRecord.getBdtManifestId()))
                    .fetchOneInto(String.class);
            baseBccpRecord.setPropertyTerm(bccpNodeDetail.getPropertyTerm());
            baseBccpRecord.setDen(bccpNodeDetail.getPropertyTerm() + ". " + dataTypeTerm);
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

            bccpManifestRecord.setBccpId(baseBccpRecord.getBccpId());
            bccpManifestRecord.update();

            updateBccByToBccp(userId, originBccpId,
                    baseBccpRecord.getBccpId().longValue(), bccpManifestRecord.getReleaseId(),
                    baseBccpRecord.getPropertyTerm(), timestamp);
        }

        return getBccpNodeByBccpManifestId(user, bccpManifestRecord.getBccpManifestId().longValue());
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
        if (accNode.getBasedAccManifestId() != null) {
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
                    ASCC_MANIFEST.RELEASE_ID)
                    .from(ASCC)
                    .join(ASCC_MANIFEST)
                    .on(ASCC.ASCC_ID.eq(ASCC_MANIFEST.ASCC_ID))
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
                    BCC_MANIFEST.RELEASE_ID)
                    .from(BCC)
                    .join(BCC_MANIFEST)
                    .on(BCC.BCC_ID.eq(BCC_MANIFEST.BCC_ID))
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
                ACC_MANIFEST.ACC_ID.as("role_of_acc_id"),
                ASCCP.STATE.as("raw_state"),
                ASCCP.REVISION_NUM,
                ASCCP.REVISION_TRACKING_NUM,
                ASCCP_MANIFEST.RELEASE_ID,
                ASCCP_MANIFEST.ASCCP_MANIFEST_ID.as("manifest_id"),
                ASCCP.OWNER_USER_ID)
                .from(ASCCP)
                .join(ASCCP_MANIFEST)
                .on(ASCCP.ASCCP_ID.eq(ASCCP_MANIFEST.ASCCP_ID))
                .join(ACC_MANIFEST)
                .on(ASCCP_MANIFEST.ROLE_OF_ACC_MANIFEST_ID.eq(ACC_MANIFEST.ACC_MANIFEST_ID))
                .where(ASCCP_MANIFEST.ASCCP_MANIFEST_ID.eq(ULong.valueOf(manifestId)))
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
                AppUser owner = userRepository.findById(ownerUserId);
                if (!owner.isDeveloper()){
                    accessPrivilege = CanEdit;
                } else {
                    accessPrivilege = CanView;
                }
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
                ASCCP_MANIFEST.RELEASE_ID)
                .from(ASCCP)
                .join(ASCCP_MANIFEST)
                .on(ASCCP.ASCCP_ID.eq(ASCCP_MANIFEST.ASCCP_ID))
                .join(ACC_MANIFEST)
                .on(ASCCP_MANIFEST.ROLE_OF_ACC_MANIFEST_ID.eq(ACC_MANIFEST.ACC_MANIFEST_ID))
                .where(and(
                        ACC_MANIFEST.ACC_ID.eq(ULong.valueOf(roleOfAccId)),
                        ASCCP_MANIFEST.RELEASE_ID.eq(releaseId)
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
                DT_MANIFEST.DT_ID.as("bdt_id"),
                BCCP.STATE.as("raw_state"),
                BCCP.REVISION_NUM,
                BCCP.REVISION_TRACKING_NUM,
                BCCP_MANIFEST.RELEASE_ID,
                BCCP_MANIFEST.BCCP_MANIFEST_ID.as("manifest_id"),
                BCCP.OWNER_USER_ID)
                .from(BCCP)
                .join(BCCP_MANIFEST)
                .on(BCCP.BCCP_ID.eq(BCCP_MANIFEST.BCCP_ID))
                .join(DT_MANIFEST)
                .on(BCCP_MANIFEST.BDT_MANIFEST_ID.eq(DT_MANIFEST.DT_MANIFEST_ID))
                .where(BCCP_MANIFEST.BCCP_MANIFEST_ID.eq(ULong.valueOf(manifestId)))
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

        AccManifestRecord accManifestRecord = manifestRepository.getAccManifestById(
                ULong.valueOf(accNode.getManifestId()));

        if (accManifestRecord.getBasedAccManifestId() != null) {
            Long basedAccId = dslContext.select(ACC_MANIFEST.ACC_ID)
                    .from(ACC_MANIFEST)
                    .where(ACC_MANIFEST.ACC_MANIFEST_ID.eq(accManifestRecord.getBasedAccManifestId()))
                    .fetchOneInto(Long.class);
            Long releaseId = accNode.getReleaseId();
            CcAccNode basedAccNode;
            basedAccNode = getAccNodeByAccManifest(user, basedAccId, releaseId);
            descendants.add(basedAccNode);
        }

        Long releaseId = accNode.getReleaseId();
        long fromAccId;
        fromAccId = accManifestRecord.getAccId().longValue();

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
                    CcAccNode uegAccNode = getAccNodeByAccManifest(user, asccpNode.getRoleOfAccId(), releaseId);
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

    private int getLatestRevisionAscc(long asccManifestId) {
        return dslContext.select(
                ASCC.REVISION_NUM
        ).from(ASCC_MANIFEST)
                .join(ASCC).on(ASCC_MANIFEST.ASCC_ID.eq(ASCC.ASCC_ID))
                .where(ASCC_MANIFEST.ASCC_MANIFEST_ID.eq(ULong.valueOf(asccManifestId)))
                .fetchOptionalInto(int.class).orElse(0);
    }

    private int getLatestRevisionBcc(long bccManifestId) {
        return dslContext.select(
                BCC.REVISION_NUM
        ).from(BCC_MANIFEST)
                .join(BCC).on(BCC_MANIFEST.BCC_ID.eq(BCC.BCC_ID))
                .where(BCC_MANIFEST.BCC_MANIFEST_ID.eq(ULong.valueOf(bccManifestId)))
                .fetchOptionalInto(int.class).orElse(0);
    }

    private List<CcAsccpNode> getAsccpNodes(User user, long fromAccId, Long releaseId) {
        List<CcAsccNode> asccNodes = dslContext.select(
                ASCC.ASCC_ID,
                ASCC_MANIFEST.ASCC_MANIFEST_ID.as("manifest_id"),
                ASCC.GUID,
                ACC_MANIFEST.ACC_ID.as("from_acc_id"),
                ASCCP_MANIFEST.ASCCP_ID.as("to_asccp_id"),
                ASCC.SEQ_KEY,
                ASCC.STATE.as("raw_state"),
                ASCC.REVISION_NUM,
                ASCC.REVISION_TRACKING_NUM,
                ASCC_MANIFEST.RELEASE_ID)
                .from(ASCC)
                .join(ASCC_MANIFEST)
                .on(ASCC.ASCC_ID.eq(ASCC_MANIFEST.ASCC_ID))
                .join(ACC_MANIFEST)
                .on(ASCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(ACC_MANIFEST.ACC_MANIFEST_ID))
                .join(ACC_MANIFEST)
                .on(ASCC_MANIFEST.TO_ASCCP_MANIFEST_ID.eq(ASCCP_MANIFEST.ASCCP_MANIFEST_ID))
                .where(and(
                        ACC_MANIFEST.ACC_ID.eq(ULong.valueOf(fromAccId)),
                        ASCC_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId))
                ))
                .fetchInto(CcAsccNode.class);

        if (asccNodes.isEmpty()) {
            return Collections.emptyList();
        }

        return asccNodes.stream().map(asccNode -> {
            long manifestId =
                    dslContext.select(ASCCP_MANIFEST.ASCCP_MANIFEST_ID)
                            .from(ASCCP_MANIFEST)
                            .where(ASCCP_MANIFEST.ASCCP_MANIFEST_ID.eq(ULong.valueOf(asccNode.getToAsccpManifestId())))
                            .fetchOneInto(Long.class);

            CcAsccpNode asccpNode =
                    getAsccpNodeByAsccpManifestId(user, manifestId);
            asccpNode.setSeqKey(asccNode.getSeqKey());
            asccpNode.setAsccId(asccNode.getAsccId());
            asccpNode.setAsccManifestId(asccNode.getManifestId());
            asccpNode.setRevisionNum(getLatestRevisionAscc(asccNode.getManifestId()));
            return asccpNode;
        }).collect(Collectors.toList());
    }

    private List<CcBccpNode> getBccpNodes(User user, long fromAccId, Long releaseId) {
        List<CcBccNode> bccNodes = dslContext.select(
                BCC.BCC_ID,
                BCC_MANIFEST.BCC_MANIFEST_ID.as("manifest_id"),
                BCC.GUID,
                BCCP_MANIFEST.BCCP_ID.as("to_bccp_id"),
                BCC.SEQ_KEY,
                BCC.ENTITY_TYPE,
                BCC.STATE.as("raw_state"),
                BCC.REVISION_NUM,
                BCC.REVISION_TRACKING_NUM,
                BCC_MANIFEST.RELEASE_ID)
                .from(BCC)
                .join(BCC_MANIFEST)
                .on(BCC.BCC_ID.eq(BCC_MANIFEST.BCC_ID))
                .join(BCCP_MANIFEST)
                .on(BCC_MANIFEST.TO_BCCP_MANIFEST_ID.eq(BCCP_MANIFEST.BCCP_MANIFEST_ID))
                .where(BCC.FROM_ACC_ID.eq(ULong.valueOf(fromAccId)).and(BCC_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId))))
                .fetchInto(CcBccNode.class);

        if (bccNodes.isEmpty()) {
            return Collections.emptyList();
        }

        return bccNodes.stream().map(bccNode -> {
            long manifestId =
                    dslContext.select(BCCP_MANIFEST.BCCP_MANIFEST_ID)
                            .from(BCCP_MANIFEST)
                            .where(BCCP_MANIFEST.BCCP_MANIFEST_ID.eq((ULong.valueOf(bccNode.getToBccpManifestId()))))
                            .fetchOneInto(Long.class);

            CcBccpNode bccpNode = getBccpNodeByBccpManifestId(user, manifestId);
            bccpNode.setSeqKey(bccNode.getSeqKey());
            bccpNode.setAttribute(BCCEntityType.valueOf(bccNode.getEntityType()) == Attribute);
            bccpNode.setBccId(bccNode.getBccId());
            bccpNode.setBccManifestId(bccNode.getManifestId());
            bccpNode.setRevisionNum(getLatestRevisionBcc(bccNode.getManifestId()));
            return bccpNode;
        }).collect(Collectors.toList());
    }

    public List<? extends CcNode> getDescendants(User user, CcAsccpNode asccpNode) {
        AsccpManifestRecord asccpManifestRecord = manifestRepository.getAsccpManifestById(
                ULong.valueOf(asccpNode.getManifestId()));
        long asccpId = asccpManifestRecord.getAsccpId().longValue();

        long roleOfAccId = dslContext.select(ASCCP.ROLE_OF_ACC_ID).from(ASCCP)
                .where(ASCCP.ASCCP_ID.eq(ULong.valueOf(asccpId)))
                .fetchOneInto(Long.class);

        Long releaseId = asccpNode.getReleaseId();

        return Arrays.asList(getAccNodeByAccManifest(user, roleOfAccId, releaseId));
    }

    public List<? extends CcNode> getDescendants(User user, CcBccpNode bccpNode) {
        BccpManifestRecord BccpManifestRecord = manifestRepository.getBccpManifestById(
                ULong.valueOf(bccpNode.getManifestId()));
        long bccpId = BccpManifestRecord.getBccpId().longValue();

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
                ACC.DEFINITION_SOURCE,
                ACC_MANIFEST.ACC_MANIFEST_ID.as("manifest_id"))
                .from(ACC_MANIFEST)
                .join(ACC)
                .on(ACC.ACC_ID.eq(ACC_MANIFEST.ACC_ID))
                .where(ACC_MANIFEST.ACC_MANIFEST_ID.eq(ULong.valueOf(accNode.getManifestId())))
                .fetchOneInto(CcAccNodeDetail.class);
    }

    public CcAsccpNodeDetail getAsccpNodeDetail(User user, CcAsccpNode asccpNode) {
        CcAsccpNodeDetail asccpNodeDetail = new CcAsccpNodeDetail();

        long asccManifestId = asccpNode.getAsccManifestId();
        if (asccManifestId > 0L) {
            CcAsccpNodeDetail.Ascc ascc = dslContext.select(
                    ASCC_MANIFEST.ASCC_MANIFEST_ID.as("manifest_id"),
                    ASCC.ASCC_ID,
                    ASCC.GUID,
                    ASCC.DEN,
                    ASCC.CARDINALITY_MIN,
                    ASCC.CARDINALITY_MAX,
                    ASCC.IS_DEPRECATED.as("deprecated"),
                    ASCC.DEFINITION,
                    ASCC.DEFINITION_SOURCE)
                    .from(ASCC_MANIFEST)
                    .join(ASCC)
                    .on(ASCC.ASCC_ID.eq(ASCC_MANIFEST.ASCC_ID))
                    .where(ASCC_MANIFEST.ASCC_MANIFEST_ID.eq(ULong.valueOf(asccManifestId)))
                    .fetchOneInto(CcAsccpNodeDetail.Ascc.class);
            ascc.setRevisionNum(getLatestRevisionAscc(asccManifestId));
            asccpNodeDetail.setAscc(ascc);
        }

        long asccpManifestIdId = asccpNode.getManifestId();
        CcAsccpNodeDetail.Asccp asccp = dslContext.select(
                ASCCP_MANIFEST.ASCCP_MANIFEST_ID.as("manifest_id"),
                ASCCP.ASCCP_ID,
                ASCCP.GUID,
                ASCCP.PROPERTY_TERM,
                ASCCP.DEN,
                ASCCP.REUSABLE_INDICATOR.as("reusable"),
                ASCCP.IS_DEPRECATED.as("deprecated"),
                ASCCP.DEFINITION,
                ASCCP.DEFINITION_SOURCE)
                .from(ASCCP_MANIFEST)
                .join(ASCCP)
                .on(ASCCP.ASCCP_ID.eq(ASCCP_MANIFEST.ASCCP_ID))
                .where(ASCCP_MANIFEST.ASCCP_MANIFEST_ID.eq(ULong.valueOf(asccpManifestIdId)))
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

    public AsccpManifestRecord getAsccpManifestById(long manifestId) {
        return dslContext.selectFrom(ASCCP_MANIFEST)
                .where(ASCCP_MANIFEST.ASCCP_MANIFEST_ID.eq(ULong.valueOf(manifestId)))
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
                    BCC.DEFINITION_SOURCE,
                    BCC.IS_NILLABLE.as("nillable"),
                    BCC_MANIFEST.BCC_MANIFEST_ID.as("manifest_id"))
                    .from(BCC_MANIFEST)
                    .join(BCC)
                    .on(BCC_MANIFEST.BCC_ID.eq(BCC.BCC_ID))
                    .where(BCC_MANIFEST.BCC_MANIFEST_ID.eq(ULong.valueOf(bccManifestId)))
                    .fetchOneInto(CcBccpNodeDetail.Bcc.class);
            bcc.setRevisionNum(getLatestRevisionBcc(bccManifestId));
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
                BCCP.DEFINITION_SOURCE,
                BCCP_MANIFEST.BCCP_MANIFEST_ID.as("manifest_id"))
                .from(BCCP_MANIFEST)
                .join(BCCP)
                .on(BCCP.BCCP_ID.eq(BCCP_MANIFEST.BCCP_ID))
                .where(BCCP_MANIFEST.BCCP_MANIFEST_ID.eq(ULong.valueOf(bccpManifestId)))
                .fetchOneInto(CcBccpNodeDetail.Bccp.class);
        bccpNodeDetail.setBccp(bccp);

        long bdtId = dslContext.select(DT_MANIFEST.DT_ID)
                .from(BCCP_MANIFEST)
                .join(DT_MANIFEST)
                .on(BCCP_MANIFEST.BDT_MANIFEST_ID.eq(DT_MANIFEST.DT_MANIFEST_ID))
                .where(BCCP_MANIFEST.BCCP_MANIFEST_ID.eq(ULong.valueOf(bccpManifestId)))
                .fetchOneInto(Long.class);

        CcBccpNodeDetail.Bdt bdt = dslContext.select(
                DT.DT_ID.as("bdt_id"),
                DT.GUID,
                DT.DATA_TYPE_TERM,
                DT.QUALIFIER,
                DT.DEN,
                DT.DEFINITION,
                DT.DEFINITION_SOURCE).from(DT)
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
                DT_SC.DEFINITION_SOURCE,
                DT_SC.DEFAULT_VALUE,
                DT_SC.FIXED_VALUE).from(DT_SC)
                .where(DT_SC.DT_SC_ID.eq(ULong.valueOf(bdtScId)))
                .fetchOneInto(CcBdtScNodeDetail.class);
    }

    public AccManifestRecord getAccManifestByAcc(long accId, long releaseId) {
        return dslContext.selectFrom(ACC_MANIFEST)
                .where(and(
                        ACC_MANIFEST.ACC_ID.eq(ULong.valueOf(accId)),
                        ACC_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId))
                ))
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
                .from(ASCCP_MANIFEST)
                .join(ACC_MANIFEST)
                .on(ASCCP_MANIFEST.ROLE_OF_ACC_MANIFEST_ID.eq(ACC_MANIFEST.ACC_MANIFEST_ID))
                .where(ACC_MANIFEST.ACC_ID.eq(ULong.valueOf(accId)))
                .fetchOptionalInto(Integer.class).orElse(0);
        if (cnt > 0) {
            return true;
        }

        cnt = dslContext.selectCount()
                .from(ACC_MANIFEST)
                .join(ACC_MANIFEST.as("base"))
                .on(ACC_MANIFEST.BASED_ACC_MANIFEST_ID.eq(ACC_MANIFEST.as("base").ACC_MANIFEST_ID))
                .where(ACC_MANIFEST.as("base").ACC_ID.eq(ULong.valueOf(accId)))
                .fetchOptionalInto(Integer.class).orElse(0);
        if (cnt > 0) {
            return true;
        }

        cnt = dslContext.selectCount()
                .from(ASCC_MANIFEST)
                .join(ACC_MANIFEST)
                .on(ASCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(ACC_MANIFEST.ACC_MANIFEST_ID))
                .where(ACC_MANIFEST.ACC_ID.eq(ULong.valueOf(accId)))
                .fetchOptionalInto(Integer.class).orElse(0);
        if (cnt > 0) {
            return true;
        }

        cnt = dslContext.selectCount()
                .from(BCC_MANIFEST)
                .join(ACC_MANIFEST)
                .on(BCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(ACC_MANIFEST.ACC_MANIFEST_ID))
                .where(ACC_MANIFEST.ACC_ID.eq(ULong.valueOf(accId)))
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
                .from(ASCC_MANIFEST)
                .join(ASCCP_MANIFEST)
                .on(ASCC_MANIFEST.TO_ASCCP_MANIFEST_ID.eq(ASCCP_MANIFEST.ASCCP_MANIFEST_ID))
                .where(ASCCP_MANIFEST.ASCCP_ID.eq(ULong.valueOf(asccpId)))
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
                .from(BCC_MANIFEST)
                .join(BCCP_MANIFEST)
                .on(BCC_MANIFEST.TO_BCCP_MANIFEST_ID.eq(BCCP_MANIFEST.BCCP_MANIFEST_ID))
                .where(BCCP_MANIFEST.BCCP_ID.eq(ULong.valueOf(bccpId)))
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

    public void deleteAccRecords(AccManifestRecord accManifestRecord) {
        String guid = dslContext.select(ACC.GUID)
                .from(ACC)
                .where(ACC.ACC_ID.eq(accManifestRecord.getAccId()))
                .fetchOneInto(String.class);

        List<ULong> accIds = dslContext.select(ACC.ACC_ID)
                .from(ACC)
                .where(ACC.GUID.eq(guid))
                .fetchInto(ULong.class);

        List<ULong> accManifestIds = dslContext.select(ACC_MANIFEST.ACC_MANIFEST_ID)
                .from(ACC_MANIFEST)
                .where(and(
                        ACC_MANIFEST.ACC_ID.in(accIds),
                        ACC_MANIFEST.RELEASE_ID.eq(accManifestRecord.getReleaseId())
                ))
                .fetchInto(ULong.class);

        dslContext.deleteFrom(ASCC_MANIFEST)
                .where(and(
                        ASCC_MANIFEST.FROM_ACC_MANIFEST_ID.in(accManifestIds),
                        ASCC_MANIFEST.RELEASE_ID.eq(accManifestRecord.getReleaseId())
                ))
                .execute();
        dslContext.deleteFrom(ASCC)
                .where(ASCC.FROM_ACC_ID.in(accIds))
                .execute();
        dslContext.deleteFrom(BCC_MANIFEST)
                .where(and(
                        BCC_MANIFEST.FROM_ACC_MANIFEST_ID.in(accManifestIds),
                        BCC_MANIFEST.RELEASE_ID.eq(accManifestRecord.getReleaseId())
                ))
                .execute();
        dslContext.deleteFrom(BCC)
                .where(BCC.FROM_ACC_ID.in(accIds))
                .execute();
        dslContext.deleteFrom(ACC_MANIFEST)
                .where(ACC_MANIFEST.ACC_ID.in(accIds))
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

    public CcNode updateAsccpRoleOfAcc(User user, ULong asccpManifestId, ULong accManifestId) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        long userId = sessionService.userId(user);

        AsccpManifestRecord asccpManifestRecord = manifestRepository.getAsccpManifestById(asccpManifestId);
        AsccpRecord asccpRecord = getAsccpRecordById(asccpManifestRecord.getAsccpManifestId());
        AccManifestRecord accManifestRecord = manifestRepository.getAccManifestById(accManifestId);
        AccRecord accRecord = getAccRecordById(accManifestRecord.getAccManifestId());

        long originAsccpId = asccpManifestRecord.getAsccpId().longValue();
        asccpRecord.setAsccpId(null);
        asccpRecord.setRoleOfAccId(accRecord.getAccId());
        asccpRecord.setDen(asccpRecord.getPropertyTerm() + ". " + accRecord.getObjectClassTerm());
        asccpRecord.setLastUpdatedBy(ULong.valueOf(userId));
        asccpRecord.setLastUpdateTimestamp(timestamp);
        asccpRecord.setRevisionAction((byte) RevisionAction.Update.getValue());
        asccpRecord.setRevisionTrackingNum(asccpRecord.getRevisionTrackingNum() + 1);
        asccpRecord.insert();

        asccpManifestRecord.setRoleOfAccManifestId(accManifestId);
        asccpManifestRecord.setAsccpId(asccpRecord.getAsccpId());
        asccpManifestRecord.update();

        updateAsccByToAsccp(userId, originAsccpId,
                asccpRecord.getAsccpId().longValue(), asccpManifestRecord.getReleaseId(),
                asccpRecord.getPropertyTerm(), timestamp);

        return getAsccpNodeByAsccpManifestId(user, asccpManifestRecord.getAsccpManifestId().longValue());
    }

    public CcNode updateBccpBdt(User user, long bccpManifestId, long bdtManifestId) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        ULong userId = ULong.valueOf(sessionService.userId(user));

        long bdtId = dslContext.select(DT_MANIFEST.DT_ID).from(DT_MANIFEST)
                .where(DT_MANIFEST.DT_MANIFEST_ID.eq(ULong.valueOf(bdtManifestId)))
                .fetchOneInto(long.class);

        DtRecord dtRecord = dslContext.selectFrom(DT)
                .where(DT.DT_ID.eq(ULong.valueOf(bdtId))).fetchOne();

        BccpManifestRecord bccpManifestRecord = dslContext.selectFrom(BCCP_MANIFEST)
                .where(BCCP_MANIFEST.BCCP_MANIFEST_ID.eq(ULong.valueOf(bccpManifestId))).fetchOne();

        long originBccpId = bccpManifestRecord.getBccpId().longValue();

        BccpRecord baseBccpRecord = dslContext.selectFrom(BCCP)
                .where(BCCP.BCCP_ID.eq(bccpManifestRecord.getBccpId())).fetchOne();

        baseBccpRecord.set(BCCP.BCCP_ID, null);
        baseBccpRecord.set(BCCP.BDT_ID, ULong.valueOf(bdtId));
        baseBccpRecord.set(BCCP.DEN, baseBccpRecord.getPropertyTerm() + ". " + dtRecord.getDataTypeTerm());
        baseBccpRecord.set(BCCP.LAST_UPDATED_BY, userId);
        baseBccpRecord.set(BCCP.LAST_UPDATE_TIMESTAMP, timestamp);
        baseBccpRecord.set(BCCP.REVISION_ACTION, RevisionAction.Update.getValue());
        baseBccpRecord.set(BCCP.REVISION_TRACKING_NUM, baseBccpRecord.getRevisionTrackingNum() + 1);
        baseBccpRecord.insert();

        bccpManifestRecord.setBccpId(baseBccpRecord.getBccpId());
        bccpManifestRecord.setBdtManifestId(ULong.valueOf(bdtManifestId));
        bccpManifestRecord.update();

        updateBccByToBccp(userId.longValue(), originBccpId,
                baseBccpRecord.getBccpId().longValue(), bccpManifestRecord.getReleaseId(),
                baseBccpRecord.getPropertyTerm(), timestamp);

        return getBccpNodeByBccpManifestId(user, bccpManifestRecord.getBccpManifestId().longValue());
    }

    public CcAccNode updateAccState(User user, ULong accManifestId, CcState ccState) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        ULong userId = ULong.valueOf(sessionService.userId(user));

        AccManifestRecord accManifestRecord = manifestRepository.getAccManifestById(accManifestId);

        ensureDependenciesOfAcc(accManifestRecord, ccState);

        AccRecord accRecord = getAccRecordById(accManifestRecord.getAccManifestId());

        long originAccId = accRecord.getAccId().longValue();
        accRecord.set(ACC.ACC_ID, null);
        if (accRecord.getOagisComponentType() == OagisComponentType.UserExtensionGroup.getValue()){
            accRecord.set(ACC.OWNER_USER_ID, userId);
        }
        accRecord.set(ACC.LAST_UPDATED_BY, userId);
        accRecord.set(ACC.LAST_UPDATE_TIMESTAMP, timestamp);
        accRecord.set(ACC.REVISION_ACTION, (byte) RevisionAction.Update.getValue());
        accRecord.set(ACC.REVISION_TRACKING_NUM, accRecord.getRevisionTrackingNum() + 1);
        accRecord.set(ACC.STATE, ccState.getValue());
        accRecord.insert();

        accManifestRecord.setAccId(accRecord.getAccId());
        accManifestRecord.update();

        updateAsccState(userId.longValue(), originAccId,
                accRecord.getAccId(), accManifestRecord.getReleaseId(),
                ccState, timestamp);
        updateBccState(userId.longValue(), originAccId,
                accRecord.getAccId(), accManifestRecord.getReleaseId(),
                ccState, timestamp);

        updateAsccpByRoleOfAcc(userId.longValue(), originAccId,
                accRecord.getAccId(), accManifestRecord.getReleaseId(),
                accRecord.getObjectClassTerm(), timestamp);
        updateAccByBasedAcc(userId.longValue(), originAccId,
                accRecord.getAccId(), accManifestRecord.getReleaseId(),
                timestamp);

        return getAccNodeByAccManifest(user, accManifestRecord.getAccId().longValue(),
                accManifestRecord.getReleaseId().longValue());
    }

    private void ensureDependenciesOfAcc(AccManifestRecord accManifestRecord, CcState ccState) {
        int minChildState = CcState.Published.getValue();
        int childState;

        List<AsccManifestRecord> asccManifestRecords =
                manifestRepository.getAsccManifestByFromAccId(
                        accManifestRecord.getAccId(),
                        accManifestRecord.getReleaseId());

        for (AsccManifestRecord asccManifestRecord : asccManifestRecords) {
            childState = getAsccpRecordById(asccManifestRecord.getToAsccpManifestId()).getState();
            if (childState < minChildState) {
                minChildState = childState;
            }
        }

        List<BccManifestRecord> bccManifestRecords =
                manifestRepository.getBccManifestByFromAccId(
                        accManifestRecord.getAccId(),
                        accManifestRecord.getReleaseId());

        for (BccManifestRecord bccManifestRecord : bccManifestRecords) {
            childState = getBccpRecordById(bccManifestRecord.getToBccpManifestId()).getState();
            if (childState < minChildState) {
                minChildState = childState;
            }
        }

        if (minChildState < ccState.getValue()) {
            throw new IllegalArgumentException("ACC must precede the state of child CCs.");
        }

        if (accManifestRecord.getBasedAccManifestId() != null) {
            AccRecord basedAccRecord = getAccRecordById(accManifestRecord.getBasedAccManifestId());
            if (basedAccRecord.getState() < ccState.getValue()) {
                throw new IllegalArgumentException("ACC cannot precede the state of basedAcc.");
            }
        }

        int minBasedState = CcState.Editing.getValue();
        int basedAccState;

        List<AccManifestRecord> accManifestRecordList =
                manifestRepository.getAccManifestByBasedAccId(
                        accManifestRecord.getAccId().longValue(),
                        accManifestRecord.getReleaseId());

        for (AccManifestRecord basedAcc : accManifestRecordList) {
            basedAccState = getAccRecordById(basedAcc.getAccManifestId()).getState();
            if (minBasedState < basedAccState) {
                minBasedState = basedAccState;
            }
        }

        if (minBasedState > ccState.getValue()) {
            throw new IllegalArgumentException("ACC cannot be behind state of Referencing ACC.");
        }

        List<AsccpManifestRecord> asccpManifestList =
                manifestRepository.getAsccpManifestByRoleOfAccId(
                        accManifestRecord.getAccId().longValue(),
                        accManifestRecord.getReleaseId());

        int minRoleOfState = CcState.Editing.getValue();
        int asccpState;

        for (AsccpManifestRecord asccpManifest : asccpManifestList) {
            asccpState = getAsccpRecordById(asccpManifest.getAsccpManifestId()).getState();
            if (asccpState < minRoleOfState) {
                minRoleOfState = asccpState;
            }
        }

        if (minRoleOfState > ccState.getValue()) {
            throw new IllegalArgumentException("ACC cannot be behind state of Referencing ASCCP.");
        }
    }

    public CcAsccpNode updateAsccpState(User user, long asccpManifestId, CcState ccState) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        ULong userId = ULong.valueOf(sessionService.userId(user));

        AsccpManifestRecord asccpManifestRecord =
                manifestRepository.getAsccpManifestById(ULong.valueOf(asccpManifestId));
        AccRecord roleOfAccRecord = getAccRecordById(asccpManifestRecord.getRoleOfAccManifestId());

        ensureDependenciesOfAsccp(asccpManifestRecord, roleOfAccRecord, ccState);

        AsccpRecord asccpRecord = dslContext.selectFrom(ASCCP)
                .where(ASCCP.ASCCP_ID.eq(asccpManifestRecord.getAsccpId())).fetchOne();

        asccpRecord.set(ASCCP.ASCCP_ID, null);
        asccpRecord.set(ASCCP.LAST_UPDATED_BY, userId);
        asccpRecord.set(ASCCP.LAST_UPDATE_TIMESTAMP, timestamp);
        asccpRecord.set(ASCCP.REVISION_ACTION, (byte) RevisionAction.Update.getValue());
        asccpRecord.set(ASCCP.REVISION_TRACKING_NUM, asccpRecord.getRevisionTrackingNum() + 1);
        asccpRecord.set(ASCCP.STATE, ccState.getValue());
        asccpRecord.insert();

        long originAsccpId = asccpManifestRecord.getAsccpId().longValue();
        asccpManifestRecord.setAsccpId(asccpRecord.getAsccpId());
        asccpManifestRecord.update();

        updateAsccByToAsccp(userId.longValue(), originAsccpId,
                asccpRecord.getAsccpId().longValue(), asccpManifestRecord.getReleaseId(),
                asccpRecord.getPropertyTerm(), timestamp);

        return getAsccpNodeByAsccpManifestId(user, asccpManifestRecord.getAsccpManifestId().longValue());
    }

    private void ensureDependenciesOfAsccp(AsccpManifestRecord asccpManifestRecord, AccRecord roleOfAccRecord, CcState ccState) {
        if (roleOfAccRecord.getState() < ccState.getValue()) {
            throw new IllegalArgumentException("ASCCP state can not precede ACC.");
        }

        List<Integer> parentAccStates = dslContext.select(ACC.STATE)
                .from(ACC)
                .join(ACC_MANIFEST).on(and(
                        ACC_MANIFEST.ACC_ID.eq(ACC.ACC_ID),
                        ACC_MANIFEST.RELEASE_ID.eq(asccpManifestRecord.getReleaseId())
                ))
                .join(ASCC_MANIFEST)
                .on(ACC_MANIFEST.ACC_MANIFEST_ID.eq(ASCC_MANIFEST.FROM_ACC_MANIFEST_ID))
                .where(ASCC_MANIFEST.TO_ASCCP_MANIFEST_ID.eq(asccpManifestRecord.getAsccpManifestId()))
                .fetchInto(Integer.class);

        for (Integer state : parentAccStates) {
            if (state > ccState.getValue()) {
                throw new IllegalArgumentException("ASCCP state cannot be behind of parent ACC.");
            }
        }
    }

    public CcBccpNode updateBccpState(User user, long bccpManifestId, CcState ccState) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        ULong userId = ULong.valueOf(sessionService.userId(user));

        BccpManifestRecord bccpManifestRecord =
                manifestRepository.getBccpManifestById(ULong.valueOf(bccpManifestId));

        ensureDependenciesOfBccp(bccpManifestRecord, ccState);

        BccpRecord bccpRecord = dslContext.selectFrom(BCCP)
                .where(BCCP.BCCP_ID.eq(bccpManifestRecord.getBccpId())).fetchOne();

        bccpRecord.set(BCCP.BCCP_ID, null);
        bccpRecord.set(BCCP.LAST_UPDATED_BY, userId);
        bccpRecord.set(BCCP.LAST_UPDATE_TIMESTAMP, timestamp);
        bccpRecord.set(BCCP.REVISION_ACTION, RevisionAction.Update.getValue());
        bccpRecord.set(BCCP.REVISION_TRACKING_NUM, bccpRecord.getRevisionTrackingNum() + 1);
        bccpRecord.set(BCCP.STATE, ccState.getValue());
        bccpRecord.insert();

        long originBccpId = bccpManifestRecord.getBccpId().longValue();
        bccpManifestRecord.setBccpId(bccpRecord.getBccpId());
        bccpManifestRecord.update();

        updateBccByToBccp(userId.longValue(), originBccpId,
                bccpRecord.getBccpId().longValue(), bccpManifestRecord.getReleaseId(),
                bccpRecord.getPropertyTerm(), timestamp);

        return getBccpNodeByBccpManifestId(user,
                bccpManifestRecord.getBccpManifestId().longValue());
    }

    private void ensureDependenciesOfBccp(BccpManifestRecord bccpManifestRecord, CcState ccState) {
        List<Integer> parentAccStates = dslContext.select(ACC.STATE)
                .from(ACC)
                .join(ACC_MANIFEST).on(and(
                        ACC_MANIFEST.ACC_ID.eq(ACC.ACC_ID),
                        ACC_MANIFEST.RELEASE_ID.eq(bccpManifestRecord.getReleaseId())
                ))
                .join(BCC_MANIFEST)
                .on(ACC_MANIFEST.ACC_MANIFEST_ID.eq(BCC_MANIFEST.FROM_ACC_MANIFEST_ID))
                .where(BCC_MANIFEST.TO_BCCP_MANIFEST_ID.eq(bccpManifestRecord.getBccpManifestId()))
                .fetchInto(Integer.class);

        for (Integer state : parentAccStates) {
            if (state > ccState.getValue()) {
                throw new IllegalArgumentException("BCCP state cannot be behind of parent ACC.");
            }
        }
    }

    public CcAccNode makeNewRevisionForAcc(User user, ULong accManifestId) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        ULong userId = ULong.valueOf(sessionService.userId(user));

        AccManifestRecord accManifestRecord = manifestRepository.getAccManifestById(accManifestId);
        AccRecord accRecord = getAccRecordById(accManifestRecord.getAccManifestId());
        if (accRecord.getState() != CcState.Published.getValue()) {
            throw new IllegalArgumentException("Creating new revision only allowed for the component in 'Published' state.");
        }

        accRecord.set(ACC.ACC_ID, null);
        accRecord.set(ACC.LAST_UPDATED_BY, userId);
        accRecord.set(ACC.LAST_UPDATE_TIMESTAMP, timestamp);
        accRecord.set(ACC.REVISION_ACTION, (byte) RevisionAction.Insert.getValue());
        accRecord.set(ACC.REVISION_NUM, accRecord.getRevisionNum() + 1);
        accRecord.set(ACC.REVISION_TRACKING_NUM, 1);
        accRecord.set(ACC.STATE, CcState.Editing.getValue());
        accRecord.insert();

        Release workingRelease = releaseRepository.getWorkingRelease();
        ULong workingReleaseId = ULong.valueOf(workingRelease.getReleaseId());
        AccManifestRecord accManifestRecordInWorkingRelease =
                dslContext.selectFrom(ACC_MANIFEST)
                        .where(and(
                                ACC_MANIFEST.ACC_ID.eq(accManifestRecord.getAccId()),
                                ACC_MANIFEST.RELEASE_ID.eq(workingReleaseId)
                        ))
                        .fetchOptional().orElse(null);

        if (accManifestRecordInWorkingRelease != null) {
            accManifestRecordInWorkingRelease.setAccId(accRecord.getAccId());
            accManifestRecordInWorkingRelease.setBasedAccManifestId(accManifestId);
            accManifestRecordInWorkingRelease.update();
        } else {
            accManifestRecordInWorkingRelease = dslContext.insertInto(ACC_MANIFEST)
                    .set(ACC_MANIFEST.ACC_ID, accRecord.getAccId())
                    .set(ACC_MANIFEST.RELEASE_ID, workingReleaseId)
                    .set(ACC_MANIFEST.BASED_ACC_MANIFEST_ID, accManifestId)
                    .set(ACC_MANIFEST.MODULE_ID, accManifestRecord.getModuleId())
                    .returning().fetchOne();
        }

        makeNewRevisionForAscc(accManifestRecord, accRecord, userId, timestamp, workingReleaseId);
        makeNewRevisionForBcc(accManifestRecord, accRecord, userId, timestamp, workingReleaseId);

        return getAccNodeByAccManifest(user, accManifestRecordInWorkingRelease);
    }

    private List<AsccManifestRecord> makeNewRevisionForAscc(AccManifestRecord accManifestRecord,
                                                            AccRecord accRecord,
                                                            ULong userId, Timestamp timestamp, ULong workingReleaseId) {
        List<AsccManifestRecord> asccManifestRecords =
                dslContext.selectFrom(ASCC_MANIFEST)
                        .where(and(
                                ASCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(accManifestRecord.getAccManifestId()),
                                ASCC_MANIFEST.RELEASE_ID.eq(accManifestRecord.getReleaseId())
                        )).fetch();

        List<AsccManifestRecord> asccManifestRecordsInWorkingRelease = new ArrayList(asccManifestRecords.size());
        for (AsccManifestRecord asccManifestRecord : asccManifestRecords) {
            AsccRecord asccRecord =
                    dslContext.selectFrom(ASCC)
                            .where(ASCC.ASCC_ID.eq(asccManifestRecord.getAsccId())).fetchOne();

            asccRecord.set(ASCC.ASCC_ID, null);
            asccRecord.set(ASCC.FROM_ACC_ID, accRecord.getAccId());
            asccRecord.set(ASCC.LAST_UPDATED_BY, userId);
            asccRecord.set(ASCC.LAST_UPDATE_TIMESTAMP, timestamp);
            asccRecord.set(ASCC.REVISION_ACTION, (byte) RevisionAction.Insert.getValue());
            asccRecord.set(ASCC.REVISION_NUM, asccRecord.getRevisionNum() + 1);
            asccRecord.set(ASCC.REVISION_TRACKING_NUM, 1);
            asccRecord.set(ASCC.STATE, CcState.Editing.getValue());
            asccRecord.insert();

            AsccManifestRecord asccManifestRecordInWorkingRelease =
                    dslContext.selectFrom(ASCC_MANIFEST)
                            .where(and(
                                    ASCC_MANIFEST.ASCC_ID.eq(asccManifestRecord.getAsccId()),
                                    ASCC_MANIFEST.RELEASE_ID.eq(workingReleaseId)
                            ))
                            .fetchOptional().orElse(null);

            if (asccManifestRecordInWorkingRelease != null) {
                asccManifestRecordInWorkingRelease.setAsccId(asccRecord.getAsccId());
                asccManifestRecordInWorkingRelease.setFromAccManifestId(asccManifestRecord.getFromAccManifestId());
                asccManifestRecordInWorkingRelease.setToAsccpManifestId(asccManifestRecord.getToAsccpManifestId());
                asccManifestRecordInWorkingRelease.update();
            } else {
                asccManifestRecordInWorkingRelease = dslContext.insertInto(ASCC_MANIFEST)
                        .set(ASCC_MANIFEST.ASCC_ID, asccRecord.getAsccId())
                        .set(ASCC_MANIFEST.RELEASE_ID, workingReleaseId)
                        .set(ASCC_MANIFEST.FROM_ACC_MANIFEST_ID, asccManifestRecord.getFromAccManifestId())
                        .set(ASCC_MANIFEST.TO_ASCCP_MANIFEST_ID, asccManifestRecord.getToAsccpManifestId())
                        .returning().fetchOne();
            }

            asccManifestRecordsInWorkingRelease.add(asccManifestRecordInWorkingRelease);
        }

        return asccManifestRecordsInWorkingRelease;
    }

    private List<BccManifestRecord> makeNewRevisionForBcc(AccManifestRecord accManifestRecord,
                                                                 AccRecord accRecord,
                                                                 ULong userId, Timestamp timestamp, ULong workingReleaseId) {
        List<BccManifestRecord> bccManifestRecords =
                dslContext.selectFrom(BCC_MANIFEST)
                        .where(and(
                                BCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(accManifestRecord.getAccManifestId()),
                                BCC_MANIFEST.RELEASE_ID.eq(accManifestRecord.getReleaseId())
                        )).fetch();

        List<BccManifestRecord> bccManifestRecordsInWorkingRelease = new ArrayList(bccManifestRecords.size());
        for (BccManifestRecord bccManifestRecord : bccManifestRecords) {
            BccRecord bccRecord =
                    dslContext.selectFrom(BCC)
                            .where(BCC.BCC_ID.eq(bccManifestRecord.getBccId())).fetchOne();

            bccRecord.set(BCC.BCC_ID, null);
            bccRecord.set(BCC.FROM_ACC_ID, accRecord.getAccId());
            bccRecord.set(BCC.LAST_UPDATED_BY, userId);
            bccRecord.set(BCC.LAST_UPDATE_TIMESTAMP, timestamp);
            bccRecord.set(BCC.REVISION_ACTION, (byte) RevisionAction.Insert.getValue());
            bccRecord.set(BCC.REVISION_NUM, bccRecord.getRevisionNum() + 1);
            bccRecord.set(BCC.REVISION_TRACKING_NUM, 1);
            bccRecord.set(BCC.STATE, CcState.Editing.getValue());
            bccRecord.insert();

            BccManifestRecord bccManifestRecordInWorkingRelease =
                    dslContext.selectFrom(BCC_MANIFEST)
                            .where(and(
                                    BCC_MANIFEST.BCC_ID.eq(bccManifestRecord.getBccId()),
                                    BCC_MANIFEST.RELEASE_ID.eq(workingReleaseId)
                            ))
                            .fetchOptional().orElse(null);

            if (bccManifestRecordInWorkingRelease != null) {
                bccManifestRecordInWorkingRelease.setBccId(bccRecord.getBccId());
                bccManifestRecordInWorkingRelease.setFromAccManifestId(bccManifestRecord.getFromAccManifestId());
                bccManifestRecordInWorkingRelease.setToBccpManifestId(bccManifestRecord.getToBccpManifestId());
                bccManifestRecordInWorkingRelease.update();
            } else {
                bccManifestRecordInWorkingRelease = dslContext.insertInto(BCC_MANIFEST)
                        .set(BCC_MANIFEST.BCC_ID, bccRecord.getBccId())
                        .set(BCC_MANIFEST.RELEASE_ID, workingReleaseId)
                        .set(BCC_MANIFEST.FROM_ACC_MANIFEST_ID, bccManifestRecord.getFromAccManifestId())
                        .set(BCC_MANIFEST.TO_BCCP_MANIFEST_ID, bccManifestRecord.getToBccpManifestId())
                        .returning().fetchOne();
            }

            bccManifestRecordsInWorkingRelease.add(bccManifestRecordInWorkingRelease);
        }

        return bccManifestRecordsInWorkingRelease;
    }

    public CcAsccpNode makeNewRevisionForAsccp(User user, ULong asccpManifestId) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        ULong userId = ULong.valueOf(sessionService.userId(user));

        AsccpManifestRecord asccpManifestRecord = manifestRepository.getAsccpManifestById(asccpManifestId);
        AsccpRecord asccpRecord = dslContext.selectFrom(ASCCP)
                .where(ASCCP.ASCCP_ID.eq(asccpManifestRecord.getAsccpId())).fetchOne();
        if (asccpRecord.getState() != CcState.Published.getValue()) {
            throw new IllegalArgumentException("Creating new revision only allowed for the component in 'Published' state.");
        }

        asccpRecord.set(ASCCP.ASCCP_ID, null);
        asccpRecord.set(ASCCP.LAST_UPDATED_BY, userId);
        asccpRecord.set(ASCCP.LAST_UPDATE_TIMESTAMP, timestamp);
        asccpRecord.set(ASCCP.REVISION_ACTION, (byte) RevisionAction.Insert.getValue());
        asccpRecord.set(ASCCP.REVISION_NUM, asccpRecord.getRevisionNum() + 1);
        asccpRecord.set(ASCCP.REVISION_TRACKING_NUM, 1);
        asccpRecord.set(ASCCP.STATE, CcState.Editing.getValue());
        asccpRecord.insert();

        Release workingRelease = releaseRepository.getWorkingRelease();
        ULong workingReleaseId = ULong.valueOf(workingRelease.getReleaseId());
        AsccpManifestRecord asccpManifestRecordInWorkingRelease =
                dslContext.selectFrom(ASCCP_MANIFEST)
                        .where(and(
                                ASCCP_MANIFEST.ASCCP_ID.eq(asccpManifestRecord.getAsccpId()),
                                ASCCP_MANIFEST.RELEASE_ID.eq(workingReleaseId)
                        ))
                        .fetchOptional().orElse(null);

        if (asccpManifestRecordInWorkingRelease != null) {
            asccpManifestRecordInWorkingRelease.setAsccpId(asccpRecord.getAsccpId());
            asccpManifestRecordInWorkingRelease.setRoleOfAccManifestId(asccpManifestRecord.getRoleOfAccManifestId());
            asccpManifestRecordInWorkingRelease.update();
        } else {
            asccpManifestRecordInWorkingRelease = dslContext.insertInto(ASCCP_MANIFEST)
                    .set(ASCCP_MANIFEST.ASCCP_ID, asccpRecord.getAsccpId())
                    .set(ASCCP_MANIFEST.RELEASE_ID, workingReleaseId)
                    .set(ASCCP_MANIFEST.ROLE_OF_ACC_MANIFEST_ID, asccpManifestRecord.getRoleOfAccManifestId())
                    .set(ASCCP_MANIFEST.MODULE_ID, asccpManifestRecord.getModuleId())
                    .returning().fetchOne();
        }

        return getAsccpNodeByAsccpManifestId(user, asccpManifestRecordInWorkingRelease.getAsccpManifestId().longValue());
    }

    public CcBccpNode makeNewRevisionForBccp(User user, ULong bccpManifestId) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        ULong userId = ULong.valueOf(sessionService.userId(user));

        BccpManifestRecord bccpManifestRecord =
                manifestRepository.getBccpManifestById(bccpManifestId);
        BccpRecord bccpRecord = dslContext.selectFrom(BCCP)
                .where(BCCP.BCCP_ID.eq(bccpManifestRecord.getBccpId())).fetchOne();
        if (bccpRecord.getState() != CcState.Published.getValue()) {
            throw new IllegalArgumentException("Creating new revision only allowed for the component in 'Published' state.");
        }

        bccpRecord.set(BCCP.BCCP_ID, null);
        bccpRecord.set(BCCP.LAST_UPDATED_BY, userId);
        bccpRecord.set(BCCP.LAST_UPDATE_TIMESTAMP, timestamp);
        bccpRecord.set(BCCP.REVISION_ACTION, RevisionAction.Insert.getValue());
        bccpRecord.set(BCCP.REVISION_NUM, bccpRecord.getRevisionNum() + 1);
        bccpRecord.set(BCCP.REVISION_TRACKING_NUM, 1);
        bccpRecord.set(BCCP.STATE, CcState.Editing.getValue());
        bccpRecord.insert();

        Release workingRelease = releaseRepository.getWorkingRelease();
        ULong workingReleaseId = ULong.valueOf(workingRelease.getReleaseId());
        BccpManifestRecord bccpManifestRecordInWorkingRelease =
                dslContext.selectFrom(BCCP_MANIFEST)
                        .where(and(
                                BCCP_MANIFEST.BCCP_ID.eq(bccpManifestRecord.getBccpId()),
                                BCCP_MANIFEST.RELEASE_ID.eq(workingReleaseId)
                        ))
                        .fetchOptional().orElse(null);

        if (bccpManifestRecordInWorkingRelease != null) {
            bccpManifestRecordInWorkingRelease.setBccpId(bccpRecord.getBccpId());
            bccpManifestRecordInWorkingRelease.setBdtManifestId(bccpManifestRecord.getBdtManifestId());
            bccpManifestRecordInWorkingRelease.update();
        } else {
            bccpManifestRecordInWorkingRelease = dslContext.insertInto(BCCP_MANIFEST)
                    .set(BCCP_MANIFEST.BCCP_ID, bccpRecord.getBccpId())
                    .set(BCCP_MANIFEST.RELEASE_ID, workingReleaseId)
                    .set(BCCP_MANIFEST.BDT_MANIFEST_ID, bccpManifestRecord.getBdtManifestId())
                    .set(BCCP_MANIFEST.MODULE_ID, bccpManifestRecord.getModuleId())
                    .returning().fetchOne();
        }

        return getBccpNodeByBccpManifestId(user, bccpManifestRecordInWorkingRelease.getBccpManifestId().longValue());
    }

    public void appendAsccp(User user, ULong accManifestId, ULong asccpManifestId) {
        long userId = sessionService.userId(user);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        AccManifestRecord accManifest = manifestRepository.getAccManifestById(accManifestId);
        AsccpManifestRecord asccpManifestRecord =
                manifestRepository.getAsccpManifestById(asccpManifestId);

        duplicateAssociationValidate(user, accManifestId, asccpManifestId, null);

        AsccpRecord asccpRecord = getAsccpRecordById(asccpManifestRecord.getAsccpManifestId());
        AccRecord accRecord = getAccRecordById(accManifest.getAccManifestId());

        int seqKey = getNextSeqKey(accManifest.getAccId().longValue(), accManifest.getReleaseId().longValue());

        AsccRecord asccRecord = new AsccRecord();
        asccRecord.setAsccId(null);
        asccRecord.setGuid(SrtGuid.randomGuid());
        asccRecord.setCardinalityMin(0);
        asccRecord.setCardinalityMax(-1);
        asccRecord.setSeqKey(seqKey);
        asccRecord.setFromAccId(accRecord.getAccId());
        asccRecord.setToAsccpId(asccpManifestRecord.getAsccpId());
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

        AsccManifestRecord asccManifestRecord = new AsccManifestRecord();
        asccManifestRecord.setAsccManifestId(null);
        asccManifestRecord.setReleaseId(accManifest.getReleaseId());
        asccManifestRecord.setAsccId(insertedAscc.getAsccId());
        asccManifestRecord.setFromAccManifestId(accManifestId);
        asccManifestRecord.setToAsccpManifestId(asccpManifestRecord.getAsccpManifestId());

        dslContext.insertInto(ASCC_MANIFEST).set(asccManifestRecord).execute();
    }

    public void appendBccp(User user, ULong accManifestId, ULong bccpManifestId) {
        long userId = sessionService.userId(user);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        AccManifestRecord accManifest = manifestRepository.getAccManifestById(accManifestId);

        BccpManifestRecord bccpManifestRecord =
                manifestRepository.getBccpManifestById(bccpManifestId);

        duplicateAssociationValidate(user, accManifestId, null, bccpManifestId);

        BccpRecord bccpRecord = getBccpRecordById(bccpManifestRecord.getBccpManifestId());
        AccRecord accRecord = getAccRecordById(accManifest.getAccManifestId());

        int seqKey = getNextSeqKey(accManifest.getAccId().longValue(), accManifest.getReleaseId().longValue());
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

        BccManifestRecord bccManifestRecord = new BccManifestRecord();
        bccManifestRecord.setBccManifestId(null);
        bccManifestRecord.setReleaseId(accManifest.getReleaseId());
        bccManifestRecord.setBccId(insertedBcc.getBccId());
        bccManifestRecord.setFromAccManifestId(accManifestId);
        bccManifestRecord.setToBccpManifestId(bccpManifestRecord.getBccpManifestId());

        dslContext.insertInto(BCC_MANIFEST).set(bccManifestRecord).execute();
    }

    public void duplicateAssociationValidate(User user, ULong accManifestId, ULong asccpManifestId, ULong bccpManifestId) {
        AccManifestRecord accManifest = manifestRepository.getAccManifestById(accManifestId);

        if (asccpManifestId != null) {
            boolean exist = dslContext.selectCount()
                    .from(ASCC_MANIFEST)
                    .where(and(
                            ASCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(accManifestId),
                            ASCC_MANIFEST.TO_ASCCP_MANIFEST_ID.eq(accManifestId),
                            ASCC_MANIFEST.RELEASE_ID.eq(accManifest.getReleaseId())
                    )).fetchOneInto(Integer.class) > 0;
            if (exist) {
                throw new IllegalArgumentException("You cannot associate the same component.");
            }
        }
        if (bccpManifestId != null) {
            boolean exist = dslContext.selectCount()
                    .from(BCC_MANIFEST)
                    .where(and(
                            BCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(accManifestId),
                            BCC_MANIFEST.TO_BCCP_MANIFEST_ID.eq(bccpManifestId),
                            BCC_MANIFEST.RELEASE_ID.eq(accManifest.getReleaseId())
                    )).fetchOneInto(Integer.class) > 0;
            if (exist) {
                throw new IllegalArgumentException("You cannot associate the same component.");
            }
        }

        CcAccNode accNode = getAccNodeByAccManifest(user, accManifest);
        OagisComponentType oagisComponentType = getOagisComponentTypeByAccId(accNode.getAccId());
        if (oagisComponentType.isGroup()) {
            CcAsccpNode roleByAsccpNode = getAsccpNodeByRoleOfAccId(accNode.getAccId(), accManifest.getReleaseId());
            ULong baseAccManifestId = dslContext.select(ACC_MANIFEST.ACC_MANIFEST_ID)
                    .from(ASCC_MANIFEST)
                    .join(ACC_MANIFEST)
                    .on(and(
                            ASCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(ACC_MANIFEST.ACC_MANIFEST_ID),
                            ASCC_MANIFEST.RELEASE_ID.eq(ACC_MANIFEST.RELEASE_ID)
                    ))
                    .join(ASCCP_MANIFEST)
                    .on(and(
                            ASCC_MANIFEST.TO_ASCCP_MANIFEST_ID.eq(ASCCP_MANIFEST.ASCCP_MANIFEST_ID),
                            ASCC_MANIFEST.RELEASE_ID.eq(ASCCP_MANIFEST.RELEASE_ID)
                    ))
                    .where(and(
                            ASCCP_MANIFEST.ASCCP_ID.eq(ULong.valueOf(roleByAsccpNode.getAsccpId())),
                            ASCC_MANIFEST.RELEASE_ID.eq(accManifest.getReleaseId())
                    ))
                    .fetchOneInto(ULong.class);
            if (baseAccManifestId != null) {
                ULong edgeAccManifestId = getEdgeAccManifestId(baseAccManifestId);

                if (edgeAccManifestId != null) {
                    duplicateAssociationValidate(user, edgeAccManifestId, asccpManifestId, bccpManifestId);
                }
            }
        }
    }

    public ULong getEdgeAccManifestId(ULong accManifestId) {
        Record4 record = dslContext.select(
                ACC_MANIFEST.as("base_acc").ACC_MANIFEST_ID,
                ACC.OAGIS_COMPONENT_TYPE,
                ACC.ACC_ID,
                ACC_MANIFEST.as("base_acc").BASED_ACC_MANIFEST_ID)
                .from(ACC_MANIFEST)
                .join(ACC_MANIFEST.as("base_acc"))
                .on(and(
                        ACC_MANIFEST.as("base_acc").ACC_MANIFEST_ID.eq(ACC_MANIFEST.BASED_ACC_MANIFEST_ID),
                        ACC_MANIFEST.as("base_acc").RELEASE_ID.eq(ACC_MANIFEST.RELEASE_ID)
                ))
                .join(ACC).on(ACC_MANIFEST.as("base_acc").ACC_ID.eq(ACC.ACC_ID))
                .where(ACC_MANIFEST.ACC_MANIFEST_ID.eq(accManifestId))
                .fetchOne();

        if (record == null) {
            return null;
        }

        if (record.get(ACC.OAGIS_COMPONENT_TYPE) == OagisComponentType.Extension.getValue()
                && record.get(ACC_MANIFEST.BASED_ACC_MANIFEST_ID) != null) {
            return getEdgeAccManifestId(record.get(ACC_MANIFEST.ACC_MANIFEST_ID));
        }

        return record.get(ACC_MANIFEST.ACC_MANIFEST_ID);
    }

    public CcAccNode discardAccBasedId(User user, ULong accManifestId) {
        long userId = sessionService.userId(user);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        AccManifestRecord accManifest = manifestRepository.getAccManifestById(accManifestId);
        AccRecord accRecord = getAccRecordById(accManifest.getAccManifestId());

        long originAccId = accRecord.getAccId().longValue();

        accRecord.setAccId(null);
        accRecord.setBasedAccId(null);
        accRecord.setLastUpdatedBy(ULong.valueOf(userId));
        accRecord.setLastUpdateTimestamp(timestamp);
        accRecord.setRevisionTrackingNum(accRecord.getRevisionTrackingNum() + 1);
        accRecord.setRevisionAction((byte) RevisionAction.Update.getValue());

        AccRecord insertedAccRecord = dslContext.insertInto(ACC).set(accRecord).returning().fetchOne();
        accManifest.setBasedAccManifestId(null);
        accManifest.setAccId(insertedAccRecord.getAccId());
        accManifest.update();

        ULong newAccId = insertedAccRecord.getAccId();

        updateAccChain(userId, originAccId, newAccId, accManifest.getReleaseId(),
                accRecord.getObjectClassTerm(), timestamp);

        return getAccNodeByAccManifest(user, accManifest);
    }

    public CcAccNode updateAccBasedId(User user, ULong accManifestId, ULong basedAccManifestId) {
        long userId = sessionService.userId(user);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        AccManifestRecord basedAccManifestRecord = manifestRepository.getAccManifestById(basedAccManifestId);
        AccManifestRecord accManifestRecord = manifestRepository.getAccManifestById(accManifestId);
        AccRecord accRecord = getAccRecordById(accManifestRecord.getAccManifestId());

        long originAccId = accManifestRecord.getAccId().longValue();

        accRecord.setAccId(null);
        accRecord.setBasedAccId(basedAccManifestRecord.getAccId());
        accRecord.setLastUpdatedBy(ULong.valueOf(userId));
        accRecord.setLastUpdateTimestamp(timestamp);
        accRecord.setRevisionTrackingNum(accRecord.getRevisionTrackingNum() + 1);
        accRecord.setRevisionAction((byte) RevisionAction.Update.getValue());
        accRecord.insert();

        accManifestRecord.setBasedAccManifestId(basedAccManifestRecord.getAccManifestId());
        accManifestRecord.setAccId(accRecord.getAccId());
        accManifestRecord.update();

        updateAccChain(userId, originAccId,
                accRecord.getAccId(), accManifestRecord.getReleaseId(),
                accRecord.getObjectClassTerm(), timestamp);

        return getAccNodeByAccManifest(user, accManifestRecord);
    }

    private AccRecord getAccRecordById(ULong accManifestId) {
        return dslContext.select(ACC.fields())
                .from(ACC)
                .join(ACC_MANIFEST)
                .on(ACC.ACC_ID.eq(ACC_MANIFEST.ACC_ID))
                .where(ACC_MANIFEST.ACC_MANIFEST_ID.eq(accManifestId))
                .fetchOneInto(AccRecord.class);
    }

    public AsccRecord getAsccRecordById(long asccId) {
        return dslContext.selectFrom(ASCC).where(ASCC.ASCC_ID.eq(ULong.valueOf(asccId))).fetchOne();
    }

    public BccRecord getBccRecordById(long bccId) {
        return dslContext.selectFrom(BCC).where(BCC.BCC_ID.eq(ULong.valueOf(bccId))).fetchOne();
    }

    private AsccpRecord getAsccpRecordById(ULong asccpManifestId) {
        return dslContext.select(ASCCP.fields())
                .from(ASCCP)
                .join(ASCCP_MANIFEST)
                .on(ASCCP.ASCCP_ID.eq(ASCCP_MANIFEST.ASCCP_ID))
                .where(ASCCP_MANIFEST.ASCCP_MANIFEST_ID.eq(asccpManifestId))
                .fetchOneInto(AsccpRecord.class);
    }

    private BccpRecord getBccpRecordById(ULong bccpManifestId) {
        return dslContext.select(BCCP.fields())
                .from(BCCP)
                .join(BCCP_MANIFEST)
                .on(BCCP.BCCP_ID.eq(BCCP_MANIFEST.BCCP_ID))
                .where(BCCP_MANIFEST.BCCP_MANIFEST_ID.eq(bccpManifestId))
                .fetchOneInto(BccpRecord.class);
    }

    private void updateAsccByFromAcc(long userId, long originAccId,
                                     ULong newAccId, ULong releaseId,
                                     String accObjectClassTerm, Timestamp timestamp) {
        List<AsccManifestRecord> asccManifestRecords = dslContext.select(ASCC_MANIFEST.fields())
                .from(ASCC_MANIFEST)
                .join(ACC_MANIFEST).on(ASCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(ACC_MANIFEST.ACC_MANIFEST_ID))
                .where(and(
                        ASCC_MANIFEST.RELEASE_ID.eq(releaseId),
                        ACC_MANIFEST.ACC_ID.eq(ULong.valueOf(originAccId))
                ))
                .fetchInto(AsccManifestRecord.class);

        for (AsccManifestRecord asccManifestRecord : asccManifestRecords) {
            AsccRecord asccRecord = getAsccRecordById(asccManifestRecord.getAsccId().longValue());
            String propertyTerm = dslContext.select(ASCCP.PROPERTY_TERM).from(ASCCP).where(ASCCP.ASCCP_ID.eq(asccRecord.getToAsccpId())).fetchOneInto(String.class);
            asccRecord.set(ASCC.ASCC_ID, null);
            asccRecord.set(ASCC.DEN, accObjectClassTerm + ". " + propertyTerm);
            asccRecord.set(ASCC.FROM_ACC_ID, newAccId);
            asccRecord.set(ASCC.LAST_UPDATED_BY, ULong.valueOf(userId));
            asccRecord.set(ASCC.LAST_UPDATE_TIMESTAMP, timestamp);
            asccRecord.set(ASCC.REVISION_ACTION, (byte) RevisionAction.Update.getValue());
            asccRecord.set(ASCC.REVISION_TRACKING_NUM, asccRecord.getRevisionTrackingNum() + 1);
            asccRecord.insert();

            asccManifestRecord.setAsccId(asccRecord.getAsccId());
            asccManifestRecord.update();
        }
    }

    private void updateBccByFromAcc(long userId, long originAccId,
                                    ULong newAccId, ULong releaseId,
                                    String accObjectClassTerm, Timestamp timestamp) {
        List<BccManifestRecord> bccManifestRecords = dslContext.select(BCC_MANIFEST.fields())
                .from(BCC_MANIFEST)
                .join(ACC_MANIFEST).on(BCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(ACC_MANIFEST.ACC_MANIFEST_ID))
                .where(and(
                        BCC_MANIFEST.RELEASE_ID.eq(releaseId),
                        ACC_MANIFEST.ACC_ID.eq(ULong.valueOf(originAccId))
                ))
                .fetchInto(BccManifestRecord.class);

        for (BccManifestRecord bccManifestRecord : bccManifestRecords) {
            BccRecord bccRecord = getBccRecordById(bccManifestRecord.getBccId().longValue());
            String propertyTerm = dslContext.select(BCCP.PROPERTY_TERM).from(BCCP).where(BCCP.BCCP_ID.eq(bccRecord.getToBccpId())).fetchOneInto(String.class);
            bccRecord.set(BCC.BCC_ID, null);
            bccRecord.set(BCC.DEN, accObjectClassTerm + ". " + propertyTerm);
            bccRecord.set(BCC.FROM_ACC_ID, newAccId);
            bccRecord.set(BCC.LAST_UPDATED_BY, ULong.valueOf(userId));
            bccRecord.set(BCC.LAST_UPDATE_TIMESTAMP, timestamp);
            bccRecord.set(BCC.REVISION_ACTION, (byte) RevisionAction.Update.getValue());
            bccRecord.set(BCC.REVISION_TRACKING_NUM, bccRecord.getRevisionTrackingNum() + 1);
            bccRecord.insert();

            bccManifestRecord.setBccId(bccRecord.getBccId());
            bccManifestRecord.update();
        }
    }

    public void updateAsccByToAsccp(long userId, long originAsccpId,
                                    long newAsccpId, ULong releaseId,
                                     String propertyTerm, Timestamp timestamp) {
        List<AsccManifestRecord> asccManifestRecords = dslContext.select(ASCC_MANIFEST.fields())
                .from(ASCC_MANIFEST)
                .join(ASCCP_MANIFEST).on(ASCC_MANIFEST.TO_ASCCP_MANIFEST_ID.eq(ASCCP_MANIFEST.ASCCP_MANIFEST_ID))
                .where(and(
                        ASCC_MANIFEST.RELEASE_ID.eq(releaseId),
                        ASCCP_MANIFEST.ASCCP_ID.eq(ULong.valueOf(originAsccpId))
                ))
                .fetchInto(AsccManifestRecord.class);

        for (AsccManifestRecord asccManifestRecord : asccManifestRecords) {
            AsccRecord asccRecord = getAsccRecordById(asccManifestRecord.getAsccId().longValue());
            String objectClassTerm = dslContext.select(ACC.OBJECT_CLASS_TERM).from(ACC).where(ACC.ACC_ID.eq(asccRecord.getFromAccId())).fetchOneInto(String.class);
            asccRecord.set(ASCC.ASCC_ID, null);
            asccRecord.set(ASCC.DEN, objectClassTerm + ". " + propertyTerm);
            asccRecord.set(ASCC.TO_ASCCP_ID, ULong.valueOf(newAsccpId));
            asccRecord.set(ASCC.LAST_UPDATED_BY, ULong.valueOf(userId));
            asccRecord.set(ASCC.LAST_UPDATE_TIMESTAMP, timestamp);
            asccRecord.set(ASCC.REVISION_ACTION, (byte) RevisionAction.Update.getValue());
            asccRecord.set(ASCC.REVISION_TRACKING_NUM, asccRecord.getRevisionTrackingNum() + 1);
            asccRecord.insert();

            asccManifestRecord.setAsccId(asccRecord.getAsccId());
            asccManifestRecord.update();
        }
    }

    private void updateBccByToBccp(long userId, long originBccpId,
                                   long newBccpId, ULong releaseId,
                                   String propertyTerm, Timestamp timestamp) {
        List<BccManifestRecord> bccManifestRecords = dslContext.select(BCC_MANIFEST.fields())
                .from(BCC_MANIFEST)
                .join(BCCP_MANIFEST).on(BCC_MANIFEST.TO_BCCP_MANIFEST_ID.eq(BCCP_MANIFEST.BCCP_MANIFEST_ID))
                .where(and(
                        BCC_MANIFEST.RELEASE_ID.eq(releaseId),
                        BCCP_MANIFEST.BCCP_ID.eq(ULong.valueOf(originBccpId))
                ))
                .fetchInto(BccManifestRecord.class);

        for (BccManifestRecord bccManifestRecord : bccManifestRecords) {
            BccRecord bccRecord = getBccRecordById(bccManifestRecord.getBccId().longValue());
            String objectClassTerm = dslContext.select(ACC.OBJECT_CLASS_TERM).from(ACC).where(ACC.ACC_ID.eq(bccRecord.getFromAccId())).fetchOneInto(String.class);
            bccRecord.set(BCC.BCC_ID, null);
            bccRecord.set(BCC.DEN, objectClassTerm + ". " + propertyTerm);
            bccRecord.set(BCC.TO_BCCP_ID, ULong.valueOf(newBccpId));
            bccRecord.set(BCC.LAST_UPDATED_BY, ULong.valueOf(userId));
            bccRecord.set(BCC.LAST_UPDATE_TIMESTAMP, timestamp);
            bccRecord.set(BCC.REVISION_ACTION, (byte) RevisionAction.Update.getValue());
            bccRecord.set(BCC.REVISION_TRACKING_NUM, bccRecord.getRevisionTrackingNum() + 1);
            bccRecord.insert();

            bccManifestRecord.setBccId(bccRecord.getBccId());
            bccManifestRecord.update();
        }
    }

    private void updateAsccState(long userId, long originAccId,
                                 ULong newAccId, ULong releaseId,
                                 CcState state, Timestamp timestamp) {
        List<AsccManifestRecord> asccManifestRecords = dslContext.select(ASCC_MANIFEST.fields())
                .from(ASCC_MANIFEST)
                .join(ACC_MANIFEST).on(ASCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(ACC_MANIFEST.ACC_MANIFEST_ID))
                .where(and(
                        ASCC_MANIFEST.RELEASE_ID.eq(releaseId),
                        ACC_MANIFEST.ACC_ID.eq(ULong.valueOf(originAccId))
                ))
                .fetchInto(AsccManifestRecord.class);

        for (AsccManifestRecord asccManifestRecord : asccManifestRecords) {
            AsccRecord asccRecord = getAsccRecordById(asccManifestRecord.getAsccId().longValue());
            asccRecord.set(ASCC.ASCC_ID, null);
            asccRecord.set(ASCC.FROM_ACC_ID, newAccId);
            asccRecord.set(ASCC.LAST_UPDATED_BY, ULong.valueOf(userId));
            asccRecord.set(ASCC.LAST_UPDATE_TIMESTAMP, timestamp);
            asccRecord.set(ASCC.REVISION_ACTION, (byte) RevisionAction.Update.getValue());
            if (asccRecord.getState() == CcState.Published.getValue() && state == CcState.Editing) {
                asccRecord.set(ASCC.REVISION_NUM, asccRecord.getRevisionNum() + 1);
                asccRecord.set(ASCC.REVISION_TRACKING_NUM, 1);
            } else {
                asccRecord.set(ASCC.REVISION_TRACKING_NUM, asccRecord.getRevisionTrackingNum() + 1);
            }
            asccRecord.set(ASCC.STATE, state.getValue());
            asccRecord.insert();

            asccManifestRecord.setAsccId(asccRecord.getAsccId());
            asccManifestRecord.update();
        }
    }

    private void updateBccState(long userId, long originAccId,
                                ULong newAccId, ULong releaseId,
                                CcState state, Timestamp timestamp) {
        List<BccManifestRecord> bccManifestRecords = dslContext.select(BCC_MANIFEST.fields())
                .from(BCC_MANIFEST)
                .join(ACC_MANIFEST).on(BCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(ACC_MANIFEST.ACC_MANIFEST_ID))
                .where(and(
                        BCC_MANIFEST.RELEASE_ID.eq(releaseId),
                        ACC_MANIFEST.ACC_ID.eq(ULong.valueOf(originAccId))
                ))
                .fetchInto(BccManifestRecord.class);

        for (BccManifestRecord bccManifestRecord : bccManifestRecords) {
            BccRecord bccRecord = getBccRecordById(bccManifestRecord.getBccId().longValue());
            bccRecord.set(BCC.BCC_ID, null);
            bccRecord.set(BCC.FROM_ACC_ID, newAccId);
            bccRecord.set(BCC.LAST_UPDATED_BY, ULong.valueOf(userId));
            bccRecord.set(BCC.LAST_UPDATE_TIMESTAMP, timestamp);
            bccRecord.set(BCC.REVISION_ACTION, (byte) RevisionAction.Update.getValue());
            if (bccRecord.getState() == CcState.Published.getValue() && state == CcState.Editing) {
                bccRecord.set(BCC.REVISION_NUM, bccRecord.getRevisionNum() + 1);
                bccRecord.set(BCC.REVISION_TRACKING_NUM, 1);
            } else {
                bccRecord.set(BCC.REVISION_TRACKING_NUM, bccRecord.getRevisionTrackingNum() + 1);
            }
            bccRecord.set(BCC.STATE, state.getValue());
            bccRecord.insert();

            bccManifestRecord.setBccId(bccRecord.getBccId());
            bccManifestRecord.update();
        }
    }

    public void discardAsccById(User user, long asccManifestId) {
        long userId = sessionService.userId(user);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        AsccManifestRecord asccManifestRecord = dslContext.selectFrom(ASCC_MANIFEST).where(
                ASCC_MANIFEST.ASCC_MANIFEST_ID.eq(ULong.valueOf(asccManifestId))).fetchOne();

        AsccRecord ascc = getAsccRecordById(asccManifestRecord.getAsccId().longValue());
        ascc.setAsccId(null);
        ascc.set(ASCC.LAST_UPDATE_TIMESTAMP, timestamp);
        ascc.set(ASCC.LAST_UPDATED_BY, ULong.valueOf(userId));
        ascc.set(ASCC.REVISION_ACTION, (byte) RevisionAction.Delete.getValue());
        ascc.set(ASCC.REVISION_TRACKING_NUM, ascc.getRevisionTrackingNum() + 1);
        dslContext.insertInto(ASCC).set(ascc).execute();

        decreaseSeqKeyGreaterThan(userId, ascc.getFromAccId().longValue(), asccManifestRecord.getReleaseId().longValue(), ascc.getSeqKey(), timestamp);

        dslContext.deleteFrom(ASCC_MANIFEST)
                .where(ASCC_MANIFEST.ASCC_MANIFEST_ID.eq(asccManifestRecord.getAsccManifestId()))
                .execute();
    }

    public void discardBccById(User user, long bccManifestId) {
        long userId = sessionService.userId(user);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        BccManifestRecord bccManifestRecord = dslContext.selectFrom(BCC_MANIFEST).where(
                BCC_MANIFEST.BCC_MANIFEST_ID.eq(ULong.valueOf(bccManifestId))).fetchOne();

        BccRecord bcc = getBccRecordById(bccManifestRecord.getBccId().longValue());
        bcc.setBccId(null);
        bcc.set(BCC.LAST_UPDATE_TIMESTAMP, timestamp);
        bcc.set(BCC.LAST_UPDATED_BY, ULong.valueOf(userId));
        bcc.set(BCC.REVISION_ACTION, (byte) RevisionAction.Delete.getValue());
        bcc.set(BCC.REVISION_TRACKING_NUM, bcc.getRevisionTrackingNum() + 1);
        dslContext.insertInto(BCC).set(bcc).execute();

        decreaseSeqKeyGreaterThan(userId, bcc.getFromAccId().longValue(), bccManifestRecord.getReleaseId().longValue(), bcc.getSeqKey(), timestamp);

        dslContext.deleteFrom(BCC_MANIFEST)
                .where(BCC_MANIFEST.BCC_MANIFEST_ID.eq(bccManifestRecord.getBccManifestId()))
                .execute();
    }

    private int getNextSeqKey(long accId, long releaseId) {
        Integer asccMaxSeqKey = dslContext.select(max(ASCC.SEQ_KEY))
                .from(ASCC)
                .join(ASCC_MANIFEST)
                .on(and(ASCC.ASCC_ID.eq(ASCC_MANIFEST.ASCC_ID),
                        ASCC_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId))))
                .where(ASCC.FROM_ACC_ID.eq(ULong.valueOf(accId)))
                .fetchOneInto(Integer.class);
        if (asccMaxSeqKey == null) {
            asccMaxSeqKey = 0;
        }

        Integer bccMaxSeqKey = dslContext.select(max(BCC.SEQ_KEY))
                .from(BCC)
                .join(BCC_MANIFEST)
                .on(and(BCC.BCC_ID.eq(BCC_MANIFEST.BCC_ID),
                        BCC_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId))))
                .where(BCC.FROM_ACC_ID.eq(ULong.valueOf(accId)))
                .fetchOneInto(Integer.class);

        if (bccMaxSeqKey == null) {
            bccMaxSeqKey = 0;
        }

        return Math.max(asccMaxSeqKey, bccMaxSeqKey) + 1;
    }

    private void decreaseSeqKeyGreaterThan(long userId, long accId, long releaseId, int seqKey, Timestamp timestamp) {
        if (seqKey == 0) {
            return;
        }

        List<AsccManifestRecord> asccManifestRecords =
                manifestRepository.getAsccManifestByFromAccId(ULong.valueOf(accId), ULong.valueOf(releaseId));

        for (AsccManifestRecord asccManifestRecord : asccManifestRecords) {
            AsccRecord asccRecord = getAsccRecordById(asccManifestRecord.getAsccId().longValue());
            if (asccRecord.getSeqKey() <= seqKey) {
                continue;
            }

            asccRecord.setAsccId(null);
            asccRecord.setLastUpdatedBy(ULong.valueOf(userId));
            asccRecord.setLastUpdateTimestamp(timestamp);
            asccRecord.setRevisionAction((byte) RevisionAction.Update.getValue());
            asccRecord.setRevisionTrackingNum(asccRecord.getRevisionTrackingNum() + 1);
            asccRecord.setSeqKey(asccRecord.getSeqKey() - 1);
            asccRecord.insert();

            asccManifestRecord.setAsccId(asccRecord.getAsccId());
            asccManifestRecord.update();
        }

        List<BccManifestRecord> bccManifestRecords =
                manifestRepository.getBccManifestByFromAccId(ULong.valueOf(accId), ULong.valueOf(releaseId));

        for (BccManifestRecord bccManifestRecord : bccManifestRecords) {
            BccRecord bccRecord = getBccRecordById(bccManifestRecord.getBccId().longValue());
            if (bccRecord.getSeqKey() <= seqKey) {
                continue;
            }

            bccRecord.setBccId(null);
            bccRecord.setLastUpdatedBy(ULong.valueOf(userId));
            bccRecord.setLastUpdateTimestamp(timestamp);
            bccRecord.setRevisionAction((byte) RevisionAction.Update.getValue());
            bccRecord.setRevisionTrackingNum(bccRecord.getRevisionTrackingNum() + 1);
            bccRecord.setSeqKey(bccRecord.getSeqKey() - 1);
            bccRecord.insert();

            bccManifestRecord.setBccId(bccRecord.getBccId());
            bccManifestRecord.update();
        }
    }
}