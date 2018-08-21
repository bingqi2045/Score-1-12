package org.oagi.srt.gateway.http.api.cc_management.repository;

import org.oagi.srt.gateway.http.api.cc_management.data.*;
import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.oagi.srt.gateway.http.helper.SrtJdbcTemplate.newSqlParameterSource;

@Repository
public class CoreComponentRepository {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SrtJdbcTemplate jdbcTemplate;

    private String GET_ACC_STATEMENT = "SELECT `acc_id`,`guid`,`object_class_term`,`den`,`definition`,`definition_source`," +
            "`based_acc_id`,`object_class_qualifier`,`oagis_component_type`,`module_id`,`namespace_id`," +
            "`created_by`,`owner_user_id`,`last_updated_by`,`creation_timestamp`,`last_update_timestamp`," +
            "`state`,`revision_num`,`revision_tracking_num`,`revision_action`,`release_id`," +
            "`current_acc_id`,`is_deprecated` as deprecated,`is_abstract` FROM `acc`";

    @Cacheable(cacheNames = "core_components:acc")
    public List<ACC> getAccList() {
        return jdbcTemplate.queryForList(GET_ACC_STATEMENT, ACC.class);
    }

    @Cacheable(cacheNames = "core_components:acc")
    public ACC getAcc(long accId) {
        return jdbcTemplate.queryForObject(GET_ACC_STATEMENT + " WHERE `acc_id` = :acc_id",
                newSqlParameterSource().addValue("acc_id", accId), ACC.class);
    }

    private String GET_ASCC_STATEMENT = "SELECT `ascc_id`,`guid`,`cardinality_min`,`cardinality_max`,`seq_key`," +
            "`from_acc_id`,`to_asccp_id`,`den`,`definition`,`definition_source`,`is_deprecated` as deprecated," +
            "`created_by`,`owner_user_id`,`last_updated_by`,`creation_timestamp`,`last_update_timestamp`," +
            "`state`,`revision_num`,`revision_tracking_num`,`revision_action`,`release_id`," +
            "`current_ascc_id` FROM `ascc`";

    @Cacheable(cacheNames = "core_components:ascc")
    public List<ASCC> getAsccList() {
        return jdbcTemplate.queryForList(GET_ASCC_STATEMENT, ASCC.class);
    }

    private String GET_BCC_STATEMENT = "SELECT `bcc_id`,`guid`,`cardinality_min`,`cardinality_max`,`to_bccp_id`,`from_acc_id`," +
            "`seq_key`,`entity_type`,`den`,`definition`,`definition_source`," +
            "`created_by`,`owner_user_id`,`last_updated_by`,`creation_timestamp`,`last_update_timestamp`," +
            "`state`,`revision_num`,`revision_tracking_num`,`revision_action`,`release_id`," +
            "`current_bcc_id`,`is_deprecated` as deprecated,`is_nillable` as nillable,`default_value` FROM bcc";

    @Cacheable(cacheNames = "core_components:bcc")
    public List<BCC> getBccList() {
        return jdbcTemplate.queryForList(GET_BCC_STATEMENT, BCC.class);
    }

    private String GET_ASCCP_STATEMENT = "SELECT `asccp_id`,`guid`,`property_term`,`definition`,`definition_source`,`role_of_acc_id`,`den`," +
            "`created_by`,`owner_user_id`,`last_updated_by`,`creation_timestamp`,`last_update_timestamp`," +
            "`state`,`module_id`,`namespace_id`,`reusable_indicator`,`is_deprecated` as deprecated," +
            "`revision_num`,`revision_tracking_num`,`revision_action`,`release_id`," +
            "`current_asccp_id`,`is_nillable` as nillable FROM `asccp`";

    @Cacheable(cacheNames = "core_components:asccp")
    public List<ASCCP> getAsccpList() {
        return jdbcTemplate.queryForList(GET_ASCCP_STATEMENT, ASCCP.class);
    }

    private String GET_BCCP_STATEMENT = "SELECT `bccp_id`,`guid`,`property_term`,`representation_term`,`bdt_id`,`den`," +
            "`definition`,`definition_source`,`module_id`,`namespace_id`,`is_deprecated` as deprecated," +
            "`created_by`,`owner_user_id`,`last_updated_by`,`creation_timestamp`,`last_update_timestamp`," +
            "`state`,`revision_num`,`revision_tracking_num`,`revision_action`,`release_id`," +
            "`current_bccp_id`,`is_nillable` as nillable,`default_value` FROM `bccp`";

    @Cacheable(cacheNames = "core_components:bccp")
    public List<BCCP> getBccpList() {
        return jdbcTemplate.queryForList(GET_BCCP_STATEMENT, BCCP.class);
    }
}
