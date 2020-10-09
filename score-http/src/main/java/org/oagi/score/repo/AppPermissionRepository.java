package org.oagi.score.repo;

import org.jooq.*;
import org.jooq.types.ULong;
import org.oagi.score.gateway.http.api.permission_management.data.AppPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static org.oagi.score.repo.api.impl.jooq.entity.Tables.APP_PERMISSION;

@Repository
public class AppPermissionRepository {
    @Autowired
    private DSLContext dslContext;

    private SelectJoinStep<Record5<ULong, String, String, String, String>> getSelectOnConditionStepForAppPermission() {
        return dslContext.select(APP_PERMISSION.APP_PERMISSION_ID, APP_PERMISSION.SEGMENT, APP_PERMISSION.OBJECT,
                APP_PERMISSION.OPERATION, APP_PERMISSION.DESCRIPTION)
                .from(APP_PERMISSION);
    }

    private void updateAppPermission(UpdateAppPermissionArguments arguments) {
        dslContext.update(APP_PERMISSION)
                .set(APP_PERMISSION.SEGMENT, arguments.getSegment())
                .set(APP_PERMISSION.OBJECT, arguments.getObject())
                .set(APP_PERMISSION.OPERATION, arguments.getOperation())
                .set(APP_PERMISSION.DESCRIPTION, arguments.getDescription())
                .where(APP_PERMISSION.APP_PERMISSION_ID.eq(arguments.getAppPermissionId()))
                .execute();
    }

    public class SelectAppPermissionArguments {
        private String segment;
        private String object;
        private final List<Condition> conditions = new ArrayList();
        private SortField sortField;
        private int offset = -1;
        private int numberOfRows = -1;

        public String getSegment() {
            return segment;
        }

        public SelectAppPermissionArguments setSegment(String segment) {
            this.segment = segment;
            return this;
        }

        public String getObject() {
            return object;
        }

        public SelectAppPermissionArguments setObject(String object) {
            this.object = object;
            return this;
        }

        public SortField getSortField() {
            return sortField;
        }

        public int getOffset() {
            return offset;
        }

        public SelectAppPermissionArguments setOffset(int offset) {
            this.offset = offset;
            return this;
        }

        public int getNumberOfRows() {
            return numberOfRows;
        }

        public SelectAppPermissionArguments setNumberOfRows(int numberOfRows) {
            this.numberOfRows = numberOfRows;
            return this;
        }

        private List<Condition> getConditions() {
            if (segment != null) {
                conditions.add(APP_PERMISSION.SEGMENT.contains(segment));
            }
            if (object != null) {
                conditions.add(APP_PERMISSION.OBJECT.contains(object));
            }
            return conditions;
        }

        public SelectAppPermissionArguments setSort(String field, String direction) {
            if (!StringUtils.isEmpty(field)) {
                switch (field) {
                    case "segment":
                        this.sortField = direction.equals("asc")
                                ? APP_PERMISSION.SEGMENT.asc() : APP_PERMISSION.SEGMENT.desc();
                        break;

                    case "object":
                        this.sortField
                                = direction.equals("asc") ? APP_PERMISSION.OBJECT.asc() : APP_PERMISSION.OBJECT.desc();
                        break;
                    case "operation":
                        this.sortField
                                = direction.equals("asc")
                                ? APP_PERMISSION.OPERATION.asc() : APP_PERMISSION.OPERATION.desc();
                        break;

                }
            }
            return this;
        }

        private <E> PaginationResponse<E> selectAppPermissions(SelectAppPermissionArguments arguments, Class<? extends E> type) {
            SelectJoinStep<Record5<ULong, String, String, String, String>> step
                    = getSelectOnConditionStepForAppPermission();

            SelectConnectByStep<Record5<ULong, String, String, String, String>> conditionStep
                    = step.where(arguments.getConditions());

            int pageCount = dslContext.fetchCount(conditionStep);

            SortField sortField = arguments.getSortField();
            SelectWithTiesAfterOffsetStep<Record5<ULong, String, String, String, String>> offsetStep = null;
            if (sortField != null) {
                if (arguments.getOffset() >= 0 && arguments.getNumberOfRows() >= 0) {
                    offsetStep = conditionStep.orderBy(sortField)
                            .limit(arguments.getOffset(), arguments.getNumberOfRows());
                }
            } else {
                if (arguments.getOffset() >= 0 && arguments.getNumberOfRows() >= 0) {
                    offsetStep = conditionStep
                            .limit(arguments.getOffset(), arguments.getNumberOfRows());
                }
            }

            return new PaginationResponse<>(pageCount,
                    (offsetStep != null) ?
                            offsetStep.fetchInto(type) : conditionStep.fetchInto(type));

        }

        public <E> PaginationResponse<E> fetchInto(Class<? extends E> type) {
            return selectAppPermissions(this, type);
        }

    }

    public class UpdateAppPermissionArguments {
        private ULong appPermissionId;
        private String segment;
        private String object;
        private String operation;
        private String description;

        public ULong getAppPermissionId() {
            return appPermissionId;
        }

        public UpdateAppPermissionArguments setAppPermissionId(ULong appPermissionId) {
            this.appPermissionId = appPermissionId;
            return this;
        }

        public String getSegment() {
            return segment;
        }

        public UpdateAppPermissionArguments setSegment(String segment) {
            this.segment = segment;
            return this;
        }

        public String getObject() {
            return object;
        }

        public UpdateAppPermissionArguments setObject(String object) {
            this.object = object;
            return this;
        }

        public String getOperation() {
            return operation;
        }

        public UpdateAppPermissionArguments setOperation(String operation) {
            this.operation = operation;
            return this;
        }

        public String getDescription() {
            return description;
        }

        public UpdateAppPermissionArguments setDescription(String description) {
            this.description = description;
            return this;
        }

        public AppPermission execute() {
            updateAppPermission(this);
            return findAppPermissionByAppPermissionId(this.getAppPermissionId());
        }
    }


    public SelectAppPermissionArguments selectAppPermissionList() {
        return new SelectAppPermissionArguments();
    }

    public UpdateAppPermissionArguments updateAppPermission() {
        return new UpdateAppPermissionArguments();
    }


    public AppPermission findAppPermissionByAppPermissionId(ULong appPermissionId) {
        if (appPermissionId == null) {
            return null;
        }

        AppPermission appPermission = getSelectOnConditionStepForAppPermission()
                .where(APP_PERMISSION.APP_PERMISSION_ID.eq(appPermissionId))
                .fetchOptionalInto(AppPermission.class).orElse(null);

        return appPermission;
    }
}
