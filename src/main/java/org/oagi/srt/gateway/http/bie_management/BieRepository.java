package org.oagi.srt.gateway.http.bie_management;

import org.oagi.srt.gateway.http.bie_management.bie_edit.*;
import org.oagi.srt.gateway.http.cc_management.CcState;
import org.oagi.srt.gateway.http.cc_management.CcUtility;
import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static org.oagi.srt.gateway.http.helper.SrtJdbcTemplate.newSqlParameterSource;

@Repository
public class BieRepository {

    @Autowired
    private SrtJdbcTemplate jdbcTemplate;

    public OagisComponentType getOagisComponentTypeByAccId(long accId) {
        int oagisComponentType = jdbcTemplate.queryForObject(
                "SELECT oagis_component_type FROM acc WHERE acc_id = :acc_id", newSqlParameterSource()
                        .addValue("acc_id", accId), Integer.class);
        return OagisComponentType.valueOf(oagisComponentType);
    }

    public long getCurrentAccIdByTopLevelAbieId(long topLevelAbieId) {
        return jdbcTemplate.queryForObject("SELECT acc.current_acc_id " +
                "FROM abie JOIN top_level_abie ON abie.abie_id = top_level_abie.abie_id " +
                "JOIN acc ON abie.based_acc_id = acc.acc_id " +
                "WHERE top_level_abie_id = :top_level_abie_id", newSqlParameterSource()
                .addValue("top_level_abie_id", topLevelAbieId), Long.class);
    }

    public BieEditAcc getAccByCurrentAccId(long currentAccId, long releaseId) {
        // BIE only can see the ACCs whose state is in Published.
        MapSqlParameterSource parameterSource = newSqlParameterSource()
                .addValue("state", CcState.Published.getValue())
                .addValue("current_acc_id", currentAccId);
        List<BieEditAcc> accList =
                jdbcTemplate.queryForList("SELECT acc_id, guid, based_acc_id, oagis_component_type, current_acc_id, " +
                                "revision_num, revision_tracking_num, revision_action, release_id FROM acc " +
                                "WHERE revision_num > 0 AND state = :state AND current_acc_id = :current_acc_id",
                        parameterSource, BieEditAcc.class);
        return CcUtility.getLatestEntity(releaseId, accList);
    }

    public BieEditAcc getAcc(long accId) {
        MapSqlParameterSource parameterSource = newSqlParameterSource()
                .addValue("acc_id", accId);
        List<BieEditAcc> res =
                jdbcTemplate.queryForList("SELECT acc_id, guid, based_acc_id, oagis_component_type, current_acc_id, " +
                                "revision_num, revision_tracking_num, revision_action, release_id FROM acc " +
                                "WHERE acc_id = :acc_id",
                        parameterSource, BieEditAcc.class);
        if (res.isEmpty()) {
            throw new EmptyResultDataAccessException(1);
        }

        return res.get(0);
    }

    public BieEditBbiep getBbiep(long bbiepId, long topLevelAbieId) {
        return jdbcTemplate.queryForObject(
                "SELECT bbiep_id, based_bccp_id FROM bbiep " +
                        "WHERE owner_top_level_abie_id = :owner_top_level_abie_id AND bbiep_id = :bbiep_id",
                newSqlParameterSource()
                        .addValue("bbiep_id", bbiepId)
                        .addValue("owner_top_level_abie_id", topLevelAbieId), BieEditBbiep.class);
    }

    public BieEditBccp getBccp(long bccpId) {
        return jdbcTemplate.queryForObject(
                "SELECT bccp_id, guid, property_term, bdt_id, current_bccp_id, " +
                        "revision_num, revision_tracking_num, revision_action, release_id " +
                        "FROM bccp WHERE bccp_id = :bccp_id",
                newSqlParameterSource()
                        .addValue("bccp_id", bccpId), BieEditBccp.class);
    }

    public int getCountDtScByOwnerDtId(long ownerDtId) {
        return jdbcTemplate.queryForObject("SELECT count(*) FROM dt_sc " +
                "WHERE owner_dt_id = :owner_dt_id AND cardinality_max != 0", newSqlParameterSource()
                .addValue("owner_dt_id", ownerDtId), Integer.class);
    }

