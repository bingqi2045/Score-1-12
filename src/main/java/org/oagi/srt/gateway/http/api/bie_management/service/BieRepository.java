package org.oagi.srt.gateway.http.api.bie_management.service;

import org.oagi.srt.data.*;
import org.oagi.srt.gateway.http.api.bie_management.data.bie_edit.*;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;
import org.oagi.srt.gateway.http.api.cc_management.helper.CcUtility;
import org.oagi.srt.gateway.http.api.cc_management.service.CcListService;
import org.oagi.srt.gateway.http.api.namespace_management.service.NamespaceService;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.oagi.srt.gateway.http.helper.SrtGuid;
import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
import org.oagi.srt.gateway.http.helper.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static org.oagi.srt.gateway.http.helper.SrtJdbcTemplate.newSqlParameterSource;

@Repository
public class BieRepository {

    @Autowired
    private SrtJdbcTemplate jdbcTemplate;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private NamespaceService namespaceService;

    @Autowired
    private CcListService ccListService;

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
                                "revision_num, revision_tracking_num, release_id FROM acc " +
                                "WHERE revision_num > 0 AND state = :state AND current_acc_id = :current_acc_id",
                        parameterSource, BieEditAcc.class);
        return CcUtility.getLatestEntity(releaseId, accList);
    }

    public BieEditAcc getAcc(long accId) {
        MapSqlParameterSource parameterSource = newSqlParameterSource()
                .addValue("acc_id", accId);
        List<BieEditAcc> res =
                jdbcTemplate.queryForList("SELECT acc_id, guid, based_acc_id, oagis_component_type, current_acc_id, " +
                                "revision_num, revision_tracking_num, release_id FROM acc " +
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
                        "revision_num, revision_tracking_num, release_id " +
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
                        "revision_num, revision_tracking_num, release_id " +
                        "FROM asccp WHERE revision_num > 0 AND current_asccp_id = :current_asccp_id", newSqlParameterSource()
                        .addValue("current_asccp_id", currentAsccpId), BieEditAsccp.class);
        return CcUtility.getLatestEntity(releaseId, asccpList);
    }

    public BieEditBccp getBccpByCurrentBccpId(long currentBccpId, long releaseId) {
        List<BieEditBccp> bccpList =
                jdbcTemplate.queryForList("SELECT bccp_id, guid, property_term, bdt_id, current_bccp_id, " +
                        "revision_num, revision_tracking_num, release_id " +
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
                                "revision_num, revision_tracking_num, release_id " +
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
                                "revision_num, revision_tracking_num, release_id " +
                                "FROM bcc WHERE revision_num > 0 AND from_acc_id = :from_acc_id AND release_id <= :release_id",
                        newSqlParameterSource()
                                .addValue("from_acc_id", fromAccId)
                                .addValue("release_id", releaseId), BieEditBcc.class);

        return bccList.stream().collect(groupingBy(BieEditBcc::getGuid)).values().stream()
                .map(e -> CcUtility.getLatestEntity(releaseId, e))
                .filter(e -> e != null).collect(Collectors.toList());
    }

    public long createTopLevelAbie(long userId, long releaseId, BieState state) {
        SimpleJdbcInsert jdbcInsert = jdbcTemplate.insert()
                .withTableName("top_level_abie")
                .usingColumns("owner_user_id", "release_id", "state")
                .usingGeneratedKeyColumns("top_level_abie_id");

        MapSqlParameterSource parameterSource = newSqlParameterSource()
                .addValue("owner_user_id", userId)
                .addValue("release_id", releaseId)
                .addValue("state", state.getValue());

        return jdbcInsert.executeAndReturnKey(parameterSource).longValue();
    }

    public void updateAbieIdOnTopLevelAbie(long abieId, long topLevelAbieId) {
        jdbcTemplate.update("UPDATE top_level_abie SET abie_id = :abie_id " +
                "WHERE top_level_abie_id = :top_level_abie_id", newSqlParameterSource()
                .addValue("abie_id", abieId)
                .addValue("top_level_abie_id", topLevelAbieId));
    }

    public void updateAbieIdAndStateOnTopLevelAbie(long abieId, long topLevelAbieId, BieState state) {
        jdbcTemplate.update("UPDATE top_level_abie SET abie_id = :abie_id, state = :state " +
                "WHERE top_level_abie_id = :top_level_abie_id", newSqlParameterSource()
                .addValue("abie_id", abieId)
                .addValue("state", state.getValue())
                .addValue("top_level_abie_id", topLevelAbieId));
    }

    public long getBizCtxIdByTopLevelAbieId(long topLevelAbieId) {
        return jdbcTemplate.queryForObject("SELECT biz_ctx_id FROM abie JOIN top_level_abie " +
                "ON abie.abie_id = top_level_abie.abie_id " +
                "WHERE top_level_abie.top_level_abie_id = :top_level_abie_id", newSqlParameterSource()
                .addValue("top_level_abie_id", topLevelAbieId), Long.class);
    }

    public long createAbie(User user, long basedAccId, long topLevelAbieId) {
        return createAbie(user, basedAccId, getBizCtxIdByTopLevelAbieId(topLevelAbieId), topLevelAbieId);
    }

    public long createAbie(User user, long basedAccId, long bizCtxId, long topLevelAbieId) {
        SimpleJdbcInsert jdbcInsert = jdbcTemplate.insert()
                .withTableName("abie")
                .usingColumns("guid", "based_acc_id", "biz_ctx_id",
                        "created_by", "last_updated_by", "creation_timestamp", "last_update_timestamp",
                        "state", "owner_top_level_abie_id")
                .usingGeneratedKeyColumns("abie_id");

        long userId = sessionService.userId(user);
        Date timestamp = new Date();

        MapSqlParameterSource parameterSource = newSqlParameterSource()
                .addValue("guid", SrtGuid.randomGuid())
                .addValue("based_acc_id", basedAccId)
                .addValue("biz_ctx_id", bizCtxId)
                .addValue("state", BieState.Editing.getValue())
                .addValue("owner_top_level_abie_id", topLevelAbieId)
                .addValue("created_by", userId)
                .addValue("last_updated_by", userId)
                .addValue("creation_timestamp", timestamp)
                .addValue("last_update_timestamp", timestamp);

        long abieId = jdbcInsert.executeAndReturnKey(parameterSource).longValue();
        return abieId;
    }

    public long createAsbiep(User user, long asccpId, long abieId, long topLevelAbieId) {
        SimpleJdbcInsert jdbcInsert = jdbcTemplate.insert()
                .withTableName("asbiep")
                .usingColumns("guid", "based_asccp_id", "role_of_abie_id",
                        "created_by", "last_updated_by", "creation_timestamp", "last_update_timestamp",
                        "owner_top_level_abie_id")
                .usingGeneratedKeyColumns("asbiep_id");

        long userId = sessionService.userId(user);
        Date timestamp = new Date();

        MapSqlParameterSource parameterSource = newSqlParameterSource()
                .addValue("guid", SrtGuid.randomGuid())
                .addValue("based_asccp_id", asccpId)
                .addValue("role_of_abie_id", abieId)
                .addValue("owner_top_level_abie_id", topLevelAbieId)
                .addValue("created_by", userId)
                .addValue("last_updated_by", userId)
                .addValue("creation_timestamp", timestamp)
                .addValue("last_update_timestamp", timestamp);

        long asbiepId = jdbcInsert.executeAndReturnKey(parameterSource).longValue();
        return asbiepId;
    }

    public long createAsbie(User user, long fromAbieId, long toAsbiepId, long basedAsccId,
                            int seqKey, long topLevelAbieId) {
        SimpleJdbcInsert jdbcInsert = jdbcTemplate.insert()
                .withTableName("asbie")
                .usingColumns("guid", "from_abie_id", "to_asbiep_id", "based_ascc_id",
                        "cardinality_min", "cardinality_max", "is_nillable",
                        "created_by", "last_updated_by", "creation_timestamp", "last_update_timestamp",
                        "seq_key", "is_used", "owner_top_level_abie_id")
                .usingGeneratedKeyColumns("asbie_id");

        long userId = sessionService.userId(user);
        Date timestamp = new Date();

        Cardinality cardinality =
                jdbcTemplate.queryForObject("SELECT cardinality_min, cardinality_max " +
                        "FROM ascc WHERE ascc_id = :ascc_id", newSqlParameterSource()
                        .addValue("ascc_id", basedAsccId), Cardinality.class);

        MapSqlParameterSource parameterSource = newSqlParameterSource()
                .addValue("guid", SrtGuid.randomGuid())
                .addValue("from_abie_id", fromAbieId)
                .addValue("to_asbiep_id", toAsbiepId)
                .addValue("based_ascc_id", basedAsccId)
                .addValue("cardinality_min", cardinality.getCardinalityMin())
                .addValue("cardinality_max", cardinality.getCardinalityMax())
                .addValue("is_nillable", false)
                .addValue("seq_key", seqKey)
                .addValue("is_used", false)
                .addValue("owner_top_level_abie_id", topLevelAbieId)
                .addValue("created_by", userId)
                .addValue("last_updated_by", userId)
                .addValue("creation_timestamp", timestamp)
                .addValue("last_update_timestamp", timestamp);

        long asbieId = jdbcInsert.executeAndReturnKey(parameterSource).longValue();
        return asbieId;
    }

    public long createBbiep(User user, long basedBccpId, long topLevelAbieId) {
        SimpleJdbcInsert jdbcInsert = jdbcTemplate.insert()
                .withTableName("bbiep")
                .usingColumns("guid", "based_bccp_id",
                        "created_by", "last_updated_by", "creation_timestamp", "last_update_timestamp",
                        "owner_top_level_abie_id")
                .usingGeneratedKeyColumns("bbiep_id");

        long userId = sessionService.userId(user);
        Date timestamp = new Date();

        MapSqlParameterSource parameterSource = newSqlParameterSource()
                .addValue("guid", SrtGuid.randomGuid())
                .addValue("based_bccp_id", basedBccpId)
                .addValue("owner_top_level_abie_id", topLevelAbieId)
                .addValue("created_by", userId)
                .addValue("last_updated_by", userId)
                .addValue("creation_timestamp", timestamp)
                .addValue("last_update_timestamp", timestamp);

        long bbiepId = jdbcInsert.executeAndReturnKey(parameterSource).longValue();
        return bbiepId;
    }

    public long createBbie(User user, long fromAbieId,
                           long toBbiepId, long basedBccId, long bdtId,
                           int seqKey, long topLevelAbieId) {
        SimpleJdbcInsert jdbcInsert = jdbcTemplate.insert()
                .withTableName("bbie")
                .usingColumns("guid", "from_abie_id", "to_bbiep_id", "based_bcc_id", "bdt_pri_restri_id",
                        "cardinality_min", "cardinality_max", "is_nillable", "is_null",
                        "created_by", "last_updated_by", "creation_timestamp", "last_update_timestamp",
                        "seq_key", "is_used", "owner_top_level_abie_id")
                .usingGeneratedKeyColumns("bbie_id");

        long userId = sessionService.userId(user);
        Date timestamp = new Date();

        Cardinality cardinality =
                jdbcTemplate.queryForObject("SELECT cardinality_min, cardinality_max " +
                        "FROM bcc WHERE bcc_id = :bcc_id", newSqlParameterSource()
                        .addValue("bcc_id", basedBccId), Cardinality.class);

        MapSqlParameterSource parameterSource = newSqlParameterSource()
                .addValue("guid", SrtGuid.randomGuid())
                .addValue("from_abie_id", fromAbieId)
                .addValue("to_bbiep_id", toBbiepId)
                .addValue("based_bcc_id", basedBccId)
                .addValue("bdt_pri_restri_id", getDefaultBdtPriRestriIdByBdtId(bdtId))
                .addValue("cardinality_min", cardinality.getCardinalityMin())
                .addValue("cardinality_max", cardinality.getCardinalityMax())
                .addValue("is_nillable", false)
                .addValue("is_null", false)
                .addValue("seq_key", seqKey)
                .addValue("is_used", false)
                .addValue("owner_top_level_abie_id", topLevelAbieId)
                .addValue("created_by", userId)
                .addValue("last_updated_by", userId)
                .addValue("creation_timestamp", timestamp)
                .addValue("last_update_timestamp", timestamp);

        long bbieId = jdbcInsert.executeAndReturnKey(parameterSource).longValue();
        return bbieId;
    }

    public long getDefaultBdtPriRestriIdByBdtId(long bdtId) {
        return jdbcTemplate.queryForObject(
                "SELECT bdt_pri_restri_id FROM bdt_pri_restri " +
                        "WHERE bdt_id = :bdt_id AND is_default = :is_default", newSqlParameterSource()
                        .addValue("bdt_id", bdtId)
                        .addValue("is_default", true), Long.class);
    }

    public long createBbieSc(User user, long bbieId, long dtScId,
                             long topLevelAbieId) {
        SimpleJdbcInsert jdbcInsert = jdbcTemplate.insert()
                .withTableName("bbie_sc")
                .usingColumns("guid", "bbie_id", "dt_sc_id", "dt_sc_pri_restri_id",
                        "cardinality_min", "cardinality_max", "is_used", "owner_top_level_abie_id")
                .usingGeneratedKeyColumns("bbie_sc_id");

        Cardinality cardinality =
                jdbcTemplate.queryForObject("SELECT cardinality_min, cardinality_max " +
                        "FROM dt_sc WHERE dt_sc_id = :dt_sc_id", newSqlParameterSource()
                        .addValue("dt_sc_id", dtScId), Cardinality.class);

        MapSqlParameterSource parameterSource = newSqlParameterSource()
                .addValue("guid", SrtGuid.randomGuid())
                .addValue("bbie_id", bbieId)
                .addValue("dt_sc_id", dtScId)
                .addValue("dt_sc_pri_restri_id", getDefaultDtScPriRestriIdByDtScId(dtScId))
                .addValue("cardinality_min", cardinality.getCardinalityMin())
                .addValue("cardinality_max", cardinality.getCardinalityMax())
                .addValue("is_used", false)
                .addValue("owner_top_level_abie_id", topLevelAbieId);

        long bbieScId = jdbcInsert.executeAndReturnKey(parameterSource).longValue();
        return bbieScId;
    }

    public long getDefaultDtScPriRestriIdByDtScId(long dtScId) {
        return jdbcTemplate.queryForObject(
                "SELECT bdt_sc_pri_restri_id FROM bdt_sc_pri_restri " +
                        "WHERE bdt_sc_id = :bdt_sc_id AND is_default = :is_default", newSqlParameterSource()
                        .addValue("bdt_sc_id", dtScId)
                        .addValue("is_default", true), Long.class);
    }

    public void updateState(long topLevelAbieId, BieState state) {
        MapSqlParameterSource parameterSource = newSqlParameterSource()
                .addValue("state", state.getValue())
                .addValue("top_level_abie_id", topLevelAbieId);

        jdbcTemplate.update("UPDATE top_level_abie SET state = :state " +
                "WHERE top_level_abie_id = :top_level_abie_id", parameterSource);

        jdbcTemplate.update("UPDATE abie SET state = :state " +
                "WHERE owner_top_level_abie_id = :top_level_abie_id", parameterSource);
    }

    public long appendLocalUserExtension(BieEditAcc eAcc, ACC ueAcc,
                                         long asccpId, long releaseId, User user) {

        return createNewUserExtensionGroupACC(ccListService.getAcc(eAcc.getAccId()), releaseId, user);
    }

    private long createNewUserExtensionGroupACC(ACC eAcc, long releaseId, User user) {
        ACC ueAcc = createACCForExtension(eAcc, user);
        createACCHistoryForExtension(ueAcc, 1, releaseId);

        ASCCP ueAsccp = createASCCPForExtension(eAcc, user, ueAcc);
        createASCCPHistoryForExtension(ueAsccp, 1, releaseId);

        ASCC ueAscc = createASCCForExtension(eAcc, user, ueAcc, ueAsccp);
        createASCCHistoryForExtension(ueAscc, 1, releaseId);

        return ueAcc.getAccId();
    }

    private ACC createACCForExtension(ACC eAcc, User user) {
        SimpleJdbcInsert jdbcInsert = jdbcTemplate.insert()
                .withTableName("acc")
                .usingColumns("guid", "object_class_term", "den", "definition", "oagis_component_type",
                        "created_by", "last_updated_by", "owner_user_id", "creation_timestamp", "last_update_timestamp",
                        "state", "revision_num", "revision_tracking_num", "namespace_id")
                .usingGeneratedKeyColumns("acc_id");

        String objectClassTerm = Utility.getUserExtensionGroupObjectClassTerm(eAcc.getObjectClassTerm());
        long namespaceId = namespaceService.getNamespaceIdByUri("http://www.openapplications.org/oagis/10");
        long userId = sessionService.userId(user);
        Date timestamp = new Date();

        MapSqlParameterSource parameterSource = newSqlParameterSource()
                .addValue("guid", SrtGuid.randomGuid())
                .addValue("object_class_term", objectClassTerm)
                .addValue("den", objectClassTerm + ". Details")
                .addValue("definition", "A system created component containing user extension to the " + eAcc.getObjectClassTerm() + ".")
                .addValue("oagis_component_type", OagisComponentType.UserExtensionGroup.getValue())
                .addValue("state", CcState.Editing.getValue())
                .addValue("revision_num", 0)
                .addValue("revision_tracking_num", 0)
                .addValue("namespace_id", namespaceId)
                .addValue("created_by", userId)
                .addValue("last_updated_by", userId)
                .addValue("owner_user_id", userId)
                .addValue("creation_timestamp", timestamp)
                .addValue("last_update_timestamp", timestamp);

        long ueAccId = jdbcInsert.executeAndReturnKey(parameterSource).longValue();
        return ccListService.getAcc(ueAccId);
    }

    private void createACCHistoryForExtension(ACC ueAcc, int revisionNum, long releaseId) {
        SimpleJdbcInsert jdbcInsert = jdbcTemplate.insert()
                .withTableName("acc")
                .usingColumns("guid", "object_class_term", "den", "definition", "oagis_component_type", "current_acc_id",
                        "created_by", "last_updated_by", "owner_user_id", "creation_timestamp", "last_update_timestamp",
                        "state", "revision_num", "revision_tracking_num", "revision_action", "release_id", "namespace_id")
                .usingGeneratedKeyColumns("acc_id");

        MapSqlParameterSource parameterSource = newSqlParameterSource()
                .addValue("guid", ueAcc.getGuid())
                .addValue("object_class_term", ueAcc.getObjectClassTerm())
                .addValue("den", ueAcc.getDen())
                .addValue("definition", ueAcc.getDefinition())
                .addValue("oagis_component_type", ueAcc.getOagisComponentType())
                .addValue("current_acc_id", ueAcc.getAccId())
                .addValue("state", ueAcc.getState())
                .addValue("revision_num", revisionNum)
                .addValue("revision_tracking_num", 1)
                .addValue("revision_action", RevisionAction.Insert.getValue())
                .addValue("release_id", releaseId)
                .addValue("namespace_id", ueAcc.getNamespaceId())
                .addValue("created_by", ueAcc.getCreatedBy())
                .addValue("last_updated_by", ueAcc.getLastUpdatedBy())
                .addValue("owner_user_id", ueAcc.getOwnerUserId())
                .addValue("creation_timestamp", ueAcc.getCreationTimestamp())
                .addValue("last_update_timestamp", ueAcc.getLastUpdateTimestamp());

        jdbcInsert.execute(parameterSource);
    }

    private ASCCP createASCCPForExtension(ACC eAcc, User user, ACC ueAcc) {
        SimpleJdbcInsert jdbcInsert = jdbcTemplate.insert()
                .withTableName("asccp")
                .usingColumns("guid", "property_term", "role_of_acc_id", "den", "definition",
                        "reusable_indicator", "is_deprecated",
                        "created_by", "last_updated_by", "owner_user_id", "creation_timestamp", "last_update_timestamp",
                        "state", "revision_num", "revision_tracking_num", "namespace_id")
                .usingGeneratedKeyColumns("asccp_id");

        long userId = sessionService.userId(user);
        Date timestamp = new Date();

        MapSqlParameterSource parameterSource = newSqlParameterSource()
                .addValue("guid", SrtGuid.randomGuid())
                .addValue("property_term", ueAcc.getObjectClassTerm())
                .addValue("role_of_acc_id", ueAcc.getAccId())
                .addValue("den", ueAcc.getObjectClassTerm() + ". " + ueAcc.getObjectClassTerm())
                .addValue("definition", "A system created component containing user extension to the " + eAcc.getObjectClassTerm() + ".")
                .addValue("reusable_indicator", false)
                .addValue("is_deprecated", false)
                .addValue("state", CcState.Published.getValue())
                .addValue("revision_num", 0)
                .addValue("revision_tracking_num", 0)
                .addValue("namespace_id", ueAcc.getNamespaceId())
                .addValue("created_by", userId)
                .addValue("last_updated_by", userId)
                .addValue("owner_user_id", userId)
                .addValue("creation_timestamp", timestamp)
                .addValue("last_update_timestamp", timestamp);

        long ueAsccpId = jdbcInsert.executeAndReturnKey(parameterSource).longValue();
        return ccListService.getAsccp(ueAsccpId);
    }

    private void createASCCPHistoryForExtension(ASCCP ueAsccp, int revisionNum, long releaseId) {
        SimpleJdbcInsert jdbcInsert = jdbcTemplate.insert()
                .withTableName("asccp")
                .usingColumns("guid", "property_term", "role_of_acc_id", "den", "definition",
                        "reusable_indicator", "is_deprecated", "current_asccp_id",
                        "created_by", "last_updated_by", "owner_user_id", "creation_timestamp", "last_update_timestamp",
                        "state", "revision_num", "revision_tracking_num", "revision_action", "release_id", "namespace_id")
                .usingGeneratedKeyColumns("asccp_id");

        MapSqlParameterSource parameterSource = newSqlParameterSource()
                .addValue("guid", ueAsccp.getGuid())
                .addValue("property_term", ueAsccp.getPropertyTerm())
                .addValue("role_of_acc_id", ueAsccp.getRoleOfAccId())
                .addValue("den", ueAsccp.getDen())
                .addValue("definition", ueAsccp.getDefinition())
                .addValue("reusable_indicator", ueAsccp.isReusableIndicator())
                .addValue("is_deprecated", ueAsccp.isDeprecated())
                .addValue("current_asccp_id", ueAsccp.getAsccpId())
                .addValue("definition", ueAsccp.getDefinition())
                .addValue("state", ueAsccp.getState())
                .addValue("revision_num", revisionNum)
                .addValue("revision_tracking_num", 1)
                .addValue("revision_action", RevisionAction.Insert.getValue())
                .addValue("release_id", releaseId)
                .addValue("namespace_id", ueAsccp.getNamespaceId())
                .addValue("created_by", ueAsccp.getCreatedBy())
                .addValue("last_updated_by", ueAsccp.getLastUpdatedBy())
                .addValue("owner_user_id", ueAsccp.getOwnerUserId())
                .addValue("creation_timestamp", ueAsccp.getCreationTimestamp())
                .addValue("last_update_timestamp", ueAsccp.getLastUpdateTimestamp());

        jdbcInsert.execute(parameterSource);
    }

    private ASCC createASCCForExtension(ACC eAcc, User user, ACC ueAcc, ASCCP ueAsccp) {
        SimpleJdbcInsert jdbcInsert = jdbcTemplate.insert()
                .withTableName("ascc")
                .usingColumns("guid", "cardinality_min", "cardinality_max", "seq_key", "from_acc_id", "to_asccp_id",
                        "den", "is_deprecated",
                        "created_by", "last_updated_by", "owner_user_id", "creation_timestamp", "last_update_timestamp",
                        "state", "revision_num", "revision_tracking_num")
                .usingGeneratedKeyColumns("ascc_id");

        long userId = sessionService.userId(user);
        Date timestamp = new Date();

        MapSqlParameterSource parameterSource = newSqlParameterSource()
                .addValue("guid", SrtGuid.randomGuid())
                .addValue("cardinality_min", 0)
                .addValue("cardinality_max", 1)
                .addValue("seq_key", 1)
                .addValue("from_acc_id", eAcc.getCurrentAccId())
                .addValue("to_asccp_id", ueAsccp.getAsccpId())
                .addValue("den", eAcc.getObjectClassTerm() + ". " + ueAsccp.getDen())
                .addValue("is_deprecated", false)
                .addValue("state", CcState.Editing.getValue())
                .addValue("revision_num", 0)
                .addValue("revision_tracking_num", 0)
                .addValue("created_by", userId)
                .addValue("last_updated_by", userId)
                .addValue("owner_user_id", userId)
                .addValue("creation_timestamp", timestamp)
                .addValue("last_update_timestamp", timestamp);

        long ueAsccId = jdbcInsert.executeAndReturnKey(parameterSource).longValue();
        return ccListService.getAscc(ueAsccId);
    }

    private void createASCCHistoryForExtension(ASCC ueAscc, int revisionNum, long releaseId) {
        SimpleJdbcInsert jdbcInsert = jdbcTemplate.insert()
                .withTableName("ascc")
                .usingColumns("guid", "cardinality_min", "cardinality_max", "seq_key", "from_acc_id", "to_asccp_id",
                        "den", "is_deprecated", "current_ascc_id",
                        "created_by", "last_updated_by", "owner_user_id", "creation_timestamp", "last_update_timestamp",
                        "state", "revision_num", "revision_tracking_num", "revision_action", "release_id")
                .usingGeneratedKeyColumns("ascc_id");

        MapSqlParameterSource parameterSource = newSqlParameterSource()
                .addValue("guid", ueAscc.getGuid())
                .addValue("cardinality_min", ueAscc.getCardinalityMin())
                .addValue("cardinality_max", ueAscc.getCardinalityMax())
                .addValue("seq_key", ueAscc.getSeqKey())
                .addValue("from_acc_id", ueAscc.getFromAccId())
                .addValue("to_asccp_id", ueAscc.getToAsccpId())
                .addValue("den", ueAscc.getDen())
                .addValue("is_deprecated", ueAscc.isDeprecated())
                .addValue("current_ascc_id", ueAscc.getAsccId())
                .addValue("definition", ueAscc.getDefinition())
                .addValue("state", ueAscc.getState())
                .addValue("revision_num", revisionNum)
                .addValue("revision_tracking_num", 1)
                .addValue("revision_action", RevisionAction.Insert.getValue())
                .addValue("release_id", releaseId)
                .addValue("created_by", ueAscc.getCreatedBy())
                .addValue("last_updated_by", ueAscc.getLastUpdatedBy())
                .addValue("owner_user_id", ueAscc.getOwnerUserId())
                .addValue("creation_timestamp", ueAscc.getCreationTimestamp())
                .addValue("last_update_timestamp", ueAscc.getLastUpdateTimestamp());

        jdbcInsert.execute(parameterSource);
    }

}
