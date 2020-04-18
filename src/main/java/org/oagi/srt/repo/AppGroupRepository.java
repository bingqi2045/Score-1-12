package org.oagi.srt.repo;

import org.jooq.*;
import org.jooq.types.UInteger;
import org.jooq.types.ULong;
import org.oagi.srt.gateway.http.api.account_management.data.AppUser;
import org.oagi.srt.gateway.http.api.group_management.data.AppGroup;
import org.oagi.srt.gateway.http.api.permission_management.data.AppPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static org.oagi.srt.entity.jooq.Tables.*;

@Repository
public class AppGroupRepository {

    @Autowired
    private DSLContext dslContext;

    private SelectJoinStep<Record3<ULong, String, UInteger>> getSelectOnConditionStepForAppGroup() {
        return dslContext.select(APP_GROUP.APP_GROUP_ID, APP_GROUP.NAME, APP_GROUP.AUTHORITY)
                .from(APP_GROUP);
    }

    private SelectWhereStep<Record4<ULong, String, String, String>> getSelectOnConditionStepForAppUser() {
        return dslContext.select(APP_USER.APP_USER_ID, APP_USER.LOGIN_ID, APP_USER.ORGANIZATION, APP_USER.NAME)
                .from(APP_GROUP)
                .join(APP_GROUP_USER).on(APP_GROUP.APP_GROUP_ID.eq(APP_GROUP_USER.APP_GROUP_ID))
                .join(APP_USER).on(APP_GROUP_USER.APP_USER_ID.eq(APP_USER.APP_USER_ID));
    }

    private SelectWhereStep<Record3<ULong, String, String>> getSelectOnConditionStepForAppPermission() {
        return dslContext.select(APP_PERMISSION.APP_PERMISSION_ID, APP_PERMISSION.SEGMENT, APP_PERMISSION.OBJECT)
                .from(APP_GROUP)
                .join(APP_PERMISSION_GROUP).on(APP_GROUP.APP_GROUP_ID.eq(APP_PERMISSION_GROUP.APP_GROUP_ID))
                .join(APP_PERMISSION).on(APP_PERMISSION_GROUP.APP_PERMISSION_ID.eq(APP_PERMISSION.APP_PERMISSION_ID));
    }

    private void updateAppGroup(UpdateAppGroupArguments arguments) {
        if (!StringUtils.isEmpty(arguments.getName())) {
            dslContext.update(APP_GROUP)
                    .set(APP_GROUP.NAME, arguments.getName())
                    .where(APP_GROUP.APP_GROUP_ID.eq(arguments.getAppGroupId()))
                    .execute();
        }
    }

    private void insertAppGroupUser(InsertAppGroupUserArguments arguments) {
        dslContext.insertInto(APP_GROUP_USER)
                .set(APP_GROUP_USER.APP_USER_ID, arguments.getAppUserId())
                .set(APP_GROUP_USER.APP_GROUP_ID, arguments.getAppGroupId())
                .execute();
    }

    private void deleteAppGroupUser(DeleteAppGroupUserArguments arguments) {
        dslContext.deleteFrom(APP_GROUP_USER)
                .where(APP_GROUP_USER.APP_GROUP_ID.eq(arguments.getAppGroupId()))
                .execute();
    }

    private void insertAppPermissionGroup(InsertAppPermissionGroupArguments arguments) {
        dslContext.insertInto(APP_PERMISSION_GROUP)
                .set(APP_PERMISSION_GROUP.APP_PERMISSION_ID, arguments.getAppPermissionId())
                .set(APP_PERMISSION_GROUP.APP_GROUP_ID, arguments.getAppGroupId())
                .execute();
    }

    private void deleteAppPermissionGroup(DeleteAppPermissionGroupArguments arguments) {
        dslContext.deleteFrom(APP_PERMISSION_GROUP)
                .where(APP_PERMISSION_GROUP.APP_GROUP_ID.eq(arguments.getAppGroupId()))
                .execute();
    }

    public class SelectAppGroupArguments {
        private String name;
        private List<Condition> conditions = new ArrayList();
        private SortField sortField;
        private int offset = -1;
        private int numberOfRows = -1;

        public String getName() {
            return name;
        }

        public SelectAppGroupArguments setName(String name) {
            this.name = name;
            return this;
        }

        public SortField getSortField() {
            return sortField;
        }

        public int getOffset() {
            return offset;
        }

        public SelectAppGroupArguments setOffset(int offset) {
            this.offset = offset;
            return this;
        }

        public int getNumberOfRows() {
            return numberOfRows;
        }

        public SelectAppGroupArguments setNumberOfRows(int numberOfRows) {
            this.numberOfRows = numberOfRows;
            return this;
        }

        private List<Condition> getConditions() {
            if (name != null) {
                conditions.add(APP_GROUP.NAME.contains(name));
            }
            return conditions;
        }

        public SelectAppGroupArguments setSort(String field, String direction) {
            if (!StringUtils.isEmpty(field)) {
                switch (field) {
                    case "name":
                        this.sortField = direction.equals("asc") ? APP_GROUP.NAME.asc() : APP_GROUP.NAME.desc();
                        break;

                    case "authority":
                        this.sortField
                                = direction.equals("asc") ? APP_GROUP.AUTHORITY.asc() : APP_GROUP.AUTHORITY.desc();
                        break;

                }
            }
            return this;
        }

