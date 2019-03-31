package org.oagi.srt.gateway.http.api.bie_management.service;

import org.oagi.srt.data.BieState;
import org.oagi.srt.gateway.http.api.bie_management.data.*;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;
import org.oagi.srt.gateway.http.api.cc_management.helper.CcUtility;
import org.oagi.srt.gateway.http.api.common.data.AccessPrivilege;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static org.oagi.srt.data.BieState.Editing;
import static org.oagi.srt.gateway.http.api.common.data.AccessPrivilege.*;
import static org.oagi.srt.gateway.http.helper.SrtJdbcTemplate.newSqlParameterSource;

@Service
@Transactional(readOnly = true)
public class BieService {

    @Autowired
    private SrtJdbcTemplate jdbcTemplate;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private BieRepository repository;


    private String GET_ASCCP_LIST_FOR_BIE_STATEMENT =
            "SELECT asccp_id, guid, property_term, module_id, state, " +
                    "revision_num, revision_tracking_num, release_id, last_update_timestamp " +
                    "FROM asccp WHERE revision_num > 0 AND state = " + CcState.Published.getValue();

    public List<AsccpForBie> getAsccpListForBie(long releaseId) {
        List<AsccpForBie> asccpForBieList =
                jdbcTemplate.queryForList(GET_ASCCP_LIST_FOR_BIE_STATEMENT, AsccpForBie.class);

        Map<String, List<AsccpForBie>> groupingByGuidAsccpForBieList =
                asccpForBieList.stream()
                        .collect(groupingBy(AsccpForBie::getGuid));

        asccpForBieList = groupingByGuidAsccpForBieList.values().stream()
                .map(e -> CcUtility.getLatestEntity(releaseId, e))
                .filter(e -> e != null)
                .collect(Collectors.toList());

        return asccpForBieList;
    }


    @Transactional
    public BieCreateResponse createBie(User user, BieCreateRequest request) {

        long userId = sessionService.userId(user);
        long releaseId = request.getReleaseId();
        long topLevelAbieId = repository.createTopLevelAbie(userId, releaseId, Editing);

        long asccpId = request.getAsccpId();
        AccForBie accForBie = findRoleOfAccByAsccpId(asccpId, releaseId);

        long basedAccId = accForBie.getAccId();
        long bizCtxId = request.getBizCtxId();

        long abieId = repository.createAbie(user, basedAccId, bizCtxId, topLevelAbieId);
        repository.createAsbiep(user, asccpId, abieId, topLevelAbieId);

        repository.updateAbieIdOnTopLevelAbie(abieId, topLevelAbieId);

        BieCreateResponse response = new BieCreateResponse();
        response.setTopLevelAbieId(topLevelAbieId);
        return response;
    }


    private String FIND_ROLE_OF_ACC_BY_ASCCP_ID_STATEMENT =
            "SELECT acc.acc_id, acc.guid, acc.revision_num, acc.revision_tracking_num, acc.revision_action, acc.release_id " +
                    "FROM acc JOIN asccp ON acc.current_acc_id = asccp.role_of_acc_id WHERE asccp.asccp_id = :asccp_id";

    private AccForBie findRoleOfAccByAsccpId(long asccpId, long releaseId) {
        MapSqlParameterSource parameterSource = newSqlParameterSource()
                .addValue("asccp_id", asccpId);

        List<AccForBie> accForBieList =
                jdbcTemplate.queryForList(FIND_ROLE_OF_ACC_BY_ASCCP_ID_STATEMENT, parameterSource,
                        AccForBie.class);

        Map<String, List<AccForBie>> groupingByGuidAccForBieList =
                accForBieList.stream()
                        .collect(groupingBy(AccForBie::getGuid));

        accForBieList = groupingByGuidAccForBieList.values().stream()
                .map(e -> CcUtility.getLatestEntity(releaseId, e))
                .filter(e -> e != null)
                .collect(Collectors.toList());

        return (accForBieList.isEmpty()) ? null : accForBieList.get(0);
    }


