package org.oagi.srt.gateway.http.api.bie_management.service;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.data.BieState;
import org.oagi.srt.entity.jooq.Tables;
import org.oagi.srt.gateway.http.api.bie_management.data.bie_edit.*;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;
import org.oagi.srt.gateway.http.api.cc_management.helper.CcUtility;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.oagi.srt.gateway.http.helper.SrtGuid;
import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
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
import static org.jooq.impl.DSL.and;
import static org.oagi.srt.gateway.http.helper.SrtJdbcTemplate.newSqlParameterSource;

@Repository
public class BieRepository {

    @Autowired
    private SrtJdbcTemplate jdbcTemplate;

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private SessionService sessionService;

    public long getCurrentAccIdByTopLevelAbieId(long topLevelAbieId) {
        return dslContext.select(Tables.ACC.CURRENT_ACC_ID)
                .from(Tables.ABIE)
                .join(Tables.TOP_LEVEL_ABIE).on(Tables.ABIE.ABIE_ID.eq(Tables.TOP_LEVEL_ABIE.ABIE_ID))
                .join(Tables.ACC).on(Tables.ABIE.BASED_ACC_ID.eq(Tables.ACC.ACC_ID))
                .where(Tables.TOP_LEVEL_ABIE.TOP_LEVEL_ABIE_ID.eq(ULong.valueOf(topLevelAbieId)))
                .fetchOneInto(Long.class);
    }

    public BieEditAcc getAccByCurrentAccId(long currentAccId, long releaseId) {
        // BIE only can see the ACCs whose state is in Published.
        List<BieEditAcc> accList = dslContext.select(
                Tables.ACC.ACC_ID,
                Tables.ACC.CURRENT_ACC_ID,
                Tables.ACC.BASED_ACC_ID,
                Tables.ACC.OAGIS_COMPONENT_TYPE,
                Tables.ACC.REVISION_NUM,
                Tables.ACC.REVISION_TRACKING_NUM,
                Tables.ACC.RELEASE_ID)
                .from(Tables.ACC)
                .where(and(
                        Tables.ACC.REVISION_NUM.greaterThan(0),
                        Tables.ACC.STATE.eq(CcState.Published.getValue()),
                        Tables.ACC.CURRENT_ACC_ID.eq(ULong.valueOf(currentAccId))))
                .fetchInto(BieEditAcc.class);
        return CcUtility.getLatestEntity(releaseId, accList);
    }

    public BieEditAcc getAcc(long accId) {
        List<BieEditAcc> res = dslContext.select(
                Tables.ACC.ACC_ID,
                Tables.ACC.CURRENT_ACC_ID,
                Tables.ACC.BASED_ACC_ID,
                Tables.ACC.OAGIS_COMPONENT_TYPE,
                Tables.ACC.REVISION_NUM,
                Tables.ACC.REVISION_TRACKING_NUM,
                Tables.ACC.RELEASE_ID)
                .from(Tables.ACC)
                .where(Tables.ACC.ACC_ID.eq(ULong.valueOf(accId)))
                .fetchInto(BieEditAcc.class);
        if (res.isEmpty()) {
            throw new EmptyResultDataAccessException(1);
        }
        return res.get(0);
    }

    public BieEditBbiep getBbiep(long bbiepId, long topLevelAbieId) {
        return dslContext.select(
                Tables.BBIEP.BBIEP_ID,
                Tables.BBIEP.BASED_BCCP_ID)
                .from(Tables.BBIEP)
                .where(and(
                        Tables.BBIEP.OWNER_TOP_LEVEL_ABIE_ID.eq(ULong.valueOf(topLevelAbieId)),
                        Tables.BBIEP.BBIEP_ID.eq(ULong.valueOf(bbiepId))))
                .fetchOptionalInto(BieEditBbiep.class).orElse(null);
    }

    public BccForBie getBcc(long bccId) {
        return dslContext.select(
                Tables.BCC.BCC_ID,
                Tables.BCC.CURRENT_BCC_ID,
                Tables.BCC.GUID,
                Tables.BCC.CARDINALITY_MIN,
                Tables.BCC.CARDINALITY_MAX,
                Tables.BCC.DEN,
                Tables.BCC.DEFINITION,
                Tables.BCC.FROM_ACC_ID,
                Tables.BCC.TO_BCCP_ID,
                Tables.BCC.ENTITY_TYPE,
                Tables.BCC.REVISION_NUM,
                Tables.BCC.REVISION_TRACKING_NUM,
                Tables.BCC.RELEASE_ID)
                .from(Tables.BCC)
                .where(Tables.BCC.BCC_ID.eq(ULong.valueOf(bccId)))
                .fetchOptionalInto(BccForBie.class).orElse(null);
    }

    public BieEditBccp getBccp(long bccpId) {
        return dslContext.select(
                Tables.BCCP.BCCP_ID,
                Tables.BCCP.CURRENT_BCCP_ID,
                Tables.BCCP.GUID,
                Tables.BCCP.PROPERTY_TERM,
                Tables.BCCP.REVISION_NUM,
                Tables.BCCP.REVISION_TRACKING_NUM,
                Tables.BCCP.RELEASE_ID)
                .from(Tables.BCCP)
                .where(Tables.BCCP.BCCP_ID.eq(ULong.valueOf(bccpId)))
                .fetchOptionalInto(BieEditBccp.class).orElse(null);
    }

