package org.oagi.srt.gateway.http.api.cc_management.repository;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.TableField;
import org.jooq.impl.DSL;
import org.jooq.types.ULong;
import org.oagi.srt.data.OagisComponentType;
import org.oagi.srt.data.Release;
import org.oagi.srt.entity.jooq.Tables;
import org.oagi.srt.entity.jooq.tables.AppUser;
import org.oagi.srt.gateway.http.api.cc_management.data.CcList;
import org.oagi.srt.gateway.http.api.cc_management.data.CcListRequest;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;
import org.oagi.srt.repository.ReleaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.oagi.srt.entity.jooq.Tables.*;

@Repository
public class CcListRepository {

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private ReleaseRepository releaseRepository;


    public List<CcList> getAccList(CcListRequest request) {
        if (!request.getTypes().isAcc()) {
            return Collections.emptyList();
        }

        Release release = releaseRepository.findById(request.getReleaseId());
        Condition whereCondition = DSL.trueCondition();
        AppUser appUserOwner = APP_USER.as("owner");
        AppUser appUserUpdater = APP_USER.as("updater");

        if (release.getReleaseNum().equals("Working")) {
            whereCondition = whereCondition.and(ACC.OAGIS_COMPONENT_TYPE.notEqual(OagisComponentType.UserExtensionGroup.getValue()));
        }
        if (request.getDeprecated() != null) {
            whereCondition = whereCondition.and(ACC.IS_DEPRECATED.eq((byte) (request.getDeprecated() ? 1 : 0)));
        }
        if (!request.getStates().isEmpty()) {
            whereCondition = whereCondition.and(ACC.STATE.in(
                    request.getStates().stream().map(CcState::getValue).collect(Collectors.toList())));
        }
        if (!request.getOwnerLoginIds().isEmpty()) {
            whereCondition = whereCondition.and(appUserOwner.LOGIN_ID.in(request.getOwnerLoginIds()));
        }
        if (!request.getUpdaterLoginIds().isEmpty()) {
            whereCondition = whereCondition.and(appUserUpdater.LOGIN_ID.in(request.getUpdaterLoginIds()));
        }
        if (request.getDen() != null && !request.getDen().isEmpty()) {
            whereCondition = whereCondition.and(getDenFilter(ACC.DEN, request.getDen()));
        }
        if (request.getDefinition() != null && !request.getDefinition().isEmpty()) {
            whereCondition = whereCondition.and(DSL.lower(ACC.DEFINITION).contains(request.getDefinition().trim().toLowerCase()));
        }
        if (request.getModule() != null && !request.getModule().isEmpty()) {
            whereCondition = whereCondition.and(DSL.lower(MODULE.MODULE_).contains(request.getModule().trim().toLowerCase()));
        }
        if (request.getUpdateStartDate() != null) {
            whereCondition = whereCondition.and(ACC.LAST_UPDATE_TIMESTAMP.greaterThan(new Timestamp(request.getUpdateStartDate().getTime())));
        }
        if (request.getUpdateEndDate() != null) {
            whereCondition = whereCondition.and(ACC.LAST_UPDATE_TIMESTAMP.lessThan(new Timestamp(request.getUpdateEndDate().getTime())));
        }

        return dslContext.select(ACC.ACC_ID,
                ACC.GUID,
                ACC.DEN,
                ACC.DEFINITION,
                ACC.DEFINITION_SOURCE,
                MODULE.MODULE_,
                ACC.OAGIS_COMPONENT_TYPE,
                ACC.STATE,
                ACC.IS_DEPRECATED,
                ACC.CURRENT_ACC_ID,
                ACC.LAST_UPDATE_TIMESTAMP,
                appUserOwner.LOGIN_ID.as("owner"),
                appUserUpdater.LOGIN_ID.as("last_update_user"),
                ACC.REVISION_NUM,
                ACC.REVISION_TRACKING_NUM,
                RELEASE.RELEASE_NUM
        )
                .from(ACC)
                .join(ACC_RELEASE_MANIFEST)
                .on(ACC.ACC_ID.eq(ACC_RELEASE_MANIFEST.ACC_ID).and(ACC_RELEASE_MANIFEST.RELEASE_ID.eq(ULong.valueOf(release.getReleaseId()))))
                .join(RELEASE)
                .on(ACC_RELEASE_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .join(MODULE)
                .on(ACC.MODULE_ID.eq(MODULE.MODULE_ID))
                .join(appUserOwner)
                .on(ACC.OWNER_USER_ID.eq(appUserOwner.APP_USER_ID))
                .join(appUserUpdater)
                .on(ACC.LAST_UPDATED_BY.eq(appUserUpdater.APP_USER_ID))
                .where(whereCondition)
                .fetch().map(row -> {
                    CcList ccList = new CcList();
                    ccList.setType("ACC");
                    ccList.setId(row.getValue(ACC.ACC_ID).longValue());
                    ccList.setGuid(row.getValue(ACC.GUID));
                    ccList.setDen(row.getValue(ACC.DEN));
                    ccList.setDefinition(row.getValue(ACC.DEFINITION));
                    ccList.setDefinitionSource(row.getValue(ACC.DEFINITION_SOURCE));
                    ccList.setModule(row.getValue(MODULE.MODULE_));
                    ccList.setOagisComponentType(OagisComponentType.valueOf(row.getValue(ACC.OAGIS_COMPONENT_TYPE)));
                    ccList.setState(CcState.valueOf(row.getValue(ACC.STATE)));
                    ccList.setDeprecated(row.getValue(ACC.IS_DEPRECATED) == 1);
                    ccList.setCurrentId(row.getValue(ACC.CURRENT_ACC_ID).longValue());
                    ccList.setLastUpdateTimestamp(row.getValue(ACC.LAST_UPDATE_TIMESTAMP));
                    ccList.setOwner((String) row.getValue("owner"));
                    ccList.setLastUpdateUser((String) row.getValue("last_update_user"));
                    ccList.setRevision(row.getValue(ACC.REVISION_NUM) + "." + row.getValue(ACC.REVISION_TRACKING_NUM));
                    return ccList;
                });
    }

    public List<CcList> getAsccList(CcListRequest request) {
        if (!request.getTypes().isAscc() || !StringUtils.isEmpty(request.getModule())) {
            return Collections.emptyList();
        }
        Release release = releaseRepository.findById(request.getReleaseId());
        Condition whereCondition = DSL.trueCondition();
        AppUser appUserOwner = APP_USER.as("owner");
        AppUser appUserUpdater = APP_USER.as("updater");

        whereCondition = whereCondition.and(ASCC.DEN.notLike("%User Extension Group"));

        if (release.getReleaseNum().equals("Working")) {
            whereCondition = whereCondition.and(ASCC.DEN.notContains("User Extension Group"));
        }
        if (request.getDeprecated() != null) {
            whereCondition = whereCondition.and(ASCC.IS_DEPRECATED.eq((byte) (request.getDeprecated() ? 1 : 0)));
        }
        if (!request.getStates().isEmpty()) {
            whereCondition = whereCondition.and(ASCC.STATE.in(
                    request.getStates().stream().map(CcState::getValue).collect(Collectors.toList())));
        }
        if (!request.getOwnerLoginIds().isEmpty()) {
            whereCondition = whereCondition.and(appUserOwner.LOGIN_ID.in(request.getOwnerLoginIds()));
        }
        if (!request.getUpdaterLoginIds().isEmpty()) {
            whereCondition = whereCondition.and(appUserUpdater.LOGIN_ID.in(request.getUpdaterLoginIds()));
        }
        if (request.getDen() != null && !request.getDen().isEmpty()) {
            whereCondition = whereCondition.and(getDenFilter(ASCC.DEN, request.getDen()));
        }
        if (request.getDefinition() != null && !request.getDefinition().isEmpty()) {
            whereCondition = whereCondition.and(DSL.lower(ASCC.DEFINITION).contains(request.getDefinition().trim().toLowerCase()));
        }
        if (request.getUpdateStartDate() != null) {
            whereCondition = whereCondition.and(ASCC.LAST_UPDATE_TIMESTAMP.greaterThan(new Timestamp(request.getUpdateStartDate().getTime())));
        }
        if (request.getUpdateEndDate() != null) {
            whereCondition = whereCondition.and(ASCC.LAST_UPDATE_TIMESTAMP.lessThan(new Timestamp(request.getUpdateEndDate().getTime())));
        }

        return dslContext.select(ASCC.ASCC_ID,
                ASCC.GUID,
                ASCC.DEN,
                ASCC.DEFINITION,
                ASCC.DEFINITION_SOURCE,
                ASCC.STATE,
                ASCC.IS_DEPRECATED,
                ASCC.CURRENT_ASCC_ID,
                ASCC.LAST_UPDATE_TIMESTAMP,
                appUserOwner.LOGIN_ID.as("owner"),
                appUserUpdater.LOGIN_ID.as("last_update_user"),
                ASCC.REVISION_NUM,
                ASCC.REVISION_TRACKING_NUM,
                RELEASE.RELEASE_NUM)
                .from(ASCC)
                .join(ASCC_RELEASE_MANIFEST)
                .on(ASCC.ASCC_ID.eq(ASCC_RELEASE_MANIFEST.ASCC_ID).and(ASCC_RELEASE_MANIFEST.RELEASE_ID.eq(ULong.valueOf(release.getReleaseId()))))
                .join(RELEASE)
                .on(ASCC_RELEASE_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .join(appUserOwner)
                .on(ASCC.OWNER_USER_ID.eq(appUserOwner.APP_USER_ID))
                .join(appUserUpdater)
                .on(ASCC.LAST_UPDATED_BY.eq(appUserUpdater.APP_USER_ID))
                .where(whereCondition)
                .fetch().map(row -> {
                    CcList ccList = new CcList();
                    ccList.setType("ASCC");
                    ccList.setId(row.getValue(ASCC.ASCC_ID).longValue());
                    ccList.setGuid(row.getValue(ASCC.GUID));
                    ccList.setDen(row.getValue(ASCC.DEN));
                    ccList.setDefinition(row.getValue(ASCC.DEFINITION));
                    ccList.setDefinitionSource(row.getValue(ASCC.DEFINITION_SOURCE));
                    ccList.setState(CcState.valueOf(row.getValue(ASCC.STATE)));
                    ccList.setDeprecated(row.getValue(ASCC.IS_DEPRECATED) == 1);
                    ccList.setCurrentId(row.getValue(ASCC.CURRENT_ASCC_ID).longValue());
                    ccList.setLastUpdateTimestamp(row.getValue(ASCC.LAST_UPDATE_TIMESTAMP));
                    ccList.setOwner((String) row.getValue("owner"));
                    ccList.setLastUpdateUser((String) row.getValue("last_update_user"));
                    ccList.setRevision(row.getValue(ASCC.REVISION_NUM) + "." + row.getValue(ASCC.REVISION_TRACKING_NUM));
                    return ccList;
                });
    }

    public List<CcList> getBccList(CcListRequest request) {
        if (!request.getTypes().isBcc() || !StringUtils.isEmpty(request.getModule())) {
            return Collections.emptyList();
        }

        Release release = releaseRepository.findById(request.getReleaseId());
        Condition whereCondition = DSL.trueCondition();
        AppUser appUserOwner = APP_USER.as("owner");
        AppUser appUserUpdater = APP_USER.as("updater");

        if (release.getReleaseNum().equals("Working")) {
            whereCondition = whereCondition.and(BCC.DEN.notContains("User Extension Group"));
        }
        if (request.getDeprecated() != null) {
            whereCondition = whereCondition.and(BCC.IS_DEPRECATED.eq((byte) (request.getDeprecated() ? 1 : 0)));
        }
        if (!request.getStates().isEmpty()) {
            whereCondition = whereCondition.and(BCC.STATE.in(
                    request.getStates().stream().map(CcState::getValue).collect(Collectors.toList())));
        }
        if (!request.getOwnerLoginIds().isEmpty()) {
            whereCondition = whereCondition.and(appUserOwner.LOGIN_ID.in(request.getOwnerLoginIds()));
        }
        if (!request.getUpdaterLoginIds().isEmpty()) {
            whereCondition = whereCondition.and(appUserUpdater.LOGIN_ID.in(request.getUpdaterLoginIds()));
        }
        if (request.getDen() != null && !request.getDen().isEmpty()) {
            whereCondition = whereCondition.and(getDenFilter(BCC.DEN, request.getDen()));
        }
        if (request.getDefinition() != null && !request.getDefinition().isEmpty()) {
            whereCondition = whereCondition.and(DSL.lower(BCC.DEFINITION).contains(request.getDefinition().trim().toLowerCase()));
        }
        if (request.getUpdateStartDate() != null) {
            whereCondition = whereCondition.and(BCC.LAST_UPDATE_TIMESTAMP.greaterThan(new Timestamp(request.getUpdateStartDate().getTime())));
        }
        if (request.getUpdateEndDate() != null) {
            whereCondition = whereCondition.and(BCC.LAST_UPDATE_TIMESTAMP.lessThan(new Timestamp(request.getUpdateEndDate().getTime())));
        }

        return dslContext.select(BCC.BCC_ID,
                BCC.GUID,
                BCC.DEN,
                BCC.DEFINITION,
                BCC.DEFINITION_SOURCE,
                BCC.STATE,
                BCC.IS_DEPRECATED,
                BCC.CURRENT_BCC_ID,
                BCC.LAST_UPDATE_TIMESTAMP,
                appUserOwner.LOGIN_ID.as("owner"),
                appUserUpdater.LOGIN_ID.as("last_update_user"),
                BCC.REVISION_NUM,
                BCC.REVISION_TRACKING_NUM,
                RELEASE.RELEASE_NUM)
                .from(BCC)
                .join(BCC_RELEASE_MANIFEST)
                .on(BCC.BCC_ID.eq(BCC_RELEASE_MANIFEST.BCC_ID).and(BCC_RELEASE_MANIFEST.RELEASE_ID.eq(ULong.valueOf(release.getReleaseId()))))
                .join(RELEASE)
                .on(BCC_RELEASE_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .join(appUserOwner)
                .on(BCC.OWNER_USER_ID.eq(appUserOwner.APP_USER_ID))
                .join(appUserUpdater)
                .on(BCC.LAST_UPDATED_BY.eq(appUserUpdater.APP_USER_ID))
                .where(whereCondition)
                .fetch().map(row -> {
                    CcList ccList = new CcList();
                    ccList.setType("BCC");
                    ccList.setId(row.getValue(BCC.BCC_ID).longValue());
                    ccList.setGuid(row.getValue(BCC.GUID));
                    ccList.setDen(row.getValue(BCC.DEN));
                    ccList.setDefinition(row.getValue(BCC.DEFINITION));
                    ccList.setDefinitionSource(row.getValue(BCC.DEFINITION_SOURCE));
                    ccList.setState(CcState.valueOf(row.getValue(BCC.STATE)));
                    ccList.setDeprecated(row.getValue(BCC.IS_DEPRECATED) == 1);
                    ccList.setCurrentId(row.getValue(BCC.CURRENT_BCC_ID).longValue());
                    ccList.setLastUpdateTimestamp(row.getValue(BCC.LAST_UPDATE_TIMESTAMP));
                    ccList.setOwner((String) row.getValue("owner"));
                    ccList.setLastUpdateUser((String) row.getValue("last_update_user"));
                    ccList.setRevision(row.getValue(BCC.REVISION_NUM) + "." + row.getValue(BCC.REVISION_TRACKING_NUM));
                    return ccList;
                });
    }

    public List<CcList> getAsccpList(CcListRequest request) {
        if (!request.getTypes().isAsccp()) {
            return Collections.emptyList();
        }
        Release release = releaseRepository.findById(request.getReleaseId());
        Condition whereCondition = DSL.trueCondition();
        AppUser appUserOwner = APP_USER.as("owner");
        AppUser appUserUpdater = APP_USER.as("updater");

        whereCondition = whereCondition.and(ASCCP.DEN.notLike("%User Extension Group"));

        if (release.getReleaseNum().equals("Working")) {
            whereCondition = whereCondition.and(ASCCP.DEN.notContains("User Extension Group"));
        }
        if (request.getDeprecated() != null) {
            whereCondition = whereCondition.and(ASCCP.IS_DEPRECATED.eq((byte) (request.getDeprecated() ? 1 : 0)));
        }
        if (!request.getStates().isEmpty()) {
            whereCondition = whereCondition.and(ASCCP.STATE.in(
                    request.getStates().stream().map(CcState::getValue).collect(Collectors.toList())));
        }
        if (!request.getOwnerLoginIds().isEmpty()) {
            whereCondition = whereCondition.and(appUserOwner.LOGIN_ID.in(request.getOwnerLoginIds()));
        }
        if (!request.getUpdaterLoginIds().isEmpty()) {
            whereCondition = whereCondition.and(appUserUpdater.LOGIN_ID.in(request.getUpdaterLoginIds()));
        }
        if (request.getDen() != null && !request.getDen().isEmpty()) {
            whereCondition = whereCondition.and(getDenFilter(ASCCP.DEN, request.getDen()));
        }
        if (request.getDefinition() != null && !request.getDefinition().isEmpty()) {
            whereCondition = whereCondition.and(DSL.lower(ASCCP.DEFINITION).contains(request.getDefinition().trim().toLowerCase()));
        }
        if (request.getUpdateStartDate() != null) {
            whereCondition = whereCondition.and(ASCCP.LAST_UPDATE_TIMESTAMP.greaterThan(new Timestamp(request.getUpdateStartDate().getTime())));
        }
        if (request.getUpdateEndDate() != null) {
            whereCondition = whereCondition.and(ASCCP.LAST_UPDATE_TIMESTAMP.lessThan(new Timestamp(request.getUpdateEndDate().getTime())));
        }

        return dslContext.select(ASCCP.ASCCP_ID,
                ASCCP.GUID,
                ASCCP.DEN,
                ASCCP.DEFINITION,
                ASCCP.DEFINITION_SOURCE,
                ASCCP.STATE,
                ASCCP.IS_DEPRECATED,
                ASCCP.CURRENT_ASCCP_ID,
                ASCCP.LAST_UPDATE_TIMESTAMP,
                appUserOwner.LOGIN_ID.as("owner"),
                appUserUpdater.LOGIN_ID.as("last_update_user"),
                ASCCP.REVISION_NUM,
                ASCCP.REVISION_TRACKING_NUM,
                RELEASE.RELEASE_NUM)
                .from(ASCCP)
                .join(ASCCP_RELEASE_MANIFEST)
                .on(ASCCP.ASCCP_ID.eq(ASCCP_RELEASE_MANIFEST.ASCCP_ID).and(ASCCP_RELEASE_MANIFEST.RELEASE_ID.eq(ULong.valueOf(release.getReleaseId()))))
                .join(RELEASE)
                .on(ASCCP_RELEASE_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .join(appUserOwner)
                .on(ASCCP.OWNER_USER_ID.eq(appUserOwner.APP_USER_ID))
                .join(appUserUpdater)
                .on(ASCCP.LAST_UPDATED_BY.eq(appUserUpdater.APP_USER_ID))
                .where(whereCondition)
                .fetch().map(row -> {
                    CcList ccList = new CcList();
                    ccList.setType("ASCCP");
                    ccList.setId(row.getValue(ASCCP.ASCCP_ID).longValue());
                    ccList.setGuid(row.getValue(ASCCP.GUID));
                    ccList.setDen(row.getValue(ASCCP.DEN));
                    ccList.setDefinition(row.getValue(ASCCP.DEFINITION));
                    ccList.setDefinitionSource(row.getValue(ASCCP.DEFINITION_SOURCE));
                    ccList.setState(CcState.valueOf(row.getValue(ASCCP.STATE)));
                    ccList.setDeprecated(row.getValue(ASCCP.IS_DEPRECATED) == 1);
                    ccList.setCurrentId(row.getValue(ASCCP.CURRENT_ASCCP_ID).longValue());
                    ccList.setLastUpdateTimestamp(row.getValue(ASCCP.LAST_UPDATE_TIMESTAMP));
                    ccList.setOwner((String) row.getValue("owner"));
                    ccList.setLastUpdateUser((String) row.getValue("last_update_user"));
                    ccList.setRevision(row.getValue(ASCCP.REVISION_NUM) + "." + row.getValue(ASCCP.REVISION_TRACKING_NUM));
                    return ccList;
                });
    }

    public List<CcList> getBccpList(CcListRequest request) {
        if (!request.getTypes().isBccp()) {
            return Collections.emptyList();
        }
        Release release = releaseRepository.findById(request.getReleaseId());
        Condition whereCondition = DSL.trueCondition();
        AppUser appUserOwner = APP_USER.as("owner");
        AppUser appUserUpdater = APP_USER.as("updater");

        if (request.getDeprecated() != null) {
            whereCondition = whereCondition.and(BCCP.IS_DEPRECATED.eq((byte) (request.getDeprecated() ? 1 : 0)));
        }
        if (!request.getStates().isEmpty()) {
            whereCondition = whereCondition.and(BCCP.STATE.in(
                    request.getStates().stream().map(CcState::getValue).collect(Collectors.toList())));
        }
        if (!request.getOwnerLoginIds().isEmpty()) {
            whereCondition = whereCondition.and(appUserOwner.LOGIN_ID.in(request.getOwnerLoginIds()));
        }
        if (!request.getUpdaterLoginIds().isEmpty()) {
            whereCondition = whereCondition.and(appUserUpdater.LOGIN_ID.in(request.getUpdaterLoginIds()));
        }
        if (request.getDen() != null && !request.getDen().isEmpty()) {
            whereCondition = whereCondition.and(getDenFilter(BCCP.DEN, request.getDen()));
        }
        if (request.getDefinition() != null && !request.getDefinition().isEmpty()) {
            whereCondition = whereCondition.and(DSL.lower(BCCP.DEFINITION).contains(request.getDefinition().trim().toLowerCase()));
        }
        if (request.getUpdateStartDate() != null) {
            whereCondition = whereCondition.and(BCCP.LAST_UPDATE_TIMESTAMP.greaterThan(new Timestamp(request.getUpdateStartDate().getTime())));
        }
        if (request.getUpdateEndDate() != null) {
            whereCondition = whereCondition.and(BCCP.LAST_UPDATE_TIMESTAMP.lessThan(new Timestamp(request.getUpdateEndDate().getTime())));
        }

        return dslContext.select(BCCP.BCCP_ID,
                BCCP.GUID,
                BCCP.DEN,
                BCCP.DEFINITION,
                BCCP.DEFINITION_SOURCE,
                MODULE.MODULE_,
                BCCP.STATE,
                BCCP.IS_DEPRECATED,
                BCCP.CURRENT_BCCP_ID,
                BCCP.LAST_UPDATE_TIMESTAMP,
                appUserOwner.LOGIN_ID.as("owner"),
                appUserUpdater.LOGIN_ID.as("last_update_user"),
                BCCP.REVISION_NUM,
                BCCP.REVISION_TRACKING_NUM,
                RELEASE.RELEASE_NUM)
                .from(BCCP)
                .join(BCCP_RELEASE_MANIFEST)
                .on(BCCP.BCCP_ID.eq(BCCP_RELEASE_MANIFEST.BCCP_ID).and(BCCP_RELEASE_MANIFEST.RELEASE_ID.eq(ULong.valueOf(release.getReleaseId()))))
                .join(RELEASE)
                .on(BCCP_RELEASE_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .join(MODULE)
                .on(BCCP.MODULE_ID.eq(MODULE.MODULE_ID))
                .join(appUserOwner)
                .on(BCCP.OWNER_USER_ID.eq(appUserOwner.APP_USER_ID))
                .join(appUserUpdater)
                .on(BCCP.LAST_UPDATED_BY.eq(appUserUpdater.APP_USER_ID))
                .where(whereCondition)
                .fetch().map(row -> {
                    CcList ccList = new CcList();
                    ccList.setType("BCCP");
                    ccList.setId(row.getValue(BCCP.BCCP_ID).longValue());
                    ccList.setGuid(row.getValue(BCCP.GUID));
                    ccList.setDen(row.getValue(BCCP.DEN));
                    ccList.setDefinition(row.getValue(BCCP.DEFINITION));
                    ccList.setDefinitionSource(row.getValue(BCCP.DEFINITION_SOURCE));
                    ccList.setModule(row.getValue(MODULE.MODULE_));
                    ccList.setState(CcState.valueOf(row.getValue(BCCP.STATE)));
                    ccList.setDeprecated(row.getValue(BCCP.IS_DEPRECATED) == 1);
                    ccList.setCurrentId(row.getValue(BCCP.CURRENT_BCCP_ID).longValue());
                    ccList.setLastUpdateTimestamp(row.getValue(BCCP.LAST_UPDATE_TIMESTAMP));
                    ccList.setOwner((String) row.getValue("owner"));
                    ccList.setLastUpdateUser((String) row.getValue("last_update_user"));
                    ccList.setRevision(row.getValue(BCCP.REVISION_NUM) + "." + row.getValue(BCCP.REVISION_TRACKING_NUM));
                    return ccList;
                });
    }

    public List<CcList> getBdtList(CcListRequest request) {
        if (!request.getTypes().isBdt()) {
            return Collections.emptyList();
        }
        Release release = releaseRepository.findById(request.getReleaseId());
        Condition whereCondition = DSL.trueCondition();
        AppUser appUserOwner = APP_USER.as("owner");
        AppUser appUserUpdater = APP_USER.as("updater");

        if (request.getDeprecated() != null) {
            whereCondition = whereCondition.and(DT.IS_DEPRECATED.eq((byte) (request.getDeprecated() ? 1 : 0)));
        }
        if (!request.getStates().isEmpty()) {
            whereCondition = whereCondition.and(DT.STATE.in(
                    request.getStates().stream().map(CcState::getValue).collect(Collectors.toList())));
        }
        if (!request.getOwnerLoginIds().isEmpty()) {
            whereCondition = whereCondition.and(appUserOwner.LOGIN_ID.in(request.getOwnerLoginIds()));
        }
        if (!request.getUpdaterLoginIds().isEmpty()) {
            whereCondition = whereCondition.and(appUserUpdater.LOGIN_ID.in(request.getUpdaterLoginIds()));
        }
        if (request.getDen() != null && !request.getDen().isEmpty()) {
            whereCondition = whereCondition.and(getDenFilter(DT.DEN, request.getDen()));
        }
        if (request.getDefinition() != null && !request.getDefinition().isEmpty()) {
            whereCondition = whereCondition.and(DSL.lower(DT.DEFINITION).contains(request.getDefinition().trim().toLowerCase()));
        }
        if (request.getUpdateStartDate() != null) {
            whereCondition = whereCondition.and(DT.LAST_UPDATE_TIMESTAMP.greaterThan(new Timestamp(request.getUpdateStartDate().getTime())));
        }
        if (request.getUpdateEndDate() != null) {
            whereCondition = whereCondition.and(DT.LAST_UPDATE_TIMESTAMP.lessThan(new Timestamp(request.getUpdateEndDate().getTime())));
        }

        return dslContext.select(DT.DT_ID,
                DT.GUID,
                DT.DEN,
                DT.DEFINITION,
                DT.DEFINITION_SOURCE,
                DT.STATE,
                DT.IS_DEPRECATED,
                DT.CURRENT_BDT_ID,
                DT.LAST_UPDATE_TIMESTAMP,
                appUserOwner.LOGIN_ID.as("owner"),
                appUserUpdater.LOGIN_ID.as("last_update_user"),
                RELEASE.RELEASE_NUM)
                .from(DT)
                .join(DT_RELEASE_MANIFEST)
                .on(DT.DT_ID.eq(DT_RELEASE_MANIFEST.DT_ID).and(DT_RELEASE_MANIFEST.RELEASE_ID.eq(ULong.valueOf(release.getReleaseId()))))
                .join(RELEASE)
                .on(DT_RELEASE_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .join(appUserOwner)
                .on(DT.OWNER_USER_ID.eq(appUserOwner.APP_USER_ID))
                .join(appUserUpdater)
                .on(DT.LAST_UPDATED_BY.eq(appUserUpdater.APP_USER_ID))
                .where(whereCondition)
                .fetch().map(row -> {
                    CcList ccList = new CcList();
                    ccList.setType("BDT");
                    ccList.setId(row.getValue(DT.DT_ID).longValue());
                    ccList.setGuid(row.getValue(DT.GUID));
                    ccList.setDen(row.getValue(DT.DEN));
                    ccList.setDefinition(row.getValue(DT.DEFINITION));
                    ccList.setDefinitionSource(row.getValue(DT.DEFINITION_SOURCE));
                    ccList.setState(CcState.valueOf(row.getValue(DT.STATE)));
                    ccList.setDeprecated(row.getValue(DT.IS_DEPRECATED) == 1);
                    ccList.setCurrentId(row.getValue(DT.CURRENT_BDT_ID).longValue());
                    ccList.setLastUpdateTimestamp(row.getValue(DT.LAST_UPDATE_TIMESTAMP));
                    ccList.setOwner((String) row.getValue("owner"));
                    ccList.setLastUpdateUser((String) row.getValue("last_update_user"));
                    ccList.setRevision(row.getValue(RELEASE.RELEASE_NUM));
                    return ccList;
                });
    }

    private Condition getDenFilter(TableField<?, String> field, String keyword) {
        Condition denCondition = null;
        List<String> filters = Arrays.asList(keyword.toLowerCase().split(" ")).stream()
                .map(e -> e.replaceAll("[^a-z]", "").trim()).collect(Collectors.toList());
        for (String token : filters) {
            if (denCondition == null) {
                denCondition = field.contains(token);
            } else {
                denCondition = denCondition.and(field.contains(token));
            }

        }
        return denCondition;
    }
}
