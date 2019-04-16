package org.oagi.srt.gateway.http.api.cc_management.repository;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jooq.DSLContext;
import org.jooq.Record11;
import org.jooq.SelectJoinStep;
import org.jooq.types.ULong;
import org.oagi.srt.data.BCCEntityType;
import org.oagi.srt.data.OagisComponentType;
import org.oagi.srt.data.SeqKeySupportable;
import org.oagi.srt.entity.jooq.Tables;
import org.oagi.srt.gateway.http.api.bie_management.service.BieRepository;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;
import org.oagi.srt.gateway.http.api.cc_management.data.node.*;
import org.oagi.srt.gateway.http.api.cc_management.helper.CcUtility;
import org.oagi.srt.gateway.http.api.common.data.TrackableImpl;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static org.jooq.impl.DSL.*;
import static org.oagi.srt.data.BCCEntityType.Attribute;
import static org.oagi.srt.gateway.http.helper.SrtJdbcTemplate.newSqlParameterSource;

@Repository
public class CcNodeRepository {

    @Autowired
    private SrtJdbcTemplate jdbcTemplate;

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private BieRepository bieRepository;

    private SelectJoinStep<Record11<
            ULong, String, String, ULong, Integer,
            String, Integer, Integer, Integer, ULong,
            ULong>> getSelectJoinStepForAccNode() {
        return dslContext.select(
                Tables.ACC.ACC_ID,
                Tables.ACC.GUID,
                Tables.ACC.DEN.as("name"),
                Tables.ACC.BASED_ACC_ID,
                Tables.ACC.OAGIS_COMPONENT_TYPE,
                Tables.ACC.OBJECT_CLASS_TERM,
                Tables.ACC.STATE.as("raw_state"),
                Tables.ACC.REVISION_NUM,
                Tables.ACC.REVISION_TRACKING_NUM,
                Tables.ACC.RELEASE_ID,
                Tables.ACC.CURRENT_ACC_ID
        ).from(Tables.ACC);
    }
    public CcAccNode getAccNodeByAccId(long accId, Long releaseId) {
        CcAccNode accNode = getSelectJoinStepForAccNode()
                .where(Tables.ACC.ACC_ID.eq(ULong.valueOf(accId)))
                .fetchOneInto(CcAccNode.class);
        return arrangeAccNode(accNode, releaseId);
    }

    public CcAccNode getAccNodeByCurrentAccId(long currentAccId, Long releaseId) {
        List<CcAccNode> accNodes = getSelectJoinStepForAccNode()
                .where(Tables.ACC.CURRENT_ACC_ID.eq(ULong.valueOf(currentAccId)))
                .fetchInto(CcAccNode.class);

        CcAccNode accNode = CcUtility.getLatestEntity(releaseId, accNodes);
        if (accNode == null) {
            throw new IllegalStateException();
        }
        return arrangeAccNode(accNode, releaseId);
    }

    public CcAccNode getAccNodeFromAsccByAsccpId(long toAsccpId, Long releaseId) {
        List<CcAsccNode> asccNodes = dslContext.select(
                Tables.ASCC.FROM_ACC_ID,
                Tables.ASCC.SEQ_KEY,
                Tables.ASCC.REVISION_NUM,
                Tables.ASCC.REVISION_TRACKING_NUM,
                Tables.ASCC.RELEASE_ID
        ).from(Tables.ASCC).where(Tables.ASCC.TO_ASCCP_ID.eq(ULong.valueOf(toAsccpId)))
                .fetchInto(CcAsccNode.class);

        CcAsccNode asccNode = CcUtility.getLatestEntity(releaseId, asccNodes);
        return getAccNodeByCurrentAccId(asccNode.getFromAccId(), releaseId);
    }

