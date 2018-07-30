package org.oagi.srt.gateway.http.bie_management;

import org.oagi.srt.gateway.http.cc_management.CcState;
import org.oagi.srt.gateway.http.cc_management.CcUtility;
import org.oagi.srt.gateway.http.helper.SrtGuid;
import org.oagi.srt.gateway.http.security.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
@Transactional(readOnly = true)
public class BieService {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    private SessionService sessionService;


    private String GET_ASCCP_LIST_FOR_BIE_STATEMENT =
            "SELECT asccp_id, guid, property_term, module_id, state, " +
                    "revision_num, revision_tracking_num, revision_action, release_id, last_update_timestamp " +
                    "FROM asccp WHERE revision_num > 0 AND state = " + CcState.Published.getValue();

    public List<AsccpForBie> getAsccpListForBie(long releaseId) {
        List<AsccpForBie> asccpForBieList =
                jdbcTemplate.query(GET_ASCCP_LIST_FOR_BIE_STATEMENT,
                        new BeanPropertyRowMapper(AsccpForBie.class));

        Map<String, List<AsccpForBie>> groupingByGuidAsccpForBieList =
                asccpForBieList.stream()
                        .collect(groupingBy(AsccpForBie::getGuid));

        asccpForBieList = groupingByGuidAsccpForBieList.values().stream()
                .map(e -> CcUtility.getLatestEntity(releaseId, e))
                .filter(e -> e != null)
                .collect(Collectors.toList());

        return asccpForBieList;
    }


    private String UPDATE_ABIE_ID_STATEMENT =
            "UPDATE top_level_abie SET abie_id = :abie_id WHERE top_level_abie_id = :top_level_abie_id";

    @Transactional
    public void createBie(User user, BieCreateRequest request) {

        long asccpId = request.getAsccpId();
        long releaseId = request.getReleaseId();
        long bizCtxId = request.getBizCtxId();

        long userId = sessionService.userId(user);

        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("top_level_abie")
                .usingColumns("owner_user_id", "release_id", "state")
                .usingGeneratedKeyColumns("top_level_abie_id");

        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("owner_user_id", userId);
        parameterSource.addValue("release_id", releaseId);
        parameterSource.addValue("state", BieState.Editing.getValue());

        long topLevelAbieId = jdbcInsert.executeAndReturnKey(parameterSource).longValue();
        AccForBie accForBie = findRoleOfAccByAsccpId(asccpId, releaseId);
        long basedAccId = accForBie.getAccId();
        long abieId = createAbie(user, basedAccId, bizCtxId, topLevelAbieId);
        createAsbiep(user, asccpId, abieId, topLevelAbieId);

        parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("abie_id", abieId);
        parameterSource.addValue("top_level_abie_id", topLevelAbieId);

        jdbcTemplate.update(UPDATE_ABIE_ID_STATEMENT, parameterSource);
    }


    private String FIND_ROLE_OF_ACC_BY_ASCCP_ID_STATEMENT =
            "SELECT acc.acc_id, acc.guid, acc.revision_num, acc.revision_tracking_num, acc.revision_action, acc.release_id " +
                    "FROM acc JOIN asccp ON acc.current_acc_id = asccp.role_of_acc_id WHERE asccp.asccp_id = :asccp_id";

    private AccForBie findRoleOfAccByAsccpId(long asccpId, long releaseId) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("asccp_id", asccpId);

        List<AccForBie> accForBieList =
                jdbcTemplate.query(FIND_ROLE_OF_ACC_BY_ASCCP_ID_STATEMENT, parameterSource,
                        new BeanPropertyRowMapper(AccForBie.class));

        Map<String, List<AccForBie>> groupingByGuidAccForBieList =
                accForBieList.stream()
                        .collect(groupingBy(AccForBie::getGuid));

        accForBieList = groupingByGuidAccForBieList.values().stream()
                .map(e -> CcUtility.getLatestEntity(releaseId, e))
                .filter(e -> e != null)
                .collect(Collectors.toList());

