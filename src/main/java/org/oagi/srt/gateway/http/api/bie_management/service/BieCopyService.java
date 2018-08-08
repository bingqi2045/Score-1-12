package org.oagi.srt.gateway.http.api.bie_management.service;

import lombok.Data;
import org.oagi.srt.gateway.http.api.bie_management.data.BieCopyRequest;
import org.oagi.srt.gateway.http.api.bie_management.data.BieCopyResponse;
import org.oagi.srt.gateway.http.api.bie_management.data.TopLevelAbie;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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


    @Transactional
    public BieCopyResponse copyBie(User user, BieCopyRequest request) {

        long sourceTopLevelAbieId = request.getTopLevelAbieId();
        long bizCtxId = request.getBizCtxId();
        long userId = sessionService.userId(user);

        TopLevelAbie sourceTopLevelAbie = repository.getTopLevelAbieById(sourceTopLevelAbieId);

        long copiedTopLevelAbieId =
                repository.createTopLevelAbie(userId, sourceTopLevelAbie.getReleaseId());


        BieCopyResponse response = new BieCopyResponse();
        response.setTopLevelAbieId(copiedTopLevelAbieId);
        return response;
    }

    private List<Abie> getAbieByOwnerTopLevelAbieId(long ownerTopLevelAbieId) {
        return jdbcTemplate.queryForList("SELECT " +
                "abie_id, guid, based_acc_id, definition, " +
                "client_id, version, status, remark, biz_term FROM abie " +
                "WHERE owner_top_level_abie_id = :owner_top_level_abie_id", newSqlParameterSource()
                .addValue("owner_top_level_abie_id", ownerTopLevelAbieId), Abie.class);
    }

    private List<Asbie> getAsbieByOwnerTopLevelAbieId(long ownerTopLevelAbieId) {
        return jdbcTemplate.queryForList("SELECT " +
                "asbie_id, guid, from_abie_id, to_asbiep_id, based_ascc_id, " +
                "definition, cardinality_min, cardinality_max, is_nillable as nillable, " +
                "remark, seq_key, is_used as used FROM asbie " +
                "WHERE owner_top_level_abie_id = :owner_top_level_abie_id", newSqlParameterSource()
                .addValue("owner_top_level_abie_id", ownerTopLevelAbieId), Asbie.class);
    }

    private List<Bbie> getBbieByOwnerTopLevelAbieId(long ownerTopLevelAbieId) {
        return jdbcTemplate.queryForList("SELECT " +
                "bbie_id, guid, based_bcc_id, from_abie_id, to_bbiep_id, " +
                "bdt_pri_restri_id, code_list_id, agency_id_list_id, " +
                "cardinality_min, cardinality_max, default_value, is_nillable as nillable," +
                "fixed_value, is_null as nill, definition, remark, seq_key, is_used as used FROM bbie " +
                "WHERE owner_top_level_abie_id = :owner_top_level_abie_id", newSqlParameterSource()
                .addValue("owner_top_level_abie_id", ownerTopLevelAbieId), Bbie.class);
    }

    private List<Asbiep> getAsbiepByOwnerTopLevelAbieId(long ownerTopLevelAbieId) {
        return jdbcTemplate.queryForList("SELECT " +
                "asbiep_id, guid, based_asccp_id, role_of_abie_id, " +
                "definition, remark, biz_term FROM asbiep " +
                "WHERE owner_top_level_abie_id = :owner_top_level_abie_id", newSqlParameterSource()
                .addValue("owner_top_level_abie_id", ownerTopLevelAbieId), Asbiep.class);
    }

    private List<Bbiep> getBbiepByOwnerTopLevelAbieId(long ownerTopLevelAbieId) {
        return jdbcTemplate.queryForList("SELECT " +
                "bbiep_id, guid, based_bccp_id, definition, remark, biz_term FROM bbiep " +
                "WHERE owner_top_level_abie_id = :owner_top_level_abie_id", newSqlParameterSource()
                .addValue("owner_top_level_abie_id", ownerTopLevelAbieId), Bbiep.class);
    }

    private List<BbieSc> getBbieScByOwnerTopLevelAbieId(long ownerTopLevelAbieId) {
        return jdbcTemplate.queryForList("SELECT " +
                "bbie_sc_id, guid, bbie_id, dt_sc_id, " +
                "dt_sc_pri_restri_id, code_list_id, agency_id_list_id, " +
                "cardinality_min, cardinality_max, default_value, fixed_value, " +
                "definition, remark, biz_term, is_used as used FROM bbie_sc " +
                "WHERE owner_top_level_abie_id = :owner_top_level_abie_id", newSqlParameterSource()
                .addValue("owner_top_level_abie_id", ownerTopLevelAbieId), BbieSc.class);
    }


    @Data
    private class Abie {

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
    private class Asbie {

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
    private class Bbie {

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
    private class Asbiep {

        private long asbiepId;
        private String guid;
        private long basedAsccpId;
        private long roleOfAbieId;
        private String definition;
        private String remark;
        private String bizTerm;

    }

    @Data
    private class Bbiep {

        private long bbiepId;
        private String guid;
        private long basedBccpId;
        private String definition;
        private String remark;
        private String bizTerm;

    }

    @Data
    private class BbieSc {

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