    public long createAcc(User user, CcAccNode ccAccNode) {
        SimpleJdbcInsert jdbcInsert = jdbcTemplate.insert()
                .withTableName("acc")
                .usingColumns("guid", "object_class_term", "den", "owner_user_id", "definition", "oagis_component_type",
                        "namespace_id", "created_by", "last_updated_by", "creation_timestamp", "last_update_timestamp", "state")
                .usingGeneratedKeyColumns("acc_id");

        long userId = sessionService.userId(user);
        Date timestamp = new Date();
        MapSqlParameterSource parameterSource = newSqlParameterSource()
                .addValue("guid", ccAccNode.getGuid())
                .addValue("object_class_term", ccAccNode.getObjectClassTerm())
                .addValue("oagis_component_type", ccAccNode.getOagisComponentType())
                .addValue("den", ccAccNode.getDen())
                .addValue("namespace_id", 1)
                .addValue("owner_user_id", userId)
                .addValue("created_by", userId)
                .addValue("state", CcState.Editing.getValue())
                .addValue("last_updated_by", userId)
                .addValue("creation_timestamp", timestamp)
                .addValue("definition", ccAccNode.getDefinition())
                .addValue("last_update_timestamp", timestamp);

        long accNode = jdbcInsert.executeAndReturnKey(parameterSource).longValue();
        return accNode;
    }

    public void updateAcc(User user, CcAccNode ccAccNode) {
        jdbcTemplate.update("UPDATE `acc` SET `definition` = :definition, `guid` = :guid, " +
                "`object_class_term` = :object_class_term, `den` = :den, `oagis_component_type` = :oagisComponentType, " +
                "`is_deprecated` = :isDeprecated, `is_abstract` = :isAbstract " +
                "WHERE acc_id = :acc_id", newSqlParameterSource()
                .addValue("acc_id", ccAccNode.getAccId())
                .addValue("guid", ccAccNode.getGuid())
                .addValue("object_class_term", ccAccNode.getObjectClassTerm())
                .addValue("den", ccAccNode.getDen())
                .addValue("isDeprecated", ccAccNode.isDeprecated())
                .addValue("isAbstract", ccAccNode.isAbstract())
                .addValue("definition", ccAccNode.getDefinition())
                .addValue("oagisComponentType", ccAccNode.getOagisComponentType()));
    }

    private CcAccNode arrangeAccNode(CcAccNode accNode, Long releaseId) {
        OagisComponentType oagisComponentType =
                OagisComponentType.valueOf(accNode.getOagisComponentType());
        accNode.setGroup(oagisComponentType.isGroup());

        accNode.setState(CcState.valueOf(accNode.getRawState()));
        accNode.setHasChild(hasChild(accNode, releaseId));

        return accNode;
    }

