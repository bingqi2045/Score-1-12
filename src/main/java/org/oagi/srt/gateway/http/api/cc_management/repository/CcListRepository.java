package org.oagi.srt.gateway.http.api.cc_management.repository;

import org.oagi.srt.gateway.http.api.cc_management.data.CcList;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;
import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;
import static org.oagi.srt.gateway.http.api.cc_management.helper.CcUtility.getLatestEntity;
import static org.oagi.srt.gateway.http.api.cc_management.helper.CcUtility.getRevision;

@Repository
public class CcListRepository {

    @Autowired
    private SrtJdbcTemplate jdbcTemplate;

    @Cacheable(cacheNames = "accCoreComponents", key = "#p0")
    public List<CcList> getAccCcList(long releaseId) {
        List<CcList> ccLists =
                jdbcTemplate.queryForList("SELECT 'acc' as type, acc_id as id, guid, den, u1.login_id as owner, " +
                        "state as row_state,is_deprecated as deprecated, " +
                        "revision_num, revision_tracking_num, revision_action, release_id, " +
                        "current_acc_id as current_id, u2.login_id as last_update_user, last_update_timestamp " +
                        "FROM acc JOIN app_user u1 ON acc.owner_user_id = u1.app_user_id " +
                        "JOIN app_user u2 ON acc.last_updated_by = u2.app_user_id", CcList.class);
        return arrangeByReleaseId(releaseId, ccLists);
    }

    @Cacheable(cacheNames = "asccpCoreComponents", key = "#p0")
    public List<CcList> getAsccpCcList(long releaseId) {
        List<CcList> ccLists =
                jdbcTemplate.queryForList("SELECT 'asccp' as type, asccp_id as id, guid, den, u1.login_id as owner, " +
                        "state as row_state,is_deprecated as deprecated, " +
                        "revision_num, revision_tracking_num, revision_action, release_id, " +
                        "current_asccp_id as current_id, u2.login_id as last_update_user, last_update_timestamp " +
                        "FROM asccp JOIN app_user u1 ON asccp.owner_user_id = u1.app_user_id " +
                        "JOIN app_user u2 ON asccp.last_updated_by = u2.app_user_id", CcList.class);
        return arrangeByReleaseId(releaseId, ccLists);
    }

    @Cacheable(cacheNames = "bccpCoreComponents", key = "#p0")
    public List<CcList> getBccpCcList(long releaseId) {
        List<CcList> ccLists =
                jdbcTemplate.queryForList("SELECT 'bccp' as type, bccp_id as id, guid, den, u1.login_id as owner, " +
                        "state as row_state,is_deprecated as deprecated, " +
                        "revision_num, revision_tracking_num, revision_action, release_id, " +
                        "current_bccp_id as current_id, u2.login_id as last_update_user, last_update_timestamp " +
                        "FROM bccp JOIN app_user u1 ON bccp.owner_user_id = u1.app_user_id " +
                        "JOIN app_user u2 ON bccp.last_updated_by = u2.app_user_id", CcList.class);
        return arrangeByReleaseId(releaseId, ccLists);
    }

    @Cacheable(cacheNames = "asccCoreComponents", key = "#p0")
    public List<CcList> getAsccCcList(long releaseId) {
        List<CcList> ccLists =
                jdbcTemplate.queryForList("SELECT 'ascc' as type, ascc_id as id, guid, den, u1.login_id as owner, " +
                        "state as row_state,is_deprecated as deprecated, " +
                        "revision_num, revision_tracking_num, revision_action, release_id, " +
                        "current_ascc_id as current_id, u2.login_id as last_update_user, last_update_timestamp " +
                        "FROM ascc JOIN app_user u1 ON ascc.owner_user_id = u1.app_user_id " +
                        "JOIN app_user u2 ON ascc.last_updated_by = u2.app_user_id", CcList.class);
        return arrangeByReleaseId(releaseId, ccLists);
    }

    @Cacheable(cacheNames = "bccCoreComponents", key = "#p0")
    public List<CcList> getBccCcList(long releaseId) {
        List<CcList> ccLists =
                jdbcTemplate.queryForList("SELECT 'bcc' as type, bcc_id as id, guid, den, u1.login_id as owner, " +
                        "state as row_state,is_deprecated as deprecated, " +
                        "revision_num, revision_tracking_num, revision_action, release_id, " +
                        "current_bcc_id as current_id, u2.login_id as last_update_user, last_update_timestamp " +
                        "FROM bcc JOIN app_user u1 ON bcc.owner_user_id = u1.app_user_id " +
                        "JOIN app_user u2 ON bcc.last_updated_by = u2.app_user_id", CcList.class);
        return arrangeByReleaseId(releaseId, ccLists);
    }

    private List<CcList> arrangeByReleaseId(long releaseId, List<CcList> ccLists) {
        Map<String, List<CcList>> groupingByTypeAndGuid =
                ccLists.stream().collect(groupingBy(e -> e.getType() + "-" + e.getGuid()));

        Stream<CcList> entityStream;
        if (releaseId == 0L) {
            entityStream = groupingByTypeAndGuid.values().stream()
                    .map(e -> {
                        for (CcList ccList : e) {
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

        Map<String, CcList> ccListMap = entityStream
                .collect(toMap(e -> e.getType() + "-" + e.getGuid(), Function.identity()));
        groupingByTypeAndGuid.entrySet().stream().forEach(entry -> {
            String revision = getRevision(releaseId, entry.getValue());
            ccListMap.get(entry.getKey()).setRevision(revision);
        });

        return ccListMap.values().stream().map(e -> {
            e.setState(CcState.valueOf(e.getRowState()));
            return e;
        }).collect(Collectors.toList());
    }
}