    public List<BieEditBdtSc> getBdtScListByOwnerDtId(long ownerDtId) {
        return jdbcTemplate.queryForList(
                "SELECT dt_sc_id, guid, property_term, representation_term, owner_dt_id " +
                        "FROM dt_sc WHERE cardinality_max != 0 AND owner_dt_id = :owner_dt_id",
                newSqlParameterSource().addValue("owner_dt_id", ownerDtId), BieEditBdtSc.class);
    }

    public BieEditBbieSc getBbieScIdByBbieIdAndDtScId(long bbieId, long dtScId, long topLevelAbieId) {
        return jdbcTemplate.queryForObject(
                "SELECT bbie_sc_id, bbie_id, dt_sc_id, is_used as used " +
                        "FROM bbie_sc WHERE bbie_id = :bbie_id AND dt_sc_id = :dt_sc_id " +
                        "AND owner_top_level_abie_id = :owner_top_level_abie_id",
                newSqlParameterSource()
                        .addValue("bbie_id", bbieId)
                        .addValue("dt_sc_id", dtScId)
                        .addValue("owner_top_level_abie_id", topLevelAbieId), BieEditBbieSc.class);
    }


    public String getAsccpPropertyTermByAsbiepId(long asbiepId) {
        return jdbcTemplate.queryForObject("SELECT asccp.property_term " +
                "FROM asccp JOIN asbiep ON asccp.asccp_id = asbiep.based_asccp_id " +
                "WHERE asbiep_id = :asbiep_id", newSqlParameterSource()
                .addValue("asbiep_id", asbiepId), String.class);
    }

    public String getBccpPropertyTermByBbiepId(long bbiepId) {
        return jdbcTemplate.queryForObject("SELECT bccp.property_term " +
                "FROM bccp JOIN bbiep ON bccp.bccp_id = bbiep.based_bccp_id " +
                "WHERE bbiep_id = :bbiep_id", newSqlParameterSource()
                .addValue("bbiep_id", bbiepId), String.class);
    }

    public BieEditAsccp getAsccpByCurrentAsccpId(long currentAsccpId, long releaseId) {
        List<BieEditAsccp> asccpList =
                jdbcTemplate.queryForList("SELECT asccp_id, guid, property_term, role_of_acc_id, current_asccp_id, " +
                        "revision_num, revision_tracking_num, revision_action, release_id " +
                        "FROM asccp WHERE revision_num > 0 AND current_asccp_id = :current_asccp_id", newSqlParameterSource()
                        .addValue("current_asccp_id", currentAsccpId), BieEditAsccp.class);
        return CcUtility.getLatestEntity(releaseId, asccpList);
    }

    public BieEditBccp getBccpByCurrentBccpId(long currentBccpId, long releaseId) {
        List<BieEditBccp> bccpList =
                jdbcTemplate.queryForList("SELECT bccp_id, guid, property_term, bdt_id, current_bccp_id, " +
                        "revision_num, revision_tracking_num, revision_action, release_id " +
                        "FROM bccp WHERE revision_num > 0 AND current_bccp_id = :current_bccp_id", newSqlParameterSource()
                        .addValue("current_bccp_id", currentBccpId), BieEditBccp.class);
        return CcUtility.getLatestEntity(releaseId, bccpList);
    }

    public BieEditAbie getAbieByAsbiepId(long asbiepId) {
        return jdbcTemplate.queryForObject("SELECT abie.abie_id, abie.based_acc_id " +
                "FROM asbiep JOIN abie ON asbiep.role_of_abie_id = abie.abie_id " +
                "WHERE asbiep.asbiep_id = :asbiep_id", newSqlParameterSource()
                .addValue("asbiep_id", asbiepId), BieEditAbie.class);
    }

