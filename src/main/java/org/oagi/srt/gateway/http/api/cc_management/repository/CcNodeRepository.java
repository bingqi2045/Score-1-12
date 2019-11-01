package org.oagi.srt.gateway.http.api.cc_management.repository;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Record10;
import org.jooq.SelectOnConditionStep;
import org.jooq.types.ULong;
import org.oagi.srt.data.BCCEntityType;
import org.oagi.srt.data.OagisComponentType;
import org.oagi.srt.data.RevisionAction;
import org.oagi.srt.data.SeqKeySupportable;
import org.oagi.srt.entity.jooq.tables.AsccpReleaseManifest;
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
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.*;
import static org.oagi.srt.data.BCCEntityType.Attribute;
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

    private SelectOnConditionStep<Record10<
            ULong, String, String, ULong, Integer,
            String, Integer, Integer, Integer, ULong>> getSelectJoinStepForAccNode() {
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
                ACC_RELEASE_MANIFEST.RELEASE_ID)
                .from(ACC)
                .join(ACC_RELEASE_MANIFEST)
                .on(ACC.ACC_ID.eq(ACC_RELEASE_MANIFEST.ACC_ID));
    }

    public CcAccNode getAccNodeByAccId(long accId, long releaseId) {
        AccReleaseManifestRecord accReleaseManifestRecord =
                dslContext.selectFrom(ACC_RELEASE_MANIFEST)
                .where(and(
                        ACC_RELEASE_MANIFEST.ACC_ID.eq(ULong.valueOf(accId)),
                        ACC_RELEASE_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId))
                ))
                .fetchOne();

        return getAccNodeByAccId(accReleaseManifestRecord);
    }

    public CcAccNode getAccNodeByAccId(AccReleaseManifestRecord accReleaseManifestRecord) {
        CcAccNode accNode = getSelectJoinStepForAccNode()
                .where(ACC_RELEASE_MANIFEST.ACC_RELEASE_MANIFEST_ID.eq(
                        accReleaseManifestRecord.getAccReleaseManifestId()))
                .fetchOneInto(CcAccNode.class);
        return arrangeAccNode(accNode, accReleaseManifestRecord.getReleaseId());
    }

    public CcAccNode getAccNodeFromAsccByAsccpId(long toAsccpId, ULong releaseId) {
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

        return getAccNodeByAccId(accReleaseManifestRecord);
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

    public void updateAcc(User user, CcAccNode ccAccNode) {
        long userId = sessionService.userId(user);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        dslContext.update(ACC)
                .set(ACC.DEFINITION, ccAccNode.getDefinition())
                .set(ACC.DEN, ccAccNode.getDen())
                .set(ACC.OBJECT_CLASS_TERM, ccAccNode.getObjectClassTerm())
                .set(ACC.OAGIS_COMPONENT_TYPE, ccAccNode.getOagisComponentType())
                .set(ASCC.IS_DEPRECATED, (byte) ((ccAccNode.isDeprecated()) ? 1 : 0))
                .set(ACC.IS_ABSTRACT, (byte) ((ccAccNode.isAbstract()) ? 1 : 0))
                .set(ACC.LAST_UPDATED_BY, ULong.valueOf(userId))
                .set(ACC.LAST_UPDATE_TIMESTAMP, timestamp)
                .where(ACC.ACC_ID.eq(ULong.valueOf(ccAccNode.getAccId())));
    }

    public CcAsccpNodeDetail updateAsccp(User user, CcAsccpNodeDetail.Asccp asccpNodeDetail, long manifestId) {
        long userId = sessionService.userId(user);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        AsccpReleaseManifestRecord asccpReleaseManifestRecord = dslContext.selectFrom(ASCCP_RELEASE_MANIFEST)
                .where(ASCCP_RELEASE_MANIFEST.ASCCP_RELEASE_MANIFEST_ID.eq(ULong.valueOf(manifestId))).fetchOne();
        AccRecord accRecord = dslContext.selectFrom(ACC)
                .where(ACC.ACC_ID.eq(asccpReleaseManifestRecord.getRoleOfAccId())).fetchOne();
        AsccpRecord baseAsccpRecord = dslContext.selectFrom(ASCCP)
                .where(ASCCP.ASCCP_ID.eq(asccpReleaseManifestRecord.getAsccpId())).fetchOne();

        AsccpRecord InsertedAsccpRecord = dslContext.insertInto(ASCCP)
                .set(ASCCP.PROPERTY_TERM, asccpNodeDetail.getPropertyTerm())
                .set(ASCCP.GUID, baseAsccpRecord.getGuid())
                .set(ASCCP.DEN, asccpNodeDetail.getPropertyTerm() + ". " + accRecord.getObjectClassTerm())
                .set(ASCCP.ROLE_OF_ACC_ID, accRecord.getAccId())
                .set(ASCCP.DEFINITION, asccpNodeDetail.getDefinition())
                .set(ASCCP.IS_DEPRECATED,(byte) (asccpNodeDetail.isDeprecated() ? 1 : 0))
                .set(ASCCP.STATE, baseAsccpRecord.getState())
                .set(ASCCP.OWNER_USER_ID, baseAsccpRecord.getOwnerUserId())
                .set(ASCCP.LAST_UPDATED_BY, ULong.valueOf(userId))
                .set(ASCCP.LAST_UPDATE_TIMESTAMP, timestamp)
                .set(ASCCP.CREATED_BY, baseAsccpRecord.getCreatedBy())
                .set(ASCCP.CREATION_TIMESTAMP, baseAsccpRecord.getCreationTimestamp())
                .set(ASCCP.REUSABLE_INDICATOR, (byte) (asccpNodeDetail.isReusable() ? 1 : 0))
                .set(ASCCP.NAMESPACE_ID, baseAsccpRecord.getNamespaceId())
                .set(ASCCP.IS_NILLABLE, baseAsccpRecord.getIsNillable())
                .set(ASCCP.REVISION_ACTION, (byte) RevisionAction.Update.getValue())
                .set(ASCCP.REVISION_TRACKING_NUM, baseAsccpRecord.getRevisionTrackingNum() + 1)
                .set(ASCCP.REVISION_NUM, baseAsccpRecord.getRevisionNum()).returning().fetchOne();

        dslContext.update(ASCCP_RELEASE_MANIFEST)
                .set(ASCCP_RELEASE_MANIFEST.ASCCP_ID, InsertedAsccpRecord.getAsccpId())
                .where(ASCCP_RELEASE_MANIFEST.ASCCP_RELEASE_MANIFEST_ID.eq(asccpReleaseManifestRecord.getAsccpReleaseManifestId()))
                .execute();

        CcAsccpNode ccAsccpNode = getAsccpNodeByAsccpManifestId(user, asccpReleaseManifestRecord.getAsccpReleaseManifestId().longValue());
        return getAsccpNodeDetail(user, ccAsccpNode);
    }

    public CcBccpNodeDetail updateBccp(User user, CcBccpNodeDetail.Bccp bccpNodeDetail, long manifestId) {
        long userId = sessionService.userId(user);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        
        BccpReleaseManifestRecord bccpReleaseManifestRecord = dslContext.selectFrom(BCCP_RELEASE_MANIFEST)
                .where(BCCP_RELEASE_MANIFEST.BCCP_RELEASE_MANIFEST_ID.eq(ULong.valueOf(manifestId))).fetchOne();

        DtRecord dtRecord = dslContext.selectFrom(DT)
                .where(DT.DT_ID.eq(bccpReleaseManifestRecord.getBdtId())).fetchOne();

        BccpRecord baseBccpRecord = dslContext.selectFrom(BCCP)
                .where(BCCP.BCCP_ID.eq(bccpReleaseManifestRecord.getBccpId())).fetchOne();

        baseBccpRecord.set(BCCP.BCCP_ID, null);
        baseBccpRecord.set(BCCP.PROPERTY_TERM, bccpNodeDetail.getPropertyTerm());
        baseBccpRecord.set(BCCP.DEFAULT_VALUE, bccpNodeDetail.getDefaultValue());
        baseBccpRecord.set(BCCP.DEFINITION, bccpNodeDetail.getDefinition());
        baseBccpRecord.set(BCCP.DEN, bccpNodeDetail.getPropertyTerm() + ". " + dtRecord.getDataTypeTerm());
        baseBccpRecord.set(BCCP.IS_DEPRECATED,(byte) (bccpNodeDetail.isDeprecated() ? 1 : 0));
        baseBccpRecord.set(BCCP.IS_NILLABLE,(byte) (bccpNodeDetail.isNillable() ? 1 : 0));
        baseBccpRecord.set(BCCP.LAST_UPDATED_BY, ULong.valueOf(userId));
        baseBccpRecord.set(BCCP.LAST_UPDATE_TIMESTAMP, timestamp);
        baseBccpRecord.set(BCCP.REVISION_ACTION, RevisionAction.Update.getValue());
        baseBccpRecord.set(BCCP.REVISION_TRACKING_NUM, baseBccpRecord.getRevisionTrackingNum() + 1);

        BccpRecord InsertedBccpRecord = dslContext.insertInto(BCCP).set(baseBccpRecord).returning().fetchOne();

        dslContext.update(BCCP_RELEASE_MANIFEST)
                .set(BCCP_RELEASE_MANIFEST.BCCP_ID, InsertedBccpRecord.getBccpId())
                .where(BCCP_RELEASE_MANIFEST.BCCP_RELEASE_MANIFEST_ID.eq(bccpReleaseManifestRecord.getBccpReleaseManifestId()))
                .execute();

        CcBccpNode ccBccpNode = getBccpNodeByBccpManifestId(user, bccpReleaseManifestRecord.getBccpReleaseManifestId().longValue());
        return getBccpNodeDetail(user, ccBccpNode);
    }

    private CcAccNode arrangeAccNode(CcAccNode accNode, ULong releaseId) {
        OagisComponentType oagisComponentType =
                OagisComponentType.valueOf(accNode.getOagisComponentType());
        accNode.setGroup(oagisComponentType.isGroup());

        accNode.setState(CcState.valueOf(accNode.getRawState()));
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

            basedAccNode = getAccNodeByAccId(basedAccId, releaseId);

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
                    CcAccNode uegAccNode = getAccNodeByAccId(asccpNode.getRoleOfAccId(), releaseId);
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
            return asccpNode;
        }).collect(Collectors.toList());
    }

    private List<CcBccpNode> getBccpNodes(User user, long fromAccId, Long releaseId) {
        List<CcBccNode> bccNodes = dslContext.select(
                BCC.BCC_ID,
                BCC.BCC_ID,
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
            return bccpNode;
        }).collect(Collectors.toList());
    }

    public List<? extends CcNode> getDescendants(User user, CcAsccpNode asccpNode) {
        long asccpId = asccpNode.getAsccpId();

        long roleOfAccId = dslContext.select(ASCCP.ROLE_OF_ACC_ID).from(ASCCP)
                .where(ASCCP.ASCCP_ID.eq(ULong.valueOf(asccpId)))
                .fetchOneInto(Long.class);

        Long releaseId = asccpNode.getReleaseId();

        return Arrays.asList(getAccNodeByAccId(roleOfAccId, releaseId));
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
                ACC.DEFINITION
        ).from(ACC).where(ACC.ACC_ID.eq(ULong.valueOf(accId)))
                .fetchOneInto(CcAccNodeDetail.class);
    }

    public CcAsccpNodeDetail getAsccpNodeDetail(User user, CcAsccpNode asccpNode) {
        CcAsccpNodeDetail asccpNodeDetail = new CcAsccpNodeDetail();

        long asccId = asccpNode.getAsccId();
        if (asccId > 0L) {
            CcAsccpNodeDetail.Ascc ascc = dslContext.select(
                    ASCC_RELEASE_MANIFEST.ASCC_RELEASE_MANIFEST_ID.as("manifest_id"),
                    ASCC.ASCC_ID,
                    ASCC.GUID,
                    ASCC.DEN,
                    ASCC.CARDINALITY_MIN,
                    ASCC.CARDINALITY_MAX,
                    ASCC.IS_DEPRECATED.as("deprecated"),
                    ASCC.DEFINITION)
                    .from(ASCC)
                    .join(ASCC_RELEASE_MANIFEST)
                    .on(ASCC.ASCC_ID.eq(ASCC_RELEASE_MANIFEST.ASCC_ID)
                            .and(ASCC_RELEASE_MANIFEST.RELEASE_ID.eq(ULong.valueOf(asccpNode.getReleaseId()))))
                    .where(ASCC.ASCC_ID.eq(ULong.valueOf(asccId)))
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

        long bccId = bccpNode.getBccId();
        if (bccId > 0L) {
            CcBccpNodeDetail.Bcc bcc = dslContext.select(
                    BCC.BCC_ID,
                    BCC.GUID,
                    BCC.DEN,
                    BCC.ENTITY_TYPE,
                    BCC.CARDINALITY_MIN,
                    BCC.CARDINALITY_MAX,
                    BCC.IS_DEPRECATED.as("deprecated"),
                    BCC.DEFAULT_VALUE,
                    BCC.DEFINITION)
                    .from(BCC)
                    .where(BCC.BCC_ID.eq(ULong.valueOf(bccId)))
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
                DT_SC.DEFINITION).from(DT_SC)
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

        dslContext.deleteFrom(ASCC)
                .where(ASCC.FROM_ACC_ID.in(accIds))
                .execute();
        dslContext.deleteFrom(BCC)
                .where(BCC.FROM_ACC_ID.in(accIds))
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

    public void updateAsccpManifest(User user, long asccpManifestId, long accManifestId) {
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

        AsccpRecord InsertedAsccpRecord = dslContext.insertInto(ASCCP)
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
                .set(ASCCP_RELEASE_MANIFEST.ASCCP_ID, InsertedAsccpRecord.getAsccpId())
                .set(ASCCP_RELEASE_MANIFEST.ROLE_OF_ACC_ID, InsertedAsccpRecord.getRoleOfAccId())
                .where(ASCCP_RELEASE_MANIFEST.ASCCP_RELEASE_MANIFEST_ID.eq(asccpReleaseManifestRecord.getAsccpReleaseManifestId()))
                .execute();
    }

    public void updateBccpManifest(User user, long bccpManifestId, long bdtManifestId) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        ULong userId = ULong.valueOf(sessionService.userId(user));

        long bdtId = dslContext.select(DT_RELEASE_MANIFEST.DT_ID).from(DT_RELEASE_MANIFEST)
                .where(DT_RELEASE_MANIFEST.DT_RELEASE_MANIFEST_ID.eq(ULong.valueOf(bdtManifestId)))
                .fetchOneInto(long.class);

        DtRecord dtRecord = dslContext.selectFrom(DT)
                .where(DT.DT_ID.eq(ULong.valueOf(bdtId))).fetchOne();

        BccpReleaseManifestRecord bccpReleaseManifestRecord = dslContext.selectFrom(BCCP_RELEASE_MANIFEST)
                .where(BCCP_RELEASE_MANIFEST.BCCP_RELEASE_MANIFEST_ID.eq(ULong.valueOf(bccpManifestId))).fetchOne();

        BccpRecord baseBccpRecord = dslContext.selectFrom(BCCP)
                .where(BCCP.BCCP_ID.eq(bccpReleaseManifestRecord.getBccpId())).fetchOne();

        baseBccpRecord.set(BCCP.BCCP_ID, null);
        baseBccpRecord.set(BCCP.BDT_ID, ULong.valueOf(bdtId));
        baseBccpRecord.set(BCCP.DEN, baseBccpRecord.getPropertyTerm() + ". " + dtRecord.getDataTypeTerm());
        baseBccpRecord.set(BCCP.LAST_UPDATED_BY, userId);
        baseBccpRecord.set(BCCP.LAST_UPDATE_TIMESTAMP, timestamp);
        baseBccpRecord.set(BCCP.REVISION_ACTION, RevisionAction.Update.getValue());
        baseBccpRecord.set(BCCP.REVISION_TRACKING_NUM, baseBccpRecord.getRevisionTrackingNum() + 1);

        BccpRecord InsertedBccpRecord = dslContext.insertInto(BCCP)
                .set(baseBccpRecord).returning().fetchOne();

        dslContext.update(BCCP_RELEASE_MANIFEST)
                .set(BCCP_RELEASE_MANIFEST.BCCP_ID, InsertedBccpRecord.getBccpId())
                .set(BCCP_RELEASE_MANIFEST.BDT_ID, InsertedBccpRecord.getBdtId())
                .where(BCCP_RELEASE_MANIFEST.BCCP_RELEASE_MANIFEST_ID.eq(bccpReleaseManifestRecord.getBccpReleaseManifestId()))
                .execute();
    }

    public CcAsccpNodeDetail updateAsccpState(User user, long asccpManifestId, CcState ccState) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        ULong userId = ULong.valueOf(sessionService.userId(user));

        AsccpReleaseManifestRecord asccpReleaseManifestRecord = dslContext.selectFrom(ASCCP_RELEASE_MANIFEST)
                .where(ASCCP_RELEASE_MANIFEST.ASCCP_RELEASE_MANIFEST_ID.eq(ULong.valueOf(asccpManifestId))).fetchOne();

        AsccpRecord baseAsccpRecord = dslContext.selectFrom(ASCCP)
                .where(ASCCP.ASCCP_ID.eq(asccpReleaseManifestRecord.getAsccpId())).fetchOne();

        baseAsccpRecord.set(ASCCP.ASCCP_ID, null);
        baseAsccpRecord.set(ASCCP.STATE, ccState.getValue());
        baseAsccpRecord.set(ASCCP.LAST_UPDATED_BY, userId);
        baseAsccpRecord.set(ASCCP.LAST_UPDATE_TIMESTAMP, timestamp);
        baseAsccpRecord.set(ASCCP.REVISION_ACTION, (byte) RevisionAction.Update.getValue());
        baseAsccpRecord.set(ASCCP.REVISION_TRACKING_NUM, baseAsccpRecord.getRevisionTrackingNum() + 1);

        AsccpRecord InsertedAsccpRecord = dslContext.insertInto(ASCCP)
                .set(baseAsccpRecord).returning().fetchOne();

        dslContext.update(ASCCP_RELEASE_MANIFEST)
                .set(ASCCP_RELEASE_MANIFEST.ASCCP_ID, InsertedAsccpRecord.getAsccpId())
                .where(ASCCP_RELEASE_MANIFEST.ASCCP_RELEASE_MANIFEST_ID.eq(asccpReleaseManifestRecord.getAsccpReleaseManifestId()))
                .execute();

        CcAsccpNode ccAsccpNode = getAsccpNodeByAsccpManifestId(user, asccpReleaseManifestRecord.getAsccpReleaseManifestId().longValue());
        return getAsccpNodeDetail(user, ccAsccpNode);
    }

    public CcBccpNodeDetail updateBccpState(User user, long bccpManifestId, CcState ccState) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        ULong userId = ULong.valueOf(sessionService.userId(user));

        BccpReleaseManifestRecord bccpReleaseManifestRecord = dslContext.selectFrom(BCCP_RELEASE_MANIFEST)
                .where(BCCP_RELEASE_MANIFEST.BCCP_RELEASE_MANIFEST_ID.eq(ULong.valueOf(bccpManifestId))).fetchOne();

        BccpRecord baseBccpRecord = dslContext.selectFrom(BCCP)
                .where(BCCP.BCCP_ID.eq(bccpReleaseManifestRecord.getBccpId())).fetchOne();

        baseBccpRecord.set(BCCP.BCCP_ID, null);
        baseBccpRecord.set(BCCP.STATE, ccState.getValue());
        baseBccpRecord.set(BCCP.LAST_UPDATED_BY, userId);
        baseBccpRecord.set(BCCP.LAST_UPDATE_TIMESTAMP, timestamp);
        baseBccpRecord.set(BCCP.REVISION_ACTION, RevisionAction.Update.getValue());
        baseBccpRecord.set(BCCP.REVISION_TRACKING_NUM, baseBccpRecord.getRevisionTrackingNum() + 1);

        BccpRecord InsertedBccpRecord = dslContext.insertInto(BCCP)
                .set(baseBccpRecord).returning().fetchOne();

        dslContext.update(BCCP_RELEASE_MANIFEST)
                .set(BCCP_RELEASE_MANIFEST.BCCP_ID, InsertedBccpRecord.getBccpId())
                .where(BCCP_RELEASE_MANIFEST.BCCP_RELEASE_MANIFEST_ID.eq(bccpReleaseManifestRecord.getBccpReleaseManifestId()))
                .execute();

        CcBccpNode ccBccpNode = getBccpNodeByBccpManifestId(user, bccpReleaseManifestRecord.getBccpReleaseManifestId().longValue());
        return getBccpNodeDetail(user, ccBccpNode);
    }
}

