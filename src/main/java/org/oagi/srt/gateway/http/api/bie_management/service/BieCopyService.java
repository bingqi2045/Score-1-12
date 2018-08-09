package org.oagi.srt.gateway.http.api.bie_management.service;

import lombok.Data;
import org.oagi.srt.gateway.http.api.bie_management.data.BieCopyRequest;
import org.oagi.srt.gateway.http.api.bie_management.data.BieState;
import org.oagi.srt.gateway.http.api.bie_management.data.TopLevelAbie;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.oagi.srt.gateway.http.helper.SrtGuid;
import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.oagi.srt.gateway.http.api.bie_management.data.BieState.Instantiating;
import static org.oagi.srt.gateway.http.helper.SrtJdbcTemplate.newSqlParameterSource;

@Service
@Transactional
public class BieCopyService {

    @Autowired
    private SrtJdbcTemplate jdbcTemplate;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private BieRepository repository;

    @Autowired
    private RedisTemplate redisTemplate;

    @Transactional
    public void copyBie(User user, BieCopyRequest request) {
        long sourceTopLevelAbieId = request.getTopLevelAbieId();
        long bizCtxId = request.getBizCtxId();
        long userId = sessionService.userId(user);

        TopLevelAbie sourceTopLevelAbie = repository.getTopLevelAbieById(sourceTopLevelAbieId);
        long copiedTopLevelAbieId =
                repository.createTopLevelAbie(userId, sourceTopLevelAbie.getReleaseId(), Instantiating);


        long abieId = sourceTopLevelAbie.getAbieId();
        BieCopyAbie abie = getAbieByAbieIdAndOwnerTopLevelAbieId(abieId, sourceTopLevelAbieId);
        BieCopyAsbiep asbiep = getAsbiepByRoleOfAbieIdAndOwnerTopLevelAbieId(abieId, sourceTopLevelAbieId);


        Map<String, Long> message = new HashMap();
        message.put("sourceTopLevelAbieId", sourceTopLevelAbieId);
        message.put("copiedTopLevelAbieId", copiedTopLevelAbieId);
        message.put("bizCtxId", bizCtxId);
        message.put("userId", userId);

        long copiedAbieId = insertAbie(abie, message);
        asbiep.setRoleOfAbieId(copiedAbieId);
        long copiedAsbiepId = insertAsbiep(asbiep, message);

        repository.updateAbieIdOnTopLevelAbie(copiedAbieId, copiedTopLevelAbieId);

        /*
         * Message Publishing
         */
        redisTemplate.convertAndSend("bie-copy", message);
    }

    private BieCopyAbie getAbieByAbieIdAndOwnerTopLevelAbieId(long abieId, long ownerTopLevelAbieId) {
        return jdbcTemplate.queryForObject("SELECT " +
                "abie_id, guid, based_acc_id, definition, " +
                "client_id, version, status, remark, biz_term FROM abie " +
                "WHERE abie_id = :abie_id " +
                "AND owner_top_level_abie_id = :owner_top_level_abie_id", newSqlParameterSource()
                .addValue("abie_id", abieId)
                .addValue("owner_top_level_abie_id", ownerTopLevelAbieId), BieCopyAbie.class);
    }

    private BieCopyAsbiep getAsbiepByRoleOfAbieIdAndOwnerTopLevelAbieId(long roleOfAbieId, long ownerTopLevelAbieId) {
        return jdbcTemplate.queryForObject("SELECT " +
                "asbiep_id, guid, based_asccp_id, role_of_abie_id, " +
                "definition, remark, biz_term FROM asbiep " +
                "WHERE role_of_abie_id = :role_of_abie_id " +
                "AND owner_top_level_abie_id = :owner_top_level_abie_id", newSqlParameterSource()
                .addValue("role_of_abie_id", roleOfAbieId)
                .addValue("owner_top_level_abie_id", ownerTopLevelAbieId), BieCopyAsbiep.class);
    }

    public void handleCopyRequest(Map message) {
        System.out.println(message);
    }

