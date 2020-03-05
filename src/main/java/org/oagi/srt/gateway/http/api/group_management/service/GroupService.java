package org.oagi.srt.gateway.http.api.group_management.service;

import org.jooq.types.ULong;
import org.oagi.srt.gateway.http.api.group_management.data.AppGroup;
import org.oagi.srt.gateway.http.api.group_management.data.GroupListRequest;
import org.oagi.srt.repo.AppGroupRepository;
import org.oagi.srt.repo.PaginationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = false)
public class GroupService {

    @Autowired
    AppGroupRepository repository;

    public PaginationResponse<AppGroup> getGroupList(GroupListRequest request) {

        PaginationResponse<AppGroup> paginationResponse = repository.selectAppGroupLists()
                .setName(request.getName())
                .setOffset(request.getPageRequest().getOffset())
                .setNumberOfRows(request.getPageRequest().getPageSize())
                .setSort(request.getPageRequest().getSortActive(), request.getPageRequest().getSortDirection())
                .fetchInto(AppGroup.class);

        return paginationResponse;

    }

    public AppGroup getGroup(int appGroupId) {
        AppGroup appGroup = repository.findAppGroupByAppGroupId(ULong.valueOf(appGroupId));
        appGroup.setGroupUsers(repository.findAppUserByAppGroupId(ULong.valueOf(appGroupId)));
        appGroup.setGroupPermissions(repository.findAppPermissionByAppGroupId(ULong.valueOf(appGroupId)));
        return appGroup;
    }

    @Transactional
    public AppGroup updateGroup(int appGroupId, String name, List<String> groupUsers, List<String> groupPermissions) {
        repository.updateAppGroup()
                .setAppGroupId(ULong.valueOf(appGroupId))
                .setName(name)
                .execute();

        repository.deleteAppGroupUserArguments()
                .setAppGroupId(ULong.valueOf(appGroupId))
                .execute();

        for (String userId : groupUsers) {
            try {
                ULong user = ULong.valueOf(userId);
                repository.insertAppGroupUserArguments()
                        .setAppGroupId(ULong.valueOf(appGroupId))
                        .setAppUserId(user)
                        .execute();
            } catch (NumberFormatException ignored) {
            }
        }

        repository.deleteAppPermissionGroupArguments()
                .setAppGroupId(ULong.valueOf(appGroupId))
                .execute();

        for (String permissionId : groupPermissions) {
            try {
                ULong permission = ULong.valueOf(permissionId);
                repository.insertAppPermissionGroupArguments()
                        .setAppGroupId(ULong.valueOf(appGroupId))
                        .setAppPermissionId(permission)
                        .execute();
            } catch (NumberFormatException ignored) {
            }
        }

        return getGroup(appGroupId);
    }

}