        private <E> PaginationResponse<E> selectAppGroups(SelectAppGroupArguments arguments, Class<? extends E> type) {
            SelectJoinStep<Record3<ULong, String, UInteger>> step = getSelectOnConditionStepForAppGroup();

            SelectConnectByStep<Record3<ULong, String, UInteger>> conditionStep
                    = step.where(arguments.getConditions());

            int pageCount = dslContext.fetchCount(conditionStep);

            SortField sortField = arguments.getSortField();
            SelectWithTiesAfterOffsetStep<Record3<ULong, String, UInteger>> offsetStep = null;
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
            return selectAppGroups(this, type);
        }

    }

    public class InsertAppGroupArguments {
        void execute() {

        }
    }

    public class UpdateAppGroupArguments {
        private ULong appGroupId;
        private String name;

        public ULong getAppGroupId() {
            return appGroupId;
        }

        public UpdateAppGroupArguments setAppGroupId(ULong appGroupId) {
            this.appGroupId = appGroupId;
            return this;
        }

        public String getName() {
            return name;
        }

        public UpdateAppGroupArguments setName(String name) {
            this.name = name;
            return this;
        }

        public AppGroup execute() {
            updateAppGroup(this);
            return findAppGroupByAppGroupId(this.getAppGroupId());
        }
    }

    public class DeleteAppGroupArguments {
        void execute() {

        }
    }

    public class InsertAppGroupUserArguments {
        ULong appGroupId;
        ULong appUserId;

        public ULong getAppGroupId() {
            return appGroupId;
        }

        public InsertAppGroupUserArguments setAppGroupId(ULong appGroupId) {
            this.appGroupId = appGroupId;
            return this;
        }

        public ULong getAppUserId() {
            return appUserId;
        }

        public InsertAppGroupUserArguments setAppUserId(ULong appUserId) {
            this.appUserId = appUserId;
            return this;
        }

        public void execute() {
            insertAppGroupUser(this);
        }
    }

    public class DeleteAppGroupUserArguments {
        ULong appGroupId;

        public ULong getAppGroupId() {
            return appGroupId;
        }

        public DeleteAppGroupUserArguments setAppGroupId(ULong appGroupId) {
            this.appGroupId = appGroupId;
            return this;
        }

        public void execute() {
            deleteAppGroupUser(this);
        }
    }

    public class InsertAppPermissionGroupArguments {
        ULong appGroupId;
        ULong appPermissionId;

        public ULong getAppGroupId() {
            return appGroupId;
        }

        public InsertAppPermissionGroupArguments setAppGroupId(ULong appGroupId) {
            this.appGroupId = appGroupId;
            return this;
        }

        public ULong getAppPermissionId() {
            return appPermissionId;
        }

        public InsertAppPermissionGroupArguments setAppPermissionId(ULong appPermissionId) {
            this.appPermissionId = appPermissionId;
            return this;
        }

        public void execute() {
            insertAppPermissionGroup(this);
        }
    }

    public class DeleteAppPermissionGroupArguments {
        ULong appGroupId;

        public ULong getAppGroupId() {
            return appGroupId;
        }

        public DeleteAppPermissionGroupArguments setAppGroupId(ULong appGroupId) {
            this.appGroupId = appGroupId;
            return this;
        }

        public void execute() {
            deleteAppPermissionGroup(this);
        }
    }

    public SelectAppGroupArguments selectAppGroupLists() {
        return new SelectAppGroupArguments();
    }

    public UpdateAppGroupArguments updateAppGroup() {
        return new UpdateAppGroupArguments();
    }

    public InsertAppGroupUserArguments insertAppGroupUserArguments() {
        return new InsertAppGroupUserArguments();
    }

    public DeleteAppGroupUserArguments deleteAppGroupUserArguments() {
        return new DeleteAppGroupUserArguments();
    }

    public InsertAppPermissionGroupArguments insertAppPermissionGroupArguments() {
        return new InsertAppPermissionGroupArguments();
    }

    public DeleteAppPermissionGroupArguments deleteAppPermissionGroupArguments() {
        return new DeleteAppPermissionGroupArguments();
    }

    public AppGroup findAppGroupByAppGroupId(ULong appGroupId) {
        if (appGroupId == null) {
            return null;
        }

        AppGroup appGroup = getSelectOnConditionStepForAppGroup()
                .where(APP_GROUP.APP_GROUP_ID.eq(appGroupId))
                .fetchOptionalInto(AppGroup.class).orElse(null);
        if (appGroup == null) {
            return null;
        }

        return appGroup;
    }

    public List<AppUser> findAppUserByAppGroupId(ULong appGroupId) {
        if (appGroupId == null) {
            return null;
        }
        return getSelectOnConditionStepForAppUser()
                .where(APP_GROUP.APP_GROUP_ID.eq(appGroupId))
                .fetchInto(AppUser.class);
    }

    public List<AppPermission> findAppPermissionByAppGroupId(ULong appGroupId) {
        if (appGroupId == null) {
            return null;
        }
        return getSelectOnConditionStepForAppPermission()
                .where(APP_GROUP.APP_GROUP_ID.eq(appGroupId))
                .fetchInto(AppPermission.class);
    }

}