    private List<BieCopyAbie> getAbieByOwnerTopLevelAbieId(long ownerTopLevelAbieId) {
        return jdbcTemplate.queryForList("SELECT " +
                "abie_id, guid, based_acc_id, definition, " +
                "client_id, version, status, remark, biz_term FROM abie " +
                "WHERE owner_top_level_abie_id = :owner_top_level_abie_id", newSqlParameterSource()
                .addValue("owner_top_level_abie_id", ownerTopLevelAbieId), BieCopyAbie.class);
    }

    private List<BieCopyAsbie> getAsbieByOwnerTopLevelAbieId(long ownerTopLevelAbieId) {
        return jdbcTemplate.queryForList("SELECT " +
                "asbie_id, guid, from_abie_id, to_asbiep_id, based_ascc_id, " +
                "definition, cardinality_min, cardinality_max, is_nillable as nillable, " +
                "remark, seq_key, is_used as used FROM asbie " +
                "WHERE owner_top_level_abie_id = :owner_top_level_abie_id", newSqlParameterSource()
                .addValue("owner_top_level_abie_id", ownerTopLevelAbieId), BieCopyAsbie.class);
    }

    private List<BieCopyBbie> getBbieByOwnerTopLevelAbieId(long ownerTopLevelAbieId) {
        return jdbcTemplate.queryForList("SELECT " +
                "bbie_id, guid, based_bcc_id, from_abie_id, to_bbiep_id, " +
                "bdt_pri_restri_id, code_list_id, agency_id_list_id, " +
                "cardinality_min, cardinality_max, default_value, is_nillable as nillable," +
                "fixed_value, is_null as nill, definition, remark, seq_key, is_used as used FROM bbie " +
                "WHERE owner_top_level_abie_id = :owner_top_level_abie_id", newSqlParameterSource()
                .addValue("owner_top_level_abie_id", ownerTopLevelAbieId), BieCopyBbie.class);
    }

    private List<BieCopyAsbiep> getAsbiepByOwnerTopLevelAbieId(long ownerTopLevelAbieId) {
        return jdbcTemplate.queryForList("SELECT " +
                "asbiep_id, guid, based_asccp_id, role_of_abie_id, " +
                "definition, remark, biz_term FROM asbiep " +
                "WHERE owner_top_level_abie_id = :owner_top_level_abie_id", newSqlParameterSource()
                .addValue("owner_top_level_abie_id", ownerTopLevelAbieId), BieCopyAsbiep.class);
    }

    private List<BieCopyBbiep> getBbiepByOwnerTopLevelAbieId(long ownerTopLevelAbieId) {
        return jdbcTemplate.queryForList("SELECT " +
                "bbiep_id, guid, based_bccp_id, definition, remark, biz_term FROM bbiep " +
                "WHERE owner_top_level_abie_id = :owner_top_level_abie_id", newSqlParameterSource()
                .addValue("owner_top_level_abie_id", ownerTopLevelAbieId), BieCopyBbiep.class);
    }

    private List<BieCopyBbieSc> getBbieScByOwnerTopLevelAbieId(long ownerTopLevelAbieId) {
        return jdbcTemplate.queryForList("SELECT " +
                "bbie_sc_id, guid, bbie_id, dt_sc_id, " +
                "dt_sc_pri_restri_id, code_list_id, agency_id_list_id, " +
                "cardinality_min, cardinality_max, default_value, fixed_value, " +
                "definition, remark, biz_term, is_used as used FROM bbie_sc " +
                "WHERE owner_top_level_abie_id = :owner_top_level_abie_id", newSqlParameterSource()
                .addValue("owner_top_level_abie_id", ownerTopLevelAbieId), BieCopyBbieSc.class);
    }



