package org.oagi.srt.gateway.http.api.cc_management.repository;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.oagi.srt.data.OagisComponentType;
import org.oagi.srt.gateway.http.api.cc_management.data.node.CcAccNode;
import org.oagi.srt.gateway.http.api.cc_management.data.node.CcAsccpNode;
import org.oagi.srt.gateway.http.api.cc_management.data.node.CcBccpNode;
import org.oagi.srt.gateway.http.api.cc_management.helper.CcUtility;
import org.oagi.srt.gateway.http.api.common.data.TrackableImpl;
import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static java.util.stream.Collectors.groupingBy;
import static org.oagi.srt.gateway.http.helper.SrtJdbcTemplate.newSqlParameterSource;

@Repository
public class CcNodeRepository {

    @Autowired
    private SrtJdbcTemplate jdbcTemplate;

    public CcAccNode getAccNode(long accId, long releaseId) {
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

    private boolean hasChild(CcAccNode accNode, long releaseId) {
        if (accNode.getBasedAccId() != null) {
            return true;
        } else {
            long fromAccId = (releaseId == 0L) ? accNode.getAccId() : accNode.getCurrentAccId();
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

    public CcAsccpNode getAsccpNode(long asccpId, long releaseId) {
        CcAsccpNode asccpNode = jdbcTemplate.queryForObject("SELECT 'asccp' as type, " +
                "asccp_id, guid, property_term as name, " +
                "revision_num, revision_tracking_num, release_id " +
                "FROM asccp WHERE asccp_id = :asccpId", newSqlParameterSource()
                .addValue("asccpId", asccpId), CcAsccpNode.class);

        asccpNode.setHasChild(true); // role_of_acc_id must not be null.

        return asccpNode;
    }

    public CcBccpNode getBccpNode(long bccpId, long releaseId) {
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
}
