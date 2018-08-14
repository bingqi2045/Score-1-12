package org.oagi.srt.gateway.http.api.bie_management.service;

import lombok.Data;
import org.oagi.srt.gateway.http.api.bie_management.data.BieCopyRequest;
import org.oagi.srt.gateway.http.api.bie_management.data.BieState;
import org.oagi.srt.gateway.http.api.bie_management.data.TopLevelAbie;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.oagi.srt.gateway.http.helper.SrtGuid;
import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static java.util.stream.Collectors.groupingBy;
import static org.oagi.srt.gateway.http.api.bie_management.data.BieState.Init;
import static org.oagi.srt.gateway.http.helper.SrtJdbcTemplate.newSqlParameterSource;

@Service
@Transactional
public class BieCopyService {

    private Logger logger = LoggerFactory.getLogger(getClass());

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
                repository.createTopLevelAbie(userId, sourceTopLevelAbie.getReleaseId(), Init);

        Map<String, Long> message = new HashMap();
        message.put("sourceTopLevelAbieId", sourceTopLevelAbieId);
        message.put("copiedTopLevelAbieId", copiedTopLevelAbieId);
        message.put("bizCtxId", bizCtxId);
        message.put("userId", userId);

        /*
         * Message Publishing
         */
        redisTemplate.convertAndSend("bie-copy", message);
    }


    /**
     * This method is invoked by 'bie-copy' channel subscriber.
     *
     * @param message
     */
    @Transactional
    public void handleCopyRequest(Map<String, Long> message) {
        logger.debug("Received copying request: " + message);

        BieCopyContext copyContext = new BieCopyContext(message);
        copyContext.execute();
    }



    private class BieCopyContext {

        private TopLevelAbie sourceTopLevelAbie;
        private TopLevelAbie copiedTopLevelAbie;
        private long bizCtxId;
        private long userId;

        private Date timestamp;

        private List<BieCopyAbie> abieList;

        private List<BieCopyAsbiep> asbiepList;
        private Map<Long, List<BieCopyAsbiep>> roleOfAbieToAsbiepMap;

        private List<BieCopyBbiep> bbiepList;

        private List<BieCopyAsbie> asbieList;
        private Map<Long, List<BieCopyAsbie>> fromAbieToAsbieMap;
        private Map<Long, List<BieCopyAsbie>> toAsbiepToAsbieMap;

        private List<BieCopyBbie> bbieList;
        private Map<Long, List<BieCopyBbie>> fromAbieToBbieMap;
        private Map<Long, List<BieCopyBbie>> toBbiepToBbieMap;

        private List<BieCopyBbieSc> bbieScList;
        private Map<Long, List<BieCopyBbieSc>> bbieToBbieScMap;

        public BieCopyContext(Map<String, Long> message) {
            long sourceTopLevelAbieId = message.get("sourceTopLevelAbieId");
            sourceTopLevelAbie = repository.getTopLevelAbieById(sourceTopLevelAbieId);

            long copiedTopLevelAbieId = message.get("copiedTopLevelAbieId");
            copiedTopLevelAbie = repository.getTopLevelAbieById(copiedTopLevelAbieId);

            bizCtxId = message.get("bizCtxId");
            userId = message.get("userId");

            abieList = getAbieByOwnerTopLevelAbieId(sourceTopLevelAbieId);

            asbiepList = getAsbiepByOwnerTopLevelAbieId(sourceTopLevelAbieId);
            roleOfAbieToAsbiepMap = asbiepList.stream().collect(groupingBy(BieCopyAsbiep::getRoleOfAbieId));

            bbiepList = getBbiepByOwnerTopLevelAbieId(sourceTopLevelAbieId);

            asbieList = getAsbieByOwnerTopLevelAbieId(sourceTopLevelAbieId);
            fromAbieToAsbieMap = asbieList.stream().collect(groupingBy(BieCopyAsbie::getFromAbieId));
            toAsbiepToAsbieMap = asbieList.stream().collect(groupingBy(BieCopyAsbie::getToAsbiepId));

            bbieList = getBbieByOwnerTopLevelAbieId(sourceTopLevelAbieId);
            fromAbieToBbieMap = bbieList.stream().collect(groupingBy(BieCopyBbie::getFromAbieId));
            toBbiepToBbieMap = bbieList.stream().collect(groupingBy(BieCopyBbie::getToBbiepId));

            bbieScList = getBbieScByOwnerTopLevelAbieId(sourceTopLevelAbieId);
            bbieToBbieScMap = bbieScList.stream().collect(groupingBy(BieCopyBbieSc::getBbieId));
        }

        public void execute() {
            timestamp = new Date();
            logger.debug("Begin copying from " + sourceTopLevelAbie.getTopLevelAbieId() +
                    " to " + copiedTopLevelAbie.getTopLevelAbieId());

            for (BieCopyAbie abie : abieList) {
                long previousAbieId = abie.getAbieId();
                long nextAbieId = insertAbie(abie);

                fireChangeEvent("abie", previousAbieId, nextAbieId);
            }

            repository.updateAbieIdOnTopLevelAbie(
                    copiedTopLevelAbie.getAbieId(),
                    copiedTopLevelAbie.getTopLevelAbieId());

            for (BieCopyAsbiep asbiep : asbiepList) {
                long previousAsbiepId = asbiep.getAsbiepId();
                long nextAsbiepId = insertAsbiep(asbiep);

                fireChangeEvent("asbiep", previousAsbiepId, nextAsbiepId);
            }

            for (BieCopyBbiep bbiep : bbiepList) {
                long previousBbiepId = bbiep.getBbiepId();
                long nextBbiepId = insertBbiep(bbiep);

                fireChangeEvent("bbiep", previousBbiepId, nextBbiepId);
            }

            for (BieCopyAsbie asbie : asbieList) {
                long previousAsbieId = asbie.getAsbieId();
                long nextAsbieId = insertAsbie(asbie);

                fireChangeEvent("asbie", previousAsbieId, nextAsbieId);
            }

            for (BieCopyBbie bbie : bbieList) {
                long previousBbieId = bbie.getBbieId();
                long nextBbieId = insertBbie(bbie);

                fireChangeEvent("bbie", previousBbieId, nextBbieId);
            }

            for (BieCopyBbieSc bbieSc : bbieScList) {
                long previousBbieScId = bbieSc.getBbieId();
                long nextBbieScId = insertBbieSc(bbieSc);

                fireChangeEvent("bbie_sc", previousBbieScId, nextBbieScId);
            }

            repository.updateState(copiedTopLevelAbie.getTopLevelAbieId(), BieState.Editing);

            logger.debug("End copying from " + sourceTopLevelAbie.getTopLevelAbieId() +
                    " to " + copiedTopLevelAbie.getTopLevelAbieId());
        }


        private void fireChangeEvent(String type, long previousVal, long nextVal) {
            switch (type) {
                case "abie":
                    if (sourceTopLevelAbie.getAbieId() == previousVal) {
                        copiedTopLevelAbie.setAbieId(nextVal);
                    }

                    roleOfAbieToAsbiepMap.getOrDefault(previousVal, Collections.emptyList()).stream().forEach(asbiep -> {
                        asbiep.setRoleOfAbieId(nextVal);
                    });
                    fromAbieToAsbieMap.getOrDefault(previousVal, Collections.emptyList()).stream().forEach(asbie -> {
                        asbie.setFromAbieId(nextVal);
                    });
                    fromAbieToBbieMap.getOrDefault(previousVal, Collections.emptyList()).stream().forEach(bbie -> {
                        bbie.setFromAbieId(nextVal);
                    });

                    break;

                case "asbiep":
                    toAsbiepToAsbieMap.getOrDefault(previousVal, Collections.emptyList()).stream().forEach(asbie -> {
                        asbie.setToAsbiepId(nextVal);
                    });

                    break;

                case "bbiep":
                    toBbiepToBbieMap.getOrDefault(previousVal, Collections.emptyList()).stream().forEach(bbie -> {
                        bbie.setToBbiepId(nextVal);
                    });

                    break;

                case "bbie":
                    bbieToBbieScMap.getOrDefault(previousVal, Collections.emptyList()).stream().forEach(bbieSc -> {
                        bbieSc.setBbieId(nextVal);
                    });

                    break;
            }
        }



        private long insertAbie(BieCopyAbie abie) {
            SimpleJdbcInsert insert = jdbcTemplate.insert()
                    .withTableName("abie")
                    .usingColumns("guid", "based_acc_id", "biz_ctx_id", "definition",
                            "created_by", "last_updated_by", "creation_timestamp", "last_update_timestamp",
                            "state", "client_id", "version", "status", "remark", "biz_term",
                            "owner_top_level_abie_id")
                    .usingGeneratedKeyColumns("abie_id");

            MapSqlParameterSource parameterSource = newSqlParameterSource()
                    .addValue("guid", SrtGuid.randomGuid())
                    .addValue("based_acc_id", abie.getBasedAccId())
                    .addValue("biz_ctx_id", bizCtxId)
                    .addValue("definition", abie.getDefinition())
                    .addValue("created_by", userId)
                    .addValue("last_updated_by", userId)
                    .addValue("creation_timestamp", timestamp)
                    .addValue("last_update_timestamp", timestamp)
                    .addValue("state", BieState.Init.getValue())
                    .addValue("client_id", abie.getClientId())
                    .addValue("version", abie.getVersion())
                    .addValue("status", abie.getStatus())
                    .addValue("remark", abie.getRemark())
                    .addValue("biz_term", abie.getBizTerm())
                    .addValue("owner_top_level_abie_id", copiedTopLevelAbie.getTopLevelAbieId());
            return insert.executeAndReturnKey(parameterSource).longValue();
        }

        private long insertAsbiep(BieCopyAsbiep asbiep) {
            SimpleJdbcInsert jdbcInsert = jdbcTemplate.insert()
                    .withTableName("asbiep")
                    .usingColumns("guid", "based_asccp_id", "role_of_abie_id",
                            "definition", "remark", "biz_term",
                            "created_by", "last_updated_by", "creation_timestamp", "last_update_timestamp",
                            "owner_top_level_abie_id")
                    .usingGeneratedKeyColumns("asbiep_id");

            MapSqlParameterSource parameterSource = newSqlParameterSource()
                    .addValue("guid", SrtGuid.randomGuid())
                    .addValue("based_asccp_id", asbiep.getBasedAsccpId())
                    .addValue("role_of_abie_id", asbiep.getRoleOfAbieId())
                    .addValue("definition", asbiep.getDefinition())
                    .addValue("remark", asbiep.getRemark())
                    .addValue("biz_term", asbiep.getBizTerm())
                    .addValue("owner_top_level_abie_id", copiedTopLevelAbie.getTopLevelAbieId())
                    .addValue("created_by", userId)
                    .addValue("last_updated_by", userId)
                    .addValue("creation_timestamp", timestamp)
                    .addValue("last_update_timestamp", timestamp);

            long asbiepId = jdbcInsert.executeAndReturnKey(parameterSource).longValue();
            return asbiepId;
        }

        private long insertBbiep(BieCopyBbiep bbiep) {
            SimpleJdbcInsert jdbcInsert = jdbcTemplate.insert()
                    .withTableName("bbiep")
                    .usingColumns("guid", "based_bccp_id",
                            "definition", "remark", "biz_term",
                            "created_by", "last_updated_by", "creation_timestamp", "last_update_timestamp",
                            "owner_top_level_abie_id")
                    .usingGeneratedKeyColumns("bbiep_id");

            MapSqlParameterSource parameterSource = newSqlParameterSource()
                    .addValue("guid", SrtGuid.randomGuid())
                    .addValue("based_bccp_id", bbiep.getBasedBccpId())
                    .addValue("definition", bbiep.getDefinition())
                    .addValue("remark", bbiep.getRemark())
                    .addValue("biz_term", bbiep.getBizTerm())
                    .addValue("owner_top_level_abie_id", copiedTopLevelAbie.getTopLevelAbieId())
                    .addValue("created_by", userId)
                    .addValue("last_updated_by", userId)
                    .addValue("creation_timestamp", timestamp)
                    .addValue("last_update_timestamp", timestamp);

            long bbiepId = jdbcInsert.executeAndReturnKey(parameterSource).longValue();
            return bbiepId;
        }

        private long insertAsbie(BieCopyAsbie asbie) {
            SimpleJdbcInsert jdbcInsert = jdbcTemplate.insert()
                    .withTableName("asbie")
                    .usingColumns("guid", "from_abie_id", "to_asbiep_id", "based_ascc_id",
                            "definition", "remark",
                            "cardinality_min", "cardinality_max", "is_nillable",
                            "created_by", "last_updated_by", "creation_timestamp", "last_update_timestamp",
                            "seq_key", "is_used", "owner_top_level_abie_id")
                    .usingGeneratedKeyColumns("asbie_id");

            MapSqlParameterSource parameterSource = newSqlParameterSource()
                    .addValue("guid", SrtGuid.randomGuid())
                    .addValue("from_abie_id", asbie.getFromAbieId())
                    .addValue("to_asbiep_id", asbie.getToAsbiepId())
                    .addValue("based_ascc_id", asbie.getBasedAsccId())
                    .addValue("definition", asbie.getDefinition())
                    .addValue("remark", asbie.getRemark())
                    .addValue("cardinality_min", asbie.getCardinalityMin())
                    .addValue("cardinality_max", asbie.getCardinalityMax())
                    .addValue("is_nillable", asbie.isNillable())
                    .addValue("seq_key", asbie.getSeqKey())
                    .addValue("is_used", asbie.isUsed())
                    .addValue("owner_top_level_abie_id", copiedTopLevelAbie.getTopLevelAbieId())
                    .addValue("created_by", userId)
                    .addValue("last_updated_by", userId)
                    .addValue("creation_timestamp", timestamp)
                    .addValue("last_update_timestamp", timestamp);

            long asbieId = jdbcInsert.executeAndReturnKey(parameterSource).longValue();
            return asbieId;
        }

        private long insertBbie(BieCopyBbie bbie) {
            SimpleJdbcInsert jdbcInsert = jdbcTemplate.insert()
                    .withTableName("bbie")
                    .usingColumns("guid", "from_abie_id", "to_bbiep_id", "based_bcc_id",
                            "bdt_pri_restri_id", "code_list_id", "agency_id_list_id",
                            "default_value", "fixed_value", "definition", "remark",
                            "cardinality_min", "cardinality_max", "is_nillable", "is_null",
                            "created_by", "last_updated_by", "creation_timestamp", "last_update_timestamp",
                            "seq_key", "is_used", "owner_top_level_abie_id")
                    .usingGeneratedKeyColumns("bbie_id");

            MapSqlParameterSource parameterSource = newSqlParameterSource()
                    .addValue("guid", SrtGuid.randomGuid())
                    .addValue("from_abie_id", bbie.getFromAbieId())
                    .addValue("to_bbiep_id", bbie.getToBbiepId())
                    .addValue("based_bcc_id", bbie.getBasedBccId())
                    .addValue("bdt_pri_restri_id", bbie.getBdtPriRestriId())
                    .addValue("code_list_id", bbie.getCodeListId())
                    .addValue("agency_id_list_id", bbie.getAgencyIdListId())
                    .addValue("default_value", bbie.getDefaultValue())
                    .addValue("fixed_value", bbie.getFixedValue())
                    .addValue("definition", bbie.getDefinition())
                    .addValue("remark", bbie.getRemark())
                    .addValue("cardinality_min", bbie.getCardinalityMin())
                    .addValue("cardinality_max", bbie.getCardinalityMax())
                    .addValue("is_nillable", bbie.isNillable())
                    .addValue("is_null", bbie.isNill())
                    .addValue("seq_key", bbie.getSeqKey())
                    .addValue("is_used", bbie.isUsed())
                    .addValue("owner_top_level_abie_id", copiedTopLevelAbie.getTopLevelAbieId())
                    .addValue("created_by", userId)
                    .addValue("last_updated_by", userId)
                    .addValue("creation_timestamp", timestamp)
                    .addValue("last_update_timestamp", timestamp);

            long bbieId = jdbcInsert.executeAndReturnKey(parameterSource).longValue();
            return bbieId;
        }

        private long insertBbieSc(BieCopyBbieSc bbieSc) {
            SimpleJdbcInsert jdbcInsert = jdbcTemplate.insert()
                    .withTableName("bbie_sc")
                    .usingColumns("guid", "bbie_id", "dt_sc_id",
                            "dt_sc_pri_restri_id", "code_list_id", "agency_id_list_id",
                            "default_value", "fixed_value", "definition", "remark", "biz_term",
                            "cardinality_min", "cardinality_max", "is_used", "owner_top_level_abie_id")
                    .usingGeneratedKeyColumns("bbie_sc_id");

            MapSqlParameterSource parameterSource = newSqlParameterSource()
                    .addValue("guid", SrtGuid.randomGuid())
                    .addValue("bbie_id", bbieSc.getBbieId())
                    .addValue("dt_sc_id", bbieSc.getDtScId())
                    .addValue("dt_sc_pri_restri_id", bbieSc.getDtScPriRestriId())
                    .addValue("code_list_id", bbieSc.getCodeListId())
                    .addValue("agency_id_list_id", bbieSc.getAgencyIdListId())
                    .addValue("default_value", bbieSc.getDefaultValue())
                    .addValue("fixed_value", bbieSc.getFixedValue())
                    .addValue("definition", bbieSc.getDefinition())
                    .addValue("remark", bbieSc.getRemark())
                    .addValue("biz_term", bbieSc.getBizTerm())
                    .addValue("cardinality_min", bbieSc.getCardinalityMin())
                    .addValue("cardinality_max", bbieSc.getCardinalityMax())
                    .addValue("is_used", bbieSc.isUsed())
                    .addValue("owner_top_level_abie_id", copiedTopLevelAbie.getTopLevelAbieId());

            long bbieScId = jdbcInsert.executeAndReturnKey(parameterSource).longValue();
            return bbieScId;
        }
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
