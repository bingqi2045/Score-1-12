package org.oagi.srt.gateway.http.api.code_list_management.service;

import org.oagi.srt.gateway.http.api.code_list_management.data.CodeList;
import org.oagi.srt.gateway.http.api.code_list_management.data.CodeListForList;
import org.oagi.srt.gateway.http.api.code_list_management.data.CodeListState;
import org.oagi.srt.gateway.http.api.code_list_management.data.CodeListValue;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.oagi.srt.gateway.http.helper.SrtGuid;
import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.oagi.srt.gateway.http.helper.SrtJdbcTemplate.newSqlParameterSource;

@Service
@Transactional(readOnly = true)
public class CodeListService {

    @Autowired
    private SrtJdbcTemplate jdbcTemplate;

    @Autowired
    private SessionService sessionService;

    public List<CodeListForList> getCodeLists() {
        return jdbcTemplate.queryForList("SELECT c.code_list_id, c.name as code_list_name, " +
                "c.based_code_list_id, b.name as based_code_list_name, " +
                "c.agency_id, a.name as agency_id_name, c.version_id, c.last_update_timestamp, " +
                "c.extensible_indicator as extensible, c.state " +
                "FROM code_list c " +
                "LEFT JOIN code_list b ON c.based_code_list_id = b.code_list_id " +
                "LEFT JOIN agency_id_list_value a ON c.agency_id = a.agency_id_list_value_id", CodeListForList.class);
    }

    public CodeList getCodeList(long id) {
        MapSqlParameterSource parameterSource = newSqlParameterSource()
                .addValue("code_list_id", id);

        CodeList codeList = jdbcTemplate.queryForObject("SELECT c.code_list_id, c.name as code_list_name, " +
                "c.based_code_list_id, b.name as based_code_list_name, " +
                "c.agency_id, a.name as agency_id_name, c.version_id," +
                "c.guid, c.list_id, c.definition, c.definition_source, c.remark, " +
                "c.extensible_indicator as extensible, c.state " +
                "FROM code_list c " +
                "LEFT JOIN code_list b ON c.based_code_list_id = b.code_list_id " +
                "LEFT JOIN agency_id_list_value a ON c.agency_id = a.agency_id_list_value_id " +
                "WHERE c.code_list_id = :code_list_id", parameterSource, CodeList.class);

        List<CodeListValue> codeListValues =
                jdbcTemplate.queryForList("SELECT code_list_value_id, value, name, definition, definition_source, " +
                        "used_indicator as used, locked_indicator as locked, extension_Indicator as extension " +
                        "FROM code_list_value WHERE code_list_id = :code_list_id", parameterSource, CodeListValue.class);
        codeList.setCodeListValues(codeListValues);

        return codeList;
    }

    @Transactional
    public void insert(User user, CodeList codeList) {

        SimpleJdbcInsert jdbcInsert = jdbcTemplate.insert()
                .withTableName("code_list")
                .usingColumns("guid", "enum_type_guid", "name", "list_id", "agency_id", "version_id",
                        "definition", "remark", "definition_source", "based_code_list_id", "extensible_indicator",
                        "created_by", "last_updated_by", "creation_timestamp", "last_update_timestamp", "state")
                .usingGeneratedKeyColumns("code_list_id");

        long userId = sessionService.userId(user);
        Date timestamp = new Date();

        Long basedCodeListId = codeList.getBasedCodeListId();
        String enumTypeGuid = null;
        if (basedCodeListId != null) {
            try {
                enumTypeGuid = jdbcTemplate.queryForObject("SELECT enum_type_guid FROM code_list " +
                        "WHERE code_list_id = :code_list_id", newSqlParameterSource()
                        .addValue("code_list_id", basedCodeListId), String.class);
            } catch (EmptyResultDataAccessException ignore) {}
        }

        MapSqlParameterSource parameterSource = newSqlParameterSource()
                .addValue("guid", SrtGuid.randomGuid())
                .addValue("enum_type_guid", enumTypeGuid)
                .addValue("name", codeList.getCodeListName())
                .addValue("list_id", codeList.getListId())
                .addValue("agency_id", codeList.getAgencyId())
                .addValue("version_id", codeList.getVersionId())
                .addValue("definition", codeList.getDefinition())
                .addValue("remark", codeList.getRemark())
                .addValue("definition_source", codeList.getDefinitionSource())
                .addValue("based_code_list_id", codeList.getBasedCodeListId())
                .addValue("extensible_indicator", codeList.isExtensible())
                .addValue("state", CodeListState.Editing.name())
                .addValue("created_by", userId)
                .addValue("last_updated_by", userId)
                .addValue("creation_timestamp", timestamp)
                .addValue("last_update_timestamp", timestamp);

        long codeListId = jdbcInsert.executeAndReturnKey(parameterSource).longValue();
        for (CodeListValue codeListValue : codeList.getCodeListValues()) {
            insert(codeListId, codeListValue);
        }
    }

