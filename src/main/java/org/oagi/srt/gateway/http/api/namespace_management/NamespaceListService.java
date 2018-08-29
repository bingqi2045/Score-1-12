package org.oagi.srt.gateway.http.api.namespace_management;

import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class NamespaceListService {

    @Autowired
    private SrtJdbcTemplate jdbcTemplate;

    public List<NamespaceList> getNamespaceList() {
        return jdbcTemplate.queryForList("SELECT `namespace_id`, `uri`, `prefix`, `description`, " +
                "u.`login_id` as owner, `last_update_timestamp` " +
                "FROM `namespace` n JOIN `app_user` u ON n.`owner_user_id` = u.`app_user_id`", NamespaceList.class);
    }
}
