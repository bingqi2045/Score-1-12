package org.oagi.srt.gateway.http.api.cc_management.repository;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jooq.DSLContext;
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

    public CcAccNode getAccNodeByAccId(long accId, Long releaseId) {
        CcAccNode accNode = dslContext.select(
                Tables.ACC.ACC_ID,
                Tables.ACC.GUID,
                Tables.ACC.DEN.as("name"),
                Tables.ACC.BASED_ACC_ID,
                Tables.ACC.OAGIS_COMPONENT_TYPE,
                Tables.ACC.OBJECT_CLASS_TERM,
                Tables.ACC.STATE,
                Tables.ACC.REVISION_NUM,
                Tables.ACC.REVISION_TRACKING_NUM,
                Tables.ACC.RELEASE_ID,
                Tables.ACC.CURRENT_ACC_ID
        ).from(Tables.ACC).where(Tables.ACC.ACC_ID.eq(ULong.valueOf(accId)))
                .fetchOneInto(CcAccNode.class);
        return arrangeAccNode(accNode, releaseId);
    }

    public CcAccNode getAccNodeByCurrentAccId(long currentAccId, Long releaseId) {
        List<CcAccNode> accNodes = dslContext.select(
                Tables.ACC.ACC_ID,
                Tables.ACC.GUID,
                Tables.ACC.DEN.as("name"),
                Tables.ACC.BASED_ACC_ID,
                Tables.ACC.OAGIS_COMPONENT_TYPE,
                Tables.ACC.OBJECT_CLASS_TERM,
                Tables.ACC.STATE,
                Tables.ACC.REVISION_NUM,
                Tables.ACC.REVISION_TRACKING_NUM,
                Tables.ACC.RELEASE_ID,
                Tables.ACC.CURRENT_ACC_ID
        ).from(Tables.ACC).where(Tables.ACC.CURRENT_ACC_ID.eq(ULong.valueOf(currentAccId)))
                .fetchInto(CcAccNode.class);

        CcAccNode accNode = CcUtility.getLatestEntity(releaseId, accNodes);
        return arrangeAccNode(accNode, releaseId);
    }

    public CcAccNode getAccNodeByAsccpIdFromAscc(long toAsccpId, Long releaseId) {
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
                      "namespace_id"  ,"created_by", "last_updated_by", "creation_timestamp", "last_update_timestamp", "state")
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

    public int getAccMaxId() {
        return jdbcTemplate.queryForObject("SELECT max(acc_id) FROM acc ", int.class);
    }

    private CcAccNode arrangeAccNode(CcAccNode accNode, Long releaseId) {
        OagisComponentType oagisComponentType =
                OagisComponentType.valueOf(accNode.getOagisComponentType());
        accNode.setGroup(oagisComponentType.isGroup());
        accNode.setHasChild(hasChild(accNode, releaseId));

        return accNode;
    }

    public long getCurrentAccIdByAccId(long accId) {
        return jdbcTemplate.queryForObject("SELECT current_acc_id " +
                "FROM acc WHERE acc_id = :accId", newSqlParameterSource()
                .addValue("accId", accId), Long.class);
    }

    private boolean hasChild(CcAccNode accNode, Long releaseId) {
        if (accNode.getBasedAccId() != null) {
            return true;
        } else {
            Long fromAccId = (releaseId == null) ? accNode.getAccId() : accNode.getCurrentAccId();
            if (fromAccId == null) {
                return false;
            }
            List<AsccForAccHasChild> asccList = dslContext.select(
                    Tables.ASCC.ASCC_ID,
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
        private String guid;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class BccForAccHasChild extends TrackableImpl {
        private long bccId;
        private String guid;
    }

    public CcAsccpNode getAsccpNodeByAsccpId(long asccpId, Long releaseId) {
        CcAsccpNode asccpNode = jdbcTemplate.queryForObject("SELECT " +
                "asccp_id, guid, property_term as name, role_of_acc_id, " +
                "state, revision_num, revision_tracking_num, release_id " +
                "FROM asccp WHERE asccp_id = :asccpId", newSqlParameterSource()
                .addValue("asccpId", asccpId), CcAsccpNode.class);

        asccpNode.setHasChild(true); // role_of_acc_id must not be null.

        return asccpNode;
    }

    public CcAsccpNode getAsccpNodeByCurrentAsccpId(long currentAsccpId, Long releaseId) {
        CcAsccpNode asccpNode = CcUtility.getLatestEntity(releaseId,
                jdbcTemplate.queryForList("SELECT " +
                        "asccp_id, guid, property_term as name, role_of_acc_id, " +
                        "state, revision_num, revision_tracking_num, release_id " +
                        "FROM asccp WHERE current_asccp_id = :currentAsccpId", newSqlParameterSource()
                        .addValue("currentAsccpId", currentAsccpId), CcAsccpNode.class));

        asccpNode.setHasChild(true); // role_of_acc_id must not be null.

        return asccpNode;
    }

    public CcAsccpNode getAsccpNodeByRoleOfAccId(long roleOfAccId, Long releaseId) {
        CcAsccpNode asccpNode = CcUtility.getLatestEntity(releaseId,
                jdbcTemplate.queryForList("SELECT " +
                        "asccp_id, guid, property_term as name, " +
                        "state, revision_num, revision_tracking_num, release_id " +
                        "FROM asccp WHERE role_of_acc_id = :roleOfAccId", newSqlParameterSource()
                        .addValue("roleOfAccId", roleOfAccId), CcAsccpNode.class));

        asccpNode.setHasChild(true); // role_of_acc_id must not be null.

        return asccpNode;
    }

    public CcBccpNode getBccpNodeByBccpId(long bccpId, Long releaseId) {
        CcBccpNode bccpNode = jdbcTemplate.queryForObject("SELECT " +
                "bccp_id, guid, property_term as name, bdt_id, " +
                "state, revision_num, revision_tracking_num, release_id " +
                "FROM bccp WHERE bccp_id = :bccpId", newSqlParameterSource()
                .addValue("bccpId", bccpId), CcBccpNode.class);

        bccpNode.setHasChild(hasChild(bccpNode));

        return bccpNode;
    }

    public CcBccpNode getBccpNodeByCurrentBccpId(long currentBccpId, Long releaseId) {
        CcBccpNode bccpNode = CcUtility.getLatestEntity(releaseId,
                jdbcTemplate.queryForList("SELECT " +
                        "bccp_id, guid, property_term as name, bdt_id, " +
                        "state, revision_num, revision_tracking_num, release_id " +
                        "FROM bccp WHERE current_bccp_id = :currentBccpId", newSqlParameterSource()
                        .addValue("currentBccpId", currentBccpId), CcBccpNode.class));

        bccpNode.setHasChild(hasChild(bccpNode));

        return bccpNode;
    }

    private boolean hasChild(CcBccpNode bccpNode) {
        long bdtId = bccpNode.getBdtId();
        int dtScCount = jdbcTemplate.queryForObject("SELECT count(*) FROM dt_sc " +
                "WHERE owner_dt_id = :bdtId AND (cardinality_min != 0 OR cardinality_max != 0)", newSqlParameterSource()
                .addValue("bdtId", bdtId), Integer.class);
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

        List<SeqKeySupportable> seqKeySupportableList = new ArrayList();
        seqKeySupportableList.addAll(getAsccpNodes(user, fromAccId, releaseId));
        seqKeySupportableList.addAll(getBccpNodes(user, fromAccId, releaseId));
        seqKeySupportableList.sort(Comparator.comparingInt(SeqKeySupportable::getSeqKey));

        int seqKey = 1;
        for (SeqKeySupportable e : seqKeySupportableList) {
            if (e instanceof CcAsccpNode) {
                CcAsccpNode asccpNode = (CcAsccpNode) e;
                OagisComponentType oagisComponentType =
                        bieRepository.getOagisComponentTypeByAccId(asccpNode.getRoleOfAccId());
                if (oagisComponentType.isGroup()) {
                    CcAccNode roleOfAccNode = getAccNodeByCurrentAccId(asccpNode.getRoleOfAccId(), releaseId);
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

    private List<CcAsccpNode> getAsccpNodes(User user, long fromAccId, Long releaseId) {
        List<CcAsccNode> asccNodes = dslContext.select(
                Tables.ASCC.ASCC_ID,
                Tables.ASCC.GUID,
                Tables.ASCC.TO_ASCCP_ID,
                Tables.ASCC.SEQ_KEY,
                Tables.ASCC.STATE,
                Tables.ASCC.REVISION_NUM,
                Tables.ASCC.REVISION_TRACKING_NUM,
                Tables.ASCC.RELEASE_ID
        ).from(Tables.ASCC).where(Tables.ASCC.FROM_ACC_ID.eq(ULong.valueOf(fromAccId)))
                .fetchInto(CcAsccNode.class);

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
                Tables.BCC.GUID,
                Tables.BCC.TO_BCCP_ID,
                Tables.BCC.SEQ_KEY,
                Tables.BCC.ENTITY_TYPE,
                Tables.BCC.STATE,
                Tables.BCC.REVISION_NUM,
                Tables.BCC.REVISION_TRACKING_NUM,
                Tables.BCC.RELEASE_ID
        ).from(Tables.BCC).where(Tables.BCC.FROM_ACC_ID.eq(ULong.valueOf(fromAccId)))
                .fetchInto(CcBccNode.class);

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

        return jdbcTemplate.queryForList("SELECT dt_sc.dt_sc_id as bdt_sc_id, dt_sc.guid, " +
                        "CONCAT(dt_sc.property_term, '. ', dt_sc.representation_term) as name " +
                        "FROM dt_sc JOIN bccp ON dt_sc.owner_dt_id = bccp.bdt_id " +
                        "WHERE bccp.bccp_id = :bccpId AND (dt_sc.cardinality_min != 0 OR dt_sc.cardinality_max != 0)",
                newSqlParameterSource()
                        .addValue("bccpId", bccpId), CcBdtScNode.class);
    }

    public CcAccNodeDetail getAccNodeDetail(User user, CcAccNode accNode) {
        long accId = accNode.getAccId();

        return jdbcTemplate.queryForObject("SELECT acc_id, guid, object_class_term, den, " +
                "oagis_component_type as component_type, " +
                "is_abstract as abstracted, is_deprecated as deprecated, definition " +
                "FROM acc WHERE acc_id = :accId", newSqlParameterSource()
                .addValue("accId", accId), CcAccNodeDetail.class);
    }

    public CcAsccpNodeDetail getAsccpNodeDetail(User user, CcAsccpNode asccpNode) {
        CcAsccpNodeDetail asccpNodeDetail = new CcAsccpNodeDetail();

        long asccId = asccpNode.getAsccId();
        if (asccId > 0L) {
            CcAsccpNodeDetail.Ascc ascc =
                    jdbcTemplate.queryForObject("SELECT ascc_id, guid, den, cardinality_min, cardinality_max, " +
                            "is_deprecated as deprecated, definition " +
                            "FROM ascc WHERE ascc_id = :asccId", newSqlParameterSource()
                            .addValue("asccId", asccId), CcAsccpNodeDetail.Ascc.class);
            asccpNodeDetail.setAscc(ascc);
        }

        long asccpId = asccpNode.getAsccpId();
        CcAsccpNodeDetail.Asccp asccp =
                jdbcTemplate.queryForObject("SELECT asccp_id, guid, property_term, den, " +
                        "reusable_indicator as reusable, is_deprecated as deprecated, definition " +
                        "FROM asccp WHERE asccp_id = :asccpId", newSqlParameterSource()
                        .addValue("asccpId", asccpId), CcAsccpNodeDetail.Asccp.class);
        asccpNodeDetail.setAsccp(asccp);

        return asccpNodeDetail;
    }

    public CcBccpNodeDetail getBccpNodeDetail(User user, CcBccpNode bccpNode) {
        CcBccpNodeDetail bccpNodeDetail = new CcBccpNodeDetail();

        long bccId = bccpNode.getBccId();
        if (bccId > 0L) {
            CcBccpNodeDetail.Bcc bcc =
                    jdbcTemplate.queryForObject("SELECT bcc_id, guid, den, entity_type, " +
                            "cardinality_min, cardinality_max, " +
                            "is_nillable as nillable, is_deprecated as deprecated, " +
                            "default_value, definition " +
                            "FROM bcc WHERE bcc_id = :bccId", newSqlParameterSource()
                            .addValue("bccId", bccId), CcBccpNodeDetail.Bcc.class);
            bccpNodeDetail.setBcc(bcc);
        }

        long bccpId = bccpNode.getBccpId();
        CcBccpNodeDetail.Bccp bccp =
                jdbcTemplate.queryForObject("SELECT bccp_id, guid, property_term, den, " +
                        "is_nillable as nillable, is_deprecated as deprecated, " +
                        "default_value, definition " +
                        "FROM bccp WHERE bccp_id = :bccpId", newSqlParameterSource()
                        .addValue("bccpId", bccpId), CcBccpNodeDetail.Bccp.class);
        bccpNodeDetail.setBccp(bccp);

        long bdtId = jdbcTemplate.queryForObject("SELECT bdt_id " +
                "FROM bccp WHERE bccp_id = :bccpId", newSqlParameterSource()
                .addValue("bccpId", bccpId), Long.class);

        CcBccpNodeDetail.Bdt bdt =
                jdbcTemplate.queryForObject("SELECT dt_id as bdt_id, guid, " +
                        "data_type_term, qualifier, den, definition " +
                        "FROM dt WHERE dt_id = :bdtId", newSqlParameterSource()
                        .addValue("bdtId", bdtId), CcBccpNodeDetail.Bdt.class);
        bccpNodeDetail.setBdt(bdt);

        return bccpNodeDetail;
    }

    public CcBdtScNodeDetail getBdtScNodeDetail(User user, CcBdtScNode bdtScNode) {
        long bdtScId = bdtScNode.getBdtScId();
        return jdbcTemplate.queryForObject("SELECT dt_sc_id as bdt_sc_id, guid, " +
                "CONCAT(property_term, '. ', representation_term) as den, " +
                "cardinality_min, cardinality_max, definition " +
                "FROM dt_sc WHERE dt_sc_id = :bdtScId", newSqlParameterSource()
                .addValue("bdtScId", bdtScId), CcBdtScNodeDetail.class);
    }
}