    private void insert(long codeListId, CodeListValue codeListValue) {
        SimpleJdbcInsert jdbcInsert = jdbcTemplate.insert()
                .withTableName("code_list_value")
                .usingColumns("code_list_id", "value", "name", "definition", "definition_source",
                        "used_indicator", "locked_indicator", "extension_Indicator");

        MapSqlParameterSource parameterSource = newSqlParameterSource()
                .addValue("code_list_id", codeListId)
                .addValue("value", codeListValue.getValue())
                .addValue("name", codeListValue.getName())
                .addValue("definition", codeListValue.getDefinition())
                .addValue("definition_source", codeListValue.getDefinitionSource())
                .addValue("used_indicator", codeListValue.isUsed())
                .addValue("locked_indicator", codeListValue.isLocked())
                .addValue("extension_Indicator", codeListValue.isExtension());

        jdbcInsert.execute(parameterSource);
    }

    private String UPDATE_CODE_LIST_STATEMENT =
            "UPDATE code_list SET name = :name, list_id = :list_id, " +
                    "agency_id = :agency_id, version_id = :version_id, definition = :definition, " +
                    "remark = :remark, definition_source = :definition_source, extensible_indicator = :extensible_indicator " +
                    "WHERE code_list_id = :code_list_id";

    @Transactional
    public void update(User user, CodeList codeList) {
        jdbcTemplate.update(UPDATE_CODE_LIST_STATEMENT, newSqlParameterSource()
                .addValue("code_list_id", codeList.getCodeListId())
                .addValue("name", codeList.getCodeListName())
                .addValue("list_id", codeList.getListId())
                .addValue("agency_id", codeList.getAgencyId())
                .addValue("version_id", codeList.getVersionId())
                .addValue("definition", codeList.getDefinition())
                .addValue("remark", codeList.getRemark())
                .addValue("definition_source", codeList.getDefinitionSource())
                .addValue("extensible_indicator", codeList.isExtensible())
                .addValue("last_updated_by", sessionService.userId(user))
                .addValue("last_update_timestamp", new Date()));

        update(codeList.getCodeListId(), codeList.getCodeListValues());
    }

    private String GET_CODE_LIST_VALUE_ID_LIST_STATEMENT =
            "SELECT code_list_value_id FROM code_list_value WHERE code_list_id = :code_list_id";
    
    @Transactional
    public void update(long codeListId, List<CodeListValue> codeListValues) {
        List<Long> oldCodeListValueIds = jdbcTemplate.queryForList(
                GET_CODE_LIST_VALUE_ID_LIST_STATEMENT,
                newSqlParameterSource().addValue("code_list_id", codeListId),
                Long.class);

        Map<Long, CodeListValue> newCodeListValues = codeListValues.stream()
                .filter(e -> e.getCodeListValueId() > 0L)
                .collect(Collectors.toMap(CodeListValue::getCodeListValueId, Function.identity()));

        oldCodeListValueIds.removeAll(newCodeListValues.keySet());
        for (long deleteCodeListValueId : oldCodeListValueIds) {
            delete(codeListId, deleteCodeListValueId);
        }

        for (CodeListValue CodeListValue : newCodeListValues.values()) {
            update(codeListId, CodeListValue);
        }

        for (CodeListValue CodeListValue : codeListValues.stream()
                .filter(e -> e.getCodeListValueId() == 0L)
                .collect(Collectors.toList())) {
            insert(codeListId, CodeListValue);
        }
    }

    private String UPDATE_CODE_LIST_VALUE_STATEMENT =
            "UPDATE code_list_value SET `value` = :value, name = :name, " +
                    "definition = :definition, definition_source = :definition_source " +
                    "WHERE code_list_value_id = :code_list_value_id AND code_list_id = :code_list_id";

    @Transactional
    public void update(long codeListId, CodeListValue codeListValue) {
        jdbcTemplate.update(UPDATE_CODE_LIST_VALUE_STATEMENT, newSqlParameterSource()
                .addValue("code_list_value_id", codeListValue.getCodeListValueId())
                .addValue("code_list_id", codeListId)
                .addValue("value", codeListValue.getValue())
                .addValue("name", codeListValue.getName())
                .addValue("definition", codeListValue.getDefinition())
                .addValue("definition_source", codeListValue.getDefinitionSource()));
    }

    private String DELETE_CODE_LIST_VALUE_STATEMENT =
            "DELETE FROM code_list_value " +
                    "WHERE code_list_value_id = :code_list_value_id AND code_list_id = :code_list_id";

    @Transactional
    public void delete(long codeListId, long codeListValueId) {
        jdbcTemplate.update(DELETE_CODE_LIST_VALUE_STATEMENT, newSqlParameterSource()
                .addValue("code_list_value_id", codeListValueId)
                .addValue("code_list_id", codeListId));
    }

    private String DELETE_CODE_LIST_VALUES_STATEMENT =
            "DELETE FROM code_list_value WHERE code_list_id = :code_list_id";

    private String DELETE_CODE_LIST_STATEMENT =
            "DELETE FROM code_list WHERE code_list_id = :code_list_id";

    @Transactional
    public void delete(long codeListId) {
        MapSqlParameterSource parameterSource = newSqlParameterSource()
                .addValue("code_list_id", codeListId);

        jdbcTemplate.update(DELETE_CODE_LIST_VALUES_STATEMENT, parameterSource);
        jdbcTemplate.update(DELETE_CODE_LIST_STATEMENT, parameterSource);
    }
}
