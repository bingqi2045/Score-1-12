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
        CcAccNode accNode = jdbcTemplate.queryForObject("SELECT 'acc' as type, " +
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
        CcAsccpNode asccpNode = jdbcTemplate.queryForObject("SELECT 'asccp' as type, " +
                "asccp_id, guid, property_term as name, " +
                "revision_num, revision_tracking_num, release_id " +
                "FROM asccp WHERE asccp_id = :asccpId", newSqlParameterSource()
                .addValue("asccpId", asccpId), CcAsccpNode.class);

        asccpNode.setHasChild(true); // role_of_acc_id must not be null.

        return asccpNode;
    }

    public CcBccpNode getBccpNode(long bccpId, Long releaseId) {
        CcBccpNode bccpNode = jdbcTemplate.queryForObject("SELECT 'bccp' as type, " +
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

        return jdbcTemplate.queryForList("SELECT 'bdt_sc' as type, dt_sc.dt_sc_id as bdt_sc_id, dt_sc.guid, " +
                        "CONCAT(dt_sc.property_term, '. ', dt_sc.representation_term) as name " +
                        "FROM dt_sc JOIN bccp ON dt_sc.owner_dt_id = bccp.bdt_id WHERE bccp.bccp_id = :bccpId",
                newSqlParameterSource()
                        .addValue("bccpId", bccpId), CcBdtScNode.class);
    }
}
