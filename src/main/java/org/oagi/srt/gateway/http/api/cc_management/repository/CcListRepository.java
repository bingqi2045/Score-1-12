package org.oagi.srt.gateway.http.api.cc_management.repository;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.TableField;
import org.jooq.impl.DSL;
import org.jooq.types.ULong;
import org.oagi.srt.data.OagisComponentType;
import org.oagi.srt.data.Release;
import org.oagi.srt.data.RevisionAction;
import org.oagi.srt.entity.jooq.tables.AppUser;
import org.oagi.srt.entity.jooq.tables.records.*;
import org.oagi.srt.gateway.http.api.cc_management.data.CcList;
import org.oagi.srt.gateway.http.api.cc_management.data.CcListRequest;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;
import org.oagi.srt.repository.ReleaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.oagi.srt.data.DTType.BDT;
import static org.oagi.srt.entity.jooq.Tables.*;

@Repository
public class CcListRepository {

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private ReleaseRepository releaseRepository;

    @Autowired
    private ReleaseManifestRepository manifestRepository;

    @Autowired
    private CcNodeRepository nodeRepository;

    public List<CcList> getAccList(CcListRequest request) {
        if (!request.getTypes().isAcc()) {
            return Collections.emptyList();
        }

        Release release = releaseRepository.findById(request.getReleaseId());
        AppUser appUserOwner = APP_USER.as("owner");
        AppUser appUserUpdater = APP_USER.as("updater");

        List<Condition> conditions = new ArrayList();
        conditions.add(ACC_RELEASE_MANIFEST.RELEASE_ID.eq(ULong.valueOf(request.getReleaseId())));
        if (release.getReleaseNum().equals("Working")) {
            conditions.add(ACC.OAGIS_COMPONENT_TYPE.notEqual(OagisComponentType.UserExtensionGroup.getValue()));
        }
        if (request.getDeprecated() != null) {
            conditions.add(ACC.IS_DEPRECATED.eq((byte) (request.getDeprecated() ? 1 : 0)));
        }
        if (!request.getStates().isEmpty()) {
            conditions.add(ACC.STATE.in(
                    request.getStates().stream().map(CcState::getValue).collect(Collectors.toList())));
        }
        if (!request.getOwnerLoginIds().isEmpty()) {
            conditions.add(appUserOwner.LOGIN_ID.in(request.getOwnerLoginIds()));
        }
        if (!request.getUpdaterLoginIds().isEmpty()) {
            conditions.add(appUserUpdater.LOGIN_ID.in(request.getUpdaterLoginIds()));
        }
        if (!request.getExcludes().isEmpty()) {
            conditions.add(ACC_RELEASE_MANIFEST.ACC_RELEASE_MANIFEST_ID.notIn(request.getExcludes()));
        }
        if (request.getDen() != null && !request.getDen().isEmpty()) {
            conditions.add(getDenFilter(ACC.DEN, request.getDen()));
        }
        if (request.getDefinition() != null && !request.getDefinition().isEmpty()) {
            conditions.add(DSL.lower(ACC.DEFINITION).contains(request.getDefinition().trim().toLowerCase()));
        }
        if (request.getModule() != null && !request.getModule().isEmpty()) {
            conditions.add(DSL.lower(MODULE.MODULE_).contains(request.getModule().trim().toLowerCase()));
        }
        if (request.getUpdateStartDate() != null) {
            conditions.add(ACC.LAST_UPDATE_TIMESTAMP.greaterThan(new Timestamp(request.getUpdateStartDate().getTime())));
        }
        if (request.getUpdateEndDate() != null) {
            conditions.add(ACC.LAST_UPDATE_TIMESTAMP.lessThan(new Timestamp(request.getUpdateEndDate().getTime())));
        }
        if (request.getComponentTypes() != null && !request.getComponentTypes().isEmpty()) {
            conditions.add(ACC.OAGIS_COMPONENT_TYPE.in(Arrays.asList(request.getComponentTypes().split(","))));
        }

        return dslContext.select(
                ACC_RELEASE_MANIFEST.ACC_RELEASE_MANIFEST_ID,
                ACC.ACC_ID,
                ACC.GUID,
                ACC.DEN,
                ACC.DEFINITION,
                ACC.DEFINITION_SOURCE,
                MODULE.MODULE_,
                ACC.OAGIS_COMPONENT_TYPE,
                ACC.STATE,
                ACC.IS_DEPRECATED,
                ACC.LAST_UPDATE_TIMESTAMP,
                appUserOwner.LOGIN_ID.as("owner"),
                appUserUpdater.LOGIN_ID.as("last_update_user"),
                ACC.REVISION_NUM,
                ACC.REVISION_TRACKING_NUM)
                .from(ACC)
                .join(ACC_RELEASE_MANIFEST)
                .on(ACC.ACC_ID.eq(ACC_RELEASE_MANIFEST.ACC_ID).and(ACC_RELEASE_MANIFEST.RELEASE_ID.eq(ULong.valueOf(release.getReleaseId()))))
                .join(RELEASE)
                .on(ACC_RELEASE_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .join(appUserOwner)
                .on(ACC.OWNER_USER_ID.eq(appUserOwner.APP_USER_ID))
                .join(appUserUpdater)
                .on(ACC.LAST_UPDATED_BY.eq(appUserUpdater.APP_USER_ID))
                .leftJoin(MODULE)
                .on(ACC_RELEASE_MANIFEST.MODULE_ID.eq(MODULE.MODULE_ID))
                .where(conditions)
                .fetch().map(row -> {
                    CcList ccList = new CcList();
                    ccList.setType("ACC");
                    ccList.setManifestId(row.getValue(ACC_RELEASE_MANIFEST.ACC_RELEASE_MANIFEST_ID).longValue());
                    ccList.setId(row.getValue(ACC.ACC_ID).longValue());
                    ccList.setGuid(row.getValue(ACC.GUID));
                    ccList.setDen(row.getValue(ACC.DEN));
                    ccList.setDefinition(org.apache.commons.lang3.StringUtils.stripToNull(row.getValue(ACC.DEFINITION)));
                    ccList.setDefinitionSource(org.apache.commons.lang3.StringUtils.stripToNull(row.getValue(ACC.DEFINITION_SOURCE)));
                    ccList.setModule(row.getValue(MODULE.MODULE_));
                    ccList.setOagisComponentType(OagisComponentType.valueOf(row.getValue(ACC.OAGIS_COMPONENT_TYPE)));
                    ccList.setState(CcState.valueOf(row.getValue(ACC.STATE)));
                    ccList.setDeprecated(row.getValue(ACC.IS_DEPRECATED) == 1);
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
        AppUser appUserOwner = APP_USER.as("owner");
        AppUser appUserUpdater = APP_USER.as("updater");

        List<Condition> conditions = new ArrayList();
        conditions.add(ASCC_RELEASE_MANIFEST.RELEASE_ID.eq(ULong.valueOf(request.getReleaseId())));
        conditions.add(ASCC.DEN.notLike("%User Extension Group"));

        if (release.getReleaseNum().equals("Working")) {
            conditions.add(ASCC.DEN.notContains("User Extension Group"));
        }
        if (request.getDeprecated() != null) {
            conditions.add(ASCC.IS_DEPRECATED.eq((byte) (request.getDeprecated() ? 1 : 0)));
        }
        if (!request.getStates().isEmpty()) {
            conditions.add(ASCC.STATE.in(
                    request.getStates().stream().map(CcState::getValue).collect(Collectors.toList())));
        }
        if (!request.getOwnerLoginIds().isEmpty()) {
            conditions.add(appUserOwner.LOGIN_ID.in(request.getOwnerLoginIds()));
        }
        if (!request.getUpdaterLoginIds().isEmpty()) {
            conditions.add(appUserUpdater.LOGIN_ID.in(request.getUpdaterLoginIds()));
        }
        if (!request.getExcludes().isEmpty()) {
            conditions.add(ASCC_RELEASE_MANIFEST.ASCC_RELEASE_MANIFEST_ID.notIn(request.getExcludes()));
        }
        if (request.getDen() != null && !request.getDen().isEmpty()) {
            conditions.add(getDenFilter(ASCC.DEN, request.getDen()));
        }
        if (request.getDefinition() != null && !request.getDefinition().isEmpty()) {
            conditions.add(DSL.lower(ASCC.DEFINITION).contains(request.getDefinition().trim().toLowerCase()));
        }
        if (request.getUpdateStartDate() != null) {
            conditions.add(ASCC.LAST_UPDATE_TIMESTAMP.greaterThan(new Timestamp(request.getUpdateStartDate().getTime())));
        }
        if (request.getUpdateEndDate() != null) {
            conditions.add(ASCC.LAST_UPDATE_TIMESTAMP.lessThan(new Timestamp(request.getUpdateEndDate().getTime())));
        }

        return dslContext.select(
                ASCC_RELEASE_MANIFEST.ASCC_RELEASE_MANIFEST_ID,
                ASCC.GUID,
                ASCC.DEN,
                ASCC.DEFINITION,
                ASCC.DEFINITION_SOURCE,
                ASCC.STATE,
                ASCC.IS_DEPRECATED,
                ASCC.LAST_UPDATE_TIMESTAMP,
                appUserOwner.LOGIN_ID.as("owner"),
                appUserUpdater.LOGIN_ID.as("last_update_user"),
                ASCC.REVISION_NUM,
                ASCC.REVISION_TRACKING_NUM)
                .from(ASCC)
                .join(ASCC_RELEASE_MANIFEST)
                .on(ASCC.ASCC_ID.eq(ASCC_RELEASE_MANIFEST.ASCC_ID).and(ASCC_RELEASE_MANIFEST.RELEASE_ID.eq(ULong.valueOf(release.getReleaseId()))))
                .join(RELEASE)
                .on(ASCC_RELEASE_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .join(appUserOwner)
                .on(ASCC.OWNER_USER_ID.eq(appUserOwner.APP_USER_ID))
                .join(appUserUpdater)
                .on(ASCC.LAST_UPDATED_BY.eq(appUserUpdater.APP_USER_ID))
                .where(conditions)
                .fetch().map(row -> {
                    CcList ccList = new CcList();
                    ccList.setType("ASCC");
                    ccList.setManifestId(row.getValue(ASCC_RELEASE_MANIFEST.ASCC_RELEASE_MANIFEST_ID).longValue());
                    ccList.setGuid(row.getValue(ASCC.GUID));
                    ccList.setDen(row.getValue(ASCC.DEN));
                    ccList.setDefinition(org.apache.commons.lang3.StringUtils.stripToNull(row.getValue(ASCC.DEFINITION)));
                    ccList.setDefinitionSource(org.apache.commons.lang3.StringUtils.stripToNull(row.getValue(ASCC.DEFINITION_SOURCE)));
                    ccList.setState(CcState.valueOf(row.getValue(ASCC.STATE)));
                    ccList.setDeprecated(row.getValue(ASCC.IS_DEPRECATED) == 1);
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
        AppUser appUserOwner = APP_USER.as("owner");
        AppUser appUserUpdater = APP_USER.as("updater");

        List<Condition> conditions = new ArrayList();
        conditions.add(BCC_RELEASE_MANIFEST.RELEASE_ID.eq(ULong.valueOf(request.getReleaseId())));
        if (release.getReleaseNum().equals("Working")) {
            conditions.add(BCC.DEN.notContains("User Extension Group"));
        }
        if (request.getDeprecated() != null) {
            conditions.add(BCC.IS_DEPRECATED.eq((byte) (request.getDeprecated() ? 1 : 0)));
        }
        if (!request.getStates().isEmpty()) {
            conditions.add(BCC.STATE.in(
                    request.getStates().stream().map(CcState::getValue).collect(Collectors.toList())));
        }
        if (!request.getOwnerLoginIds().isEmpty()) {
            conditions.add(appUserOwner.LOGIN_ID.in(request.getOwnerLoginIds()));
        }
        if (!request.getUpdaterLoginIds().isEmpty()) {
            conditions.add(appUserUpdater.LOGIN_ID.in(request.getUpdaterLoginIds()));
        }
        if (!request.getExcludes().isEmpty()) {
            conditions.add(BCC_RELEASE_MANIFEST.BCC_RELEASE_MANIFEST_ID.notIn(request.getExcludes()));
        }
        if (request.getDen() != null && !request.getDen().isEmpty()) {
            conditions.add(getDenFilter(BCC.DEN, request.getDen()));
        }
        if (request.getDefinition() != null && !request.getDefinition().isEmpty()) {
            conditions.add(DSL.lower(BCC.DEFINITION).contains(request.getDefinition().trim().toLowerCase()));
        }
        if (request.getUpdateStartDate() != null) {
            conditions.add(BCC.LAST_UPDATE_TIMESTAMP.greaterThan(new Timestamp(request.getUpdateStartDate().getTime())));
        }
        if (request.getUpdateEndDate() != null) {
            conditions.add(BCC.LAST_UPDATE_TIMESTAMP.lessThan(new Timestamp(request.getUpdateEndDate().getTime())));
        }

        return dslContext.select(
                BCC_RELEASE_MANIFEST.BCC_RELEASE_MANIFEST_ID,
                BCC.GUID,
                BCC.DEN,
                BCC.DEFINITION,
                BCC.DEFINITION_SOURCE,
                BCC.STATE,
                BCC.IS_DEPRECATED,
                BCC.LAST_UPDATE_TIMESTAMP,
                appUserOwner.LOGIN_ID.as("owner"),
                appUserUpdater.LOGIN_ID.as("last_update_user"),
                BCC.REVISION_NUM,
                BCC.REVISION_TRACKING_NUM)
                .from(BCC)
                .join(BCC_RELEASE_MANIFEST)
                .on(BCC.BCC_ID.eq(BCC_RELEASE_MANIFEST.BCC_ID).and(BCC_RELEASE_MANIFEST.RELEASE_ID.eq(ULong.valueOf(release.getReleaseId()))))
                .join(RELEASE)
                .on(BCC_RELEASE_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .join(appUserOwner)
                .on(BCC.OWNER_USER_ID.eq(appUserOwner.APP_USER_ID))
                .join(appUserUpdater)
                .on(BCC.LAST_UPDATED_BY.eq(appUserUpdater.APP_USER_ID))
                .where(conditions)
                .fetch().map(row -> {
                    CcList ccList = new CcList();
                    ccList.setType("BCC");
                    ccList.setManifestId(row.getValue(BCC_RELEASE_MANIFEST.BCC_RELEASE_MANIFEST_ID).longValue());
                    ccList.setGuid(row.getValue(BCC.GUID));
                    ccList.setDen(row.getValue(BCC.DEN));
                    ccList.setDefinition(org.apache.commons.lang3.StringUtils.stripToNull(row.getValue(BCC.DEFINITION)));
                    ccList.setDefinitionSource(org.apache.commons.lang3.StringUtils.stripToNull(row.getValue(BCC.DEFINITION_SOURCE)));
                    ccList.setState(CcState.valueOf(row.getValue(BCC.STATE)));
                    ccList.setDeprecated(row.getValue(BCC.IS_DEPRECATED) == 1);
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
        AppUser appUserOwner = APP_USER.as("owner");
        AppUser appUserUpdater = APP_USER.as("updater");

        List<Condition> conditions = new ArrayList();
        conditions.add(ASCCP_RELEASE_MANIFEST.RELEASE_ID.eq(ULong.valueOf(request.getReleaseId())));
        conditions.add(ASCCP.DEN.notLike("%User Extension Group"));

        if (release.getReleaseNum().equals("Working")) {
            conditions.add(ASCCP.DEN.notContains("User Extension Group"));
        }
        if (request.getDeprecated() != null) {
            conditions.add(ASCCP.IS_DEPRECATED.eq((byte) (request.getDeprecated() ? 1 : 0)));
        }
        if (!request.getStates().isEmpty()) {
            conditions.add(ASCCP.STATE.in(
                    request.getStates().stream().map(CcState::getValue).collect(Collectors.toList())));
        }
        if (!request.getOwnerLoginIds().isEmpty()) {
            conditions.add(appUserOwner.LOGIN_ID.in(request.getOwnerLoginIds()));
        }
        if (!request.getUpdaterLoginIds().isEmpty()) {
            conditions.add(appUserUpdater.LOGIN_ID.in(request.getUpdaterLoginIds()));
        }
        if (!request.getExcludes().isEmpty()) {
            conditions.add(ASCCP_RELEASE_MANIFEST.ASCCP_RELEASE_MANIFEST_ID.notIn(request.getExcludes()));
        }
        if (request.getDen() != null && !request.getDen().isEmpty()) {
            conditions.add(getDenFilter(ASCCP.DEN, request.getDen()));
        }
        if (request.getDefinition() != null && !request.getDefinition().isEmpty()) {
            conditions.add(DSL.lower(ASCCP.DEFINITION).contains(request.getDefinition().trim().toLowerCase()));
        }
        if (request.getUpdateStartDate() != null) {
            conditions.add(ASCCP.LAST_UPDATE_TIMESTAMP.greaterThan(new Timestamp(request.getUpdateStartDate().getTime())));
        }
        if (request.getUpdateEndDate() != null) {
            conditions.add(ASCCP.LAST_UPDATE_TIMESTAMP.lessThan(new Timestamp(request.getUpdateEndDate().getTime())));
        }

        return dslContext.select(
                ASCCP_RELEASE_MANIFEST.ASCCP_RELEASE_MANIFEST_ID,
                ASCCP.GUID,
                ASCCP.DEN,
                ASCCP.DEFINITION,
                ASCCP.DEFINITION_SOURCE,
                MODULE.MODULE_,
                ASCCP.STATE,
                ASCCP.IS_DEPRECATED,
                ASCCP.LAST_UPDATE_TIMESTAMP,
                appUserOwner.LOGIN_ID.as("owner"),
                appUserUpdater.LOGIN_ID.as("last_update_user"),
                ASCCP.REVISION_NUM,
                ASCCP.REVISION_TRACKING_NUM)
                .from(ASCCP)
                .join(ASCCP_RELEASE_MANIFEST)
                .on(ASCCP.ASCCP_ID.eq(ASCCP_RELEASE_MANIFEST.ASCCP_ID).and(ASCCP_RELEASE_MANIFEST.RELEASE_ID.eq(ULong.valueOf(release.getReleaseId()))))
                .join(RELEASE)
                .on(ASCCP_RELEASE_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .join(appUserOwner)
                .on(ASCCP.OWNER_USER_ID.eq(appUserOwner.APP_USER_ID))
                .join(appUserUpdater)
                .on(ASCCP.LAST_UPDATED_BY.eq(appUserUpdater.APP_USER_ID))
                .leftOuterJoin(MODULE)
                .on(ASCCP_RELEASE_MANIFEST.MODULE_ID.eq(MODULE.MODULE_ID))
                .where(conditions)
                .fetch().map(row -> {
                    CcList ccList = new CcList();
                    ccList.setType("ASCCP");
                    ccList.setManifestId(row.getValue(ASCCP_RELEASE_MANIFEST.ASCCP_RELEASE_MANIFEST_ID).longValue());
                    ccList.setGuid(row.getValue(ASCCP.GUID));
                    ccList.setDen(row.getValue(ASCCP.DEN));
                    ccList.setDefinition(org.apache.commons.lang3.StringUtils.stripToNull(row.getValue(ASCCP.DEFINITION)));
                    ccList.setDefinitionSource(org.apache.commons.lang3.StringUtils.stripToNull(row.getValue(ASCCP.DEFINITION_SOURCE)));
                    ccList.setModule(row.getValue(MODULE.MODULE_));
                    ccList.setState(CcState.valueOf(row.getValue(ASCCP.STATE)));
                    ccList.setDeprecated(row.getValue(ASCCP.IS_DEPRECATED) == 1);
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
        AppUser appUserOwner = APP_USER.as("owner");
        AppUser appUserUpdater = APP_USER.as("updater");

        List<Condition> conditions = new ArrayList();
        conditions.add(BCCP_RELEASE_MANIFEST.RELEASE_ID.eq(ULong.valueOf(request.getReleaseId())));
        if (request.getDeprecated() != null) {
            conditions.add(BCCP.IS_DEPRECATED.eq((byte) (request.getDeprecated() ? 1 : 0)));
        }
        if (!request.getStates().isEmpty()) {
            conditions.add(BCCP.STATE.in(
                    request.getStates().stream().map(CcState::getValue).collect(Collectors.toList())));
        }
        if (!request.getOwnerLoginIds().isEmpty()) {
            conditions.add(appUserOwner.LOGIN_ID.in(request.getOwnerLoginIds()));
        }
        if (!request.getUpdaterLoginIds().isEmpty()) {
            conditions.add(appUserUpdater.LOGIN_ID.in(request.getUpdaterLoginIds()));
        }
        if (!request.getExcludes().isEmpty()) {
            conditions.add(BCCP_RELEASE_MANIFEST.BCCP_RELEASE_MANIFEST_ID.notIn(request.getExcludes()));
        }
        if (request.getDen() != null && !request.getDen().isEmpty()) {
            conditions.add(getDenFilter(BCCP.DEN, request.getDen()));
        }
        if (request.getDefinition() != null && !request.getDefinition().isEmpty()) {
            conditions.add(DSL.lower(BCCP.DEFINITION).contains(request.getDefinition().trim().toLowerCase()));
        }
        if (request.getUpdateStartDate() != null) {
            conditions.add(BCCP.LAST_UPDATE_TIMESTAMP.greaterThan(new Timestamp(request.getUpdateStartDate().getTime())));
        }
        if (request.getUpdateEndDate() != null) {
            conditions.add(BCCP.LAST_UPDATE_TIMESTAMP.lessThan(new Timestamp(request.getUpdateEndDate().getTime())));
        }

        return dslContext.select(
                BCCP_RELEASE_MANIFEST.BCCP_RELEASE_MANIFEST_ID,
                BCCP.GUID,
                BCCP.DEN,
                BCCP.DEFINITION,
                BCCP.DEFINITION_SOURCE,
                MODULE.MODULE_,
                BCCP.STATE,
                BCCP.IS_DEPRECATED,
                BCCP.LAST_UPDATE_TIMESTAMP,
                appUserOwner.LOGIN_ID.as("owner"),
                appUserUpdater.LOGIN_ID.as("last_update_user"),
                BCCP.REVISION_NUM,
                BCCP.REVISION_TRACKING_NUM)
                .from(BCCP)
                .join(BCCP_RELEASE_MANIFEST)
                .on(BCCP.BCCP_ID.eq(BCCP_RELEASE_MANIFEST.BCCP_ID).and(BCCP_RELEASE_MANIFEST.RELEASE_ID.eq(ULong.valueOf(release.getReleaseId()))))
                .join(RELEASE)
                .on(BCCP_RELEASE_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .join(appUserOwner)
                .on(BCCP.OWNER_USER_ID.eq(appUserOwner.APP_USER_ID))
                .join(appUserUpdater)
                .on(BCCP.LAST_UPDATED_BY.eq(appUserUpdater.APP_USER_ID))
                .leftJoin(MODULE)
                .on(BCCP_RELEASE_MANIFEST.MODULE_ID.eq(MODULE.MODULE_ID))
                .where(conditions)
                .fetch().map(row -> {
                    CcList ccList = new CcList();
                    ccList.setType("BCCP");
                    ccList.setManifestId(row.getValue(BCCP_RELEASE_MANIFEST.BCCP_RELEASE_MANIFEST_ID).longValue());
                    ccList.setGuid(row.getValue(BCCP.GUID));
                    ccList.setDen(row.getValue(BCCP.DEN));
                    ccList.setDefinition(org.apache.commons.lang3.StringUtils.stripToNull(row.getValue(BCCP.DEFINITION)));
                    ccList.setDefinitionSource(org.apache.commons.lang3.StringUtils.stripToNull(row.getValue(BCCP.DEFINITION_SOURCE)));
                    ccList.setModule(row.getValue(MODULE.MODULE_));
                    ccList.setState(CcState.valueOf(row.getValue(BCCP.STATE)));
                    ccList.setDeprecated(row.getValue(BCCP.IS_DEPRECATED) == 1);
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
        AppUser appUserOwner = APP_USER.as("owner");
        AppUser appUserUpdater = APP_USER.as("updater");

        List<Condition> conditions = new ArrayList();
        conditions.add(DT.TYPE.eq(BDT.getValue()));
        conditions.add(DT_RELEASE_MANIFEST.RELEASE_ID.eq(ULong.valueOf(request.getReleaseId())));
        if (request.getDeprecated() != null) {
            conditions.add(DT.IS_DEPRECATED.eq((byte) (request.getDeprecated() ? 1 : 0)));
        }
        if (!request.getStates().isEmpty()) {
            conditions.add(DT.STATE.in(
                    request.getStates().stream().map(CcState::getValue).collect(Collectors.toList())));
        }
        if (!request.getOwnerLoginIds().isEmpty()) {
            conditions.add(appUserOwner.LOGIN_ID.in(request.getOwnerLoginIds()));
        }
        if (!request.getUpdaterLoginIds().isEmpty()) {
            conditions.add(appUserUpdater.LOGIN_ID.in(request.getUpdaterLoginIds()));
        }
        if (!request.getExcludes().isEmpty()) {
            conditions.add(DT.DT_ID.notIn(request.getExcludes()));
        }
        if (request.getDen() != null && !request.getDen().isEmpty()) {
            conditions.add(getDenFilter(DT.DEN, request.getDen()));
        }
        if (request.getDefinition() != null && !request.getDefinition().isEmpty()) {
            conditions.add(DSL.lower(DT.DEFINITION).contains(request.getDefinition().trim().toLowerCase()));
        }
        if (request.getUpdateStartDate() != null) {
            conditions.add(DT.LAST_UPDATE_TIMESTAMP.greaterThan(new Timestamp(request.getUpdateStartDate().getTime())));
        }
        if (request.getUpdateEndDate() != null) {
            conditions.add(DT.LAST_UPDATE_TIMESTAMP.lessThan(new Timestamp(request.getUpdateEndDate().getTime())));
        }

        return dslContext.select(
                DT_RELEASE_MANIFEST.DT_RELEASE_MANIFEST_ID,
                DT.DT_ID,
                DT.GUID,
                DT.DEN,
                DT.DEFINITION,
                DT.DEFINITION_SOURCE,
                MODULE.MODULE_,
                DT.STATE,
                DT.IS_DEPRECATED,
                DT.LAST_UPDATE_TIMESTAMP,
                appUserOwner.LOGIN_ID.as("owner"),
                appUserUpdater.LOGIN_ID.as("last_update_user"),
                DT.REVISION_NUM,
                DT.REVISION_TRACKING_NUM)
                .from(DT)
                .join(DT_RELEASE_MANIFEST)
                .on(DT.DT_ID.eq(DT_RELEASE_MANIFEST.DT_ID).and(DT_RELEASE_MANIFEST.RELEASE_ID.eq(ULong.valueOf(release.getReleaseId()))))
                .join(RELEASE)
                .on(DT_RELEASE_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .join(appUserOwner)
                .on(DT.OWNER_USER_ID.eq(appUserOwner.APP_USER_ID))
                .join(appUserUpdater)
                .on(DT.LAST_UPDATED_BY.eq(appUserUpdater.APP_USER_ID))
                .leftJoin(MODULE)
                .on(DT_RELEASE_MANIFEST.MODULE_ID.eq(MODULE.MODULE_ID))
                .where(conditions)
                .fetch().map(row -> {
                    CcList ccList = new CcList();
                    ccList.setType("BDT");
                    ccList.setManifestId(row.getValue(DT_RELEASE_MANIFEST.DT_RELEASE_MANIFEST_ID).longValue());
                    ccList.setId(row.getValue(DT.DT_ID).longValue());
                    ccList.setGuid(row.getValue(DT.GUID));
                    String den = row.getValue(DT.DEN);
                    if (!StringUtils.isEmpty(den)) {
                        ccList.setDen(den.replaceAll("_", " ").replaceAll("  ", " "));
                    }
                    ccList.setDefinition(org.apache.commons.lang3.StringUtils.stripToNull(row.getValue(DT.DEFINITION)));
                    ccList.setDefinitionSource(org.apache.commons.lang3.StringUtils.stripToNull(row.getValue(DT.DEFINITION_SOURCE)));
                    ccList.setModule(row.getValue(MODULE.MODULE_));
                    ccList.setState(CcState.valueOf(row.getValue(DT.STATE)));
                    ccList.setDeprecated(row.getValue(DT.IS_DEPRECATED) == 1);
                    ccList.setLastUpdateTimestamp(row.getValue(DT.LAST_UPDATE_TIMESTAMP));
                    ccList.setOwner((String) row.getValue("owner"));
                    ccList.setLastUpdateUser((String) row.getValue("last_update_user"));
                    ccList.setRevision(row.getValue(DT.REVISION_NUM) + "." + row.getValue(DT.REVISION_TRACKING_NUM));
                    return ccList;
                });
    }

    private Condition getDenFilter(TableField<?, String> field, String keyword) {
        Condition denCondition = null;
        List<String> filters = Arrays.asList(keyword.toLowerCase().split(" ")).stream()
                .map(e -> e.replaceAll("[^a-z0-9]", "").trim()).collect(Collectors.toList());
        for (String token : filters) {
            if (denCondition == null) {
                denCondition = field.contains(token);
            } else {
                denCondition = denCondition.and(field.contains(token));
            }

        }
        return denCondition;
    }

    public ULong updateAccOwnerUserId(AccReleaseManifestRecord accReleaseManifest, ULong target, ULong userId, Timestamp timestamp) {
        AccRecord acc = dslContext.selectFrom(ACC).where(ACC.ACC_ID.eq(accReleaseManifest.getAccId())).fetchOne();
        acc.setAccId(null);
        acc.setOwnerUserId(target);
        acc.setLastUpdatedBy(userId);
        acc.setLastUpdateTimestamp(timestamp);
        acc.setRevisionTrackingNum(acc.getRevisionTrackingNum() + 1);
        acc.setRevisionAction((byte) RevisionAction.Update.getValue());
        acc.insert();
        accReleaseManifest.setAccId(acc.getAccId());
        accReleaseManifest.update();

        return acc.getAccId();
    }

    public void updateAsccOwnerUserId(ULong originAccId, ULong newAccId, ULong releaseId, ULong target, ULong userId, Timestamp timestamp) {
        List<AsccReleaseManifestRecord> asccReleaseManifestRecords = manifestRepository.getAsccReleaseManifestByFromAccId(originAccId.longValue(), releaseId.longValue());
        for(AsccReleaseManifestRecord asccReleaseManifestRecord: asccReleaseManifestRecords) {
            AsccRecord asccRecord = nodeRepository.getAsccRecordById(asccReleaseManifestRecord.getAsccId().longValue());
            asccRecord.setAsccId(null);
            asccRecord.setFromAccId(newAccId);
            asccRecord.setOwnerUserId(target);
            asccRecord.setLastUpdatedBy(userId);
            asccRecord.setLastUpdateTimestamp(timestamp);
            asccRecord.setRevisionAction((byte) RevisionAction.Update.getValue());
            asccRecord.setRevisionTrackingNum(asccRecord.getRevisionTrackingNum() + 1);
            asccRecord.insert();

            asccReleaseManifestRecord.setAsccId(asccRecord.getAsccId());
            asccReleaseManifestRecord.setFromAccId(newAccId);
            asccReleaseManifestRecord.update();
        }
        
        nodeRepository.updateAsccpByRoleOfAcc(userId.longValue(), originAccId.longValue(), newAccId.longValue(), releaseId.longValue(), null, timestamp);
        nodeRepository.updateAccByBasedAcc(userId.longValue(), originAccId.longValue(), newAccId.longValue(), releaseId.longValue(), timestamp);
    }

    public void updateBccOwnerUserId(ULong originAccId, ULong newAccId, ULong releaseId, ULong target, ULong userId, Timestamp timestamp) {
        List<BccReleaseManifestRecord> bccRelebeManifestRecords = manifestRepository.getBccReleaseManifestByFromAccId(originAccId.longValue(), releaseId.longValue());
        for(BccReleaseManifestRecord bccRelebeManifestRecord: bccRelebeManifestRecords) {
            BccRecord bccRecord = nodeRepository.getBccRecordById(bccRelebeManifestRecord.getBccId().longValue());
            bccRecord.setBccId(null);
            bccRecord.setFromAccId(newAccId);
            bccRecord.setOwnerUserId(target);
            bccRecord.setLastUpdatedBy(userId);
            bccRecord.setLastUpdateTimestamp(timestamp);
            bccRecord.setRevisionAction((byte) RevisionAction.Update.getValue());
            bccRecord.setRevisionTrackingNum(bccRecord.getRevisionTrackingNum() + 1);
            bccRecord.insert();

            bccRelebeManifestRecord.setBccId(bccRecord.getBccId());
            bccRelebeManifestRecord.setFromAccId(newAccId);
            bccRelebeManifestRecord.update();
        }
        nodeRepository.updateAsccpByRoleOfAcc(userId.longValue(), originAccId.longValue(), newAccId.longValue(), releaseId.longValue(), null, timestamp);
        nodeRepository.updateAccByBasedAcc(userId.longValue(), originAccId.longValue(), newAccId.longValue(), releaseId.longValue(), timestamp);
    }

    public void updateAsccpOwnerUserId(AsccpReleaseManifestRecord asccpReleaseManifest, ULong target, ULong userId, Timestamp timestamp) {
        List<AsccReleaseManifestRecord> asccReleaseManifestRecords = manifestRepository.getAsccReleaseManifestByToAsccpId(
                asccpReleaseManifest.getAsccpId().longValue(), asccpReleaseManifest.getReleaseId().longValue());
        AsccpRecord asccp = dslContext.selectFrom(ASCCP).where(ASCCP.ASCCP_ID.eq(asccpReleaseManifest.getAsccpId())).fetchOne();
        asccp.setAsccpId(null);
        asccp.setOwnerUserId(target);
        asccp.setLastUpdatedBy(userId);
        asccp.setLastUpdateTimestamp(timestamp);
        asccp.setRevisionTrackingNum(asccp.getRevisionTrackingNum() + 1);
        asccp.setRevisionAction((byte) RevisionAction.Update.getValue());
        asccp.insert();
        asccpReleaseManifest.setAsccpId(asccp.getAsccpId());
        asccpReleaseManifest.update();
        
        for(AsccReleaseManifestRecord asccReleaseManifestRecord: asccReleaseManifestRecords) {
            AsccRecord asccRecord = nodeRepository.getAsccRecordById(asccReleaseManifestRecord.getAsccId().longValue());
            asccRecord.setAsccId(null);
            asccRecord.setToAsccpId(asccp.getAsccpId());
            asccRecord.setLastUpdatedBy(userId);
            asccRecord.setLastUpdateTimestamp(timestamp);
            asccRecord.setRevisionAction((byte) RevisionAction.Update.getValue());
            asccRecord.setRevisionTrackingNum(asccRecord.getRevisionTrackingNum() + 1);
            asccRecord.insert();

            asccReleaseManifestRecord.setAsccId(asccRecord.getAsccId());
            asccReleaseManifestRecord.setToAsccpId(asccp.getAsccpId());
            asccReleaseManifestRecord.update();
        }
    }

    public void updateBccpOwnerUserId(BccpReleaseManifestRecord bccpReleaseManifest, ULong target, ULong userId, Timestamp timestamp) {
        List<BccReleaseManifestRecord> bccReleaseManifestRecords = manifestRepository.getBccReleaseManifestByToBccpId(
                bccpReleaseManifest.getBccpId().longValue(), bccpReleaseManifest.getReleaseId().longValue());
        BccpRecord bccp = dslContext.selectFrom(BCCP).where(BCCP.BCCP_ID.eq(bccpReleaseManifest.getBccpId())).fetchOne();

        bccp.setBccpId(null);
        bccp.setOwnerUserId(target);
        bccp.setLastUpdatedBy(userId);
        bccp.setLastUpdateTimestamp(timestamp);
        bccp.setRevisionTrackingNum(bccp.getRevisionTrackingNum() + 1);
        bccp.setRevisionAction(RevisionAction.Update.getValue());
        bccp.insert();
        bccpReleaseManifest.setBccpId(bccp.getBccpId());
        bccpReleaseManifest.update();

        for(BccReleaseManifestRecord bccReleaseManifestRecord: bccReleaseManifestRecords) {
            BccRecord bccRecord = nodeRepository.getBccRecordById(bccReleaseManifestRecord.getBccId().longValue());
            bccRecord.setBccId(null);
            bccRecord.setToBccpId(bccp.getBccpId());
            bccRecord.setLastUpdatedBy(userId);
            bccRecord.setLastUpdateTimestamp(timestamp);
            bccRecord.setRevisionAction((byte) RevisionAction.Update.getValue());
            bccRecord.setRevisionTrackingNum(bccRecord.getRevisionTrackingNum() + 1);
            bccRecord.insert();

            bccReleaseManifestRecord.setBccId(bccRecord.getBccId());
            bccReleaseManifestRecord.setToBccpId(bccp.getBccpId());
            bccReleaseManifestRecord.update();
        }
    }
}
