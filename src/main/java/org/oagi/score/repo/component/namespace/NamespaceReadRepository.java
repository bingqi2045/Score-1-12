package org.oagi.score.repo.component.namespace;

import org.jooq.*;
import org.jooq.tools.StringUtils;
import org.jooq.types.ULong;
import org.oagi.score.data.AppUser;
import org.oagi.score.gateway.http.api.common.data.PageRequest;
import org.oagi.score.gateway.http.api.common.data.PageResponse;
import org.oagi.score.gateway.http.api.namespace_management.data.NamespaceList;
import org.oagi.score.gateway.http.api.namespace_management.data.NamespaceListRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.oagi.score.entity.jooq.Tables.APP_USER;
import static org.oagi.score.entity.jooq.Tables.NAMESPACE;

@Repository
public class NamespaceReadRepository {

    @Autowired
    private DSLContext dslContext;

    private SelectOnConditionStep<Record8<
            ULong, String, String, ULong, String,
            String, LocalDateTime, String>> getSelectOnConditionStep() {
        return dslContext.select(NAMESPACE.NAMESPACE_ID, NAMESPACE.URI, NAMESPACE.PREFIX,
                APP_USER.as("owner").APP_USER_ID.as("owner_user_id"),
                APP_USER.as("owner").LOGIN_ID.as("owner"),
                NAMESPACE.DESCRIPTION, NAMESPACE.LAST_UPDATE_TIMESTAMP,
                APP_USER.as("updater").LOGIN_ID.as("last_update_user"))
                .from(NAMESPACE)
                .join(APP_USER.as("owner"))
                .on(NAMESPACE.OWNER_USER_ID.eq(APP_USER.as("owner").APP_USER_ID))
                .join(APP_USER.as("updater"))
                .on(APP_USER.as("updater").APP_USER_ID.eq(NAMESPACE.LAST_UPDATED_BY));
    }

    public PageResponse<NamespaceList> fetch(AppUser requester,
                                             NamespaceListRequest request) {

        PageRequest pageRequest = request.getPageRequest();
        SelectOnConditionStep<Record8<
                ULong, String, String, ULong, String,
                String, LocalDateTime, String>> selectOnConditionStep = getSelectOnConditionStep();

        List<Condition> conditions = new ArrayList();
        if (!StringUtils.isEmpty(request.getUri())) {
            conditions.add(NAMESPACE.URI.containsIgnoreCase(request.getUri()));
        }
        if (!StringUtils.isEmpty(request.getPrefix())) {
            conditions.add(NAMESPACE.PREFIX.containsIgnoreCase(request.getPrefix()));
        }
        if (!StringUtils.isEmpty(request.getDescription())) {
            conditions.add(NAMESPACE.DESCRIPTION.containsIgnoreCase(request.getDescription()));
        }
        if (!request.getOwnerLoginIds().isEmpty()) {
            conditions.add(APP_USER.as("owner").LOGIN_ID.in(request.getOwnerLoginIds()));
        }
        if (!request.getUpdaterLoginIds().isEmpty()) {
            conditions.add(APP_USER.as("updater").LOGIN_ID.in(request.getUpdaterLoginIds()));
        }
        if (request.getUpdateStartDate() != null) {
            conditions.add(NAMESPACE.LAST_UPDATE_TIMESTAMP.greaterOrEqual(
                    new Timestamp(request.getUpdateStartDate().getTime()).toLocalDateTime()));
        }
        if (request.getUpdateEndDate() != null) {
            conditions.add(NAMESPACE.LAST_UPDATE_TIMESTAMP.lessThan(
                    new Timestamp(request.getUpdateEndDate().getTime()).toLocalDateTime()));
        }

        SelectConditionStep<Record8<
                ULong, String, String, ULong, String,
                String, LocalDateTime, String>> conditionStep = selectOnConditionStep.where(conditions);

        int length = dslContext.fetchCount(conditionStep);

        SortField sortField = null;
        if (!StringUtils.isEmpty(pageRequest.getSortActive())) {
            Field field = null;
            switch (pageRequest.getSortActive()) {
                case "uri":
                    field = NAMESPACE.URI;
                    break;

                case "prefix":
                    field = NAMESPACE.PREFIX;
                    break;

                case "lastUpdateTimestamp":
                    field = NAMESPACE.LAST_UPDATE_TIMESTAMP;
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

        ResultQuery<Record8<
                ULong, String, String, ULong, String,
                String, LocalDateTime, String>> query;
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

        List<NamespaceList> results = query.fetchStream().map(record -> {
            NamespaceList namespaceList = new NamespaceList();
            namespaceList.setNamespaceId(record.get(NAMESPACE.NAMESPACE_ID).toBigInteger());
            namespaceList.setUri(record.get(NAMESPACE.URI));
            namespaceList.setPrefix(record.get(NAMESPACE.PREFIX));
            namespaceList.setDescription(record.get(NAMESPACE.DESCRIPTION));
            namespaceList.setOwner(record.get(APP_USER.as("owner").LOGIN_ID.as("owner")));
            namespaceList.setLastUpdateTimestamp(Date.from(record.get(NAMESPACE.LAST_UPDATE_TIMESTAMP).atZone(ZoneId.systemDefault()).toInstant()));
            namespaceList.setLastUpdateUser(record.get(APP_USER.as("updater").LOGIN_ID.as("last_update_user")));
            return namespaceList;
        }).collect(Collectors.toList());

        PageResponse<NamespaceList> response = new PageResponse();
        response.setList(results);
        response.setPage(pageRequest.getPageIndex());
        response.setSize(pageRequest.getPageSize());
        response.setLength(length);

        return response;
    }
}
