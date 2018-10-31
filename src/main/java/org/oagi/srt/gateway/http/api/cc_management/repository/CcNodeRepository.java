package org.oagi.srt.gateway.http.api.cc_management.repository;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.oagi.srt.data.BCCEntityType;
import org.oagi.srt.data.OagisComponentType;
import org.oagi.srt.data.SeqKeySupportable;
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
    private SessionService sessionService;

    public CcAccNode getAccNodeByAccId(long accId, Long releaseId) {
        CcAccNode accNode = jdbcTemplate.queryForObject("SELECT " +
                "acc_id, guid, den as name, based_acc_id, oagis_component_type, object_class_term, " +
                "revision_num, revision_tracking_num, release_id, current_acc_id " +
                "FROM acc WHERE acc_id = :accId", newSqlParameterSource()
                .addValue("accId", accId), CcAccNode.class);
        return arrangeAccNode(accNode, releaseId);
    }

    public CcAccNode getAccNodeByCurrentAccId(long currentAccId, Long releaseId) {
        CcAccNode accNode = CcUtility.getLatestEntity(releaseId, jdbcTemplate.queryForList("SELECT " +
                "acc_id, guid, den as name, based_acc_id, oagis_component_type, object_class_term, " +
                "revision_num, revision_tracking_num, release_id, current_acc_id " +
                "FROM acc WHERE current_acc_id = :currentAccId", newSqlParameterSource()
                .addValue("currentAccId", currentAccId), CcAccNode.class));
        return arrangeAccNode(accNode, releaseId);
    }

    public CcAccNode getAccNodeByAsccpIdFromAscc(long toAsccpId, Long releaseId) {
        CcAsccNode asccNode = CcUtility.getLatestEntity(releaseId, jdbcTemplate.queryForList("SELECT " +
                        "`from_acc_id`, `seq_key`, `revision_num`, `revision_tracking_num`, `release_id` " +
                        "FROM `ascc` WHERE `to_asccp_id` = :toAsccpId",
                newSqlParameterSource()
                        .addValue("toAsccpId", toAsccpId), CcAsccNode.class));
        return getAccNodeByCurrentAccId(asccNode.getFromAccId(), releaseId);
    }

    public long createAcc(User user, CcAccNode ccAccNode) {
        SimpleJdbcInsert jdbcInsert = jdbcTemplate.insert()
                .withTableName("acc")
                .usingColumns("guid", "object_class_term", "den", "owner_user_id", "definition", "oagis_component_type",
                        "created_by", "last_updated_by", "creation_timestamp", "last_update_timestamp", "state")
                .usingGeneratedKeyColumns("acc_id");
        System.out.println("ccAcc node   =" + ccAccNode);
        long userId = sessionService.userId(user);
        Date timestamp = new Date();
        MapSqlParameterSource parameterSource = newSqlParameterSource()
                .addValue("guid", ccAccNode.getGuid())
                .addValue("object_class_term", ccAccNode.getObjectClassTerm())
                .addValue("oagis_component_type", ccAccNode.getOagisComponentType())
                .addValue("den", ccAccNode.getDen())
                .addValue("owner_user_id", userId)
                .addValue("created_by", userId)
                .addValue("state", CcState.Editing.getValue())
                .addValue("last_updated_by", userId)
                .addValue("creation_timestamp", timestamp)
                .addValue("last_update_timestamp", timestamp)
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
            List<AsccForAccHasChild> asccList =
                    jdbcTemplate.queryForList("SELECT ascc_id, guid, revision_num, revision_tracking_num, release_id " +
                            "FROM ascc WHERE from_acc_id = :fromAccId", newSqlParameterSource()
                            .addValue("fromAccId", fromAccId), AsccForAccHasChild.class);

            long asccCount = asccList.stream().collect(groupingBy(e -> e.getGuid())).values().stream()
                    .map(entities -> CcUtility.getLatestEntity(releaseId, entities))
                    .count();
            if (asccCount > 0L) {
                return true;
            }

            List<BccForAccHasChild> bccList =
                    jdbcTemplate.queryForList("SELECT bcc_id, guid, revision_num, revision_tracking_num, release_id " +
                            "FROM bcc WHERE from_acc_id = :fromAccId", newSqlParameterSource()
                            .addValue("fromAccId", fromAccId), BccForAccHasChild.class);

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
                "asccp_id, guid, property_term as name, " +
                "revision_num, revision_tracking_num, release_id " +
                "FROM asccp WHERE asccp_id = :asccpId", newSqlParameterSource()
                .addValue("asccpId", asccpId), CcAsccpNode.class);

        asccpNode.setHasChild(true); // role_of_acc_id must not be null.

        return asccpNode;
    }

    public CcAsccpNode getAsccpNodeByCurrentAsccpId(long currentAsccpId, Long releaseId) {
        CcAsccpNode asccpNode = CcUtility.getLatestEntity(releaseId,
                jdbcTemplate.queryForList("SELECT " +
                        "asccp_id, guid, property_term as name, " +
                        "revision_num, revision_tracking_num, release_id " +
                        "FROM asccp WHERE current_asccp_id = :currentAsccpId", newSqlParameterSource()
                        .addValue("currentAsccpId", currentAsccpId), CcAsccpNode.class));

        asccpNode.setHasChild(true); // role_of_acc_id must not be null.

        return asccpNode;
    }

    public CcAsccpNode getAsccpNodeByRoleOfAccId(long roleOfAccId, Long releaseId) {
        CcAsccpNode asccpNode = CcUtility.getLatestEntity(releaseId,
                jdbcTemplate.queryForList("SELECT " +
                        "asccp_id, guid, property_term as name, " +
                        "revision_num, revision_tracking_num, release_id " +
                        "FROM asccp WHERE role_of_acc_id = :roleOfAccId", newSqlParameterSource()
                        .addValue("roleOfAccId", roleOfAccId), CcAsccpNode.class));

        asccpNode.setHasChild(true); // role_of_acc_id must not be null.

        return asccpNode;
    }

    public CcBccpNode getBccpNodeByBccpId(long bccpId, Long releaseId) {
        CcBccpNode bccpNode = jdbcTemplate.queryForObject("SELECT " +
                "bccp_id, guid, property_term as name, bdt_id, " +
                "revision_num, revision_tracking_num, release_id " +
                "FROM bccp WHERE bccp_id = :bccpId", newSqlParameterSource()
                .addValue("bccpId", bccpId), CcBccpNode.class);

        bccpNode.setHasChild(hasChild(bccpNode));

        return bccpNode;
    }

    public CcBccpNode getBccpNodeByCurrentBccpId(long currentBccpId, Long releaseId) {
        CcBccpNode bccpNode = CcUtility.getLatestEntity(releaseId,
                jdbcTemplate.queryForList("SELECT " +
                        "bccp_id, guid, property_term as name, bdt_id, " +
                        "revision_num, revision_tracking_num, release_id " +
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

        Long basedAccId = jdbcTemplate.queryForObject(
                "SELECT based_acc_id FROM acc WHERE acc_id = :accId", newSqlParameterSource()
                        .addValue("accId", accNode.getAccId()), Long.class);
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
            fromAccId = jdbcTemplate.queryForObject(
                    "SELECT current_acc_id FROM acc WHERE acc_id = :accId", newSqlParameterSource()
                            .addValue("accId", accNode.getAccId()), Long.class);
        }

        List<SeqKeySupportable> seqKeySupportableList = new ArrayList();
        seqKeySupportableList.addAll(getAsccpNodes(user, fromAccId, releaseId));
        seqKeySupportableList.addAll(getBccpNodes(user, fromAccId, releaseId));

        descendants.addAll(
                seqKeySupportableList.stream()
                        .sorted(Comparator.comparingInt(SeqKeySupportable::getSeqKey))
                        .map(e -> (CcNode) e)
                        .collect(Collectors.toList())
        );

        return descendants;
    }

    private List<CcAsccpNode> getAsccpNodes(User user, long fromAccId, Long releaseId) {
        List<CcAsccNode> asccNodes =
                jdbcTemplate.queryForList("SELECT ascc_id, guid, to_asccp_id, seq_key, " +
                        "revision_num, revision_tracking_num, release_id " +
                        "FROM ascc WHERE from_acc_id = :fromAccId", newSqlParameterSource()
                        .addValue("fromAccId", fromAccId), CcAsccNode.class).stream()
                        .collect(groupingBy(CcAsccNode::getGuid)).values().stream()
                        .map(entities -> CcUtility.getLatestEntity(releaseId, entities))
                        .collect(Collectors.toList());

        return asccNodes.stream().map(asccNode -> {
            CcAsccpNode asccpNode;
            if (releaseId == null) {
                asccpNode = getAsccpNodeByAsccpId(asccNode.getToAsccpId(), releaseId);
            } else {
                asccpNode = getAsccpNodeByCurrentAsccpId(asccNode.getToAsccpId(), releaseId);
            }

            asccpNode.setSeqKey(asccNode.getSeqKey());
            asccpNode.setAsccId(asccNode.getAsccId());
            return asccpNode;
        }).collect(Collectors.toList());
    }

    private List<CcBccpNode> getBccpNodes(User user, long fromAccId, Long releaseId) {
        List<CcBccNode> bccNodes =
                jdbcTemplate.queryForList("SELECT bcc_id, guid, to_bccp_id, seq_key, entity_type, " +
                        "revision_num, revision_tracking_num, release_id " +
                        "FROM bcc WHERE from_acc_id = :fromAccId", newSqlParameterSource()
                        .addValue("fromAccId", fromAccId), CcBccNode.class).stream()
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
        long roleOfAccId = jdbcTemplate.queryForObject(
                "SELECT role_of_acc_id FROM asccp WHERE asccp_id = :asccpId", newSqlParameterSource()
                        .addValue("asccpId", asccpId), Long.class);

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

