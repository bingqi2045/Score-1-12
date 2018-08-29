package org.oagi.srt.gateway.http.api.namespace_management.service;

import org.oagi.srt.gateway.http.api.namespace_management.data.Namespace;
import org.oagi.srt.gateway.http.api.namespace_management.data.NamespaceList;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static org.oagi.srt.gateway.http.helper.SrtJdbcTemplate.newSqlParameterSource;

@Service
@Transactional(readOnly = true)
public class NamespaceService {

    @Autowired
    private SrtJdbcTemplate jdbcTemplate;

    @Autowired
    private SessionService sessionService;

    public List<NamespaceList> getNamespaceList(User user) {
        long userId = sessionService.userId(user);

        List<NamespaceList> namespaceLists =
                jdbcTemplate.queryForList("SELECT `namespace_id`, `uri`, `prefix`, `description`, `owner_user_id`, " +
                        "u.`login_id` as owner, `last_update_timestamp` " +
                        "FROM `namespace` n JOIN `app_user` u ON n.`owner_user_id` = u.`app_user_id`", NamespaceList.class);
        namespaceLists.stream().forEach(namespaceList -> {
            namespaceList.setCanEdit(namespaceList.getOwnerUserId() == userId);
        });

        return namespaceLists;
    }

    public Namespace getNamespace(User user, long namespaceId) {
        long userId = sessionService.userId(user);

        Namespace namespace =
                jdbcTemplate.queryForObject("SELECT `namespace_id`, `uri`, `prefix`, `description`, `owner_user_id` " +
                        "FROM `namespace` WHERE `namespace_id` = :namespace_id", newSqlParameterSource()
                        .addValue("namespace_id", namespaceId), Namespace.class);
        if (namespace.getOwnerUserId() != userId) {
            throw new AccessDeniedException("Access is denied");
        }
        return namespace;
    }

    @Transactional
    public void create(User user, Namespace namespace) {
        SimpleJdbcInsert jdbcInsert = jdbcTemplate.insert()
                .withTableName("namespace")
                .usingColumns("uri", "prefix", "description", "is_std_nmsp",
                        "owner_user_id", "created_by", "last_updated_by", "creation_timestamp", "last_update_timestamp");

        long userId = sessionService.userId(user);
        Date timestamp = new Date();

        jdbcInsert.execute(newSqlParameterSource()
                .addValue("uri", namespace.getUri())
                .addValue("prefix", namespace.getPrefix())
                .addValue("description", namespace.getDescription())
                .addValue("is_std_nmsp", false)
                .addValue("owner_user_id", userId)
                .addValue("created_by", userId)
                .addValue("last_updated_by", userId)
                .addValue("creation_timestamp", timestamp)
                .addValue("last_update_timestamp", timestamp));
    }

    @Transactional
    public void update(User user, Namespace namespace) {
        long userId = sessionService.userId(user);
        Date timestamp = new Date();

        int res = jdbcTemplate.update("UPDATE `namespace` SET " +
                "`uri` = :uri, `prefix` = :prefix, `description` = :description, " +
                "`last_updated_by` = :user_id, `last_update_timestamp` = :last_update_timestamp " +
                "WHERE `namespace_id` = :namespace_id AND `owner_user_id` = :user_id", newSqlParameterSource()
                .addValue("uri", namespace.getUri())
                .addValue("prefix", namespace.getPrefix())
                .addValue("description", namespace.getDescription())
                .addValue("user_id", userId)
                .addValue("last_update_timestamp", timestamp)
                .addValue("namespace_id", namespace.getNamespaceId()));
        if (res != 1) {
            throw new AccessDeniedException("Access is denied");
        }
    }
}
