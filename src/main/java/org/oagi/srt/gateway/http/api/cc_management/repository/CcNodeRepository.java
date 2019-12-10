package org.oagi.srt.gateway.http.api.cc_management.repository;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jooq.*;
import org.jooq.types.ULong;
import org.oagi.srt.data.BCCEntityType;
import org.oagi.srt.data.OagisComponentType;
import org.oagi.srt.data.RevisionAction;
import org.oagi.srt.data.SeqKeySupportable;
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
        boolean isUpdateAsccDen = false;


        AccReleaseManifestRecord accReleaseManifestRecord = dslContext.selectFrom(ACC_RELEASE_MANIFEST)
                .where(ACC_RELEASE_MANIFEST.ACC_RELEASE_MANIFEST_ID.eq(ULong.valueOf(accNodeDetail.getManifestId()))).fetchOne();

        AccRecord baseAccRecord = dslContext.selectFrom(ACC)
                .where(ACC.ACC_ID.eq(accReleaseManifestRecord.getAccId())).fetchOne();

        long originAccid = accReleaseManifestRecord.getAccId().longValue();

        if (!baseAccRecord.getObjectClassTerm().equals(accNodeDetail.getObjectClassTerm())){
            isUpdateAsccDen = true;
        }

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

        AccRecord insertedAccRecord = dslContext.insertInto(ACC).set(baseAccRecord).returning().fetchOne();

        accReleaseManifestRecord.setAccId(insertedAccRecord.getAccId());

        dslContext.update(ACC_RELEASE_MANIFEST)
                .set(accReleaseManifestRecord)
                .where(ACC_RELEASE_MANIFEST.ACC_RELEASE_MANIFEST_ID.eq(accReleaseManifestRecord.getAccReleaseManifestId()))
                .execute();

        if (isUpdateAsccDen) {
            updateAsccDenByAccReleaseManifest(userId, accReleaseManifestRecord,
                    accNodeDetail.getAccId(), accNodeDetail.getObjectClassTerm(), timestamp);
            updateBccDenByAccReleaseManifest(userId, accReleaseManifestRecord,
                    accNodeDetail.getAccId(), accNodeDetail.getObjectClassTerm(), timestamp);
        } else {
            updateAsccByFromAccId(originAccid, insertedAccRecord.getAccId().longValue(), accReleaseManifestRecord.getReleaseId().longValue());
            updateBccByFromAccId(originAccid, insertedAccRecord.getAccId().longValue(), accReleaseManifestRecord.getReleaseId().longValue());
        }

        updateAsccpRoleOfAccIdByAcc(accNodeDetail.getAccId(), insertedAccRecord.getAccId().longValue(),
                accReleaseManifestRecord.getReleaseId().longValue(), accNodeDetail.getObjectClassTerm());

        CcAccNode updateAccNode = getAccNodeByAccId(user, accReleaseManifestRecord);
        return getAccNodeDetail(user, updateAccNode);
    }

    public void updateAsccpRoleOfAccIdByAcc(long originRoleOfAccId, long newRoleOfAccId, long releaseId, String accObjectClassTerm) {
        List<AsccpReleaseManifestRecord> asccpReleaseManifestRecords =
                manifestRepository.getAsccpReleaseManifestByRoleOfAccId(originRoleOfAccId, releaseId);

        for(AsccpReleaseManifestRecord asccpReleaseManifestRecord : asccpReleaseManifestRecords) {
            String getPropertyTerm = dslContext.select(ASCCP.PROPERTY_TERM).from(ASCCP)
                    .where(ASCCP.ASCCP_ID.eq(asccpReleaseManifestRecord.getAsccpId())).fetchOneInto(String.class);
            dslContext.update(ASCCP)
                    .set(ASCCP.ROLE_OF_ACC_ID, ULong.valueOf(newRoleOfAccId))
                    .set(ASCCP.DEN, getPropertyTerm + ". " + accObjectClassTerm)
                    .where(ASCCP.ASCCP_ID.eq(asccpReleaseManifestRecord.getAsccpId()))
                    .execute();

            dslContext.update(ASCCP_RELEASE_MANIFEST)
                    .set(ASCCP_RELEASE_MANIFEST.ROLE_OF_ACC_ID, ULong.valueOf(newRoleOfAccId))
                    .where(ASCCP_RELEASE_MANIFEST.ASCCP_RELEASE_MANIFEST_ID.eq(asccpReleaseManifestRecord.getAsccpReleaseManifestId()))
                    .execute();
        }
    }

    public void updateAsccByFromAccId(long originFromAccId, long newFromAccId, long releaseId) {
        List<AsccReleaseManifestRecord> asccReleaseManifestRecords =
                manifestRepository.getAsccReleaseManifestByFromAccId(originFromAccId, releaseId);

        for(AsccReleaseManifestRecord asccReleaseManifestRecord : asccReleaseManifestRecords) {
            dslContext.update(ASCC)
                    .set(ASCC.FROM_ACC_ID, ULong.valueOf(newFromAccId))
                    .where(ASCC.ASCC_ID.eq(asccReleaseManifestRecord.getAsccId()))
                    .execute();

            dslContext.update(ASCC_RELEASE_MANIFEST)
                    .set(ASCC_RELEASE_MANIFEST.FROM_ACC_ID, ULong.valueOf(newFromAccId))
                    .where(ASCC_RELEASE_MANIFEST.ASCC_RELEASE_MANIFEST_ID.eq(asccReleaseManifestRecord.getAsccReleaseManifestId()))
                    .execute();
        }
    }

    public void updateBccByFromAccId(long originFromAccId, long newFromAccId, long releaseId) {
        List<BccReleaseManifestRecord> bccReleaseManifestRecords =
                manifestRepository.getBccReleaseManifestByFromAccId(originFromAccId, releaseId);

        for(BccReleaseManifestRecord bccReleaseManifestRecord : bccReleaseManifestRecords) {
            dslContext.update(BCC)
                    .set(BCC.FROM_ACC_ID, ULong.valueOf(newFromAccId))
                    .where(BCC.BCC_ID.eq(bccReleaseManifestRecord.getBccId()))
                    .execute();

            dslContext.update(BCC_RELEASE_MANIFEST)
                    .set(BCC_RELEASE_MANIFEST.FROM_ACC_ID, ULong.valueOf(newFromAccId))
                    .where(BCC_RELEASE_MANIFEST.BCC_RELEASE_MANIFEST_ID.eq(bccReleaseManifestRecord.getBccReleaseManifestId()))
                    .execute();
        }
    }

    public void updateAsccByToAsccpId(long originToAsccpId, long newToAsccpId, long releaseId, String accpPropertyTerm) {
        List<AsccReleaseManifestRecord> asccReleaseManifestRecords =
                manifestRepository.getAsccReleaseManifestByToAsccpId(originToAsccpId, releaseId);

        for(AsccReleaseManifestRecord asccReleaseManifestRecord : asccReleaseManifestRecords) {
            AsccRecord asccRecord = getAsccRecordById(asccReleaseManifestRecord.getAsccId().longValue());
            if (!accpPropertyTerm.isEmpty()) {
                AccRecord accRecord = getAccRecordById(asccReleaseManifestRecord.getFromAccId().longValue());
                asccRecord.setDen(accRecord.getObjectClassTerm() + ". " + accpPropertyTerm);
            }
            asccRecord.setToAsccpId(ULong.valueOf(newToAsccpId));
            asccRecord.update();

            dslContext.update(ASCC_RELEASE_MANIFEST)
                    .set(ASCC_RELEASE_MANIFEST.TO_ASCCP_ID, ULong.valueOf(newToAsccpId))
                    .where(ASCC_RELEASE_MANIFEST.ASCC_RELEASE_MANIFEST_ID.eq(asccReleaseManifestRecord.getAsccReleaseManifestId()))
                    .execute();
        }
    }

    public void updateBccByToBccpId(long originToBccpId, long newToBccpId, long releaseId, String bccpPropertyTerm) {
        List<BccReleaseManifestRecord> bccReleaseManifestRecords =
                manifestRepository.getBccReleaseManifestByToBccpId(originToBccpId, releaseId);

        for(BccReleaseManifestRecord bccReleaseManifestRecord : bccReleaseManifestRecords) {
            BccRecord bccRecord = getBccRecordById(bccReleaseManifestRecord.getBccId().longValue());
            if (!bccpPropertyTerm.isEmpty()) {
                AccRecord accRecord = getAccRecordById(bccReleaseManifestRecord.getFromAccId().longValue());
                bccRecord.setDen(accRecord.getObjectClassTerm() + ". " + bccpPropertyTerm);
            }
            bccRecord.setToBccpId(ULong.valueOf(newToBccpId));
            bccRecord.update();

            dslContext.update(BCC_RELEASE_MANIFEST)
                    .set(BCC_RELEASE_MANIFEST.TO_BCCP_ID, ULong.valueOf(newToBccpId))
                    .where(BCC_RELEASE_MANIFEST.BCC_RELEASE_MANIFEST_ID.eq(bccReleaseManifestRecord.getBccReleaseManifestId()))
                    .execute();
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
            baseAsccRecord.setRevisionNum(baseAsccRecord.getRevisionTrackingNum());
            baseAsccRecord.insert();

            dslContext.update(ASCC_RELEASE_MANIFEST)
                    .set(ASCC_RELEASE_MANIFEST.ASCC_ID, baseAsccRecord.getAsccId())
                    .where(ASCC_RELEASE_MANIFEST.ASCC_RELEASE_MANIFEST_ID.eq(asccReleaseManifestRecord.getAsccReleaseManifestId()))
                    .execute();
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
            baseAsccpRecord.setRevisionNum(baseAsccpRecord.getRevisionTrackingNum());

            baseAsccpRecord.insert();

            dslContext.update(ASCCP_RELEASE_MANIFEST)
                    .set(ASCCP_RELEASE_MANIFEST.ASCCP_ID, baseAsccpRecord.getAsccpId())
                    .where(ASCCP_RELEASE_MANIFEST.ASCCP_RELEASE_MANIFEST_ID.eq(asccpReleaseManifestRecord.getAsccpReleaseManifestId()))
                    .execute();
            updateAsccByToAsccpId(originAsccpId, baseAsccpRecord.getAsccpId().longValue(),
                    asccpReleaseManifestRecord.getReleaseId().longValue(), baseAsccpRecord.getPropertyTerm());
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
            baseBccRecord.setRevisionNum(baseBccRecord.getRevisionTrackingNum());
            baseBccRecord.insert();

            dslContext.update(BCC_RELEASE_MANIFEST)
                    .set(BCC_RELEASE_MANIFEST.BCC_ID, baseBccRecord.getBccId())
                    .where(BCC_RELEASE_MANIFEST.BCC_RELEASE_MANIFEST_ID.eq(bccReleaseManifestRecord.getBccReleaseManifestId()))
                    .execute();
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

            dslContext.update(BCCP_RELEASE_MANIFEST)
                    .set(BCCP_RELEASE_MANIFEST.BCCP_ID, baseBccpRecord.getBccpId())
                    .where(BCCP_RELEASE_MANIFEST.BCCP_RELEASE_MANIFEST_ID.eq(bccpReleaseManifestRecord.getBccpReleaseManifestId()))
                    .execute();

            updateBccByToBccpId(originBccpId, baseBccpRecord.getBccpId().longValue(),
                    bccpReleaseManifestRecord.getReleaseId().longValue(), baseBccpRecord.getPropertyTerm());
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

        Long basedAccId = dslContext.select(ACC.BASED_ACC_ID).from(ACC)
                .where(ACC.ACC_ID.eq(ULong.valueOf(accNode.getAccId())))
                .fetchOneInto(Long.class);
        if (basedAccId != null) {
            Long releaseId = accNode.getReleaseId();
            CcAccNode basedAccNode;

            basedAccNode = getAccNodeByAccId(user, basedAccId, releaseId);

            descendants.add(basedAccNode);
        }

        Long releaseId = accNode.getReleaseId();
        long fromAccId;
        fromAccId = accNode.getAccId();

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
                ASCC.TO_ASCCP_ID,
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
        long asccpId = asccpNode.getAsccpId();

        long roleOfAccId = dslContext.select(ASCCP.ROLE_OF_ACC_ID).from(ASCCP)
                .where(ASCCP.ASCCP_ID.eq(ULong.valueOf(asccpId)))
                .fetchOneInto(Long.class);

        Long releaseId = asccpNode.getReleaseId();

        return Arrays.asList(getAccNodeByAccId(user, roleOfAccId, releaseId));
    }

    public List<? extends CcNode> getDescendants(User user, CcBccpNode bccpNode) {
        long bccpId = bccpNode.getBccpId();

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
        long accId = accNode.getAccId();

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
                .from(ACC)
                .join(ACC_RELEASE_MANIFEST)
                .on(ACC.ACC_ID.eq(ACC_RELEASE_MANIFEST.ACC_ID).and(ACC_RELEASE_MANIFEST.RELEASE_ID.eq(ULong.valueOf(accNode.getReleaseId()))))
                .where(ACC.ACC_ID.eq(ULong.valueOf(accId)))
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

        long asccpId = asccpNode.getAsccpId();
        CcAsccpNodeDetail.Asccp asccp = dslContext.select(
                ASCCP_RELEASE_MANIFEST.ASCCP_RELEASE_MANIFEST_ID.as("manifest_id"),
                ASCCP.ASCCP_ID,
                ASCCP.GUID,
                ASCCP.PROPERTY_TERM,
                ASCCP.DEN,
                ASCCP.REUSABLE_INDICATOR.as("reusable"),
                ASCCP.IS_DEPRECATED.as("deprecated"),
                ASCCP.DEFINITION)
                .from(ASCCP)
                .join(ASCCP_RELEASE_MANIFEST)
                .on(ASCCP.ASCCP_ID.eq(ASCCP_RELEASE_MANIFEST.ASCCP_ID)
                        .and(ASCCP_RELEASE_MANIFEST.RELEASE_ID.eq(ULong.valueOf(asccpNode.getReleaseId()))))
                .where(ASCCP.ASCCP_ID.eq(ULong.valueOf(asccpId)))
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

        long bccpId = bccpNode.getBccpId();
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
                .from(BCCP)
                .join(BCCP_RELEASE_MANIFEST)
                .on(BCCP.BCCP_ID.eq(BCCP_RELEASE_MANIFEST.BCCP_ID)
                        .and(BCCP_RELEASE_MANIFEST.RELEASE_ID.eq(ULong.valueOf(bccpNode.getReleaseId()))))
                .where(BCCP.BCCP_ID.eq(ULong.valueOf(bccpId)))
                .fetchOneInto(CcBccpNodeDetail.Bccp.class);
        bccpNodeDetail.setBccp(bccp);

        long bdtId = dslContext.select(BCCP.BDT_ID).from(BCCP)
                .where(BCCP.BCCP_ID.eq(ULong.valueOf(bccpId))).fetchOneInto(Long.class);

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
        ULong userId = ULong.valueOf(sessionService.userId(user));

        long roleOfAccId = dslContext.select(ACC_RELEASE_MANIFEST.ACC_ID).from(ACC_RELEASE_MANIFEST)
                .where(ACC_RELEASE_MANIFEST.ACC_RELEASE_MANIFEST_ID.eq(ULong.valueOf(accManifestId)))
                .fetchOneInto(long.class);

        AccRecord roleOfAccRecord = dslContext.selectFrom(ACC)
                .where(ACC.ACC_ID.eq(ULong.valueOf(roleOfAccId))).fetchOne();

        AsccpReleaseManifestRecord asccpReleaseManifestRecord = dslContext.selectFrom(ASCCP_RELEASE_MANIFEST)
                .where(ASCCP_RELEASE_MANIFEST.ASCCP_RELEASE_MANIFEST_ID.eq(ULong.valueOf(asccpManifestId))).fetchOne();

        AsccpRecord baseAsccpRecord = dslContext.selectFrom(ASCCP)
                .where(ASCCP.ASCCP_ID.eq(asccpReleaseManifestRecord.getAsccpId())).fetchOne();

        long originAsccpId = asccpReleaseManifestRecord.getAsccpId().longValue();

        AsccpRecord insertedAsccpRecord = dslContext.insertInto(ASCCP)
                .set(ASCCP.PROPERTY_TERM, baseAsccpRecord.getPropertyTerm())
                .set(ASCCP.GUID, baseAsccpRecord.getGuid())
                .set(ASCCP.DEN, baseAsccpRecord.getPropertyTerm() + ". " + roleOfAccRecord.getObjectClassTerm())
                .set(ASCCP.ROLE_OF_ACC_ID, roleOfAccRecord.getAccId())
                .set(ASCCP.DEFINITION, baseAsccpRecord.getDefinition())
                .set(ASCCP.IS_DEPRECATED, baseAsccpRecord.getIsDeprecated())
                .set(ASCCP.STATE, baseAsccpRecord.getState())
                .set(ASCCP.OWNER_USER_ID, baseAsccpRecord.getOwnerUserId())
                .set(ASCCP.LAST_UPDATED_BY, userId)
                .set(ASCCP.LAST_UPDATE_TIMESTAMP, timestamp)
                .set(ASCCP.CREATED_BY, baseAsccpRecord.getCreatedBy())
                .set(ASCCP.CREATION_TIMESTAMP, baseAsccpRecord.getCreationTimestamp())
                .set(ASCCP.REUSABLE_INDICATOR, baseAsccpRecord.getReusableIndicator())
                .set(ASCCP.NAMESPACE_ID, baseAsccpRecord.getNamespaceId())
                .set(ASCCP.IS_NILLABLE, baseAsccpRecord.getIsNillable())
                .set(ASCCP.REVISION_ACTION, (byte) RevisionAction.Update.getValue())
                .set(ASCCP.REVISION_TRACKING_NUM, baseAsccpRecord.getRevisionTrackingNum() + 1)
                .set(ASCCP.REVISION_NUM, baseAsccpRecord.getRevisionNum()).returning().fetchOne();

        dslContext.update(ASCCP_RELEASE_MANIFEST)
                .set(ASCCP_RELEASE_MANIFEST.ASCCP_ID, insertedAsccpRecord.getAsccpId())
                .set(ASCCP_RELEASE_MANIFEST.ROLE_OF_ACC_ID, insertedAsccpRecord.getRoleOfAccId())
                .where(ASCCP_RELEASE_MANIFEST.ASCCP_RELEASE_MANIFEST_ID.eq(asccpReleaseManifestRecord.getAsccpReleaseManifestId()))
                .execute();

        updateAsccByToAsccpId(originAsccpId, insertedAsccpRecord.getAsccpId().longValue(), asccpReleaseManifestRecord.getReleaseId().longValue(), "");

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

        BccpRecord insertedBccpRecord = dslContext.insertInto(BCCP)
                .set(baseBccpRecord).returning().fetchOne();

        dslContext.update(BCCP_RELEASE_MANIFEST)
                .set(BCCP_RELEASE_MANIFEST.BCCP_ID, insertedBccpRecord.getBccpId())
                .set(BCCP_RELEASE_MANIFEST.BDT_ID, insertedBccpRecord.getBdtId())
                .where(BCCP_RELEASE_MANIFEST.BCCP_RELEASE_MANIFEST_ID.eq(bccpReleaseManifestRecord.getBccpReleaseManifestId()))
                .execute();

        updateBccByToBccpId(originBccpId, insertedBccpRecord.getBccpId().longValue(), bccpReleaseManifestRecord.getReleaseId().longValue(), "");

        return getBccpNodeByBccpManifestId(user, bccpReleaseManifestRecord.getBccpReleaseManifestId().longValue());
    }
    public CcAccNodeDetail updateAccState(User user, long accManifestId, CcState ccState) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        ULong userId = ULong.valueOf(sessionService.userId(user));

        AccReleaseManifestRecord accReleaseManifestRecord = manifestRepository.getAccReleaseManifestById(accManifestId);

        AccRecord baseAccRecord = getAccRecordById(accReleaseManifestRecord.getAccId().longValue());

        List<AsccpReleaseManifestRecord> asccpReleaseManifestRecords =
                manifestRepository.getAsccpReleaseManifestByRoleOfAccId(accReleaseManifestRecord.getAccId().longValue(),
                        accReleaseManifestRecord.getReleaseId().longValue());

        for (AsccpReleaseManifestRecord asccpReleaseManifestRecord : asccpReleaseManifestRecords) {
            int asccpState = dslContext.select(ASCCP.STATE).from(ASCCP)
                    .where(ASCCP.ASCCP_ID.eq(asccpReleaseManifestRecord.getAsccpId())).fetchOneInto(Integer.class);
            if (asccpState > ccState.getValue()) {
                throw new IllegalArgumentException("ACC must precede the state of ASCCP which based this.");
            }
        }

        List<Integer> asccpStates = dslContext.select(ASCCP.STATE).from(ACC_RELEASE_MANIFEST)
                .join(ASCC_RELEASE_MANIFEST).on(and(
                        ASCC_RELEASE_MANIFEST.FROM_ACC_ID.eq(ACC_RELEASE_MANIFEST.ACC_ID),
                        ASCC_RELEASE_MANIFEST.RELEASE_ID.eq(ACC_RELEASE_MANIFEST.RELEASE_ID)))
                .join(ASCCP_RELEASE_MANIFEST).on(and(
                        ASCCP_RELEASE_MANIFEST.ASCCP_ID.eq(ASCC_RELEASE_MANIFEST.TO_ASCCP_ID),
                        ASCCP_RELEASE_MANIFEST.RELEASE_ID.eq(ASCC_RELEASE_MANIFEST.RELEASE_ID)))
                .join(ASCCP).on(ASCCP.ASCCP_ID.eq(ASCCP_RELEASE_MANIFEST.ASCCP_ID))
                .where(ACC_RELEASE_MANIFEST.ACC_RELEASE_MANIFEST_ID.eq(accReleaseManifestRecord.getAccReleaseManifestId()))
                .fetchInto(Integer.class);

        for(Integer state: asccpStates) {
            if (state > ccState.getValue()) {
                throw new IllegalArgumentException("ACC must precede the state of child ASCCP.");
            }
        }

        long originAccId = baseAccRecord.getAccId().longValue();

        baseAccRecord.set(ACC.ACC_ID, null);
        baseAccRecord.set(ACC.STATE, ccState.getValue());
        baseAccRecord.set(ACC.LAST_UPDATED_BY, userId);
        baseAccRecord.set(ACC.LAST_UPDATE_TIMESTAMP, timestamp);
        baseAccRecord.set(ACC.REVISION_ACTION, (byte) RevisionAction.Update.getValue());
        baseAccRecord.set(ACC.REVISION_TRACKING_NUM, baseAccRecord.getRevisionTrackingNum() + 1);

        AccRecord insertedAccRecord = dslContext.insertInto(ACC)
                .set(baseAccRecord).returning().fetchOne();

        dslContext.update(ACC_RELEASE_MANIFEST)
                .set(ACC_RELEASE_MANIFEST.ACC_ID, insertedAccRecord.getAccId())
                .where(ACC_RELEASE_MANIFEST.ACC_RELEASE_MANIFEST_ID.eq(accReleaseManifestRecord.getAccReleaseManifestId()))
                .execute();

        updateAsccpRoleOfAccIdByAcc(originAccId, insertedAccRecord.getAccId().longValue(),
                accReleaseManifestRecord.getReleaseId().longValue(), insertedAccRecord.getObjectClassTerm());

        updateBccStateByAccId(userId.longValue(), originAccId, insertedAccRecord.getAccId().longValue(),
                accReleaseManifestRecord.getReleaseId().longValue(), timestamp, ccState);

        updateAsccStateByAccId(userId.longValue(), originAccId, insertedAccRecord.getAccId().longValue(),
                accReleaseManifestRecord.getReleaseId().longValue(), timestamp, ccState);
        
        CcAccNode ccAccNode = getAccNodeByAccId(user, accReleaseManifestRecord);
        return getAccNodeDetail(user, ccAccNode);
    }

    private void updateBccStateByAccId(long userId, long orginAccId, long insertedAccId, long releaseId,
                                       Timestamp timestamp, CcState ccState) {

        List<BccReleaseManifestRecord> bccReleaseManifestRecords = dslContext.selectFrom(BCC_RELEASE_MANIFEST)
                .where(and(BCC_RELEASE_MANIFEST.FROM_ACC_ID.eq(ULong.valueOf(orginAccId)),
                        BCC_RELEASE_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId)))).fetch();
        
        for (BccReleaseManifestRecord bccReleaseManifestRecord : bccReleaseManifestRecords) {
            BccRecord baseBccRecord = getBccRecordById(bccReleaseManifestRecord.getBccId().longValue());

            baseBccRecord.set(BCC.BCC_ID, null);
            baseBccRecord.set(BCC.STATE, ccState.getValue());
            baseBccRecord.set(BCC.FROM_ACC_ID, ULong.valueOf(insertedAccId));
            baseBccRecord.set(BCC.TO_BCCP_ID, bccReleaseManifestRecord.getToBccpId());
            baseBccRecord.set(BCC.LAST_UPDATED_BY, ULong.valueOf(userId));
            baseBccRecord.set(BCC.LAST_UPDATE_TIMESTAMP, timestamp);
            baseBccRecord.set(BCC.REVISION_ACTION, (byte) RevisionAction.Update.getValue());
            baseBccRecord.set(BCC.REVISION_TRACKING_NUM, baseBccRecord.getRevisionTrackingNum() + 1);

            BccRecord insertedBccRecord = dslContext.insertInto(BCC)
                    .set(baseBccRecord).returning().fetchOne();

            dslContext.update(BCC_RELEASE_MANIFEST)
                    .set(BCC_RELEASE_MANIFEST.BCC_ID, insertedBccRecord.getBccId())
                    .set(BCC_RELEASE_MANIFEST.FROM_ACC_ID, ULong.valueOf(insertedAccId))
                    .where(BCC_RELEASE_MANIFEST.BCC_RELEASE_MANIFEST_ID.eq(bccReleaseManifestRecord.getBccReleaseManifestId()))
                    .execute();
        }
    }

    private void updateAsccStateByAccId(long userId, long orginAccId, long insertedAccId, long releaseId,
                                        Timestamp timestamp, CcState ccState) {

        List<AsccReleaseManifestRecord> asccReleaseManifestRecords = dslContext.selectFrom(ASCC_RELEASE_MANIFEST)
                .where(and(ASCC_RELEASE_MANIFEST.FROM_ACC_ID.eq(ULong.valueOf(orginAccId)),
                        ASCC_RELEASE_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId)))).fetch();

        for (AsccReleaseManifestRecord asccReleaseManifestRecord : asccReleaseManifestRecords) {
            AsccRecord baseAsccRecord = getAsccRecordById(asccReleaseManifestRecord.getAsccId().longValue());

            baseAsccRecord.set(ASCC.ASCC_ID, null);
            baseAsccRecord.set(ASCC.STATE, ccState.getValue());
            baseAsccRecord.set(ASCC.FROM_ACC_ID, ULong.valueOf(insertedAccId));
            baseAsccRecord.set(ASCC.TO_ASCCP_ID, asccReleaseManifestRecord.getToAsccpId());
            baseAsccRecord.set(ASCC.LAST_UPDATED_BY, ULong.valueOf(userId));
            baseAsccRecord.set(ASCC.LAST_UPDATE_TIMESTAMP, timestamp);
            baseAsccRecord.set(ASCC.REVISION_ACTION, (byte) RevisionAction.Update.getValue());
            baseAsccRecord.set(ASCC.REVISION_TRACKING_NUM, baseAsccRecord.getRevisionTrackingNum() + 1);

            AsccRecord insertedAsccRecord = dslContext.insertInto(ASCC)
                    .set(baseAsccRecord).returning().fetchOne();

            dslContext.update(ASCC_RELEASE_MANIFEST)
                    .set(ASCC_RELEASE_MANIFEST.ASCC_ID, insertedAsccRecord.getAsccId())
                    .set(ASCC_RELEASE_MANIFEST.FROM_ACC_ID, ULong.valueOf(insertedAccId))
                    .where(ASCC_RELEASE_MANIFEST.ASCC_RELEASE_MANIFEST_ID.eq(asccReleaseManifestRecord.getAsccReleaseManifestId()))
                    .execute();
        }
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
                throw new IllegalArgumentException("ASCCP state can not precede parent ACC.");
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
        baseAsccpRecord.set(ASCCP.REVISION_TRACKING_NUM, baseAsccpRecord.getRevisionTrackingNum() + 1);

        AsccpRecord insertedAsccpRecord = dslContext.insertInto(ASCCP)
                .set(baseAsccpRecord).returning().fetchOne();

        dslContext.update(ASCCP_RELEASE_MANIFEST)
                .set(ASCCP_RELEASE_MANIFEST.ASCCP_ID, insertedAsccpRecord.getAsccpId())
                .where(ASCCP_RELEASE_MANIFEST.ASCCP_RELEASE_MANIFEST_ID.eq(asccpReleaseManifestRecord.getAsccpReleaseManifestId()))
                .execute();

        updateAsccByToAsccpId(originAsccpId, insertedAsccpRecord.getAsccpId().longValue(), asccpReleaseManifestRecord.getReleaseId().longValue(), "");

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

        baseBccpRecord.set(BCCP.BCCP_ID, null);
        baseBccpRecord.set(BCCP.STATE, ccState.getValue());
        baseBccpRecord.set(BCCP.LAST_UPDATED_BY, userId);
        baseBccpRecord.set(BCCP.LAST_UPDATE_TIMESTAMP, timestamp);
        baseBccpRecord.set(BCCP.REVISION_ACTION, RevisionAction.Update.getValue());
        baseBccpRecord.set(BCCP.REVISION_TRACKING_NUM, baseBccpRecord.getRevisionTrackingNum() + 1);

        BccpRecord insertedBccpRecord = dslContext.insertInto(BCCP)
                .set(baseBccpRecord).returning().fetchOne();

        dslContext.update(BCCP_RELEASE_MANIFEST)
                .set(BCCP_RELEASE_MANIFEST.BCCP_ID, insertedBccpRecord.getBccpId())
                .where(BCCP_RELEASE_MANIFEST.BCCP_RELEASE_MANIFEST_ID.eq(bccpReleaseManifestRecord.getBccpReleaseManifestId()))
                .execute();

        updateBccByToBccpId(originBccpId, insertedBccpRecord.getBccpId().longValue(), bccpReleaseManifestRecord.getReleaseId().longValue(), "");

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

        int seqKey = getNextSeqKey(accReleaseManifest.getAccId().longValue());

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

        int seqKey = getNextSeqKey(accReleaseManifest.getAccId().longValue());
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

        updateAsccByFromAccId(originAccId, newAccId, accReleaseManifest.getReleaseId().longValue());
        updateBccByFromAccId(originAccId, newAccId, accReleaseManifest.getReleaseId().longValue());

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

        AccRecord insertedAccRecord = dslContext.insertInto(ACC).set(accRecord).returning().fetchOne();
        accReleaseManifestRecord.setBasedAccId(basedAccReleaseManifestRecord.getAccId());
        accReleaseManifestRecord.setAccId(insertedAccRecord.getAccId());
        accReleaseManifestRecord.update();

        long newAccId = insertedAccRecord.getAccId().longValue();

        updateAsccByFromAccId(originAccId, newAccId, accReleaseManifestRecord.getReleaseId().longValue());
        updateBccByFromAccId(originAccId, newAccId, accReleaseManifestRecord.getReleaseId().longValue());

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

    private void updateAsccDenByAccReleaseManifest(long userId, AccReleaseManifestRecord accReleaseManifestRecord,
                                                   long originAccId,
                                                   String accObjectClassTerm, Timestamp timestamp) {
        Result<AsccReleaseManifestRecord> asccReleaseManifestRecords = dslContext.selectFrom(ASCC_RELEASE_MANIFEST)
                .where(and(ASCC_RELEASE_MANIFEST.RELEASE_ID.eq(accReleaseManifestRecord.getReleaseId()),
                        ASCC_RELEASE_MANIFEST.FROM_ACC_ID.eq(ULong.valueOf(originAccId))))
                .fetch();
        
        for(AsccReleaseManifestRecord asccReleaseManifestRecord : asccReleaseManifestRecords) {
            AsccRecord asccRecord = getAsccRecordById(asccReleaseManifestRecord.getAsccId().longValue());
            String propertyTerm = dslContext.select(ASCCP.PROPERTY_TERM).from(ASCCP).where(ASCCP.ASCCP_ID.eq(asccRecord.getToAsccpId())).fetchOneInto(String.class);
            asccRecord.set(ASCC.ASCC_ID, null);
            asccRecord.set(ASCC.DEN, accObjectClassTerm + ". " + propertyTerm);
            asccRecord.set(ASCC.FROM_ACC_ID, accReleaseManifestRecord.getAccId());
            asccRecord.set(ASCC.TO_ASCCP_ID, asccReleaseManifestRecord.getToAsccpId());
            asccRecord.set(ASCC.LAST_UPDATED_BY, ULong.valueOf(userId));
            asccRecord.set(ASCC.LAST_UPDATE_TIMESTAMP, timestamp);
            asccRecord.set(ASCC.REVISION_ACTION, (byte) RevisionAction.Update.getValue());
            asccRecord.set(ASCC.REVISION_TRACKING_NUM, asccRecord.getRevisionTrackingNum() + 1);
            AsccRecord insertedAsccRecord = dslContext.insertInto(ASCC)
                    .set(asccRecord).returning().fetchOne();

            dslContext.update(ASCC_RELEASE_MANIFEST)
                    .set(ASCC_RELEASE_MANIFEST.ASCC_ID, insertedAsccRecord.getAsccId())
                    .set(ASCC_RELEASE_MANIFEST.FROM_ACC_ID, accReleaseManifestRecord.getAccId())
                    .where(ASCC_RELEASE_MANIFEST.ASCC_RELEASE_MANIFEST_ID.eq(asccReleaseManifestRecord.getAsccReleaseManifestId()))
                    .execute();
        }

    }

    private void updateBccDenByAccReleaseManifest(long userId, AccReleaseManifestRecord accReleaseManifestRecord,
                                                  long originAccId,
                                                   String accObjectClassTerm, Timestamp timestamp) {
        Result<BccReleaseManifestRecord> bccReleaseManifestRecords = dslContext.selectFrom(BCC_RELEASE_MANIFEST)
                .where(and(BCC_RELEASE_MANIFEST.RELEASE_ID.eq(accReleaseManifestRecord.getReleaseId()),
                        BCC_RELEASE_MANIFEST.FROM_ACC_ID.eq(ULong.valueOf(originAccId))))
                .fetch();

        for(BccReleaseManifestRecord bccReleaseManifestRecord : bccReleaseManifestRecords) {
            BccRecord bccRecord = getBccRecordById(bccReleaseManifestRecord.getBccId().longValue());
            String propertyTerm = dslContext.select(BCCP.PROPERTY_TERM).from(BCCP).where(BCCP.BCCP_ID.eq(bccRecord.getToBccpId())).fetchOneInto(String.class);
            bccRecord.set(BCC.BCC_ID, null);
            bccRecord.set(BCC.DEN, accObjectClassTerm + ". " + propertyTerm);
            bccRecord.set(BCC.FROM_ACC_ID, accReleaseManifestRecord.getAccId());
            bccRecord.set(BCC.TO_BCCP_ID, bccReleaseManifestRecord.getToBccpId());
            bccRecord.set(BCC.LAST_UPDATED_BY, ULong.valueOf(userId));
            bccRecord.set(BCC.LAST_UPDATE_TIMESTAMP, timestamp);
            bccRecord.set(BCC.REVISION_ACTION, (byte) RevisionAction.Update.getValue());
            bccRecord.set(BCC.REVISION_TRACKING_NUM, bccRecord.getRevisionTrackingNum() + 1);
            BccRecord insertedBccRecord = dslContext.insertInto(BCC)
                    .set(bccRecord).returning().fetchOne();

            dslContext.update(BCC_RELEASE_MANIFEST)
                    .set(BCC_RELEASE_MANIFEST.BCC_ID, insertedBccRecord.getBccId())
                    .set(BCC_RELEASE_MANIFEST.FROM_ACC_ID, accReleaseManifestRecord.getAccId())
                    .where(BCC_RELEASE_MANIFEST.BCC_RELEASE_MANIFEST_ID.eq(bccReleaseManifestRecord.getBccReleaseManifestId()))
                    .execute();
        }

    }

    public void discardAsccById(User user, long asccId, long releaseId){
        long userId = sessionService.userId(user);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        AsccReleaseManifestRecord asccReleaseManifestRecord = dslContext.selectFrom(ASCC_RELEASE_MANIFEST).where(
                and(ASCC_RELEASE_MANIFEST.ASCC_ID.eq(ULong.valueOf(asccId)),
                        ASCC_RELEASE_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId)))).fetchOne();

        dslContext.deleteFrom(ASCC_RELEASE_MANIFEST)
                .where(ASCC_RELEASE_MANIFEST.ASCC_RELEASE_MANIFEST_ID.eq(asccReleaseManifestRecord.getAsccReleaseManifestId())).execute();

        AsccRecord ascc = getAsccRecordById(asccReleaseManifestRecord.getAsccId().longValue());
        ascc.setAsccId(null);
        ascc.set(ASCC.LAST_UPDATE_TIMESTAMP, timestamp);
        ascc.set(ASCC.LAST_UPDATED_BY, ULong.valueOf(userId));
        ascc.set(ASCC.REVISION_ACTION, (byte) RevisionAction.Delete.getValue());
        ascc.set(ASCC.REVISION_TRACKING_NUM, ascc.getRevisionTrackingNum() + 1);
        dslContext.insertInto(ASCC).set(ascc).execute();

        decreaseSeqKeyGreaterThan(ascc.getFromAccId().longValue(), ascc.getSeqKey());
    }

    public void discardBccById(User user, long bccId, long releaseId){
        long userId = sessionService.userId(user);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        BccReleaseManifestRecord bccReleaseManifestRecord = dslContext.selectFrom(BCC_RELEASE_MANIFEST).where(
                and(BCC_RELEASE_MANIFEST.BCC_ID.eq(ULong.valueOf(bccId)),
                        BCC_RELEASE_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId)))).fetchOne();

        dslContext.deleteFrom(BCC_RELEASE_MANIFEST)
                .where(BCC_RELEASE_MANIFEST.BCC_RELEASE_MANIFEST_ID.eq(bccReleaseManifestRecord.getBccReleaseManifestId())).execute();
        BccRecord bcc = getBccRecordById(bccReleaseManifestRecord.getBccId().longValue());
        bcc.setBccId(null);
        bcc.set(BCC.LAST_UPDATE_TIMESTAMP, timestamp);
        bcc.set(BCC.LAST_UPDATED_BY, ULong.valueOf(userId));
        bcc.set(BCC.REVISION_ACTION, (byte) RevisionAction.Delete.getValue());
        bcc.set(BCC.REVISION_TRACKING_NUM, bcc.getRevisionTrackingNum() + 1);
        dslContext.insertInto(BCC).set(bcc).execute();

        decreaseSeqKeyGreaterThan(bcc.getFromAccId().longValue(), bcc.getSeqKey());
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

    private void decreaseSeqKeyGreaterThan(long accId, int seqKey) {
        dslContext.update(ASCC)
                .set(ASCC.SEQ_KEY, ASCC.SEQ_KEY.subtract(1))
                .where(and(
                        ASCC.FROM_ACC_ID.eq(ULong.valueOf(accId)),
                        ASCC.SEQ_KEY.greaterThan(seqKey)
                ))
                .execute();

        dslContext.update(BCC)
                .set(BCC.SEQ_KEY, BCC.SEQ_KEY.subtract(1))
                .where(and(
                        BCC.FROM_ACC_ID.eq(ULong.valueOf(accId)),
                        BCC.SEQ_KEY.greaterThan(seqKey)
                ))
                .execute();
    }
}