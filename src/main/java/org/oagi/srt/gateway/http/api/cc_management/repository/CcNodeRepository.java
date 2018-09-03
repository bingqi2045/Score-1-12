package org.oagi.srt.gateway.http.api.cc_management.repository;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.oagi.srt.data.BCCEntityType;
import org.oagi.srt.data.OagisComponentType;
import org.oagi.srt.data.SeqKeySupportable;
import org.oagi.srt.gateway.http.api.cc_management.data.node.*;
import org.oagi.srt.gateway.http.api.cc_management.helper.CcUtility;
import org.oagi.srt.gateway.http.api.common.data.TrackableImpl;
import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static org.oagi.srt.data.BCCEntityType.Attribute;
import static org.oagi.srt.gateway.http.helper.SrtJdbcTemplate.newSqlParameterSource;

@Repository
public class CcNodeRepository {

    @Autowired
    private SrtJdbcTemplate jdbcTemplate;

    public CcAccNode getAccNode(long accId, Long releaseId) {
        CcAccNode accNode = jdbcTemplate.queryForObject("SELECT " +
                "acc_id, guid, den as name, based_acc_id, oagis_component_type, " +
                "revision_num, revision_tracking_num, release_id, current_acc_id " +
                "FROM acc WHERE acc_id = :accId", newSqlParameterSource()
                .addValue("accId", accId), CcAccNode.class);

        OagisComponentType oagisComponentType =
                OagisComponentType.valueOf(accNode.getOagisComponentType());
        accNode.setGroup(oagisComponentType.isGroup());
        accNode.setHasChild(hasChild(accNode, releaseId));

        return accNode;
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

    public CcAsccpNode getAsccpNode(long asccpId, Long releaseId) {
        CcAsccpNode asccpNode = jdbcTemplate.queryForObject("SELECT " +
                "asccp_id, guid, property_term as name, " +
                "revision_num, revision_tracking_num, release_id " +
                "FROM asccp WHERE asccp_id = :asccpId", newSqlParameterSource()
                .addValue("asccpId", asccpId), CcAsccpNode.class);

        asccpNode.setHasChild(true); // role_of_acc_id must not be null.

        return asccpNode;
    }

    public CcBccpNode getBccpNode(long bccpId, Long releaseId) {
        CcBccpNode bccpNode = jdbcTemplate.queryForObject("SELECT " +
                "bccp_id, guid, property_term as name, bdt_id, " +
                "revision_num, revision_tracking_num, release_id " +
                "FROM bccp WHERE bccp_id = :bccpId", newSqlParameterSource()
                .addValue("bccpId", bccpId), CcBccpNode.class);

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
            CcAccNode basedAccNode = getAccNode(basedAccId, accNode.getReleaseId());
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
            CcAsccpNode asccpNode = getAsccpNode(asccNode.getToAsccpId(), releaseId);
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
            CcBccpNode bccpNode = getBccpNode(bccNode.getToBccpId(), releaseId);
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

        return Arrays.asList(getAccNode(roleOfAccId, asccpNode.getReleaseId()));
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
        return jdbcTemplate.queryForObject("SELECT dt_sc_id as bdt_sc_id, guid, den, " +
                "cardinality_min, cardinality_max, definition " +
                "FROM dt_sc WHERE dt_sc_id = :bdtScId", newSqlParameterSource()
                .addValue("bdtScId", bdtScId), CcBdtScNodeDetail.class);
    }
}
