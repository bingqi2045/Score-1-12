package org.oagi.srt.repository;

import org.oagi.srt.data.TopLevelAbie;
import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.oagi.srt.gateway.http.helper.SrtJdbcTemplate.newSqlParameterSource;

@Repository
public class TopLevelAbieRepository implements SrtRepository<TopLevelAbie> {

    @Autowired
    private SrtJdbcTemplate jdbcTemplate;

    @Override
    public List<TopLevelAbie> findAll() {
        return jdbcTemplate.queryForList("SELECT `top_level_abie_id`,`abie_id`,`owner_user_id`,`release_id`,`state` " +
                "FROM `top_level_abie`", TopLevelAbie.class);
    }

    @Override
    public TopLevelAbie findById(long id) {
        return jdbcTemplate.queryForObject("SELECT `top_level_abie_id`,`abie_id`,`owner_user_id`,`release_id`,`state` " +
                "FROM `top_level_abie` WHERE `top_level_abie_id` = :top_level_abie_id", newSqlParameterSource()
                .addValue("top_level_abie_id", id), TopLevelAbie.class);
    }

    public List<TopLevelAbie> findByIdIn(List<Long> topLevelAbieIds) {
        return jdbcTemplate.queryForList("SELECT `top_level_abie_id`,`abie_id`,`owner_user_id`,`release_id`,`state` " +
                "FROM `top_level_abie` WHERE `top_level_abie_id` IN (:top_level_abie_ids)", newSqlParameterSource()
                .addValue("top_level_abie_ids", topLevelAbieIds), TopLevelAbie.class);
    }
}
