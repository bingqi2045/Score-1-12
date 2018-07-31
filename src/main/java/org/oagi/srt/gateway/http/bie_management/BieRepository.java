package org.oagi.srt.gateway.http.bie_management;

import org.oagi.srt.gateway.http.bie_management.bie_edit.BieEditAcc;
import org.oagi.srt.gateway.http.bie_management.bie_edit.BieEditBbiep;
import org.oagi.srt.gateway.http.bie_management.bie_edit.BieEditBccp;
import org.oagi.srt.gateway.http.bie_management.bie_edit.BieEditBdtSc;
import org.oagi.srt.gateway.http.cc_management.CcState;
import org.oagi.srt.gateway.http.cc_management.CcUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BieRepository {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public OagisComponentType getOagisComponentTypeByAccId(long accId) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("acc_id", accId);
        int oagisComponentType =
                jdbcTemplate.queryForObject("SELECT oagis_component_type FROM acc WHERE acc_id = :acc_id",
                        parameterSource, Integer.class);
        return OagisComponentType.valueOf(oagisComponentType);
    }

    public long getCurrentAccIdByTopLevelAbieId(long topLevelAbieId) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("top_level_abie_id", topLevelAbieId);
        return jdbcTemplate.queryForObject("SELECT acc.current_acc_id " +
                "FROM abie JOIN top_level_abie ON abie.abie_id = top_level_abie.abie_id " +
                "JOIN acc ON abie.based_acc_id = acc.acc_id " +
                "WHERE top_level_abie_id = :top_level_abie_id", parameterSource, Long.class);
    }

    public BieEditAcc getAccByCurrentAccId(long currentAccId, long releaseId) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        // BIE only can see the ACCs whose state is in Published.
        parameterSource.addValue("state", CcState.Published.getValue());
        parameterSource.addValue("current_acc_id", currentAccId);
        List<BieEditAcc> accList =
                jdbcTemplate.query("SELECT acc_id, guid, based_acc_id, oagis_component_type, current_acc_id, " +
                                "revision_num, revision_tracking_num, revision_action, release_id FROM acc " +
                                "WHERE revision_num > 0 AND state = :state AND current_acc_id = :current_acc_id",
                        parameterSource, new BeanPropertyRowMapper(BieEditAcc.class));
        return CcUtility.getLatestEntity(releaseId, accList);
    }

    public BieEditAcc getAcc(long accId) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("acc_id", accId);
        List<BieEditAcc> res =
                jdbcTemplate.query("SELECT acc_id, guid, based_acc_id, oagis_component_type, current_acc_id, " +
                                "revision_num, revision_tracking_num, revision_action, release_id FROM acc " +
                                "WHERE acc_id = :acc_id",
                        parameterSource, new BeanPropertyRowMapper(BieEditAcc.class));
        if (res.isEmpty()) {
            throw new EmptyResultDataAccessException(1);
        }

        return res.get(0);
    }

    public BieEditBbiep getBbiep(long bbiepId, long topLevelAbieId) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("bbiep_id", bbiepId);
        parameterSource.addValue("owner_top_level_abie_id", topLevelAbieId);
        List<BieEditBbiep> res =
                jdbcTemplate.query("SELECT bbiep_id, based_bccp_id " +
                                "FROM bbiep WHERE owner_top_level_abie_id = :owner_top_level_abie_id AND bbiep_id = :bbiep_id",
                        parameterSource, new BeanPropertyRowMapper(BieEditBbiep.class));
        if (res.isEmpty()) {
            throw new EmptyResultDataAccessException(1);
        }

        return res.get(0);
    }

    public BieEditBccp getBccp(long bccpId) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("bccp_id", bccpId);
        List<BieEditBccp> res =
                jdbcTemplate.query("SELECT bccp_id, guid, property_term, bdt_id, current_bccp_id, " +
                                "revision_num, revision_tracking_num, revision_action, release_id " +
                                "FROM bccp WHERE bccp_id = :bccp_id",
                        parameterSource, new BeanPropertyRowMapper(BieEditBccp.class));
        if (res.isEmpty()) {
            throw new EmptyResultDataAccessException(1);
        }

        return res.get(0);
    }

    public BieEditBccp getBccpByCurrentBccpId(long currentBccpId, long releaseId) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("current_bccp_id", currentBccpId);
        List<BieEditBccp> res =
                jdbcTemplate.query("SELECT bccp_id, guid, property_term, bdt_id, current_bccp_id, " +
                                "revision_num, revision_tracking_num, revision_action, release_id " +
                                "FROM bccp WHERE revision_num > 0 AND current_bccp_id = :current_bccp_id",
                        parameterSource, new BeanPropertyRowMapper(BieEditBccp.class));
        return CcUtility.getLatestEntity(releaseId, res);
    }

    public int getCountDtScByOwnerDtId(long ownerDtId) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("owner_dt_id", ownerDtId);
        return jdbcTemplate.queryForObject("SELECT count(*) FROM dt_sc " +
                "WHERE owner_dt_id = :owner_dt_id AND cardinality_max != 0", parameterSource, Integer.class);
    }

    public List<BieEditBdtSc> getBdtScListByOwnerDtId(long ownerDtId) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("owner_dt_id", ownerDtId);
        return jdbcTemplate.query("SELECT dt_sc_id, guid, property_term, representation_term, owner_dt_id " +
                        "FROM dt_sc WHERE cardinality_max != 0 AND owner_dt_id = :owner_dt_id",
                parameterSource, new BeanPropertyRowMapper(BieEditBdtSc.class));
    }

    public long getBbieScIdByBbieIdAndDtScId(long bbieId, long dtScId, long topLevelAbieId) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("bbie_id", bbieId);
        parameterSource.addValue("dt_sc_id", dtScId);
        parameterSource.addValue("owner_top_level_abie_id", topLevelAbieId);
        return jdbcTemplate.queryForObject("SELECT bbie_sc_id FROM bbie_sc " +
                "WHERE bbie_id = :bbie_id AND dt_sc_id = :dt_sc_id " +
                "AND owner_top_level_abie_id = :owner_top_level_abie_id", parameterSource, Long.class);
    }
}