    private boolean hasChild(CcAccNode accNode, Long releaseId) {
        if (accNode.getBasedAccId() != null) {
            return true;
        } else {
            Long fromAccId = (releaseId == null || releaseId == 0L) ?
                    accNode.getAccId() : accNode.getCurrentAccId();
            if (fromAccId == null) {
                return false;
            }
            List<AsccForAccHasChild> asccList = dslContext.select(
                    Tables.ASCC.ASCC_ID,
                    Tables.ASCC.CURRENT_ASCC_ID,
                    Tables.ASCC.GUID,
                    Tables.ASCC.REVISION_NUM,
                    Tables.ASCC.REVISION_TRACKING_NUM,
                    Tables.ASCC.RELEASE_ID
            ).from(Tables.ASCC).where(Tables.ASCC.FROM_ACC_ID.eq(ULong.valueOf(fromAccId)))
                    .fetchInto(AsccForAccHasChild.class);

            long asccCount = asccList.stream().collect(groupingBy(e -> e.getGuid())).values().stream()
                    .map(entities -> CcUtility.getLatestEntity(releaseId, entities))
                    .count();
            if (asccCount > 0L) {
                return true;
            }

            List<BccForAccHasChild> bccList = dslContext.select(
                    Tables.BCC.BCC_ID,
                    Tables.BCC.CURRENT_BCC_ID,
                    Tables.BCC.GUID,
                    Tables.BCC.REVISION_NUM,
                    Tables.BCC.REVISION_TRACKING_NUM,
                    Tables.BCC.RELEASE_ID
            ).from(Tables.BCC).where(Tables.BCC.FROM_ACC_ID.eq(ULong.valueOf(fromAccId)))
                    .fetchInto(BccForAccHasChild.class);

            long bccCount = bccList.stream().collect(groupingBy(e -> e.getGuid())).values().stream()
                    .map(entities -> CcUtility.getLatestEntity(releaseId, entities))
                    .count();
            return (bccCount > 0L);
        }
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class AsccForAccHasChild extends TrackableImpl {
        private long asccId;
        private Long currentAsccId;
        private String guid;

        @Override
        public long getId() {
            return asccId;
        }

        @Override
        public Long getCurrentId() {
            return currentAsccId;
        }
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class BccForAccHasChild extends TrackableImpl {
        private long bccId;
        private Long currentBccId;
        private String guid;

        @Override
        public long getId() {
            return bccId;
        }

        @Override
        public Long getCurrentId() {
            return currentBccId;
        }
    }

    public CcAsccpNode getAsccpNodeByAsccpId(long asccpId, Long releaseId) {
        CcAsccpNode asccpNode = dslContext.select(
                Tables.ASCCP.ASCCP_ID,
                Tables.ASCCP.CURRENT_ASCCP_ID,
                Tables.ASCCP.GUID,
                Tables.ASCCP.PROPERTY_TERM.as("name"),
                Tables.ASCCP.ROLE_OF_ACC_ID,
                Tables.ASCCP.STATE.as("raw_state"),
                Tables.ASCCP.REVISION_NUM,
                Tables.ASCCP.REVISION_TRACKING_NUM,
                Tables.ASCCP.RELEASE_ID).from(Tables.ASCCP)
                .where(Tables.ASCCP.ASCCP_ID.eq(ULong.valueOf(asccpId)))
                .fetchOneInto(CcAsccpNode.class);

        asccpNode.setHasChild(true); // role_of_acc_id must not be null.

        return asccpNode;
    }

    public CcAsccpNode getAsccpNodeByCurrentAsccpId(long currentAsccpId, Long releaseId) {
        List<CcAsccpNode> asccpNodes = dslContext.select(
                Tables.ASCCP.ASCCP_ID,
                Tables.ASCCP.CURRENT_ASCCP_ID,
                Tables.ASCCP.GUID,
                Tables.ASCCP.PROPERTY_TERM.as("name"),
                Tables.ASCCP.ROLE_OF_ACC_ID,
                Tables.ASCCP.STATE.as("raw_state"),
                Tables.ASCCP.REVISION_NUM,
                Tables.ASCCP.REVISION_TRACKING_NUM,
                Tables.ASCCP.RELEASE_ID).from(Tables.ASCCP)
                .where(Tables.ASCCP.CURRENT_ASCCP_ID.eq(ULong.valueOf(currentAsccpId)))
                .fetchInto(CcAsccpNode.class);

        CcAsccpNode asccpNode = CcUtility.getLatestEntity(releaseId, asccpNodes);
        asccpNode.setHasChild(true); // role_of_acc_id must not be null.

        return asccpNode;
    }

    public CcAsccpNode getAsccpNodeByRoleOfAccId(long roleOfAccId, Long releaseId) {
        List<CcAsccpNode> asccpNodes = dslContext.select(
                Tables.ASCCP.ASCCP_ID,
                Tables.ASCCP.CURRENT_ASCCP_ID,
                Tables.ASCCP.GUID,
                Tables.ASCCP.PROPERTY_TERM.as("name"),
                Tables.ASCCP.STATE.as("raw_state"),
                Tables.ASCCP.REVISION_NUM,
                Tables.ASCCP.REVISION_TRACKING_NUM,
                Tables.ASCCP.RELEASE_ID).from(Tables.ASCCP)
                .where(Tables.ASCCP.ROLE_OF_ACC_ID.eq(ULong.valueOf(roleOfAccId)))
                .fetchInto(CcAsccpNode.class);

        CcAsccpNode asccpNode = CcUtility.getLatestEntity(releaseId, asccpNodes);
        asccpNode.setHasChild(true); // role_of_acc_id must not be null.

        return asccpNode;
    }

    public CcBccpNode getBccpNodeByBccpId(long bccpId, Long releaseId) {
        CcBccpNode bccpNode = dslContext.select(
                Tables.BCCP.BCCP_ID,
                Tables.BCCP.CURRENT_BCCP_ID,
                Tables.BCCP.GUID,
                Tables.BCCP.PROPERTY_TERM.as("name"),
                Tables.BCCP.BDT_ID,
                Tables.BCCP.STATE.as("raw_state"),
                Tables.BCCP.REVISION_NUM,
                Tables.BCCP.REVISION_TRACKING_NUM,
                Tables.BCCP.RELEASE_ID).from(Tables.BCCP)
                .where(Tables.BCCP.BCCP_ID.eq(ULong.valueOf(bccpId)))
                .fetchOneInto(CcBccpNode.class);

        bccpNode.setHasChild(hasChild(bccpNode));

        return bccpNode;
    }

    public CcBccpNode getBccpNodeByCurrentBccpId(long currentBccpId, Long releaseId) {
        List<CcBccpNode> bccpNodes = dslContext.select(
                Tables.BCCP.BCCP_ID,
                Tables.BCCP.CURRENT_BCCP_ID,
                Tables.BCCP.GUID,
                Tables.BCCP.PROPERTY_TERM.as("name"),
                Tables.BCCP.BDT_ID,
                Tables.BCCP.STATE.as("raw_state"),
                Tables.BCCP.REVISION_NUM,
                Tables.BCCP.REVISION_TRACKING_NUM,
                Tables.BCCP.RELEASE_ID).from(Tables.BCCP)
                .where(Tables.BCCP.CURRENT_BCCP_ID.eq(ULong.valueOf(currentBccpId)))
                .fetchInto(CcBccpNode.class);

        CcBccpNode bccpNode = CcUtility.getLatestEntity(releaseId, bccpNodes);
        bccpNode.setHasChild(hasChild(bccpNode));
        return bccpNode;
    }

    private boolean hasChild(CcBccpNode bccpNode) {
        long bdtId = bccpNode.getBdtId();
        int dtScCount = dslContext.selectCount().from(Tables.DT_SC)
                .where(and(
                        Tables.DT_SC.OWNER_DT_ID.eq(ULong.valueOf(bdtId)),
                        or(
                                Tables.DT_SC.CARDINALITY_MIN.ne(0),
                                Tables.DT_SC.CARDINALITY_MAX.ne(0)
                        ))).fetchOneInto(Integer.class);
        return (dtScCount > 0);
    }

    public List<? extends CcNode> getDescendants(User user, CcAccNode accNode) {
        List<CcNode> descendants = new ArrayList();

        Long basedAccId = dslContext.select(Tables.ACC.BASED_ACC_ID).from(Tables.ACC)
                .where(Tables.ACC.ACC_ID.eq(ULong.valueOf(accNode.getAccId())))
                .fetchOneInto(Long.class);
        if (basedAccId != null) {
            Long releaseId = accNode.getReleaseId();
            CcAccNode basedAccNode;
            if (releaseId == null) {
                basedAccNode = getAccNodeByAccId(basedAccId, releaseId);
            } else {
                basedAccNode = getAccNodeByCurrentAccId(basedAccId, releaseId);
            }
            descendants.add(basedAccNode);
        }

        Long releaseId = accNode.getReleaseId();
        long fromAccId;
        if (releaseId == null) {
            fromAccId = accNode.getAccId();
        } else {
            fromAccId = dslContext.select(Tables.ACC.CURRENT_ACC_ID).from(Tables.ACC)
                    .where(Tables.ACC.ACC_ID.eq(ULong.valueOf(accNode.getAccId())))
                    .fetchOneInto(Long.class);
        }

        boolean isUserExtensionGroup = accNode.getOagisComponentType() == OagisComponentType.UserExtensionGroup.getValue();
        List<SeqKeySupportable> seqKeySupportableList = new ArrayList();
        seqKeySupportableList.addAll(
                getAsccpNodes(user, fromAccId, (isUserExtensionGroup) ? null : releaseId)
        );
        seqKeySupportableList.addAll(
                getBccpNodes(user, fromAccId, (isUserExtensionGroup) ? null : releaseId)
        );
        seqKeySupportableList.sort(Comparator.comparingInt(SeqKeySupportable::getSeqKey));

        int seqKey = 1;
        for (SeqKeySupportable e : seqKeySupportableList) {
            if (e instanceof CcAsccpNode) {
                CcAsccpNode asccpNode = (CcAsccpNode) e;
                OagisComponentType oagisComponentType =
                        getOagisComponentTypeByAccId(asccpNode.getRoleOfAccId());
                if (oagisComponentType.isGroup()) {
                    CcAccNode roleOfAccNode;
                    if (releaseId == null) {
                        roleOfAccNode = getAccNodeByAccId(asccpNode.getRoleOfAccId(), releaseId);
                    } else {
                        roleOfAccNode = getAccNodeByCurrentAccId(asccpNode.getRoleOfAccId(), releaseId);
                    }
                    List<? extends CcNode> groupDescendants = getDescendants(user, roleOfAccNode);
                    for (CcNode groupNode : groupDescendants) {
                        ((SeqKeySupportable) groupNode).setSeqKey(seqKey++);
                    }
                    descendants.addAll(groupDescendants);
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
        int oagisComponentType = dslContext.select(Tables.ACC.OAGIS_COMPONENT_TYPE)
                .from(Tables.ACC).where(Tables.ACC.ACC_ID.eq(ULong.valueOf(accId)))
                .fetchOneInto(Integer.class);
        return OagisComponentType.valueOf(oagisComponentType);
    }

    private List<CcAsccpNode> getAsccpNodes(User user, long fromAccId, Long releaseId) {
        List<CcAsccNode> asccNodes = dslContext.select(
                Tables.ASCC.ASCC_ID,
                Tables.ASCC.CURRENT_ASCC_ID,
                Tables.ASCC.GUID,
                Tables.ASCC.TO_ASCCP_ID,
                Tables.ASCC.SEQ_KEY,
                Tables.ASCC.STATE.as("raw_state"),
                Tables.ASCC.REVISION_NUM,
                Tables.ASCC.REVISION_TRACKING_NUM,
                Tables.ASCC.RELEASE_ID
        ).from(Tables.ASCC).where(Tables.ASCC.FROM_ACC_ID.eq(ULong.valueOf(fromAccId)))
                .fetchInto(CcAsccNode.class);

        if (asccNodes.isEmpty()) {
            return Collections.emptyList();
        }

        asccNodes = asccNodes.stream()
                .collect(groupingBy(CcAsccNode::getGuid)).values().stream()
                .map(entities -> CcUtility.getLatestEntity(releaseId, entities))
                .collect(Collectors.toList());

        List<CcAsccpNode> asccpNodes = new ArrayList();
        for (CcAsccNode asccNode : asccNodes) {
            CcAsccpNode asccpNode;
            if (releaseId == null) {
                asccpNode = getAsccpNodeByAsccpId(asccNode.getToAsccpId(), releaseId);
            } else {
                asccpNode = getAsccpNodeByCurrentAsccpId(asccNode.getToAsccpId(), releaseId);
            }

            asccpNode.setSeqKey(asccNode.getSeqKey());
            asccpNode.setAsccId(asccNode.getAsccId());
            asccpNodes.add(asccpNode);
        }
        return asccpNodes;
    }

    private List<CcBccpNode> getBccpNodes(User user, long fromAccId, Long releaseId) {
        List<CcBccNode> bccNodes = dslContext.select(
                Tables.BCC.BCC_ID,
                Tables.BCC.CURRENT_BCC_ID,
                Tables.BCC.GUID,
                Tables.BCC.TO_BCCP_ID,
                Tables.BCC.SEQ_KEY,
                Tables.BCC.ENTITY_TYPE,
                Tables.BCC.STATE.as("raw_state"),
                Tables.BCC.REVISION_NUM,
                Tables.BCC.REVISION_TRACKING_NUM,
                Tables.BCC.RELEASE_ID
        ).from(Tables.BCC).where(Tables.BCC.FROM_ACC_ID.eq(ULong.valueOf(fromAccId)))
                .fetchInto(CcBccNode.class);

        if (bccNodes.isEmpty()) {
            return Collections.emptyList();
        }

        bccNodes = bccNodes.stream()
                .collect(groupingBy(CcBccNode::getGuid)).values().stream()
                .map(entities -> CcUtility.getLatestEntity(releaseId, entities))
                .collect(Collectors.toList());

        return bccNodes.stream().map(bccNode -> {
            CcBccpNode bccpNode;
            if (releaseId == null) {
                bccpNode = getBccpNodeByBccpId(bccNode.getToBccpId(), releaseId);
            } else {
                bccpNode = getBccpNodeByCurrentBccpId(bccNode.getToBccpId(), releaseId);
            }
            bccpNode.setSeqKey(bccNode.getSeqKey());
            bccpNode.setAttribute(BCCEntityType.valueOf(bccNode.getEntityType()) == Attribute);
            bccpNode.setBccId(bccNode.getBccId());
            return bccpNode;
        }).collect(Collectors.toList());
    }

    public List<? extends CcNode> getDescendants(User user, CcAsccpNode asccpNode) {
        long asccpId = asccpNode.getAsccpId();

        long roleOfAccId = dslContext.select(Tables.ASCCP.ROLE_OF_ACC_ID).from(Tables.ASCCP)
                .where(Tables.ASCCP.ASCCP_ID.eq(ULong.valueOf(asccpId)))
                .fetchOneInto(Long.class);

        Long releaseId = asccpNode.getReleaseId();
        if (releaseId == null) {
            return Arrays.asList(getAccNodeByAccId(roleOfAccId, releaseId));
        } else {
            return Arrays.asList(getAccNodeByCurrentAccId(roleOfAccId, releaseId));
        }
    }

    public List<? extends CcNode> getDescendants(User user, CcBccpNode bccpNode) {
        long bccpId = bccpNode.getBccpId();

        return dslContext.select(
                Tables.DT_SC.DT_SC_ID.as("bdt_sc_id"),
                Tables.DT_SC.GUID,
                concat(Tables.DT_SC.PROPERTY_TERM, val(". "), Tables.DT_SC.REPRESENTATION_TERM).as("name")
        ).from(Tables.DT_SC).join(Tables.BCCP).on(Tables.DT_SC.OWNER_DT_ID.eq(Tables.BCCP.BDT_ID))
                .where(and(
                        Tables.BCCP.BCCP_ID.eq(ULong.valueOf(bccpId)),
                        or(
                                Tables.DT_SC.CARDINALITY_MIN.ne(0),
                                Tables.DT_SC.CARDINALITY_MAX.ne(0)
                        ))).fetchInto(CcBdtScNode.class);
    }

    public CcAccNodeDetail getAccNodeDetail(User user, CcAccNode accNode) {
        long accId = accNode.getAccId();

        return dslContext.select(
                Tables.ACC.ACC_ID,
                Tables.ACC.GUID,
                Tables.ACC.OBJECT_CLASS_TERM,
                Tables.ACC.DEN,
                Tables.ACC.OAGIS_COMPONENT_TYPE.as("component_type"),
                Tables.ACC.IS_ABSTRACT.as("abstracted"),
                Tables.ACC.IS_DEPRECATED.as("deprecated"),
                Tables.ACC.DEFINITION
        ).from(Tables.ACC).where(Tables.ACC.ACC_ID.eq(ULong.valueOf(accId)))
                .fetchOneInto(CcAccNodeDetail.class);
    }

    public CcAsccpNodeDetail getAsccpNodeDetail(User user, CcAsccpNode asccpNode) {
        CcAsccpNodeDetail asccpNodeDetail = new CcAsccpNodeDetail();

        long asccId = asccpNode.getAsccId();
        if (asccId > 0L) {
            CcAsccpNodeDetail.Ascc ascc = dslContext.select(
                    Tables.ASCC.ASCC_ID,
                    Tables.ASCC.GUID,
                    Tables.ASCC.DEN,
                    Tables.ASCC.CARDINALITY_MIN,
                    Tables.ASCC.CARDINALITY_MAX,
                    Tables.ASCC.IS_DEPRECATED.as("deprecated"),
                    Tables.ASCC.DEFINITION).from(Tables.ASCC)
                    .where(Tables.ASCC.ASCC_ID.eq(ULong.valueOf(asccId)))
                    .fetchOneInto(CcAsccpNodeDetail.Ascc.class);
            asccpNodeDetail.setAscc(ascc);
        }

        long asccpId = asccpNode.getAsccpId();
        CcAsccpNodeDetail.Asccp asccp = dslContext.select(
                Tables.ASCCP.ASCCP_ID,
                Tables.ASCCP.GUID,
                Tables.ASCCP.PROPERTY_TERM,
                Tables.ASCCP.DEN,
                Tables.ASCCP.REUSABLE_INDICATOR.as("reusable"),
                Tables.ASCCP.IS_DEPRECATED.as("deprecated"),
                Tables.ASCCP.DEFINITION).from(Tables.ASCCP)
                .where(Tables.ASCCP.ASCCP_ID.eq(ULong.valueOf(asccpId)))
                .fetchOneInto(CcAsccpNodeDetail.Asccp.class);
        asccpNodeDetail.setAsccp(asccp);

        return asccpNodeDetail;
    }

    public CcBccpNodeDetail getBccpNodeDetail(User user, CcBccpNode bccpNode) {
        CcBccpNodeDetail bccpNodeDetail = new CcBccpNodeDetail();

        long bccId = bccpNode.getBccId();
        if (bccId > 0L) {
            CcBccpNodeDetail.Bcc bcc = dslContext.select(
                    Tables.BCC.BCC_ID,
                    Tables.BCC.GUID,
                    Tables.BCC.DEN,
                    Tables.BCC.ENTITY_TYPE,
                    Tables.BCC.CARDINALITY_MIN,
                    Tables.BCC.CARDINALITY_MAX,
                    Tables.BCC.IS_DEPRECATED.as("deprecated"),
                    Tables.BCC.DEFAULT_VALUE,
                    Tables.BCC.DEFINITION).from(Tables.BCC)
                    .where(Tables.BCC.BCC_ID.eq(ULong.valueOf(bccId)))
                    .fetchOneInto(CcBccpNodeDetail.Bcc.class);
            bccpNodeDetail.setBcc(bcc);
        }

        long bccpId = bccpNode.getBccpId();
        CcBccpNodeDetail.Bccp bccp = dslContext.select(
                Tables.BCCP.BCCP_ID,
                Tables.BCCP.GUID,
                Tables.BCCP.PROPERTY_TERM,
                Tables.BCCP.DEN,
                Tables.BCCP.IS_NILLABLE.as("nillable"),
                Tables.BCCP.IS_DEPRECATED.as("deprecated"),
                Tables.BCCP.DEFAULT_VALUE,
                Tables.BCCP.DEFINITION).from(Tables.BCCP)
                .where(Tables.BCCP.BCCP_ID.eq(ULong.valueOf(bccpId)))
                .fetchOneInto( CcBccpNodeDetail.Bccp.class);
        bccpNodeDetail.setBccp(bccp);

        long bdtId = dslContext.select(Tables.BCCP.BDT_ID).from(Tables.BCCP)
                .where(Tables.BCCP.BCCP_ID.eq(ULong.valueOf(bccpId))).fetchOneInto(Long.class);

        CcBccpNodeDetail.Bdt bdt = dslContext.select(
                Tables.DT.DT_ID.as("bdt_id"),
                Tables.DT.GUID,
                Tables.DT.DATA_TYPE_TERM,
                Tables.DT.QUALIFIER,
                Tables.DT.DEN,
                Tables.DT.DEFINITION).from(Tables.DT)
                .where(Tables.DT.DT_ID.eq(ULong.valueOf(bdtId)))
                .fetchOneInto(CcBccpNodeDetail.Bdt.class);
        bccpNodeDetail.setBdt(bdt);

        return bccpNodeDetail;
    }

    public CcBdtScNodeDetail getBdtScNodeDetail(User user, CcBdtScNode bdtScNode) {
        long bdtScId = bdtScNode.getBdtScId();
        return dslContext.select(
                Tables.DT_SC.DT_SC_ID.as("bdt_sc_id"),
                Tables.DT_SC.GUID,
                concat(Tables.DT_SC.PROPERTY_TERM, val(". "), Tables.DT_SC.PROPERTY_TERM).as("den"),
                Tables.DT_SC.CARDINALITY_MIN,
                Tables.DT_SC.CARDINALITY_MAX,
                Tables.DT_SC.DEFINITION).from(Tables.DT_SC)
                .where(Tables.DT_SC.DT_SC_ID.eq(ULong.valueOf(bdtScId)))
                .fetchOneInto(CcBdtScNodeDetail.class);
    }

}

