package org.oagi.srt.gateway.http.api.permission_management.service;

import org.jooq.types.ULong;
import org.oagi.srt.gateway.http.api.permission_management.data.AppPermission;
import org.oagi.srt.gateway.http.api.permission_management.data.PermissionListRequest;
import org.oagi.srt.repo.AppPermissionRepository;
import org.oagi.srt.repo.PaginationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = false)
public class PermissionService {

    @Autowired AppPermissionRepository repository;

    public PaginationResponse<AppPermission> getPermissionList(PermissionListRequest request) {

        PaginationResponse<AppPermission> paginationResponse = repository.selectAppPermissionList()
                .setSegment(request.getSegment())
                .setObject(request.getObject())
                .setOffset(request.getPageRequest().getOffset())
                .setNumberOfRows(request.getPageRequest().getPageSize())
                .setSort(request.getPageRequest().getSortActive(), request.getPageRequest().getSortDirection())
                .fetchInto(AppPermission.class);

        return paginationResponse;

    }

    public AppPermission getPermission(int appPermissionId) {
        AppPermission appPermission = repository.findAppPermissionByAppPermissionId(ULong.valueOf(appPermissionId));
        return appPermission;
    }

    @Transactional
    public AppPermission updatePermission(int appPermissionId, String segment, String object, String operation,
                                          String description) {
        repository.updateAppPermission()
                .setAppPermissionId(ULong.valueOf(appPermissionId))
                .setSegment(segment)
                .setObject(object)
                .setOperation(operation)
                .setDescription(description)
                .execute();

        return getPermission(appPermissionId);
    }

}
