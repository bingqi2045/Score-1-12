package org.oagi.srt.gateway.http.context_management;

import org.oagi.srt.gateway.http.helper.SrtGuid;
import org.oagi.srt.gateway.http.security.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class BusinessContextService {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    private SessionService sessionService;

    private String GET_BUSINESS_CONTEXT_LIST_STATEMENT =
            "SELECT biz_ctx_id, guid, `name`, last_update_timestamp FROM biz_ctx";

    public List<BusinessContext> getBusinessContextList() {
        return jdbcTemplate.query(GET_BUSINESS_CONTEXT_LIST_STATEMENT,
                new BeanPropertyRowMapper(BusinessContext.class));
    }

    private String GET_BUSINESS_CONTEXT_STATEMENT =
            "SELECT biz_ctx_id, guid, `name`, last_update_timestamp FROM biz_ctx " +
                    "WHERE biz_ctx_id = :biz_ctx_id";

    public BusinessContext getBusinessContext(long bizCtxId) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("biz_ctx_id", bizCtxId);

        List<BusinessContext> res =
                jdbcTemplate.query(GET_BUSINESS_CONTEXT_STATEMENT, parameterSource,
                        new BeanPropertyRowMapper(BusinessContext.class));
        if (res.isEmpty()) {
            throw new EmptyResultDataAccessException(1);
        }

        BusinessContext bizCtx = res.get(0);
        bizCtx.setBizCtxValues(getBusinessContextValuesByBizCtxId(bizCtxId));
        return bizCtx;
    }

    private String GET_BUSINESS_CONTEXT_VALUE_LIST_STATEMENT =
            "SELECT biz_ctx_value_id, " +
                    "ctx_category.ctx_category_id, ctx_category.name as ctx_category_name, " +
                    "ctx_scheme.ctx_scheme_id, ctx_scheme.scheme_name as ctx_scheme_name, " +
                    "ctx_scheme_value.ctx_scheme_value_id, ctx_scheme_value.value as ctx_scheme_value " +
                    "FROM biz_ctx_value JOIN ctx_scheme_value ON biz_ctx_value.ctx_scheme_value_id = ctx_scheme_value.ctx_scheme_value_id " +
                    "JOIN ctx_scheme ON ctx_scheme_value.owner_ctx_scheme_id = ctx_scheme.ctx_scheme_id " +
                    "JOIN ctx_category ON ctx_scheme.ctx_category_id = ctx_category.ctx_category_id " +
                    "WHERE biz_ctx_id = :biz_ctx_id";

    public List<BusinessContextValue> getBusinessContextValuesByBizCtxId(long bizCtxId) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("biz_ctx_id", bizCtxId);

        return jdbcTemplate.query(GET_BUSINESS_CONTEXT_VALUE_LIST_STATEMENT, parameterSource,
                new BeanPropertyRowMapper(BusinessContextValue.class));
    }

    @Transactional
    public void insert(User user, BusinessContext bizCtx) {
        if (StringUtils.isEmpty(bizCtx.getGuid())) {
            bizCtx.setGuid(SrtGuid.randomGuid());
        }

        long userId = sessionService.userId(user);

        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("biz_ctx")
                .usingColumns("guid", "name",
                        "created_by", "last_updated_by", "creation_timestamp", "last_update_timestamp")
                .usingGeneratedKeyColumns("biz_ctx_id");

        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("guid", bizCtx.getGuid());
        parameterSource.addValue("name", bizCtx.getName());

        Date timestamp = new Date();
        parameterSource.addValue("created_by", userId);
        parameterSource.addValue("last_updated_by", userId);
        parameterSource.addValue("creation_timestamp", timestamp);
        parameterSource.addValue("last_update_timestamp", timestamp);

        long bizCtxId = jdbcInsert.executeAndReturnKey(parameterSource).longValue();
        for (BusinessContextValue bizCtxValue : bizCtx.getBizCtxValues()) {
            insert(bizCtxId, bizCtxValue);
        }
    }

    @Transactional
    public void insert(long bizCtxId, BusinessContextValue bizCtxValue) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("biz_ctx_value")
                .usingColumns("biz_ctx_id", "ctx_scheme_value_id");

        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("biz_ctx_id", bizCtxId);
        parameterSource.addValue("ctx_scheme_value_id", bizCtxValue.getCtxSchemeValueId());

        jdbcInsert.execute(parameterSource);
    }

    private String UPDATE_BUSINESS_CONTEXT_STATEMENT =
            "UPDATE biz_ctx SET `name` = :name WHERE biz_ctx_id = :biz_ctx_id";

    @Transactional
    public void update(User user, BusinessContext bizCtx) {
        long userId = sessionService.userId(user);

        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("biz_ctx_id", bizCtx.getBizCtxId());
        parameterSource.addValue("name", bizCtx.getName());

        Date timestamp = new Date();
        parameterSource.addValue("last_updated_by", userId);
        parameterSource.addValue("last_update_timestamp", timestamp);

        jdbcTemplate.update(UPDATE_BUSINESS_CONTEXT_STATEMENT, parameterSource);

        update(bizCtx.getBizCtxId(), bizCtx.getBizCtxValues());
    }

    private String GET_BUSINESS_CONTEXT_VALUE_ID_LIST_STATEMENT =
            "SELECT biz_ctx_value_id FROM biz_ctx_value WHERE biz_ctx_id = :biz_ctx_id";

    @Transactional
    public void update(final long bizCtxId, List<BusinessContextValue> bizCtxValues) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("biz_ctx_id", bizCtxId);

        List<Long> oldBizCtxValueIds =
                jdbcTemplate.queryForList(GET_BUSINESS_CONTEXT_VALUE_ID_LIST_STATEMENT,
                        parameterSource, Long.class);

        Map<Long, BusinessContextValue> newBizCtxValues = bizCtxValues.stream()
                .filter(e -> e.getBizCtxValueId() > 0L)
                .collect(Collectors.toMap(BusinessContextValue::getBizCtxValueId, Function.identity()));

        oldBizCtxValueIds.removeAll(newBizCtxValues.keySet());
        for (long deleteBizCtxValueId : oldBizCtxValueIds) {
            delete(bizCtxId, deleteBizCtxValueId);
        }

        for (BusinessContextValue bizCtxValue : newBizCtxValues.values()) {
            update(bizCtxId, bizCtxValue);
        }

        for (BusinessContextValue bizCtxValue : bizCtxValues.stream()
                .filter(e -> e.getBizCtxValueId() == 0L)
                .collect(Collectors.toList())) {
            insert(bizCtxId, bizCtxValue);
        }
    }

    private String UPDATE_BUSINESS_CONTEXT_VALUE_STATEMENT =
            "UPDATE biz_ctx_value SET ctx_scheme_value_id = :ctx_scheme_value_id " +
                    "WHERE biz_ctx_value_id = :biz_ctx_value_id AND biz_ctx_id = :biz_ctx_id";

    @Transactional
    public void update(long bizCtxId, BusinessContextValue bizCtxValue) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("biz_ctx_value_id", bizCtxValue.getBizCtxValueId());
        parameterSource.addValue("biz_ctx_id", bizCtxId);
        parameterSource.addValue("ctx_scheme_value_id", bizCtxValue.getCtxSchemeValueId());

        jdbcTemplate.update(UPDATE_BUSINESS_CONTEXT_VALUE_STATEMENT, parameterSource);
    }

    private String DELETE_BUSINESS_CONTEXT_VALUE_STATEMENT =
            "DELETE FROM biz_ctx_value " +
                    "WHERE biz_ctx_value_id = :biz_ctx_value_id AND biz_ctx_id = :biz_ctx_id";

    @Transactional
    public void delete(long bizCtxId, long bizCtxValueId) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("biz_ctx_value_id", bizCtxValueId);
        parameterSource.addValue("biz_ctx_id", bizCtxId);

        jdbcTemplate.update(DELETE_BUSINESS_CONTEXT_VALUE_STATEMENT, parameterSource);
    }

    private String DELETE_BUSINESS_CONTEXT_VALUES_STATEMENT =
            "DELETE FROM biz_ctx_value WHERE biz_ctx_id = :biz_ctx_id";

    private String DELETE_BUSINESS_CONTEXT_STATEMENT =
            "DELETE FROM biz_ctx WHERE biz_ctx_id = :biz_ctx_id";

    @Transactional
    public void delete(long bizCtxId) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("biz_ctx_id", bizCtxId);

        jdbcTemplate.update(DELETE_BUSINESS_CONTEXT_VALUES_STATEMENT, parameterSource);
        jdbcTemplate.update(DELETE_BUSINESS_CONTEXT_STATEMENT, parameterSource);
    }
}
