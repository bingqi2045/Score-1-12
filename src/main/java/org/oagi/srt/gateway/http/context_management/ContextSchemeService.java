package org.oagi.srt.gateway.http.context_management;

import org.oagi.srt.gateway.http.helper.SrtGuid;
import org.oagi.srt.gateway.http.security.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ContextSchemeService {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    private SessionService sessionService;

    private String GET_CONTEXT_SCHEME_LIST_STATEMENT =
            "SELECT ctx_scheme_id, ctx_scheme.guid, scheme_name, ctx_scheme.ctx_category_id, ctx_category.name as ctx_category_name," +
                    "scheme_id, scheme_agency_id, scheme_version_id, ctx_scheme.description, last_update_timestamp " +
                    "FROM ctx_scheme JOIN ctx_category ON ctx_scheme.ctx_category_id = ctx_category.ctx_category_id";

    public List<ContextScheme> getContextSchemeList() {
        return jdbcTemplate.query(GET_CONTEXT_SCHEME_LIST_STATEMENT,
                new BeanPropertyRowMapper(ContextScheme.class));
    }

    private String CREATE_CONTEXT_SCHEME_STATEMENT =
            "INSERT INTO ctx_scheme (guid, scheme_name, ctx_category_id, " +
                    "scheme_id, scheme_agency_id, scheme_version_id, description, " +
                    "created_by, last_updated_by, creation_timestamp, last_update_timestamp) VALUES " +
                    "(:guid, :scheme_name, :ctx_category_id, " +
                    ":scheme_id, :scheme_agency_id, :scheme_version_id, :description, " +
                    ":created_by, :last_updated_by, :creation_timestamp, :last_update_timestamp)";

    @Transactional
    public void insert(User user, ContextScheme contextScheme) {
        if (StringUtils.isEmpty(contextScheme.getGuid())) {
            contextScheme.setGuid(SrtGuid.randomGuid());
        }

        long userId = sessionService.userId(user);

        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("guid", contextScheme.getGuid());
        parameterSource.addValue("scheme_name", contextScheme.getGuid());
        parameterSource.addValue("ctx_category_id", contextScheme.getCtxCategoryId());
        parameterSource.addValue("scheme_id", contextScheme.getSchemeId());
        parameterSource.addValue("scheme_agency_id", contextScheme.getSchemeAgencyId());
        parameterSource.addValue("scheme_version_id", contextScheme.getSchemeVersionId());
        parameterSource.addValue("description", contextScheme.getDescription());

        Date timestamp = new Date();
        parameterSource.addValue("created_by", userId);
        parameterSource.addValue("last_updated_by", userId);
        parameterSource.addValue("creation_timestamp", timestamp);
        parameterSource.addValue("last_update_timestamp", timestamp);

        jdbcTemplate.update(CREATE_CONTEXT_SCHEME_STATEMENT, parameterSource);
    }
}
