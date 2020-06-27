package org.oagi.srt.repo.component.module;

import org.jooq.*;
import org.jooq.tools.StringUtils;
import org.jooq.types.ULong;
import org.oagi.srt.data.AppUser;
import org.oagi.srt.gateway.http.api.common.data.PageRequest;
import org.oagi.srt.gateway.http.api.common.data.PageResponse;
import org.oagi.srt.gateway.http.api.module_management.data.ModuleSet;
import org.oagi.srt.gateway.http.api.module_management.data.ModuleSetListRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.oagi.srt.entity.jooq.Tables.APP_USER;
import static org.oagi.srt.entity.jooq.Tables.MODULE_SET;

@Repository
public class ModuleSetReadRepository {
    
    @Autowired
    private DSLContext dslContext;

    private SelectOnConditionStep<Record6<
            ULong, String, String, String, LocalDateTime, 
            String>> getSelectOnConditionStep() {
        return dslContext.select(MODULE_SET.MODULE_SET_ID, MODULE_SET.GUID, MODULE_SET.NAME,
                MODULE_SET.DESCRIPTION, MODULE_SET.LAST_UPDATE_TIMESTAMP,
                APP_USER.as("updater").LOGIN_ID.as("last_uppdate_user"))
                .from(MODULE_SET)
                .join(APP_USER.as("updater"))
                .on(APP_USER.as("updater").APP_USER_ID.eq(MODULE_SET.LAST_UPDATED_BY));
    }
    
    public PageResponse<ModuleSet> fetch(AppUser requester, ModuleSetListRequest request) {
        PageRequest pageRequest = request.getPageRequest();
        SelectOnConditionStep<Record6<
                ULong, String, String, String, LocalDateTime,
                String>> selectOnConditionStep = getSelectOnConditionStep();

        List<Condition> conditions = new ArrayList();
        if (!StringUtils.isEmpty(request.getName())) {
            conditions.add(MODULE_SET.NAME.containsIgnoreCase(request.getName()));
        }
        if (!StringUtils.isEmpty(request.getDescription())) {
            conditions.add(MODULE_SET.DESCRIPTION.containsIgnoreCase(request.getDescription()));
        }
        if (!request.getUpdaterLoginIds().isEmpty()) {
            conditions.add(APP_USER.as("updater").LOGIN_ID.in(request.getUpdaterLoginIds()));
        }
        if (request.getUpdateStartDate() != null) {
            conditions.add(MODULE_SET.LAST_UPDATE_TIMESTAMP.greaterOrEqual(
                    new Timestamp(request.getUpdateStartDate().getTime()).toLocalDateTime()));
        }
        if (request.getUpdateEndDate() != null) {
            conditions.add(MODULE_SET.LAST_UPDATE_TIMESTAMP.lessThan(
                    new Timestamp(request.getUpdateEndDate().getTime()).toLocalDateTime()));
        }

        SelectConditionStep<Record6<
                ULong, String, String, String, LocalDateTime,
                String>> conditionStep = selectOnConditionStep.where(conditions);

        int length = dslContext.fetchCount(conditionStep);

        SortField sortField = null;
        if (!StringUtils.isEmpty(pageRequest.getSortActive())) {
            Field field = null;
            switch (pageRequest.getSortActive()) {
                case "name":
                    field = MODULE_SET.NAME;
                    break;

                case "lastUpdateTimestamp":
                    field = MODULE_SET.LAST_UPDATE_TIMESTAMP;
                    break;
            }

            if (field != null) {
                if ("asc".equalsIgnoreCase(pageRequest.getSortDirection())) {
                    sortField = field.asc();
                } else if ("desc".equalsIgnoreCase(pageRequest.getSortDirection())) {
                    sortField = field.desc();
                }
            }
        }

        ResultQuery<Record6<
                ULong, String, String, String, LocalDateTime,
                String>> query;
        if (sortField != null) {
            if (pageRequest.getOffset() >= 0 && pageRequest.getPageSize() >= 0) {
                query = conditionStep.orderBy(sortField)
                        .limit(pageRequest.getOffset(), pageRequest.getPageSize());
            } else {
                query = conditionStep.orderBy(sortField);
            }
        } else {
            if (pageRequest.getOffset() >= 0 && pageRequest.getPageSize() >= 0) {
                query = conditionStep.limit(pageRequest.getOffset(), pageRequest.getPageSize());
            } else {
                query = conditionStep;
            }
        }

        List<ModuleSet> results = query.fetchStream().map(record -> {
            ModuleSet moduleSet = new ModuleSet();
            moduleSet.setModuleSetId(record.get(MODULE_SET.MODULE_SET_ID).toBigInteger());
            moduleSet.setGuid(record.get(MODULE_SET.GUID));
            moduleSet.setName(record.get(MODULE_SET.NAME));
            moduleSet.setDescription(record.get(MODULE_SET.DESCRIPTION));
            moduleSet.setLastUpdateTimestamp(Date.from(record.get(MODULE_SET.LAST_UPDATE_TIMESTAMP).atZone(ZoneId.systemDefault()).toInstant()));
            moduleSet.setLastUpdateUser(record.get(APP_USER.as("updater").LOGIN_ID.as("last_uppdate_user")));
            return moduleSet;
        }).collect(Collectors.toList());

        PageResponse<ModuleSet> response = new PageResponse();
        response.setList(results);
        response.setPage(pageRequest.getPageIndex());
        response.setSize(pageRequest.getPageSize());
        response.setLength(length);

        return response;
    }
}
