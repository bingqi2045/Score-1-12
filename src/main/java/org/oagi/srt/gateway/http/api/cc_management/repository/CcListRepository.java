package org.oagi.srt.gateway.http.api.cc_management.repository;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.data.OagisComponentType;
import org.oagi.srt.data.Release;
import org.oagi.srt.entity.jooq.tables.AppUser;
import org.oagi.srt.gateway.http.api.cc_management.data.CcList;
import org.oagi.srt.gateway.http.api.cc_management.data.CcListRequest;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;
import org.oagi.srt.repo.CoreComponentRepository;
import org.oagi.srt.repo.RevisionRepository;
import org.oagi.srt.repository.ReleaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static org.oagi.srt.data.DTType.BDT;
import static org.oagi.srt.entity.jooq.Tables.*;
import static org.oagi.srt.gateway.http.helper.filter.ContainsFilterBuilder.contains;

@Repository
public class CcListRepository {

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private ReleaseRepository releaseRepository;

    @Autowired
    private ManifestRepository manifestRepository;

    @Autowired
    private CoreComponentRepository ccRepository;

    @Autowired
    private RevisionRepository revisionRepository;

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
        conditions.add(ACC_MANIFEST.RELEASE_ID.eq(ULong.valueOf(request.getReleaseId())));
        if ("Working".equals(release.getReleaseNum())) {
            conditions.add(ACC.OAGIS_COMPONENT_TYPE.notEqual(OagisComponentType.UserExtensionGroup.getValue()));
        }
        if (request.getDeprecated() != null) {
            conditions.add(ACC.IS_DEPRECATED.eq((byte) (request.getDeprecated() ? 1 : 0)));
        }
        if (!request.getStates().isEmpty()) {
            conditions.add(ACC.STATE.in(
                    request.getStates().stream().map(CcState::name).collect(Collectors.toList())));
        }
        if (!request.getOwnerLoginIds().isEmpty()) {
            conditions.add(appUserOwner.LOGIN_ID.in(request.getOwnerLoginIds()));
        }
        if (!request.getUpdaterLoginIds().isEmpty()) {
            conditions.add(appUserUpdater.LOGIN_ID.in(request.getUpdaterLoginIds()));
        }
        if (!request.getExcludes().isEmpty()) {
            conditions.add(ACC_MANIFEST.ACC_MANIFEST_ID.notIn(request.getExcludes()));
        }
        if (!StringUtils.isEmpty(request.getDen())) {
            conditions.addAll(contains(request.getDen(), ACC.DEN));
        }
        if (!StringUtils.isEmpty(request.getDefinition())) {
            conditions.addAll(contains(request.getDefinition(), ACC.DEFINITION));
        }
        if (!StringUtils.isEmpty(request.getModule())) {
            conditions.addAll(contains(request.getModule(), MODULE.MODULE_));
        }
        if (request.getUpdateStartDate() != null) {
            conditions.add(ACC.LAST_UPDATE_TIMESTAMP.greaterThan(new Timestamp(request.getUpdateStartDate().getTime()).toLocalDateTime()));
        }
        if (request.getUpdateEndDate() != null) {
            conditions.add(ACC.LAST_UPDATE_TIMESTAMP.lessThan(new Timestamp(request.getUpdateEndDate().getTime()).toLocalDateTime()));
        }
        if (request.getComponentTypes() != null && !request.getComponentTypes().isEmpty()) {
            conditions.add(ACC.OAGIS_COMPONENT_TYPE.in(Arrays.asList(request.getComponentTypes().split(","))));
        }

