package org.oagi.srt.gateway.http.api.cc_management.service;

import org.oagi.srt.gateway.http.api.cc_management.data.CCList;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;
import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;
import static org.oagi.srt.gateway.http.api.cc_management.helper.CcUtility.getLatestEntity;
import static org.oagi.srt.gateway.http.api.cc_management.helper.CcUtility.getRevision;

@Service
@Transactional(readOnly = true)
public class CCListService {

    @Autowired
    private SrtJdbcTemplate jdbcTemplate;

    private String GET_CC_LIST_STATEMENT =
            "SELECT 'acc' as type, acc_id as id, guid, den, u1.login_id as owner, state, is_deprecated as deprecated, " +
                    "revision_num, revision_tracking_num, revision_action, release_id, " +
                    "current_acc_id as current_id, u2.login_id as last_update_user, last_update_timestamp " +
                    "FROM acc JOIN app_user u1 ON acc.owner_user_id = u1.app_user_id " +
                    "JOIN app_user u2 ON acc.last_updated_by = u2.app_user_id " +
                    "UNION ALL " +
                    "SELECT 'asccp' as type, asccp_id as id, guid, den, u1.login_id as owner, state, is_deprecated as deprecated, " +
                    "revision_num, revision_tracking_num, revision_action, release_id, " +
                    "current_asccp_id as current_id, u2.login_id as last_update_user, last_update_timestamp " +
                    "FROM asccp JOIN app_user u1 ON asccp.owner_user_id = u1.app_user_id " +
                    "JOIN app_user u2 ON asccp.last_updated_by = u2.app_user_id " +
                    "UNION ALL " +
                    "SELECT 'bccp' as type, bccp_id as id, guid, den, u1.login_id as owner, state, is_deprecated as deprecated, " +
                    "revision_num, revision_tracking_num, revision_action, release_id, " +
                    "current_bccp_id as current_id, u2.login_id as last_update_user, last_update_timestamp " +
                    "FROM bccp JOIN app_user u1 ON bccp.owner_user_id = u1.app_user_id " +
                    "JOIN app_user u2 ON bccp.last_updated_by = u2.app_user_id " +
                    "UNION ALL " +
                    "SELECT 'ascc' as type, ascc_id as id, guid, den, u1.login_id as owner, state, is_deprecated as deprecated, " +
                    "revision_num, revision_tracking_num, revision_action, release_id, " +
                    "current_ascc_id as current_id, u2.login_id as last_update_user, last_update_timestamp " +
                    "FROM ascc JOIN app_user u1 ON ascc.owner_user_id = u1.app_user_id " +
                    "JOIN app_user u2 ON ascc.last_updated_by = u2.app_user_id " +
                    "UNION ALL " +
                    "SELECT 'bcc' as type, bcc_id as id, guid, den, u1.login_id as owner, state, is_deprecated as deprecated, " +
                    "revision_num, revision_tracking_num, revision_action, release_id, " +
                    "current_bcc_id as current_id, u2.login_id as last_update_user, last_update_timestamp " +
                    "FROM bcc JOIN app_user u1 ON bcc.owner_user_id = u1.app_user_id " +
                    "JOIN app_user u2 ON bcc.last_updated_by = u2.app_user_id";

    @Cacheable("coreComponents")
    public List<CCList> getAllCCList() {
        return jdbcTemplate.queryForList(GET_CC_LIST_STATEMENT, CCList.class);
    }

    @Cacheable("coreComponentsByReleaseId")
    public List<CCList> getCCListByReleaseId(long releaseId) {
        List<CCList> allCcLists = getAllCCList();
        Map<String, List<CCList>> groupingByTypeAndGuid =
                allCcLists.stream().collect(groupingBy(e -> e.getType() + "-" + e.getGuid()));

        Stream<CCList> entityStream;
        if (releaseId == 0L) {
            entityStream = groupingByTypeAndGuid.values().stream()
                    .map(e -> {
                        for (CCList ccList : e) {
                            if (ccList.getReleaseId() == null) {
                                return ccList;
                            }
                        }
                        return null;
                    });
        } else {
            entityStream = groupingByTypeAndGuid.values().stream()
                    .map(e -> getLatestEntity(releaseId, e));
        }

        Map<String, CCList> ccListMap = entityStream
                .collect(toMap(e -> e.getType() + "-" + e.getGuid(), Function.identity()));
        groupingByTypeAndGuid.entrySet().stream().forEach(entry -> {
            String revision = getRevision(releaseId, entry.getValue());
            ccListMap.get(entry.getKey()).setRevision(revision);
        });

        return ccListMap.values().stream().map(e -> {
            e.setState(CcState.valueOf((Integer) e.getState()));
            return e;
        }).collect(Collectors.toList());
    }
}