        return (accForBieList.isEmpty()) ? null : accForBieList.get(0);
    }


    @Transactional
    public long createAsbiep(User user, long asccpId, long abieId, long topLevelAbieId) {
        long userId = sessionService.userId(user);

        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("asbiep")
                .usingColumns("guid", "based_asccp_id", "role_of_abie_id",
                        "created_by", "last_updated_by", "creation_timestamp", "last_update_timestamp",
                        "owner_top_level_abie_id")
                .usingGeneratedKeyColumns("asbiep_id");

        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("guid", SrtGuid.randomGuid());
        parameterSource.addValue("based_asccp_id", asccpId);
        parameterSource.addValue("role_of_abie_id", abieId);
        parameterSource.addValue("owner_top_level_abie_id", topLevelAbieId);

        Date timestamp = new Date();
        parameterSource.addValue("created_by", userId);
        parameterSource.addValue("last_updated_by", userId);
        parameterSource.addValue("creation_timestamp", timestamp);
        parameterSource.addValue("last_update_timestamp", timestamp);

        long asbiepId = jdbcInsert.executeAndReturnKey(parameterSource).longValue();
        return asbiepId;
    }

    @Transactional
    public long createAbie(User user, long basedAccId, long bizCtxId, long topLevelAbieId) {

        long userId = sessionService.userId(user);

        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("abie")
                .usingColumns("guid", "based_acc_id", "biz_ctx_id",
                        "created_by", "last_updated_by", "creation_timestamp", "last_update_timestamp",
                        "state", "owner_top_level_abie_id")
                .usingGeneratedKeyColumns("abie_id");

        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("guid", SrtGuid.randomGuid());
        parameterSource.addValue("based_acc_id", basedAccId);
        parameterSource.addValue("biz_ctx_id", bizCtxId);
        parameterSource.addValue("state", BieState.Editing.getValue());
        parameterSource.addValue("owner_top_level_abie_id", topLevelAbieId);

        Date timestamp = new Date();
        parameterSource.addValue("created_by", userId);
        parameterSource.addValue("last_updated_by", userId);
        parameterSource.addValue("creation_timestamp", timestamp);
        parameterSource.addValue("last_update_timestamp", timestamp);

        long abieId = jdbcInsert.executeAndReturnKey(parameterSource).longValue();
        return abieId;
    }


    private String GET_BIE_LIST_STATEMENT =
            "SELECT top_level_abie_id, asccp.property_term, `release`.release_num, biz_ctx.biz_ctx_id, biz_ctx.name as biz_ctx_name, " +
                    "app_user.login_id as owner, abie.version, abie.`status`, abie.last_update_timestamp, top_level_abie.state " +
                    "FROM top_level_abie JOIN abie ON top_level_abie.top_level_abie_id = abie.owner_top_level_abie_id " +
                    "JOIN asbiep ON asbiep.role_of_abie_id = abie.abie_id " +
                    "JOIN asccp ON asbiep.based_asccp_id = asccp.asccp_id " +
                    "JOIN biz_ctx ON biz_ctx.biz_ctx_id = abie.biz_ctx_id " +
                    "JOIN app_user ON app_user.app_user_id = top_level_abie.owner_user_id " +
                    "JOIN `release` ON top_level_abie.release_id = `release`.release_id";

    public List<BieList> getBieList() {
        return jdbcTemplate.query(GET_BIE_LIST_STATEMENT,
                new BeanPropertyRowMapper(BieList.class));
    }

    @Transactional
    public void deleteBieList(List<Long> topLevelAbieIds) {
        if (topLevelAbieIds == null || topLevelAbieIds.isEmpty()) {
            return;
        }

        jdbcTemplate.query("SET FOREIGN_KEY_CHECKS = 0", rse -> null);

        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("owner_top_level_abie_ids", topLevelAbieIds);

        jdbcTemplate.update("DELETE FROM abie WHERE owner_top_level_abie_id IN (:owner_top_level_abie_ids)", parameterSource);
        jdbcTemplate.update("DELETE FROM asbie WHERE owner_top_level_abie_id IN (:owner_top_level_abie_ids)", parameterSource);
        jdbcTemplate.update("DELETE FROM asbiep WHERE owner_top_level_abie_id IN (:owner_top_level_abie_ids)", parameterSource);
        jdbcTemplate.update("DELETE FROM bbie WHERE owner_top_level_abie_id IN (:owner_top_level_abie_ids)", parameterSource);
        jdbcTemplate.update("DELETE FROM bbiep WHERE owner_top_level_abie_id IN (:owner_top_level_abie_ids)", parameterSource);
        jdbcTemplate.update("DELETE FROM bbie_sc WHERE owner_top_level_abie_id IN (:owner_top_level_abie_ids)", parameterSource);
        jdbcTemplate.update("DELETE FROM top_level_abie WHERE top_level_abie_id IN (:owner_top_level_abie_ids)", parameterSource);

        jdbcTemplate.query("SET FOREIGN_KEY_CHECKS = 1", rse -> null);
    }
}
