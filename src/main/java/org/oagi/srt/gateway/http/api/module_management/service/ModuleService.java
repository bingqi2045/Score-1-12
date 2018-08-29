package org.oagi.srt.gateway.http.api.module_management.service;

import org.oagi.srt.gateway.http.api.module_management.data.*;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.oagi.srt.gateway.http.helper.SrtJdbcTemplate.newSqlParameterSource;

@Service
@Transactional(readOnly = true)
public class ModuleService {

    @Autowired
    private SrtJdbcTemplate jdbcTemplate;

    @Autowired
    private SessionService sessionService;

    public List<SimpleModule> getSimpleModules() {
        return jdbcTemplate.queryForList("SELECT `module_id`, `module` FROM `module`", SimpleModule.class);
    }

    public List<ModuleList> getModuleList(User user) {
        List<ModuleList> moduleLists =
                jdbcTemplate.queryForList("SELECT m.`module_id`, m.`module`, m.`owner_user_id`, u1.`login_id` as owner, " +
                        "u2.`login_id` as last_updated_by, m.`last_update_timestamp`, " +
                        "n.`uri` as namespace, r.`release_num` as since_release " +
                        "FROM `module` m " +
                        "JOIN `namespace` n ON m.`namespace_id` = n.`namespace_id` " +
                        "JOIN `release` r ON m.`release_id` = r.`release_id` " +
                        "JOIN `app_user` u1 ON m.`owner_user_id` = u1.`app_user_id` " +
                        "JOIN `app_user` u2 ON m.`last_updated_by` = u2.`app_user_id`", ModuleList.class);

        long userId = sessionService.userId(user);

        moduleLists.stream().forEach(moduleList -> {
            moduleList.setCanEdit(moduleList.getOwnerUserId() == userId);
        });

        return moduleLists;
    }

    public Module getModule(User user, long moduleId) {
        Module module = jdbcTemplate.queryForObject("SELECT m.`module_id`, m.`module`, m.`namespace_id` " +
                "FROM `module` m WHERE m.`module_id` = :module_id", newSqlParameterSource()
                .addValue("module_id", moduleId), Module.class);
        module.setModuleDependencies(getModuleDependencies(moduleId));
        return module;
    }

    private List<ModuleDependency> getModuleDependencies(long moduleId) {
        List<ModuleDependency> moduleDependencies = new ArrayList();

        moduleDependencies.addAll(
                jdbcTemplate.queryForList("SELECT `module_dep_id`, '" + ModuleDependencyType.Include.name() + "' as dependency_type, " +
                        "`depended_module_id` as related_module_id FROM `module_dep` " +
                        "WHERE `depending_module_id` = :module_id AND `dependency_type` = :dependency_type", newSqlParameterSource()
                        .addValue("module_id", moduleId)
                        .addValue("dependency_type", ModuleDependencyType.Include.getValue()), ModuleDependency.class)
        );

        moduleDependencies.addAll(
                jdbcTemplate.queryForList("SELECT `module_dep_id`, '" + ModuleDependencyType.Import.name() + "' as dependency_type, " +
                        "`depending_module_id` as related_module_id FROM `module_dep` " +
                        "WHERE `depended_module_id` = :module_id AND `dependency_type` = :dependency_type", newSqlParameterSource()
                        .addValue("module_id", moduleId)
                        .addValue("dependency_type", ModuleDependencyType.Import.getValue()), ModuleDependency.class)
        );

        return moduleDependencies;
    }

}
