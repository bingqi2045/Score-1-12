package org.oagi.srt.gateway.http.api.cc_management.repository;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jooq.DSLContext;
import org.jooq.Record11;
import org.jooq.Record4;
import org.jooq.SelectOnConditionStep;
import org.jooq.types.ULong;
import org.oagi.srt.data.AppUser;
import org.oagi.srt.data.BCCEntityType;
import org.oagi.srt.data.OagisComponentType;
import org.oagi.srt.data.SeqKeySupportable;
import org.oagi.srt.entity.jooq.tables.records.*;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;
import org.oagi.srt.gateway.http.api.cc_management.data.node.*;
import org.oagi.srt.gateway.http.api.common.data.AccessPrivilege;
import org.oagi.srt.gateway.http.api.common.data.TrackableImpl;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.oagi.srt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
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
    private ManifestRepository manifestRepository;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private UserRepository userRepository;

    private SelectOnConditionStep<Record11<
            ULong, String, String, ULong, Integer,
            String, String, ULong, ULong,
            ULong, ULong>> getSelectJoinStepForAccNode() {
        return dslContext.select(
                ACC.ACC_ID,
                ACC.GUID,
                ACC.DEN.as("name"),
                ACC_MANIFEST.BASED_ACC_MANIFEST_ID,
                ACC.OAGIS_COMPONENT_TYPE,
                ACC.OBJECT_CLASS_TERM,
                ACC.STATE,
                ACC_MANIFEST.REVISION_ID,
                ACC_MANIFEST.RELEASE_ID,
                ACC.OWNER_USER_ID,
                ACC_MANIFEST.ACC_MANIFEST_ID.as("manifest_id"))
                .from(ACC)
                .join(ACC_MANIFEST)
                .on(ACC.ACC_ID.eq(ACC_MANIFEST.ACC_ID));
    }

    public CcAccNode getAccNodeByAccId(User user, long accId, long releaseId) {
        AccManifestRecord accManifestRecord =
                dslContext.selectFrom(ACC_MANIFEST)
                        .where(and(
                                ACC_MANIFEST.ACC_ID.eq(ULong.valueOf(accId)),
                                ACC_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId))
                        ))
                        .fetchOne();

        return getAccNodeByAccManifestId(user, accManifestRecord.getAccManifestId().toBigInteger());
    }

    public CcAccNode getAccNodeByAccManifestId(User user, BigInteger accManifestId) {
        CcAccNode accNode = getSelectJoinStepForAccNode()
                .where(ACC_MANIFEST.ACC_MANIFEST_ID.eq(ULong.valueOf(accManifestId)))
                .fetchOneInto(CcAccNode.class);
        return arrangeAccNode(user, accNode);
    }

    public CcAccNode getAccNodeFromAsccByAsccpId(User user, long toAsccpId, ULong releaseId) {
        CcAsccNode asccNode = dslContext.select(
                ASCC.ASCC_ID,
                ASCC_MANIFEST.FROM_ACC_MANIFEST_ID,
                ASCC.SEQ_KEY,
                ASCC_MANIFEST.REVISION_ID,
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
                .where(ACC_MANIFEST.ACC_MANIFEST_ID.eq(ULong.valueOf(asccNode.getFromAccManifestId())))
                .fetchOneInto(AccManifestRecord.class);

        return getAccNodeByAccManifestId(user, accManifestRecord.getAccManifestId().toBigInteger());
    }

    private CcAccNode arrangeAccNode(User user, CcAccNode accNode) {
        OagisComponentType oagisComponentType =
                OagisComponentType.valueOf(accNode.getOagisComponentType());
        accNode.setGroup(oagisComponentType.isGroup());
        accNode.setAccess(getAccess(accNode, user));
        accNode.setHasChild(hasChild(accNode));

        return accNode;
    }

    private boolean hasChild(CcAccNode ccAccNode) {
        if (ccAccNode.getBasedAccManifestId() != null) {
            return true;
        }
        if (ccAccNode.getManifestId() == 0) {
            return false;
        }
        long asccCount = dslContext.selectCount()
                .from(ASCC_MANIFEST)
                .join(ASCC).on(ASCC_MANIFEST.ASCC_ID.eq(ASCC.ASCC_ID))
                .where(and(ASCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(ULong.valueOf(ccAccNode.getManifestId())),
                        ASCC.STATE.notEqual(CcState.Deleted.name())))
                .fetchOneInto(long.class);
        if (asccCount > 0) {
            return true;
        }

        long bccCount = dslContext.selectCount()
                .from(BCC_MANIFEST)
                .join(BCC).on(BCC_MANIFEST.BCC_ID.eq(BCC.BCC_ID))
                .where(and(BCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(ULong.valueOf(ccAccNode.getManifestId())),
                        BCC.STATE.notEqual(CcState.Deleted.name())))
                .fetchOneInto(long.class);
        return bccCount > 0;
    }

    public CcAsccpNode getAsccpNodeByAsccpManifestId(User user, BigInteger manifestId) {
        CcAsccpNode asccpNode = dslContext.select(
                ASCCP.ASCCP_ID,
                ASCCP.GUID,
                ASCCP.PROPERTY_TERM.as("name"),
                ACC_MANIFEST.ACC_ID.as("role_of_acc_id"),
                ASCCP.STATE,
                REVISION.REVISION_NUM,
                REVISION.REVISION_TRACKING_NUM,
                ASCCP_MANIFEST.RELEASE_ID,
                ASCCP_MANIFEST.ASCCP_MANIFEST_ID.as("manifest_id"),
                ASCCP.OWNER_USER_ID,
                ASCCP.PREV_ASCCP_ID,
                ASCCP.NEXT_ASCCP_ID)
                .from(ASCCP)
                .join(ASCCP_MANIFEST)
                .on(ASCCP.ASCCP_ID.eq(ASCCP_MANIFEST.ASCCP_ID))
                .join(REVISION)
                .on(ASCCP_MANIFEST.REVISION_ID.eq(REVISION.REVISION_ID))
                .join(ACC_MANIFEST)
                .on(ASCCP_MANIFEST.ROLE_OF_ACC_MANIFEST_ID.eq(ACC_MANIFEST.ACC_MANIFEST_ID))
                .where(ASCCP_MANIFEST.ASCCP_MANIFEST_ID.eq(ULong.valueOf(manifestId)))
                .fetchOneInto(CcAsccpNode.class);

        asccpNode.setAccess(getAccess(asccpNode, user));
        asccpNode.setHasChild(true); // role_of_acc_id must not be null.

        return asccpNode;
    }

    private String getAccess(CcNode ccNode, User user) {
        AccessPrivilege accessPrivilege = Prohibited;
        long userId = sessionService.userId(user);
        long ownerUserId = ccNode.getOwnerUserId();
        AppUser owner = userRepository.findById(ownerUserId);
        AppUser currentUser = userRepository.findById(userId);
        switch (ccNode.getState()) {
            case WIP:
                if (userId == ownerUserId) {
                    accessPrivilege = CanEdit;
                } else {
                    accessPrivilege = Prohibited;
                }
                break;
            case Draft:
            case QA:
            case Production:
            case Candidate:
            case Published:
                if (owner.isDeveloper() == currentUser.isDeveloper()) {
                    accessPrivilege = CanMove;
                } else {
                    accessPrivilege = CanView;
                }
                break;
            case Deleted:
                accessPrivilege = CanMove;
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
                REVISION.REVISION_NUM,
                REVISION.REVISION_TRACKING_NUM,
                ASCCP_MANIFEST.RELEASE_ID,
                ASCCP.PREV_ASCCP_ID,
                ASCCP.NEXT_ASCCP_ID)
                .from(ASCCP)
                .join(ASCCP_MANIFEST)
                .on(ASCCP.ASCCP_ID.eq(ASCCP_MANIFEST.ASCCP_ID))
                .join(REVISION)
                .on(ASCCP_MANIFEST.REVISION_ID.eq(REVISION.REVISION_ID))
                .join(ACC_MANIFEST)
                .on(ASCCP_MANIFEST.ROLE_OF_ACC_MANIFEST_ID.eq(ACC_MANIFEST.ACC_MANIFEST_ID))
                .where(and(
                        ACC_MANIFEST.ACC_ID.eq(ULong.valueOf(roleOfAccId)),
                        ASCCP_MANIFEST.RELEASE_ID.eq(releaseId)
                ))
                .fetchOneInto(CcAsccpNode.class);
        if (asccpNode == null) {
            return null;
        }
        asccpNode.setState(asccpNode.getState());
        asccpNode.setHasChild(true); // role_of_acc_id must not be null.

        return asccpNode;
    }

    public CcBccpNode getBccpNodeByBccpManifestId(User user, BigInteger manifestId) {
        CcBccpNode bccpNode = dslContext.select(
                BCCP.BCCP_ID,
                BCCP.GUID,
                BCCP.PROPERTY_TERM.as("name"),
                DT_MANIFEST.DT_ID.as("bdt_id"),
                BCCP.STATE,
                REVISION.REVISION_NUM,
                REVISION.REVISION_TRACKING_NUM,
                BCCP_MANIFEST.RELEASE_ID,
                BCCP_MANIFEST.BCCP_MANIFEST_ID.as("manifest_id"),
                BCCP.OWNER_USER_ID,
                BCCP.PREV_BCCP_ID,
                BCCP.NEXT_BCCP_ID)
                .from(BCCP)
                .join(BCCP_MANIFEST)
                .on(BCCP.BCCP_ID.eq(BCCP_MANIFEST.BCCP_ID))
                .join(REVISION)
                .on(BCCP_MANIFEST.REVISION_ID.eq(REVISION.REVISION_ID))
                .join(DT_MANIFEST)
                .on(BCCP_MANIFEST.BDT_MANIFEST_ID.eq(DT_MANIFEST.DT_MANIFEST_ID))
                .where(BCCP_MANIFEST.BCCP_MANIFEST_ID.eq(ULong.valueOf(manifestId)))
                .fetchOneInto(CcBccpNode.class);
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

        Long releaseId = accNode.getReleaseId();

        if (accManifestRecord.getBasedAccManifestId() != null) {
            CcAccNode basedAccNode = getAccNodeByAccManifestId(user, accManifestRecord.getBasedAccManifestId().toBigInteger());
            descendants.add(basedAccNode);
        }
        List<SeqKeySupportable> seqKeySupportableList = new ArrayList();
        seqKeySupportableList.addAll(
                getAsccpNodes(user, accManifestRecord.getAccManifestId().longValue())
        );
        seqKeySupportableList.addAll(
                getBccpNodes(user, accManifestRecord.getAccManifestId().longValue())
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

    private List<CcAsccpNode> getAsccpNodes(User user, long fromAccManifestId) {
        List<CcAsccNode> asccNodes = dslContext.select(
                ASCC.ASCC_ID,
                ASCC_MANIFEST.ASCC_MANIFEST_ID.as("manifest_id"),
                ASCC_MANIFEST.FROM_ACC_MANIFEST_ID,
                ASCC_MANIFEST.TO_ASCCP_MANIFEST_ID,
                ASCC.GUID,
                ASCC.SEQ_KEY,
                ASCC.STATE.as("raw_state"),
                REVISION.REVISION_NUM,
                REVISION.REVISION_TRACKING_NUM,
                ASCC_MANIFEST.RELEASE_ID)
                .from(ASCC)
                .join(ASCC_MANIFEST)
                .on(ASCC.ASCC_ID.eq(ASCC_MANIFEST.ASCC_ID))
                .join(REVISION)
                .on(ASCC_MANIFEST.REVISION_ID.eq(REVISION.REVISION_ID))
                .where(ASCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(ULong.valueOf(fromAccManifestId)))
                .fetchInto(CcAsccNode.class);

        if (asccNodes.isEmpty()) {
            return Collections.emptyList();
        }

        return asccNodes.stream().map(asccNode -> {
            ULong manifestId =
                    dslContext.select(ASCCP_MANIFEST.ASCCP_MANIFEST_ID)
                            .from(ASCCP_MANIFEST)
                            .where(ASCCP_MANIFEST.ASCCP_MANIFEST_ID.eq(ULong.valueOf(asccNode.getToAsccpManifestId())))
                            .fetchOneInto(ULong.class);

            CcAsccpNode asccpNode =
                    getAsccpNodeByAsccpManifestId(user, manifestId.toBigInteger());
            asccpNode.setSeqKey(asccNode.getSeqKey());
            asccpNode.setAsccId(asccNode.getAsccId());
            asccpNode.setAsccManifestId(asccNode.getManifestId());
            return asccpNode;
        }).collect(Collectors.toList());
    }

    private List<CcBccpNode> getBccpNodes(User user, long fromAccManifestId) {
        List<CcBccNode> bccNodes = dslContext.select(
                BCC.BCC_ID,
                BCC_MANIFEST.BCC_MANIFEST_ID.as("manifest_id"),
                BCC.GUID,
                BCC_MANIFEST.FROM_ACC_MANIFEST_ID,
                BCC_MANIFEST.TO_BCCP_MANIFEST_ID,
                BCC.SEQ_KEY,
                BCC.ENTITY_TYPE,
                BCC.STATE.as("raw_state"),
                REVISION.REVISION_NUM,
                REVISION.REVISION_TRACKING_NUM,
                BCC_MANIFEST.RELEASE_ID)
                .from(BCC)
                .join(BCC_MANIFEST)
                .on(BCC.BCC_ID.eq(BCC_MANIFEST.BCC_ID))
                .join(REVISION)
                .on(BCC_MANIFEST.REVISION_ID.eq(REVISION.REVISION_ID))
                .join(BCCP_MANIFEST)
                .on(BCC_MANIFEST.TO_BCCP_MANIFEST_ID.eq(BCCP_MANIFEST.BCCP_MANIFEST_ID))
                .where(BCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(ULong.valueOf(fromAccManifestId)))
                .fetchInto(CcBccNode.class);

        if (bccNodes.isEmpty()) {
            return Collections.emptyList();
        }

        return bccNodes.stream().map(bccNode -> {
            ULong manifestId =
                    dslContext.select(BCCP_MANIFEST.BCCP_MANIFEST_ID)
                            .from(BCCP_MANIFEST)
                            .where(BCCP_MANIFEST.BCCP_MANIFEST_ID.eq((ULong.valueOf(bccNode.getToBccpManifestId()))))
                            .fetchOneInto(ULong.class);

            CcBccpNode bccpNode = getBccpNodeByBccpManifestId(user, manifestId.toBigInteger());
            bccpNode.setSeqKey(bccNode.getSeqKey());
            bccpNode.setAttribute(BCCEntityType.valueOf(bccNode.getEntityType()) == Attribute);
            bccpNode.setBccId(bccNode.getBccId());
            bccpNode.setBccManifestId(bccNode.getManifestId());
            return bccpNode;
        }).collect(Collectors.toList());
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
                ACC.NAMESPACE_ID,
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
            asccpNodeDetail.setAscc(ascc);
        }

        long asccpManifestIdId = asccpNode.getManifestId();
        CcAsccpNodeDetail.Asccp asccp = dslContext.select(
                ASCCP_MANIFEST.ASCCP_MANIFEST_ID.as("manifest_id"),
                ASCCP.ASCCP_ID,
                ASCCP.GUID,
                ASCCP.PROPERTY_TERM,
                ASCCP.DEN,
                ASCCP.NAMESPACE_ID,
                ASCCP.REUSABLE_INDICATOR.as("reusable"),
                ASCCP.IS_DEPRECATED.as("deprecated"),
                ASCCP.IS_NILLABLE.as("nillable"),
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
                    BCC_MANIFEST.BCC_MANIFEST_ID.as("manifest_id"))
                    .from(BCC_MANIFEST)
                    .join(BCC)
                    .on(BCC_MANIFEST.BCC_ID.eq(BCC.BCC_ID))
                    .where(BCC_MANIFEST.BCC_MANIFEST_ID.eq(ULong.valueOf(bccManifestId)))
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
                BCCP.NAMESPACE_ID,
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
        long manifestId = bdtScNode.getManifestId();
        return dslContext.select(
                DT_SC_MANIFEST.DT_SC_MANIFEST_ID.as("manifestId"),
                DT_SC.DT_SC_ID.as("bdt_sc_id"),
                DT_SC.GUID,
                concat(DT_SC.PROPERTY_TERM, val(". "), DT_SC.PROPERTY_TERM).as("den"),
                DT_SC.CARDINALITY_MIN,
                DT_SC.CARDINALITY_MAX,
                DT_SC.DEFINITION,
                DT_SC.DEFINITION_SOURCE,
                DT_SC.DEFAULT_VALUE,
                DT_SC.FIXED_VALUE).from(DT_SC_MANIFEST)
                .join(DT_SC).on(DT_SC_MANIFEST.DT_SC_ID.eq(DT_SC.DT_SC_ID))
                .where(DT_SC_MANIFEST.DT_SC_MANIFEST_ID.eq(ULong.valueOf(manifestId)))
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

    public boolean isAccManifestUsed(long accManifestId) {
        int cnt = dslContext.selectCount()
                .from(ASCCP_MANIFEST)
                .join(ACC_MANIFEST)
                .on(ASCCP_MANIFEST.ROLE_OF_ACC_MANIFEST_ID.eq(ACC_MANIFEST.ACC_MANIFEST_ID))
                .where(ACC_MANIFEST.ACC_MANIFEST_ID.eq(ULong.valueOf(accManifestId)))
                .fetchOptionalInto(Integer.class).orElse(0);
        if (cnt > 0) {
            return true;
        }

        cnt = dslContext.selectCount()
                .from(ACC_MANIFEST)
                .join(ACC_MANIFEST.as("base"))
                .on(ACC_MANIFEST.BASED_ACC_MANIFEST_ID.eq(ACC_MANIFEST.as("base").ACC_MANIFEST_ID))
                .where(ACC_MANIFEST.as("base").ACC_MANIFEST_ID.eq(ULong.valueOf(accManifestId)))
                .fetchOptionalInto(Integer.class).orElse(0);
        if (cnt > 0) {
            return true;
        }

        cnt = dslContext.selectCount()
                .from(ASCC_MANIFEST)
                .join(ACC_MANIFEST)
                .on(ASCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(ACC_MANIFEST.ACC_MANIFEST_ID))
                .where(ACC_MANIFEST.ACC_MANIFEST_ID.eq(ULong.valueOf(accManifestId)))
                .fetchOptionalInto(Integer.class).orElse(0);
        if (cnt > 0) {
            return true;
        }

        cnt = dslContext.selectCount()
                .from(BCC_MANIFEST)
                .join(ACC_MANIFEST)
                .on(BCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(ACC_MANIFEST.ACC_MANIFEST_ID))
                .where(ACC_MANIFEST.ACC_MANIFEST_ID.eq(ULong.valueOf(accManifestId)))
                .fetchOptionalInto(Integer.class).orElse(0);
        if (cnt > 0) {
            return true;
        }

        cnt = dslContext.selectCount()
                .from(ABIE)
                .where(ABIE.BASED_ACC_MANIFEST_ID.eq(ULong.valueOf(accManifestId)))
                .fetchOptionalInto(Integer.class).orElse(0);
        if (cnt > 0) {
            return true;
        }

        return false;
    }

    public boolean isAsccpManifestUsed(long asccpManifestId) {
        int cnt = dslContext.selectCount()
                .from(ASCC_MANIFEST)
                .join(ASCCP_MANIFEST)
                .on(ASCC_MANIFEST.TO_ASCCP_MANIFEST_ID.eq(ASCCP_MANIFEST.ASCCP_MANIFEST_ID))
                .where(ASCCP_MANIFEST.ASCCP_MANIFEST_ID.eq(ULong.valueOf(asccpManifestId)))
                .fetchOptionalInto(Integer.class).orElse(0);
        if (cnt > 0) {
            return true;
        }

        cnt = dslContext.selectCount()
                .from(ASBIEP)
                .where(ASBIEP.BASED_ASCCP_MANIFEST_ID.eq(ULong.valueOf(asccpManifestId)))
                .fetchOptionalInto(Integer.class).orElse(0);
        if (cnt > 0) {
            return true;
        }

        return false;
    }

    public boolean isBccpManifestUsed(long bccpManifestId) {
        int cnt = dslContext.selectCount()
                .from(BCC_MANIFEST)
                .join(BCCP_MANIFEST)
                .on(BCC_MANIFEST.TO_BCCP_MANIFEST_ID.eq(BCCP_MANIFEST.BCCP_MANIFEST_ID))
                .where(BCCP_MANIFEST.BCCP_MANIFEST_ID.eq(ULong.valueOf(bccpManifestId)))
                .fetchOptionalInto(Integer.class).orElse(0);
        if (cnt > 0) {
            return true;
        }

        cnt = dslContext.selectCount()
                .from(BBIEP)
                .where(BBIEP.BASED_BCCP_MANIFEST_ID.eq(ULong.valueOf(bccpManifestId)))
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

    private void insert(AccRecord accRecord) {
        ULong prevAccId = accRecord.getAccId();
        accRecord.setAccId(null);
        accRecord.setPrevAccId(prevAccId);
        accRecord.insert();

        ULong nextAccId = accRecord.getAccId();
        dslContext.update(ACC)
                .set(ACC.NEXT_ACC_ID, nextAccId)
                .where(ACC.ACC_ID.eq(prevAccId))
                .execute();
    }

    private void insert(AsccpRecord asccpRecord) {
        ULong prevAsccpId = asccpRecord.getAsccpId();
        asccpRecord.setAsccpId(null);
        asccpRecord.setPrevAsccpId(prevAsccpId);
        asccpRecord.insert();

        ULong nextAsccpId = asccpRecord.getAsccpId();
        dslContext.update(ASCCP)
                .set(ASCCP.NEXT_ASCCP_ID, nextAsccpId)
                .where(ASCCP.ASCCP_ID.eq(prevAsccpId))
                .execute();
    }

    private void insert(BccpRecord bccpRecord) {
        ULong prevBccpId = bccpRecord.getBccpId();
        bccpRecord.setBccpId(null);
        bccpRecord.setPrevBccpId(prevBccpId);
        bccpRecord.insert();

        ULong nextBccpId = bccpRecord.getBccpId();
        dslContext.update(BCCP)
                .set(BCCP.NEXT_BCCP_ID, nextBccpId)
                .where(BCCP.BCCP_ID.eq(prevBccpId))
                .execute();
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

        CcAccNode accNode = getAccNodeByAccManifestId(user, accManifest.getAccManifestId().toBigInteger());
        OagisComponentType oagisComponentType = getOagisComponentTypeByAccId(accNode.getAccId());
        if (oagisComponentType.isGroup()) {
            CcAsccpNode roleByAsccpNode = getAsccpNodeByRoleOfAccId(accNode.getAccId(), accManifest.getReleaseId());
            if (roleByAsccpNode == null) {
                return;
            }
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
}