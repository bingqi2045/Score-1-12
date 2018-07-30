package org.oagi.srt.gateway.http.bie_management;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class BieEditService {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    private String GET_ROOT_NODE_STATEMENT =
            "SELECT 'ASCCP' as type, asbiep.asbiep_id as bie_id, asbiep.based_asccp_id as cc_id, asccp.property_term as name " +
                    "FROM top_level_abie JOIN abie ON top_level_abie.abie_id = abie.abie_id " +
                    "JOIN asbiep ON asbiep.role_of_abie_id = abie.abie_id " +
                    "JOIN asccp ON asbiep.based_asccp_id = asccp.asccp_id " +
                    "WHERE top_level_abie_id = :top_level_abie_id";

    public BieEditNode getRootNode(long topLevelAbieId) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("top_level_abie_id", topLevelAbieId);

        List<BieEditNode> res = jdbcTemplate.query(GET_ROOT_NODE_STATEMENT, parameterSource,
                new BeanPropertyRowMapper(BieEditNode.class));
        if (res.isEmpty()) {
            throw new EmptyResultDataAccessException(1);
        }

        return res.get(0);
    }


}