    public List<BieEditAsbie> getAsbieListByFromAbieId(long fromAbieId, BieEditNode node) {
        return jdbcTemplate.queryForList("SELECT asbie_id, from_abie_id, to_asbiep_id, based_ascc_id, is_used as used " +
                        "FROM asbie WHERE owner_top_level_abie_id = :owner_top_level_abie_id AND from_abie_id = :from_abie_id",
                newSqlParameterSource()
                        .addValue("owner_top_level_abie_id", node.getTopLevelAbieId())
                        .addValue("from_abie_id", fromAbieId), BieEditAsbie.class);
    }

    public List<BieEditBbie> getBbieListByFromAbieId(long fromAbieId, BieEditNode node) {
        return jdbcTemplate.queryForList("SELECT bbie_id, from_abie_id, to_bbiep_id, based_bcc_id, is_used as used " +
                        "FROM bbie WHERE owner_top_level_abie_id = :owner_top_level_abie_id AND from_abie_id = :from_abie_id",
                newSqlParameterSource()
                        .addValue("owner_top_level_abie_id", node.getTopLevelAbieId())
                        .addValue("from_abie_id", fromAbieId), BieEditBbie.class);
    }

    public long getRoleOfAccIdByAsbiepId(long asbiepId) {
        return jdbcTemplate.queryForObject("SELECT asccp.role_of_acc_id " +
                "FROM asbiep JOIN asccp ON asbiep.based_asccp_id = asccp.asccp_id " +
                "WHERE asbiep_id = :asbiep_id", newSqlParameterSource()
                .addValue("asbiep_id", asbiepId), Long.class);
    }

    public long getRoleOfAccIdByAsccpId(long asccpId) {
        return jdbcTemplate.queryForObject("SELECT asccp.role_of_acc_id " +
                "FROM asccp WHERE asccp_id = :asccp_id", newSqlParameterSource()
                .addValue("asccp_id", asccpId), Long.class);
    }

    public List<BieEditAscc> getAsccListByFromAccId(long fromAccId, long releaseId) {
        List<BieEditAscc> asccList =
                jdbcTemplate.queryForList("SELECT ascc_id, guid, from_acc_id, to_asccp_id, seq_key, current_ascc_id, " +
                                "revision_num, revision_tracking_num, revision_action, release_id " +
                                "FROM ascc WHERE revision_num > 0 AND from_acc_id = :from_acc_id AND release_id <= :release_id",
                        newSqlParameterSource()
                                .addValue("from_acc_id", fromAccId)
                                .addValue("release_id", releaseId), BieEditAscc.class);

        return asccList.stream().collect(groupingBy(BieEditAscc::getGuid)).values().stream()
                .map(e -> CcUtility.getLatestEntity(releaseId, e))
                .filter(e -> e != null).collect(Collectors.toList());
    }

    public List<BieEditBcc> getBccListByFromAccId(long fromAccId, long releaseId) {
        List<BieEditBcc> bccList =
                jdbcTemplate.queryForList("SELECT bcc_id, guid, from_acc_id, to_bccp_id, seq_key, entity_type, current_bcc_id, " +
                                "revision_num, revision_tracking_num, revision_action, release_id " +
                                "FROM bcc WHERE revision_num > 0 AND from_acc_id = :from_acc_id AND release_id <= :release_id",
                        newSqlParameterSource()
                                .addValue("from_acc_id", fromAccId)
                                .addValue("release_id", releaseId), BieEditBcc.class);

        return bccList.stream().collect(groupingBy(BieEditBcc::getGuid)).values().stream()
                .map(e -> CcUtility.getLatestEntity(releaseId, e))
                .filter(e -> e != null).collect(Collectors.toList());
    }

    public TopLevelAbie getTopLevelAbie(long topLevelAbieId) {
        MapSqlParameterSource parameterSource = newSqlParameterSource()
                .addValue("top_level_abie_id", topLevelAbieId);

        return jdbcTemplate.queryForObject(
                "SELECT top_level_abie_id, abie_id, owner_user_id, release_id, state " +
                        "FROM top_level_abie WHERE top_level_abie_id = :top_level_abie_id",
                parameterSource, TopLevelAbie.class);
    }
}