    public int getCountDtScByOwnerDtId(long ownerDtId) {
        return jdbcTemplate.queryForObject("SELECT count(*) FROM dt_sc " +
                "WHERE owner_dt_id = :owner_dt_id AND cardinality_max != 0", newSqlParameterSource()
                .addValue("owner_dt_id", ownerDtId), Integer.class);
    }

    public int getCountBbieScByBbieIdAndIsUsedAndOwnerTopLevelAbieId(Long bbieId,
                                                                     boolean used, long ownerTopLevelAbieId) {
        if (bbieId == null || bbieId == 0L) {
            return 0;
        }

        return dslContext.selectCount()
                .from(Tables.BBIE_SC)
                .where(and(Tables.BBIE_SC.BBIE_ID.eq(ULong.valueOf(bbieId)),
                        Tables.BBIE_SC.IS_USED.eq((byte) ((used) ? 1 : 0)),
                        Tables.BBIE_SC.OWNER_TOP_LEVEL_ABIE_ID.eq(ULong.valueOf(ownerTopLevelAbieId))))
                .fetchOptionalInto(Integer.class).orElse(0);
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
        List<BieEditAsccp> asccpList = dslContext.select(
                Tables.ASCCP.ASCCP_ID,
                Tables.ASCCP.CURRENT_ASCCP_ID,
                Tables.ASCCP.GUID,
                Tables.ASCCP.PROPERTY_TERM,
                Tables.ASCCP.ROLE_OF_ACC_ID,
                Tables.ASCCP.REVISION_NUM,
                Tables.ASCCP.REVISION_TRACKING_NUM,
                Tables.ASCCP.RELEASE_ID)
                .from(Tables.ASCCP)
                .where(and(
                        Tables.ASCCP.REVISION_NUM.greaterThan(0),
                        Tables.ASCCP.CURRENT_ASCCP_ID.eq(ULong.valueOf(currentAsccpId))))
                .fetchInto(BieEditAsccp.class);
        return CcUtility.getLatestEntity(releaseId, asccpList);
    }

    public BieEditBccp getBccpByCurrentBccpId(long currentBccpId, long releaseId) {
        List<BieEditBccp> bccpList = dslContext.select(
                Tables.BCCP.BCCP_ID,
                Tables.BCCP.CURRENT_BCCP_ID,
                Tables.BCCP.GUID,
                Tables.BCCP.PROPERTY_TERM,
                Tables.BCCP.BDT_ID,
                Tables.BCCP.REVISION_NUM,
                Tables.BCCP.REVISION_TRACKING_NUM,
                Tables.BCCP.RELEASE_ID)
                .from(Tables.BCCP)
                .where(and(
                        Tables.BCCP.REVISION_NUM.greaterThan(0),
                        Tables.BCCP.CURRENT_BCCP_ID.eq(ULong.valueOf(currentBccpId))))
                .fetchInto(BieEditBccp.class);
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
        return dslContext.select(
                Tables.ASCC.ASCC_ID,
                Tables.ASCC.CURRENT_ASCC_ID,
                Tables.ASCC.GUID,
                Tables.ASCC.FROM_ACC_ID,
                Tables.ASCC.TO_ASCCP_ID,
                Tables.ASCC.SEQ_KEY,
                Tables.ASCC.REVISION_NUM,
                Tables.ASCC.REVISION_TRACKING_NUM,
                Tables.ASCC.RELEASE_ID)
                .from(Tables.ASCC)
                .where(and(
                        Tables.ASCC.REVISION_NUM.greaterThan(0),
                        Tables.ASCC.FROM_ACC_ID.eq(ULong.valueOf(fromAccId)),
                        Tables.ASCC.RELEASE_ID.lessOrEqual(ULong.valueOf(releaseId))
                )).fetchInto(BieEditAscc.class)
                .stream()
                .collect(groupingBy(BieEditAscc::getGuid)).values().stream()
                .map(e -> CcUtility.getLatestEntity(releaseId, e))
                .filter(e -> e != null).collect(Collectors.toList());
    }

    public List<BieEditBcc> getBccListByFromAccId(long fromAccId, long releaseId) {
        List<BieEditBcc> bccList = dslContext.select(
                Tables.BCC.BCC_ID,
                Tables.BCC.CURRENT_BCC_ID,
                Tables.BCC.GUID,
                Tables.BCC.FROM_ACC_ID,
                Tables.BCC.TO_BCCP_ID,
                Tables.BCC.SEQ_KEY,
                Tables.BCC.ENTITY_TYPE,
                Tables.BCC.REVISION_NUM,
                Tables.BCC.REVISION_TRACKING_NUM,
                Tables.BCC.RELEASE_ID)
                .from(Tables.BCC)
                .where(and(
                        Tables.BCC.REVISION_NUM.greaterThan(0),
                        Tables.BCC.FROM_ACC_ID.eq(ULong.valueOf(fromAccId)),
                        Tables.BCC.RELEASE_ID.lessOrEqual(ULong.valueOf(releaseId))
                ))
                .fetchInto(BieEditBcc.class);

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

}