    private String GET_BIE_LIST_STATEMENT =
            "SELECT top_level_abie_id, asccp.property_term, `release`.release_num, biz_ctx.biz_ctx_id, biz_ctx.name as biz_ctx_name, " +
            "top_level_abie.owner_user_id, app_user.login_id as owner, abie.version, abie.`status`, " +
            "abie.last_update_timestamp, top_level_abie.state as raw_state " +
            "FROM top_level_abie " +
            "JOIN abie ON top_level_abie.top_level_abie_id = abie.owner_top_level_abie_id " +
            "AND abie.abie_id = top_level_abie.abie_id " +
            "JOIN asbiep ON asbiep.role_of_abie_id = abie.abie_id " +
            "JOIN asccp ON asbiep.based_asccp_id = asccp.asccp_id " +
            "JOIN biz_ctx ON biz_ctx.biz_ctx_id = abie.biz_ctx_id " +
            "JOIN app_user ON app_user.app_user_id = top_level_abie.owner_user_id " +
            "JOIN `release` ON top_level_abie.release_id = `release`.release_id";


    private List<BieList> getBieList(User user, String whereClauses) {
        List<BieList> bieLists = jdbcTemplate.queryForList(
                GET_BIE_LIST_STATEMENT + " WHERE " + whereClauses, BieList.class);
        long userId = sessionService.userId(user);

        bieLists.stream().forEach(e -> {
            BieState state = BieState.valueOf(e.getRawState());
            e.setState(state);

            AccessPrivilege accessPrivilege = Prohibited;
            switch (state) {
                case Init:
                    accessPrivilege = Unprepared;
                    break;

                case Editing:
                    if (userId == e.getOwnerUserId()) {
                        accessPrivilege = CanEdit;
                    } else {
                        accessPrivilege = Prohibited;
                    }
                    break;

                case Candidate:
                    if (userId == e.getOwnerUserId()) {
                        accessPrivilege = CanEdit;
                    } else {
                        accessPrivilege = CanView;
                    }

                    break;

                case Published:
                    accessPrivilege = CanView;
                    break;

            }

            e.setAccess(accessPrivilege.name());
        });

        return bieLists;
    }

    public List<BieList> getBieList(User user) {
        return getBieList(user, "asccp.property_term NOT IN ('Meta Header', 'Pagination Response')");
    }

    public List<BieList> getMetaHeaderBieList(User user) {
        return getBieList(user, "asccp.property_term = 'Meta Header'");
    }

    public List<BieList> getPaginationResponseBieList(User user) {
        return getBieList(user, "asccp.property_term = 'Pagination Response'");
    }

    @Transactional
    public void deleteBieList(List<Long> topLevelAbieIds) {
        if (topLevelAbieIds == null || topLevelAbieIds.isEmpty()) {
            return;
        }

        jdbcTemplate.query("SET FOREIGN_KEY_CHECKS = 0");

        MapSqlParameterSource parameterSource = newSqlParameterSource()
                .addValue("owner_top_level_abie_ids", topLevelAbieIds);

        jdbcTemplate.update("DELETE FROM abie WHERE owner_top_level_abie_id IN (:owner_top_level_abie_ids)", parameterSource);
        jdbcTemplate.update("DELETE FROM asbie WHERE owner_top_level_abie_id IN (:owner_top_level_abie_ids)", parameterSource);
        jdbcTemplate.update("DELETE FROM asbiep WHERE owner_top_level_abie_id IN (:owner_top_level_abie_ids)", parameterSource);
        jdbcTemplate.update("DELETE FROM bbie WHERE owner_top_level_abie_id IN (:owner_top_level_abie_ids)", parameterSource);
        jdbcTemplate.update("DELETE FROM bbiep WHERE owner_top_level_abie_id IN (:owner_top_level_abie_ids)", parameterSource);
        jdbcTemplate.update("DELETE FROM bbie_sc WHERE owner_top_level_abie_id IN (:owner_top_level_abie_ids)", parameterSource);
        jdbcTemplate.update("DELETE FROM top_level_abie WHERE top_level_abie_id IN (:owner_top_level_abie_ids)", parameterSource);

        jdbcTemplate.query("SET FOREIGN_KEY_CHECKS = 1");
    }

}
