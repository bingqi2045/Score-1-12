package org.oagi.srt.gateway.http.module_management;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ModuleService {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    private String GET_SIMPLE_MODULES_STATEMENT =
            "SELECT module_id, `module` FROM `module`";

    public List<SimpleModule> getSimpleModules() {
        return jdbcTemplate.query(GET_SIMPLE_MODULES_STATEMENT,
                new BeanPropertyRowMapper(SimpleModule.class));
    }

}