    private long insertAbie(BieCopyAbie abie, Map<String, Long> message) {
        SimpleJdbcInsert insert = jdbcTemplate.insert()
                .withTableName("abie")
                .usingColumns("guid", "based_acc_id", "biz_ctx_id", "definition",
                        "created_by", "last_updated_by", "creation_timestamp", "last_update_timestamp",
                        "state", "client_id", "version", "status", "remark", "biz_term",
                        "owner_top_level_abie_id")
                .usingGeneratedKeyColumns("abie_id");

        Date timestamp = new Date();
        MapSqlParameterSource parameterSource = newSqlParameterSource()
                .addValue("guid", SrtGuid.randomGuid())
                .addValue("based_acc_id", abie.getBasedAccId())
                .addValue("biz_ctx_id", message.get("bizCtxId"))
                .addValue("definition", abie.getDefinition())
                .addValue("created_by", message.get("userId"))
                .addValue("last_updated_by", message.get("userId"))
                .addValue("creation_timestamp", timestamp)
                .addValue("last_update_timestamp", timestamp)
                .addValue("state", BieState.Instantiating.getValue())
                .addValue("client_id", abie.getClientId())
                .addValue("version", abie.getVersion())
                .addValue("status", abie.getStatus())
                .addValue("remark", abie.getRemark())
                .addValue("biz_term", abie.getBizTerm())
                .addValue("owner_top_level_abie_id", message.get("copiedTopLevelAbieId"));
        return insert.executeAndReturnKey(parameterSource).longValue();
    }

    private long insertAsbiep(BieCopyAsbiep asbiep, Map<String, Long> message) {
        SimpleJdbcInsert jdbcInsert = jdbcTemplate.insert()
                .withTableName("asbiep")
                .usingColumns("guid", "based_asccp_id", "role_of_abie_id",
                        "definition", "remark", "biz_term",
                        "created_by", "last_updated_by", "creation_timestamp", "last_update_timestamp",
                        "owner_top_level_abie_id")
                .usingGeneratedKeyColumns("asbiep_id");

        Date timestamp = new Date();
        MapSqlParameterSource parameterSource = newSqlParameterSource()
                .addValue("guid", SrtGuid.randomGuid())
                .addValue("based_asccp_id", asbiep.getBasedAsccpId())
                .addValue("role_of_abie_id", asbiep.getRoleOfAbieId())
                .addValue("definition", asbiep.getDefinition())
                .addValue("remark", asbiep.getRemark())
                .addValue("biz_term", asbiep.getBizTerm())
                .addValue("owner_top_level_abie_id", message.get("copiedTopLevelAbieId"))
                .addValue("created_by", message.get("userId"))
                .addValue("last_updated_by", message.get("userId"))
                .addValue("creation_timestamp", timestamp)
                .addValue("last_update_timestamp", timestamp);

        long asbiepId = jdbcInsert.executeAndReturnKey(parameterSource).longValue();
        return asbiepId;
    }


    @Data
    public static class BieCopyAbie {

        private long abieId;
        private String guid;
        private long basedAccId;
        private String definition;
        private Long clientId;
        private String version;
        private String status;
        private String remark;
        private String bizTerm;

    }

    @Data
    public static class BieCopyAsbie {

        private long asbieId;
        private String guid;
        private long fromAbieId;
        private long toAsbiepId;
        private long basedAsccId;
        private String definition;
        private int cardinalityMin;
        private int cardinalityMax;
        private boolean nillable;
        private String remark;
        private double seqKey;
        private boolean used;

    }

    @Data
    public static class BieCopyBbie {

        private long bbieId;
        private String guid;
        private long basedBccId;
        private long fromAbieId;
        private long toBbiepId;
        private Long bdtPriRestriId;
        private Long codeListId;
        private Long agencyIdListId;
        private int cardinalityMin;
        private int cardinalityMax;
        private String defaultValue;
        private boolean nillable;
        private String fixedValue;
        private boolean nill;
        private String definition;
        private String remark;
        private double seqKey;
        private boolean used;

    }

    @Data
    public static class BieCopyAsbiep {

        private long asbiepId;
        private String guid;
        private long basedAsccpId;
        private long roleOfAbieId;
        private String definition;
        private String remark;
        private String bizTerm;

    }

    @Data
    public static class BieCopyBbiep {

        private long bbiepId;
        private String guid;
        private long basedBccpId;
        private String definition;
        private String remark;
        private String bizTerm;

    }

    @Data
    public static class BieCopyBbieSc {

        private long bbieScId;
        private String guid;
        private long bbieId;
        private long dtScId;
        private Long dtScPriRestriId;
        private Long codeListId;
        private Long agencyIdListId;
        private int cardinalityMin;
        private int cardinalityMax;
        private String defaultValue;
        private String fixedValue;
        private String definition;
        private String remark;
        private String bizTerm;
        private boolean used;

    }

}