        return dslContext.select(
                ACC_MANIFEST.ACC_MANIFEST_ID,
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
                REVISION.REVISION_NUM,
                REVISION.REVISION_TRACKING_NUM,
                RELEASE.RELEASE_NUM)
                .from(ACC)
                .join(REVISION)
                .on(ACC.REVISION_ID.eq(REVISION.REVISION_ID))
                .join(ACC_MANIFEST)
                .on(ACC.ACC_ID.eq(ACC_MANIFEST.ACC_ID).and(ACC_MANIFEST.RELEASE_ID.eq(ULong.valueOf(release.getReleaseId()))))
                .join(RELEASE)
                .on(ACC_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .join(appUserOwner)
                .on(ACC.OWNER_USER_ID.eq(appUserOwner.APP_USER_ID))
                .join(appUserUpdater)
                .on(ACC.LAST_UPDATED_BY.eq(appUserUpdater.APP_USER_ID))
                .leftJoin(MODULE)
                .on(ACC_MANIFEST.MODULE_ID.eq(MODULE.MODULE_ID))
                .where(conditions)
                .fetch().map(row -> {
                    CcList ccList = new CcList();
                    ccList.setType("ACC");
                    ccList.setManifestId(row.getValue(ACC_MANIFEST.ACC_MANIFEST_ID).longValue());
                    ccList.setId(row.getValue(ACC.ACC_ID).longValue());
                    ccList.setGuid(row.getValue(ACC.GUID));
                    ccList.setDen(row.getValue(ACC.DEN));
                    ccList.setDefinition(org.apache.commons.lang3.StringUtils.stripToNull(row.getValue(ACC.DEFINITION)));
                    ccList.setDefinitionSource(org.apache.commons.lang3.StringUtils.stripToNull(row.getValue(ACC.DEFINITION_SOURCE)));
                    ccList.setModule(row.getValue(MODULE.MODULE_));
                    ccList.setOagisComponentType(OagisComponentType.valueOf(row.getValue(ACC.OAGIS_COMPONENT_TYPE)));
                    ccList.setState(CcState.valueOf(row.getValue(ACC.STATE)));
                    ccList.setDeprecated(row.getValue(ACC.IS_DEPRECATED) == 1);
                    ccList.setLastUpdateTimestamp(Date.from(row.getValue(ACC.LAST_UPDATE_TIMESTAMP).atZone(ZoneId.systemDefault()).toInstant()));
                    ccList.setOwner((String) row.getValue("owner"));
                    ccList.setLastUpdateUser((String) row.getValue("last_update_user"));
                    ccList.setRevision(row.getValue(REVISION.REVISION_NUM) + "." + row.getValue(REVISION.REVISION_TRACKING_NUM));
                    ccList.setReleaseNum(row.getValue(RELEASE.RELEASE_NUM));
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
        conditions.add(ASCC_MANIFEST.RELEASE_ID.eq(ULong.valueOf(request.getReleaseId())));
        conditions.add(ASCC.DEN.notContains("User Extension Group"));

        if (request.getDeprecated() != null) {
            conditions.add(ASCC.IS_DEPRECATED.eq((byte) (request.getDeprecated() ? 1 : 0)));
        }
        if (!request.getStates().isEmpty()) {
            conditions.add(ASCC.STATE.in(
                    request.getStates().stream().map(CcState::name).collect(Collectors.toList())));
        }
        if (!request.getOwnerLoginIds().isEmpty()) {
            conditions.add(appUserOwner.LOGIN_ID.in(request.getOwnerLoginIds()));
        }
        if (!request.getUpdaterLoginIds().isEmpty()) {
            conditions.add(appUserUpdater.LOGIN_ID.in(request.getUpdaterLoginIds()));
        }
        if (!request.getExcludes().isEmpty()) {
            conditions.add(ASCC_MANIFEST.ASCC_MANIFEST_ID.notIn(request.getExcludes()));
        }
        if (!StringUtils.isEmpty(request.getDen())) {
            conditions.addAll(contains(request.getDen(), ASCC.DEN));
        }
        if (!StringUtils.isEmpty(request.getDefinition())) {
            conditions.addAll(contains(request.getDefinition(), ASCC.DEFINITION));
        }
        if (request.getUpdateStartDate() != null) {
            conditions.add(ASCC.LAST_UPDATE_TIMESTAMP.greaterThan(new Timestamp(request.getUpdateStartDate().getTime()).toLocalDateTime()));
        }
        if (request.getUpdateEndDate() != null) {
            conditions.add(ASCC.LAST_UPDATE_TIMESTAMP.lessThan(new Timestamp(request.getUpdateEndDate().getTime()).toLocalDateTime()));
        }

        return dslContext.select(
                ASCC_MANIFEST.ASCC_MANIFEST_ID,
                ASCC.GUID,
                ASCC.DEN,
                ASCC.DEFINITION,
                ASCC.DEFINITION_SOURCE,
                ASCC.STATE,
                ASCC.IS_DEPRECATED,
                ASCC.LAST_UPDATE_TIMESTAMP,
                appUserOwner.LOGIN_ID.as("owner"),
                appUserUpdater.LOGIN_ID.as("last_update_user"),
                REVISION.REVISION_NUM,
                REVISION.REVISION_TRACKING_NUM,
                RELEASE.RELEASE_NUM)
                .from(ASCC)
                .join(REVISION)
                .on(ASCC.REVISION_ID.eq(REVISION.REVISION_ID))
                .join(ASCC_MANIFEST)
                .on(ASCC.ASCC_ID.eq(ASCC_MANIFEST.ASCC_ID).and(ASCC_MANIFEST.RELEASE_ID.eq(ULong.valueOf(release.getReleaseId()))))
                .join(RELEASE)
                .on(ASCC_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .join(appUserOwner)
                .on(ASCC.OWNER_USER_ID.eq(appUserOwner.APP_USER_ID))
                .join(appUserUpdater)
                .on(ASCC.LAST_UPDATED_BY.eq(appUserUpdater.APP_USER_ID))
                .where(conditions)
                .fetch().map(row -> {
                    CcList ccList = new CcList();
                    ccList.setType("ASCC");
                    ccList.setManifestId(row.getValue(ASCC_MANIFEST.ASCC_MANIFEST_ID).longValue());
                    ccList.setGuid(row.getValue(ASCC.GUID));
                    ccList.setDen(row.getValue(ASCC.DEN));
                    ccList.setDefinition(org.apache.commons.lang3.StringUtils.stripToNull(row.getValue(ASCC.DEFINITION)));
                    ccList.setDefinitionSource(org.apache.commons.lang3.StringUtils.stripToNull(row.getValue(ASCC.DEFINITION_SOURCE)));
                    ccList.setState(CcState.valueOf(row.getValue(ASCC.STATE)));
                    ccList.setDeprecated(row.getValue(ASCC.IS_DEPRECATED) == 1);
                    ccList.setLastUpdateTimestamp(Date.from(row.getValue(ASCC.LAST_UPDATE_TIMESTAMP).atZone(ZoneId.systemDefault()).toInstant()));
                    ccList.setOwner((String) row.getValue("owner"));
                    ccList.setLastUpdateUser((String) row.getValue("last_update_user"));
                    ccList.setRevision(row.getValue(REVISION.REVISION_NUM) + "." + row.getValue(REVISION.REVISION_TRACKING_NUM));
                    ccList.setReleaseNum(row.getValue(RELEASE.RELEASE_NUM));
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
        conditions.add(BCC_MANIFEST.RELEASE_ID.eq(ULong.valueOf(request.getReleaseId())));
        conditions.add(BCC.DEN.notContains("User Extension Group"));

        if (request.getDeprecated() != null) {
            conditions.add(BCC.IS_DEPRECATED.eq((byte) (request.getDeprecated() ? 1 : 0)));
        }
        if (!request.getStates().isEmpty()) {
            conditions.add(BCC.STATE.in(
                    request.getStates().stream().map(CcState::name).collect(Collectors.toList())));
        }
        if (!request.getOwnerLoginIds().isEmpty()) {
            conditions.add(appUserOwner.LOGIN_ID.in(request.getOwnerLoginIds()));
        }
        if (!request.getUpdaterLoginIds().isEmpty()) {
            conditions.add(appUserUpdater.LOGIN_ID.in(request.getUpdaterLoginIds()));
        }
        if (!request.getExcludes().isEmpty()) {
            conditions.add(BCC_MANIFEST.BCC_MANIFEST_ID.notIn(request.getExcludes()));
        }
        if (!StringUtils.isEmpty(request.getDen())) {
            conditions.addAll(contains(request.getDen(), BCC.DEN));
        }
        if (!StringUtils.isEmpty(request.getDefinition())) {
            conditions.addAll(contains(request.getDefinition(), BCC.DEFINITION));
        }
        if (request.getUpdateStartDate() != null) {
            conditions.add(BCC.LAST_UPDATE_TIMESTAMP.greaterThan(new Timestamp(request.getUpdateStartDate().getTime()).toLocalDateTime()));
        }
        if (request.getUpdateEndDate() != null) {
            conditions.add(BCC.LAST_UPDATE_TIMESTAMP.lessThan(new Timestamp(request.getUpdateEndDate().getTime()).toLocalDateTime()));
        }

        return dslContext.select(
                BCC_MANIFEST.BCC_MANIFEST_ID,
                BCC.GUID,
                BCC.DEN,
                BCC.DEFINITION,
                BCC.DEFINITION_SOURCE,
                BCC.STATE,
                BCC.IS_DEPRECATED,
                BCC.LAST_UPDATE_TIMESTAMP,
                appUserOwner.LOGIN_ID.as("owner"),
                appUserUpdater.LOGIN_ID.as("last_update_user"),
                REVISION.REVISION_NUM,
                REVISION.REVISION_TRACKING_NUM,
                RELEASE.RELEASE_NUM)
                .from(BCC)
                .join(REVISION)
                .on(BCC.REVISION_ID.eq(REVISION.REVISION_ID))
                .join(BCC_MANIFEST)
                .on(BCC.BCC_ID.eq(BCC_MANIFEST.BCC_ID).and(BCC_MANIFEST.RELEASE_ID.eq(ULong.valueOf(release.getReleaseId()))))
                .join(RELEASE)
                .on(BCC_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .join(appUserOwner)
                .on(BCC.OWNER_USER_ID.eq(appUserOwner.APP_USER_ID))
                .join(appUserUpdater)
                .on(BCC.LAST_UPDATED_BY.eq(appUserUpdater.APP_USER_ID))
                .where(conditions)
                .fetch().map(row -> {
                    CcList ccList = new CcList();
                    ccList.setType("BCC");
                    ccList.setManifestId(row.getValue(BCC_MANIFEST.BCC_MANIFEST_ID).longValue());
                    ccList.setGuid(row.getValue(BCC.GUID));
                    ccList.setDen(row.getValue(BCC.DEN));
                    ccList.setDefinition(org.apache.commons.lang3.StringUtils.stripToNull(row.getValue(BCC.DEFINITION)));
                    ccList.setDefinitionSource(org.apache.commons.lang3.StringUtils.stripToNull(row.getValue(BCC.DEFINITION_SOURCE)));
                    ccList.setState(CcState.valueOf(row.getValue(BCC.STATE)));
                    ccList.setDeprecated(row.getValue(BCC.IS_DEPRECATED) == 1);
                    ccList.setLastUpdateTimestamp(Date.from(row.getValue(BCC.LAST_UPDATE_TIMESTAMP).atZone(ZoneId.systemDefault()).toInstant()));
                    ccList.setOwner((String) row.getValue("owner"));
                    ccList.setLastUpdateUser((String) row.getValue("last_update_user"));
                    ccList.setRevision(row.getValue(REVISION.REVISION_NUM) + "." + row.getValue(REVISION.REVISION_TRACKING_NUM));
                    ccList.setReleaseNum(row.getValue(RELEASE.RELEASE_NUM));
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
        conditions.add(ASCCP_MANIFEST.RELEASE_ID.eq(ULong.valueOf(request.getReleaseId())));
        conditions.add(ASCCP.DEN.notContains("User Extension Group"));

        if (request.getDeprecated() != null) {
            conditions.add(ASCCP.IS_DEPRECATED.eq((byte) (request.getDeprecated() ? 1 : 0)));
        }
        if (!request.getStates().isEmpty()) {
            conditions.add(ASCCP.STATE.in(
                    request.getStates().stream().map(CcState::name).collect(Collectors.toList())));
        }
        if (!request.getOwnerLoginIds().isEmpty()) {
            conditions.add(appUserOwner.LOGIN_ID.in(request.getOwnerLoginIds()));
        }
        if (!request.getUpdaterLoginIds().isEmpty()) {
            conditions.add(appUserUpdater.LOGIN_ID.in(request.getUpdaterLoginIds()));
        }
        if (!request.getExcludes().isEmpty()) {
            conditions.add(ASCCP_MANIFEST.ASCCP_MANIFEST_ID.notIn(request.getExcludes()));
        }
        if (!StringUtils.isEmpty(request.getDen())) {
            conditions.addAll(contains(request.getDen(), ASCCP.DEN));
        }
        if (!StringUtils.isEmpty(request.getDefinition())) {
            conditions.addAll(contains(request.getDefinition(), ASCCP.DEFINITION));
        }
        if (!StringUtils.isEmpty(request.getModule())) {
            conditions.addAll(contains(request.getModule(), MODULE.MODULE_));
        }
        if (request.getUpdateStartDate() != null) {
            conditions.add(ASCCP.LAST_UPDATE_TIMESTAMP.greaterThan(new Timestamp(request.getUpdateStartDate().getTime()).toLocalDateTime()));
        }
        if (request.getUpdateEndDate() != null) {
            conditions.add(ASCCP.LAST_UPDATE_TIMESTAMP.lessThan(new Timestamp(request.getUpdateEndDate().getTime()).toLocalDateTime()));
        }

        return dslContext.select(
                ASCCP_MANIFEST.ASCCP_MANIFEST_ID,
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
                REVISION.REVISION_NUM,
                REVISION.REVISION_TRACKING_NUM,
                RELEASE.RELEASE_NUM)
                .from(ASCCP)
                .join(REVISION)
                .on(ASCCP.REVISION_ID.eq(REVISION.REVISION_ID))
                .join(ASCCP_MANIFEST)
                .on(ASCCP.ASCCP_ID.eq(ASCCP_MANIFEST.ASCCP_ID).and(ASCCP_MANIFEST.RELEASE_ID.eq(ULong.valueOf(release.getReleaseId()))))
                .join(RELEASE)
                .on(ASCCP_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .join(appUserOwner)
                .on(ASCCP.OWNER_USER_ID.eq(appUserOwner.APP_USER_ID))
                .join(appUserUpdater)
                .on(ASCCP.LAST_UPDATED_BY.eq(appUserUpdater.APP_USER_ID))
                .leftOuterJoin(MODULE)
                .on(ASCCP_MANIFEST.MODULE_ID.eq(MODULE.MODULE_ID))
                .where(conditions)
                .fetch().map(row -> {
                    CcList ccList = new CcList();
                    ccList.setType("ASCCP");
                    ccList.setManifestId(row.getValue(ASCCP_MANIFEST.ASCCP_MANIFEST_ID).longValue());
                    ccList.setGuid(row.getValue(ASCCP.GUID));
                    ccList.setDen(row.getValue(ASCCP.DEN));
                    ccList.setDefinition(org.apache.commons.lang3.StringUtils.stripToNull(row.getValue(ASCCP.DEFINITION)));
                    ccList.setDefinitionSource(org.apache.commons.lang3.StringUtils.stripToNull(row.getValue(ASCCP.DEFINITION_SOURCE)));
                    ccList.setModule(row.getValue(MODULE.MODULE_));
                    ccList.setState(CcState.valueOf(row.getValue(ASCCP.STATE)));
                    ccList.setDeprecated(row.getValue(ASCCP.IS_DEPRECATED) == 1);
                    ccList.setLastUpdateTimestamp(Date.from(row.getValue(ASCCP.LAST_UPDATE_TIMESTAMP).atZone(ZoneId.systemDefault()).toInstant()));
                    ccList.setOwner((String) row.getValue("owner"));
                    ccList.setLastUpdateUser((String) row.getValue("last_update_user"));
                    ccList.setRevision(row.getValue(REVISION.REVISION_NUM) + "." + row.getValue(REVISION.REVISION_TRACKING_NUM));
                    ccList.setReleaseNum(row.getValue(RELEASE.RELEASE_NUM));
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
        conditions.add(BCCP_MANIFEST.RELEASE_ID.eq(ULong.valueOf(request.getReleaseId())));
        conditions.add(BCCP.DEN.notContains("User Extension Group"));
        if (request.getDeprecated() != null) {
            conditions.add(BCCP.IS_DEPRECATED.eq((byte) (request.getDeprecated() ? 1 : 0)));
        }
        if (!request.getStates().isEmpty()) {
            conditions.add(BCCP.STATE.in(
                    request.getStates().stream().map(CcState::name).collect(Collectors.toList())));
        }
        if (!request.getOwnerLoginIds().isEmpty()) {
            conditions.add(appUserOwner.LOGIN_ID.in(request.getOwnerLoginIds()));
        }
        if (!request.getUpdaterLoginIds().isEmpty()) {
            conditions.add(appUserUpdater.LOGIN_ID.in(request.getUpdaterLoginIds()));
        }
        if (!request.getExcludes().isEmpty()) {
            conditions.add(BCCP_MANIFEST.BCCP_MANIFEST_ID.notIn(request.getExcludes()));
        }
        if (!StringUtils.isEmpty(request.getDen())) {
            conditions.addAll(contains(request.getDen(), BCCP.DEN));
        }
        if (!StringUtils.isEmpty(request.getDefinition())) {
            conditions.addAll(contains(request.getDefinition(), BCCP.DEFINITION));
        }
        if (!StringUtils.isEmpty(request.getModule())) {
            conditions.addAll(contains(request.getModule(), MODULE.MODULE_));
        }
        if (request.getUpdateStartDate() != null) {
            conditions.add(BCCP.LAST_UPDATE_TIMESTAMP.greaterThan(new Timestamp(request.getUpdateStartDate().getTime()).toLocalDateTime()));
        }
        if (request.getUpdateEndDate() != null) {
            conditions.add(BCCP.LAST_UPDATE_TIMESTAMP.lessThan(new Timestamp(request.getUpdateEndDate().getTime()).toLocalDateTime()));
        }

        return dslContext.select(
                BCCP_MANIFEST.BCCP_MANIFEST_ID,
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
                REVISION.REVISION_NUM,
                REVISION.REVISION_TRACKING_NUM,
                RELEASE.RELEASE_NUM)
                .from(BCCP)
                .join(REVISION)
                .on(BCCP.REVISION_ID.eq(REVISION.REVISION_ID))
                .join(BCCP_MANIFEST)
                .on(BCCP.BCCP_ID.eq(BCCP_MANIFEST.BCCP_ID).and(BCCP_MANIFEST.RELEASE_ID.eq(ULong.valueOf(release.getReleaseId()))))
                .join(RELEASE)
                .on(BCCP_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .join(appUserOwner)
                .on(BCCP.OWNER_USER_ID.eq(appUserOwner.APP_USER_ID))
                .join(appUserUpdater)
                .on(BCCP.LAST_UPDATED_BY.eq(appUserUpdater.APP_USER_ID))
                .leftJoin(MODULE)
                .on(BCCP_MANIFEST.MODULE_ID.eq(MODULE.MODULE_ID))
                .where(conditions)
                .fetch().map(row -> {
                    CcList ccList = new CcList();
                    ccList.setType("BCCP");
                    ccList.setManifestId(row.getValue(BCCP_MANIFEST.BCCP_MANIFEST_ID).longValue());
                    ccList.setGuid(row.getValue(BCCP.GUID));
                    ccList.setDen(row.getValue(BCCP.DEN));
                    ccList.setDefinition(org.apache.commons.lang3.StringUtils.stripToNull(row.getValue(BCCP.DEFINITION)));
                    ccList.setDefinitionSource(org.apache.commons.lang3.StringUtils.stripToNull(row.getValue(BCCP.DEFINITION_SOURCE)));
                    ccList.setModule(row.getValue(MODULE.MODULE_));
                    ccList.setState(CcState.valueOf(row.getValue(BCCP.STATE)));
                    ccList.setDeprecated(row.getValue(BCCP.IS_DEPRECATED) == 1);
                    ccList.setLastUpdateTimestamp(Date.from(row.getValue(BCCP.LAST_UPDATE_TIMESTAMP).atZone(ZoneId.systemDefault()).toInstant()));
                    ccList.setOwner((String) row.getValue("owner"));
                    ccList.setLastUpdateUser((String) row.getValue("last_update_user"));
                    ccList.setRevision(row.getValue(REVISION.REVISION_NUM) + "." + row.getValue(REVISION.REVISION_TRACKING_NUM));
                    ccList.setReleaseNum(row.getValue(RELEASE.RELEASE_NUM));
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
        conditions.add(DT_MANIFEST.RELEASE_ID.eq(ULong.valueOf(request.getReleaseId())));
        if (request.getDeprecated() != null) {
            conditions.add(DT.IS_DEPRECATED.eq((byte) (request.getDeprecated() ? 1 : 0)));
        }
        if (!request.getStates().isEmpty()) {
            conditions.add(DT.STATE.in(
                    request.getStates().stream().map(CcState::name).collect(Collectors.toList())));
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
        if (!StringUtils.isEmpty(request.getDen())) {
            conditions.addAll(contains(request.getDen(), DT.DEN));
        }
        if (!StringUtils.isEmpty(request.getDefinition())) {
            conditions.addAll(contains(request.getDefinition(), DT.DEFINITION));
        }
        if (request.getUpdateStartDate() != null) {
            conditions.add(DT.LAST_UPDATE_TIMESTAMP.greaterThan(new Timestamp(request.getUpdateStartDate().getTime()).toLocalDateTime()));
        }
        if (request.getUpdateEndDate() != null) {
            conditions.add(DT.LAST_UPDATE_TIMESTAMP.lessThan(new Timestamp(request.getUpdateEndDate().getTime()).toLocalDateTime()));
        }

        return dslContext.select(
                DT_MANIFEST.DT_MANIFEST_ID,
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
                REVISION.REVISION_NUM,
                REVISION.REVISION_TRACKING_NUM,
                RELEASE.RELEASE_NUM)
                .from(DT)
                .join(REVISION)
                .on(DT.REVISION_ID.eq(REVISION.REVISION_ID))
                .join(DT_MANIFEST)
                .on(DT.DT_ID.eq(DT_MANIFEST.DT_ID).and(DT_MANIFEST.RELEASE_ID.eq(ULong.valueOf(release.getReleaseId()))))
                .join(RELEASE)
                .on(DT_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .join(appUserOwner)
                .on(DT.OWNER_USER_ID.eq(appUserOwner.APP_USER_ID))
                .join(appUserUpdater)
                .on(DT.LAST_UPDATED_BY.eq(appUserUpdater.APP_USER_ID))
                .leftJoin(MODULE)
                .on(DT_MANIFEST.MODULE_ID.eq(MODULE.MODULE_ID))
                .where(conditions)
                .fetch().map(row -> {
                    CcList ccList = new CcList();
                    ccList.setType("BDT");
                    ccList.setManifestId(row.getValue(DT_MANIFEST.DT_MANIFEST_ID).longValue());
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
                    ccList.setLastUpdateTimestamp(Date.from(row.getValue(DT.LAST_UPDATE_TIMESTAMP).atZone(ZoneId.systemDefault()).toInstant()));
                    ccList.setOwner((String) row.getValue("owner"));
                    ccList.setLastUpdateUser((String) row.getValue("last_update_user"));
                    ccList.setRevision(row.getValue(REVISION.REVISION_NUM) + "." + row.getValue(REVISION.REVISION_TRACKING_NUM));
                    ccList.setReleaseNum(row.getValue(RELEASE.RELEASE_NUM));
                    return ccList;
                });
    }
}
