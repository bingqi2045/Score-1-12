package org.oagi.srt.gateway.http.module_management;

import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ModuleService {

    @Autowired
    private SrtJdbcTemplate jdbcTemplate;

    public List<SimpleModule> getSimpleModules() {
        return jdbcTemplate.queryForList("SELECT module_id, `module` FROM `module`", SimpleModule.class);
    }

}
